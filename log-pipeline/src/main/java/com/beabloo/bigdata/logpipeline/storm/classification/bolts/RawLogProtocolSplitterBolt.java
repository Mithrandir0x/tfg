package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.google.common.base.Charsets;
import com.google.common.hash.*;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.api.async.RedisHLLAsyncCommands;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class RawLogProtocolSplitterBolt extends LogPipelineBaseBolt {

    private static final Logger log = LoggerFactory.getLogger(RawLogProtocolSplitterBolt.class);

    public static final String ID = "RAWLOG_PROTOCOL_SPLITTER_BOLT_ID";

    private static Pattern cockroachUri = Pattern.compile("^.*/activityTracking/[a-zA-Z_0-9]*.*$");

    public static String HTTP_COCKROACH_STREAM = "http.cockroach";
    public static String HTTP_EXANDS_STREAM = "http.exands";

    private OutputCollector outputCollector;

    private HashFunction hashFunction;
    private Funnel<Tuple> funnel;

    transient Counter successCountMetric;
    transient Counter errorCountMetric;
    transient Counter duplicatedCountMetric;
    transient Histogram executionDurationHistogram;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);

        hashFunction = Hashing.murmur3_128();
        funnel = new TupleFunnel();

        outputCollector = collector;
        CollectorRegistry collectorRegistry = getCollectorRegistry();

        successCountMetric = Counter.build()
                .name("storm_logpipeline_rawlog_success_total")
                .help("RawLogProtocolSplitterBolt metric count")
                .labelNames("protocol")
                .register(collectorRegistry);

        errorCountMetric = Counter.build()
                .name("storm_logpipeline_rawlog_error_total")
                .help("RawLogProtocolSplitterBolt metric count")
                .register(collectorRegistry);

        duplicatedCountMetric = Counter.build()
                .name("storm_logpipeline_rawlog_dubs_total")
                .help("RawLogProtocolSplitterBolt CHECK_EM metric count")
                .register(collectorRegistry);

        executionDurationHistogram = Histogram.build()
                .name("storm_logpipeline_rawlog_execution_duration")
                .help("RawLogProtocolSplitterBolt metric count")
                .register(collectorRegistry);
    }

    @Override
    public void processTuple(Tuple input) {
        Histogram.Timer timer = executionDurationHistogram.startTimer();

        try {
            RedisClient client = RedisClient.create("redis://stats.local.vm:6379/0");;
            StatefulRedisConnection<String, String> connection = client.connect();
            RedisAsyncCommands<String, String> commands = connection.async();

            RedisFuture<Long> future = ((RedisHLLAsyncCommands) commands).pfadd(getTaskId(), getUniqueRawLogId(input));
            final Long currentlyAdded = future.get(1, TimeUnit.SECONDS);

            if ( currentlyAdded == 1l  ) {
                String type = input.getStringByField("type");
                if (type.startsWith("http") && cockroachUri.matcher(type).matches()) {
                    log.info(String.format("Found new raw log for cockroach..."));
                    outputCollector.emit(HTTP_COCKROACH_STREAM, input.getValues());

                    successCountMetric.labels("cockroach").inc();
                } else {
                    log.error(String.format("Unknown protocol [%s]", type));

                    successCountMetric.labels("unknown").inc();
                }
            } else {
                log.error(String.format("Found duplicated raw-log"));
                duplicatedCountMetric.inc();
            }

            connection.close();
            client.shutdown();
        } catch ( Exception ex ) {
            log.error(ex.getMessage(), ex);

            errorCountMetric.inc();
        } finally {
            timer.observeDuration();
            outputCollector.ack(input);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        super.declareOutputFields(declarer);

        declarer.declareStream(HTTP_COCKROACH_STREAM, new Fields("timestamp", "type", "data"));
        declarer.declareStream(HTTP_EXANDS_STREAM, new Fields("timestamp", "type", "data"));
    }

    private String getUniqueRawLogId(Tuple input) {
        HashCode hashCode = hashFunction.hashObject(input, funnel);

        StringBuilder build = new StringBuilder();
        build.append("storm-rawlog-");
        build.append(hashCode.asLong());

        return build.toString();
    }

    private static class TupleFunnel implements Funnel<Tuple> {

        @Override
        public void funnel(Tuple from, PrimitiveSink into) {
            into.putLong(from.getLongByField("timestamp"))
                    .putString(from.getStringByField("type"), Charsets.UTF_8)
                    .putString(from.getStringByField("data"), Charsets.UTF_8);
        }

    }

}
