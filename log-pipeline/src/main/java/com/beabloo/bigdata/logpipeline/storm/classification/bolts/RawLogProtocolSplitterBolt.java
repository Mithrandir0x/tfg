package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.monitoring.storm.aspects.StormBoltMetricsGenerator;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.PushGateway;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@StormBoltMetricsGenerator
public class RawLogProtocolSplitterBolt extends BaseRichBolt {

    private static final Logger log = LoggerFactory.getLogger(RawLogProtocolSplitterBolt.class);

    public static final String ID = "RAWLOG_PROTOCOL_SPLITTER_BOLT_ID";

    private static Pattern cockroachUri = Pattern.compile("^.*/activityTracking/[a-zA-Z_0-9]*.*$");

    public static String HTTP_COCKROACH_STREAM = "http.cockroach";
    public static String HTTP_EXANDS_STREAM = "http.exands";

    private OutputCollector outputCollector;

    transient PushGateway pushGateway;
    transient CollectorRegistry collectorRegistry;
    transient String taskId;

    transient Counter successCountMetric;
    transient Counter errorCountMetric;
    transient Histogram executionDurationHistogram;

    // @TODO This metric should be done for each protocol
//     transient CountMetric successCountMetric;
//     transient CountMetric errorCountMetric;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;

        taskId = "" + context.getThisTaskId();

        pushGateway = new PushGateway("stats.local.vm:9091");
        collectorRegistry = new CollectorRegistry();

        successCountMetric = Counter.build()
                .name("rawlog_success")
                .help("RawLogProtocolSplitterBolt metric count")
                .labelNames("protocol")
                .register(collectorRegistry);

        errorCountMetric = Counter.build()
                .name("rawlog_error")
                .help("RawLogProtocolSplitterBolt metric count")
                .register(collectorRegistry);

        executionDurationHistogram = Histogram.build()
                .name("execution_duration")
                .help("RawLogProtocolSplitterBolt metric count")
                .register(collectorRegistry);

//         successCountMetric = new CountMetric();
//         context.registerMetric("rawlog_success", successCountMetric, 1);
//
//         errorCountMetric = new CountMetric();
//         context.registerMetric("rawlog_error", successCountMetric, 1);
    }

    @Override
    public void execute(Tuple input) {
        Histogram.Timer timer = executionDurationHistogram.startTimer();

        try {
            String type = input.getStringByField("type");
            if (type.startsWith("http") && cockroachUri.matcher(type).matches()) {
                log.info(String.format("Found new raw log for cockroach..."));
                outputCollector.emit(HTTP_COCKROACH_STREAM, input.getValues());

                successCountMetric.labels("cockroach").inc();
            } else {
                log.error(String.format("Unknown protocol [%s]", type));

                successCountMetric.labels("unknown").inc();
            }
        } catch ( Exception ex ) {
            log.error(ex.getMessage(), ex);

            errorCountMetric.inc();
        } finally {
            timer.observeDuration();

            outputCollector.ack(input);
        }

        try {
            Map<String, String> groupingKey = new HashMap<>();
            groupingKey.put("instance", taskId);
            pushGateway.pushAdd(collectorRegistry, "storm_logpipeline", groupingKey);
        } catch ( Exception ex ) {
            log.error("Error while trying to send metrics to push gateway", ex);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream(HTTP_COCKROACH_STREAM, new Fields("timestamp", "type", "data"));
        declarer.declareStream(HTTP_EXANDS_STREAM, new Fields("timestamp", "type", "data"));
    }

}
