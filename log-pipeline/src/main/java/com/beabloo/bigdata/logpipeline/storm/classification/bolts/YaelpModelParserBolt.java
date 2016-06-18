package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.yaelp.model.YaelpLog;
import com.beabloo.bigdata.yaelp.serdes.YaelpModelDeserializer;
import com.beabloo.bigdata.model.WifiPresenceLog;
import com.google.common.base.Charsets;
import com.google.common.hash.*;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.codec.Utf8StringCodec;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class YaelpModelParserBolt extends LogPipelineBaseBolt {

    private static final Logger log = LoggerFactory.getLogger(YaelpModelParserBolt.class);

    public static final String ID = "COCKROACH_MODEL_PARSER_BOLT_ID";

    private OutputCollector outputCollector;
    private YaelpModelDeserializer cockroachModelDeserialize;

    protected transient RedisClient redisClient;
    protected transient StatefulRedisConnection<String, String> redisConnection;
    protected transient HashFunction hashFunction;
    protected transient Funnel<YaelpLog> funnel;

    transient Counter successCountMetric;
    transient Counter errorCountMetric;
    transient Histogram executionDurationHistogram;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);

        redisClient = RedisClient.create("redis://stats.local.vm:6379/0");
        redisConnection = redisClient.connect(new Utf8StringCodec());
        hashFunction = Hashing.murmur3_128();
        funnel = new CockroachLogFunnel();

        outputCollector = collector;
        cockroachModelDeserialize = new YaelpModelDeserializer();
        CollectorRegistry collectorRegistry = getCollectorRegistry();

        successCountMetric = Counter.build()
                .name("storm_logpipeline_modelparser_success_total")
                .help("YaelpModelParserBolt metric count")
                .labelNames("environment")
                .register(collectorRegistry);

        errorCountMetric = Counter.build()
                .name("storm_logpipeline_modelparser_error_total")
                .help("YaelpModelParserBolt metric count")
                .labelNames("type")
                .register(collectorRegistry);

        executionDurationHistogram = Histogram.build()
                .name("storm_logpipeline_modelparser_execution_duration")
                .help("YaelpModelParserBolt metric count")
                .register(collectorRegistry);
    }

    @Override
    public void processTuple(Tuple input) {
        Histogram.Timer timer = executionDurationHistogram.startTimer();

        try {
            String platform = input.getStringByField("environment");
            String paramsValues = input.getStringByField("paramsValues");
            String extraParams = input.getStringByField("extraParams");

            log.info(String.format("Received new trigger from environment [%s] paramsValues [%s] extraParams [%s]", platform, paramsValues, extraParams));

            YaelpLog yaelpLog = cockroachModelDeserialize.deserialize(platform, paramsValues, extraParams);

            if ( yaelpLog != null ) {
                String key = getNamespaceKey(yaelpLog);
                String uuid = getUniqueCockroachLogId(yaelpLog);

                RedisAsyncCommands<String, String> commands = redisConnection.async();
                RedisFuture<Boolean> future = commands.hsetnx(key, uuid, "1");

                future.thenAccept(currentlyAdded -> {
                    log.info(String.format("Added yaelp-log hash [%s@%s] to redis. currentlyAdded [%s]", key, uuid, currentlyAdded));

                    if ( currentlyAdded ) {
                        log.info(String.format("Emitting value yaelpLog [%s]", yaelpLog));
                        outputCollector.emit(new Values(
                                yaelpLog.getActivityDefinition().name(),
                                yaelpLog,
                                yaelpLog.getStartEvent(),
                                input.getLongByField("timestamp")));

                        successCountMetric.labels(platform).inc();
                    } else  {
                        log.error(String.format("Found duplicated raw-log [%s]", uuid));
                        errorCountMetric.labels("dubs").inc();
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
            } else {
                log.error("Invalid yaelp log received");
                errorCountMetric.labels("badformat").inc();
            }
        } catch ( Exception ex ) {
            // @TODO Treat Exception nicely
            ex.printStackTrace();
            errorCountMetric.labels("exception").inc();

            timer.observeDuration();
            outputCollector.ack(input);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        super.declareOutputFields(declarer);

        declarer.declare(new Fields("activity", "log", "startEvent", "timestamp"));
    }

    byte[] getByteArrayCockroachLogId(YaelpLog input) {
        HashCode hashCode = hashFunction.hashObject(input, funnel);
        return hashCode.asBytes();
    }

    String getUniqueCockroachLogId(YaelpLog input) {
        HashCode hashCode = hashFunction.hashObject(input, funnel);
        return hashCode.toString();
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(input.getOrganization());
//        stringBuilder.append(input.getStartEvent());
//
//        if ( input instanceof WifiPresenceLog ) {
//            WifiPresenceLog wifi = (WifiPresenceLog) input;
//            stringBuilder.append(wifi.getHotspot());
//            stringBuilder.append(wifi.getSensor());
//            stringBuilder.append(wifi.getDevice());
//        }
//
//        return stringBuilder.toString();
    }

    HashCode getCockroachLogHashCode(YaelpLog input) {
        return hashFunction.hashObject(input, funnel);
    }

    String getNamespaceKey(YaelpLog input) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(input.getStartEvent() * 1000);
        return String.format("st:lp:mp:%02d%02d%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private static class CockroachLogFunnel implements Funnel<YaelpLog> {

        @Override
        public void funnel(YaelpLog from, PrimitiveSink into) {
            into.putLong(from.getOrganization())
                    .putLong(from.getStartEvent());

            if ( from instanceof WifiPresenceLog ) {
                // log.info("Processing wifi-presence-log through funnel");

                WifiPresenceLog log = (WifiPresenceLog) from;
                into.putLong(log.getHotspot());
                into.putLong(log.getSensor());
                into.putString(log.getDevice(), Charsets.UTF_8);
            }
        }

    }

}
