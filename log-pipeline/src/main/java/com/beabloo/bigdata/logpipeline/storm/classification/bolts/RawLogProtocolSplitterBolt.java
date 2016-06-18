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
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Map;
import java.util.regex.Pattern;

public class RawLogProtocolSplitterBolt extends LogPipelineBaseBolt {

    private static final Logger log = LoggerFactory.getLogger(RawLogProtocolSplitterBolt.class);

    public static final String ID = "RAWLOG_PROTOCOL_SPLITTER_BOLT_ID";

    private static Pattern cockroachUri = Pattern.compile("^.*/activityTracking/[a-zA-Z_0-9]*.*$");

    public static String HTTP_COCKROACH_STREAM = "http.yaelp";
    public static String HTTP_EXANDS_STREAM = "http.exands";

    private OutputCollector outputCollector;

    protected transient RedisClient redisClient;
    protected transient StatefulRedisConnection<String, String> redisConnection;
    protected transient HashFunction hashFunction;
    protected transient Funnel<Tuple> funnel;

    private transient Gauge gaugeTest;
    private transient Counter successCountMetric;
    private transient Counter errorCountMetric;
    private transient Counter duplicatedCountMetric;
    private transient Histogram executionDurationHistogram;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);

        redisClient = RedisClient.create("redis://stats.local.vm:6379/0");
        redisConnection = redisClient.connect();
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
        String uuid = getUniqueRawLogId(input);

        Histogram.Timer timer = executionDurationHistogram.startTimer();

        try {
            RedisAsyncCommands<String, String> commands = redisConnection.async();
            RedisFuture<Boolean> future = commands.hsetnx("st:lp:rl", uuid, "1");

            future.thenAccept(currentlyAdded -> {
                log.info(String.format("Added raw-log hash [%s] to redis. currentlyAdded [%s]", uuid, currentlyAdded));

                if ( currentlyAdded ) {
                    String type = input.getStringByField("type");
                    if ( type.startsWith("http") && cockroachUri.matcher(type).matches() ) {
                        log.info(String.format("Found new raw log for yaelp..."));
                        outputCollector.emit(HTTP_COCKROACH_STREAM, input.getValues());

                        successCountMetric.labels("yaelp").inc();
                    } else {
                        log.error(String.format("Unknown protocol [%s]", type));

                        successCountMetric.labels("unknown").inc();
                    }
                } else {
                    log.error(String.format("Found duplicated raw-log [%s]", uuid));
                    duplicatedCountMetric.inc();
                }

                timer.observeDuration();
                outputCollector.ack(input);
            });

            future.exceptionally(throwable -> {
                if ( throwable != null ) {
                    log.error(throwable.getMessage(), throwable);
                }
                return false;
            });
        } catch ( Exception ex ) {
            log.error(ex.getMessage(), ex);

            errorCountMetric.inc();

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

    @Override
    public void cleanup() {
        redisConnection.close();
        redisClient.shutdown();
    }

    String getUniqueRawLogId(Tuple input) {
        HashCode hashCode = hashFunction.hashObject(input, funnel);
        return hashCode.toString();
        //return String.format("%s_%s", input.getStringByField("type"), input.getStringByField("data"));
    }

    private String getNamespaceKey(Tuple input) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(input.getLongByField("timestamp"));
        return String.format("st:lp:mp:%02d%02d%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private static class TupleFunnel implements Funnel<Tuple> {

        @Override
        public void funnel(Tuple from, PrimitiveSink into) {
            into.putString(from.getStringByField("type"), Charsets.UTF_8)
                    .putString(from.getStringByField("data"), Charsets.UTF_8);
        }

    }

}
