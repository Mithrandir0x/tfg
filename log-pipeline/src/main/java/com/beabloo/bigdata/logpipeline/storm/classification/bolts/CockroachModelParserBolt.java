package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.serdes.CockroachModelDeserializer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.PushGateway;
import org.apache.storm.metric.api.CountMetric;
import org.apache.storm.metric.api.MultiCountMetric;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CockroachModelParserBolt extends BaseRichBolt {

    private static final Logger log = LoggerFactory.getLogger(CockroachModelParserBolt.class);

    public static final String ID = "COCKROACH_MODEL_PARSER_BOLT_ID";

    private OutputCollector outputCollector;
    private CockroachModelDeserializer cockroachModelDeserialize;

    transient PushGateway pushGateway;
    transient CollectorRegistry collectorRegistry;
    transient String taskId;

    transient Counter successCountMetric;
    transient Counter errorCountMetric;
    transient Histogram executionDurationHistogram;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;
        cockroachModelDeserialize = new CockroachModelDeserializer();

        taskId = String.format("%s_%s_%s", context.getThisComponentId(), "" + context.getThisTaskId(), context.getThisWorkerPort());

        pushGateway = new PushGateway("stats.local.vm:9091");
        collectorRegistry = new CollectorRegistry();

        successCountMetric = Counter.build()
                .name("storm_logpipeline_modelparser_success_total")
                .help("CockroachModelParserBolt metric count")
                .labelNames("platform")
                .register(collectorRegistry);

        errorCountMetric = Counter.build()
                .name("storm_logpipeline_modelparser_error_total")
                .help("CockroachModelParserBolt metric count")
                .labelNames("type")
                .register(collectorRegistry);

        executionDurationHistogram = Histogram.build()
                .name("storm_logpipeline_modelparser_execution_duration")
                .help("CockroachModelParserBolt metric count")
                .register(collectorRegistry);

//        platformSuccessMetric = new MultiCountMetric();
//        context.registerMetric("parser_success", platformSuccessMetric, 1);
//
//        badFormattedJsonErrorMetric = new CountMetric();
//        context.registerMetric("parser_error_badjson", badFormattedJsonErrorMetric, 1);
//
//        exceptionErrorMetric = new CountMetric();
//        context.registerMetric("parser_error_exception", exceptionErrorMetric, 1);
    }

    @Override
    public void execute(Tuple input) {
        Histogram.Timer timer = executionDurationHistogram.startTimer();

        try {
            String platform = input.getStringByField("platform");
            String paramsValues = input.getStringByField("paramsValues");
            String extraParams = input.getStringByField("extraParams");

            log.info(String.format("Received new event from platform [%s] paramsValues [%s] extraParams [%s]", platform, paramsValues, extraParams));

            CockroachLog cockroachLog = cockroachModelDeserialize.deserialize(platform, paramsValues, extraParams);
            if ( cockroachLog != null ) {
                log.info(String.format("Emitting value cockroachLog [%s]", cockroachLog));
                outputCollector.emit(new Values(
                        cockroachLog.getActivityDefinition().name(),
                        cockroachLog,
                        cockroachLog.getStartEvent(),
                        input.getLongByField("timestamp")));
                
                successCountMetric.labels(platform).inc();
            } else {
                log.error("Invalid cockroach log received");
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
        declarer.declare(new Fields("activity", "log", "startEvent", "recordCreationTime"));
    }

}
