package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.serdes.CockroachModelDeserializer;
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

public class CockroachModelParserBolt extends LogPipelineBaseBolt {

    private static final Logger log = LoggerFactory.getLogger(CockroachModelParserBolt.class);

    public static final String ID = "COCKROACH_MODEL_PARSER_BOLT_ID";

    private OutputCollector outputCollector;
    private CockroachModelDeserializer cockroachModelDeserialize;

    transient Counter successCountMetric;
    transient Counter errorCountMetric;
    transient Histogram executionDurationHistogram;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);

        outputCollector = collector;
        cockroachModelDeserialize = new CockroachModelDeserializer();
        CollectorRegistry collectorRegistry = getCollectorRegistry();

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
    }

    @Override
    public void processTuple(Tuple input) {
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
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        super.declareOutputFields(declarer);

        declarer.declare(new Fields("activity", "log", "startEvent", "timestamp"));
    }

}
