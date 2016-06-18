package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.yaelp.model.YaelpEventContainer;
import com.beabloo.bigdata.yaelp.model.YaelpRawEvent;
import com.beabloo.bigdata.yaelp.serdes.YaelpRawEventFastDeserializer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YaelpUnpackerBolt extends LogPipelineBaseBolt {

    private static final Logger log = LoggerFactory.getLogger(YaelpUnpackerBolt.class);

    public static final String ID = "YAELP_UNPACKER_BOLT_ID";

    private static final Pattern platformPattern = Pattern.compile("^.*/activityTracking/([a-zA-Z_0-9]+).*$");
    private static final Pattern jsonParamPattern = Pattern.compile("^\\??json=(.*)$");

    private OutputCollector outputCollector;
    private ObjectMapper objectMapper;

    transient Counter successCountMetric;
    transient Counter errorCountMetric;
    transient Histogram executionDurationHistogram;
    transient Gauge currentUnpackedLogsMetric;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);

        outputCollector = collector;
        CollectorRegistry collectorRegistry = getCollectorRegistry();

        successCountMetric = Counter.build()
                .name("storm_logpipeline_unpacker_success_total")
                .help("YaelpUnpackerBolt metric count")
                .labelNames("environment")
                .register(collectorRegistry);

        errorCountMetric = Counter.build()
                .name("storm_logpipeline_unpacker_error_total")
                .help("YaelpUnpackerBolt metric count")
                .labelNames("type")
                .register(collectorRegistry);

        executionDurationHistogram = Histogram.build()
                .name("storm_logpipeline_unpacker_execution_duration")
                .help("YaelpUnpackerBolt metric count")
                .register(collectorRegistry);

        currentUnpackedLogsMetric = Gauge.build()
                .name("storm_logpipeline_unpacker_current_logs_count")
                .help("YaelpUnpackerBolt metric count")
                .labelNames("environment")
                .register(collectorRegistry);

        // @TODO Wrap this into a class
        objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("UNK", Version.unknownVersion());
        simpleModule.addDeserializer(YaelpRawEvent.class, new YaelpRawEventFastDeserializer());
        objectMapper.registerModule(simpleModule);
    }

    @Override
    public void processTuple(Tuple input) {
        Histogram.Timer timer = executionDurationHistogram.startTimer();

        try {
            String json = getJson(input.getStringByField("data"));
            String platform = getPlatform(input.getStringByField("type"));
            if ( json != null && platform != null ) {
                log.debug(String.format("Received new raw log. json [%s]", json));

                YaelpEventContainer container = objectMapper.readValue(URLDecoder.decode(json, "UTF-8"), YaelpEventContainer.class);
                int containerSize = container.getEvents().size();

                log.info(String.format("Unpacked [%s] yaelp events", containerSize));
                currentUnpackedLogsMetric.labels(platform).set(containerSize);

                for ( YaelpRawEvent yaelpRawEvent : container.getEvents() ) {
                    log.debug(String.format("Emiting new trigger log [%s]", yaelpRawEvent));
                    outputCollector.emit(new Values(input.getValueByField("timestamp"),
                            platform,
                            yaelpRawEvent.getData(),
                            yaelpRawEvent.getMeta()));

                    successCountMetric.labels(platform).inc();
                }
            } else {
                // Notify problem to another stream
                log.error(String.format("Badly formatted data. environment [%s] json [%s]", platform, json));
                errorCountMetric.labels("badformat").inc();
            }
        } catch ( Exception ex ) {
            // @TODO Treat Exception nicely
            ex.printStackTrace();
            errorCountMetric.labels("exception").inc();
        } finally {
            timer.observeDuration();
            outputCollector.ack(input);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        super.declareOutputFields(declarer);

        declarer.declare(new Fields("timestamp", "environment", "paramsValues", "extraParams"));
    }

    protected String getJson(String rawData) {
        String json = null;

        String token;
        StringTokenizer paramTokenizer = new StringTokenizer(rawData, "&");

        while ( paramTokenizer.hasMoreTokens() ) {
            token = paramTokenizer.nextToken();
            Matcher matcher = jsonParamPattern.matcher(token);
            if ( matcher.matches() ) {
                json = matcher.group(1);
                break;
            }
        }

        return json;
    }

    protected String getPlatform(String url) {
        String platform = null;

        Matcher matcher = platformPattern.matcher(url);

        if ( matcher.matches() ) {
            platform = matcher.group(1);
        }

        return platform;
    }

}
