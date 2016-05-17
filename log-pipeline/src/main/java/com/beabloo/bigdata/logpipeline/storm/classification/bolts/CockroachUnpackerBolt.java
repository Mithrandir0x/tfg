package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.cockroach.model.CockroachEventHttpRequestContainer;
import com.beabloo.bigdata.cockroach.model.ParamsContainer;
import com.beabloo.bigdata.cockroach.serdes.ParamsContainerFastDeserializer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.PushGateway;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CockroachUnpackerBolt extends BaseRichBolt {

    private static final Logger log = LoggerFactory.getLogger(CockroachUnpackerBolt.class);

    public static final String ID = "COCKROACH_UNPACKER_BOLT_ID";

    private static final Pattern platformPattern = Pattern.compile("^.*/activityTracking/([a-zA-Z_0-9]+).*$");
    private static final Pattern jsonParamPattern = Pattern.compile("^\\??json=(.*)$");

    private OutputCollector outputCollector;
    private ObjectMapper objectMapper;

    transient PushGateway pushGateway;
    transient CollectorRegistry collectorRegistry;
    transient String taskId;

    transient Counter successCountMetric;
    transient Counter errorCountMetric;
    transient Histogram executionDurationHistogram;
    transient Gauge currentUnpackedLogsMetric;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;

        taskId = String.format("%s_%s_%s", context.getThisComponentId(), "" + context.getThisTaskId(), context.getThisWorkerPort());

        // @TODO Metric collection should be wrapped
        pushGateway = new PushGateway("stats.local.vm:9091");
        collectorRegistry = new CollectorRegistry();

        successCountMetric = Counter.build()
                .name("storm_logpipeline_unpacker_success_total")
                .help("CockroachUnpackerBolt metric count")
                .labelNames("platform")
                .register(collectorRegistry);

        errorCountMetric = Counter.build()
                .name("storm_logpipeline_unpacker_error_total")
                .help("CockroachUnpackerBolt metric count")
                .labelNames("type")
                .register(collectorRegistry);

        executionDurationHistogram = Histogram.build()
                .name("storm_logpipeline_unpacker_execution_duration")
                .help("CockroachUnpackerBolt metric count")
                .register(collectorRegistry);

        currentUnpackedLogsMetric = Gauge.build()
                .name("storm_logpipeline_unpacker_current_logs_count")
                .help("CockroachUnpackerBolt metric count")
                .labelNames("platform")
                .register(collectorRegistry);

        // @TODO Wrap this into a class
        objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("UNK", Version.unknownVersion());
        simpleModule.addDeserializer(ParamsContainer.class, new ParamsContainerFastDeserializer());
        objectMapper.registerModule(simpleModule);
    }

    @Override
    public void execute(Tuple input) {
        Histogram.Timer timer = executionDurationHistogram.startTimer();

        try {
            String json = getJson(input.getStringByField("data"));
            String platform = getPlatform(input.getStringByField("type"));
            if ( json != null && platform != null ) {
                log.debug(String.format("Received new raw log. json [%s]", json));

                CockroachEventHttpRequestContainer container = objectMapper.readValue(URLDecoder.decode(json, "UTF-8"), CockroachEventHttpRequestContainer.class);
                int containerSize = container.getEvents().size();

                log.info(String.format("Unpacked [%s] cockroach events", containerSize));
                currentUnpackedLogsMetric.labels(platform).set(containerSize);

                for ( ParamsContainer paramsContainer : container.getEvents() ) {
                    log.debug(String.format("Emiting new event log [%s]", paramsContainer));
                    outputCollector.emit(new Values(input.getValueByField("timestamp"),
                            platform,
                            paramsContainer.getParamsValues(),
                            paramsContainer.getExtraParams()));

                    successCountMetric.labels(platform).inc();
                }
            } else {
                // Notify problem to another stream
                log.error(String.format("Badly formatted data. platform [%s] json [%s]", platform, json));
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

        try {
            Map<String, String> groupingKey = new HashMap<>();
            groupingKey.put("instance", taskId);
            pushGateway.push(collectorRegistry, "storm_logpipeline", groupingKey);
        } catch ( Exception ex ) {
            log.error("Error while trying to send metrics to push gateway", ex);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("timestamp", "platform", "paramsValues", "extraParams"));
    }

    String getJson(String rawData) {
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

    String getPlatform(String url) {
        String platform = null;

        Matcher matcher = platformPattern.matcher(url);

        if ( matcher.matches() ) {
            platform = matcher.group(1);
        }

        return platform;
    }

}
