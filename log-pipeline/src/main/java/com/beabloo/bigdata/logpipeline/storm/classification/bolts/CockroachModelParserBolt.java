package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.serdes.CockroachModelDeserializer;
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

    transient MultiCountMetric platformSuccessMetric;
    transient CountMetric badFormattedJsonErrorMetric;
    transient CountMetric exceptionErrorMetric;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;
        cockroachModelDeserialize = new CockroachModelDeserializer();

        platformSuccessMetric = new MultiCountMetric();
        context.registerMetric("CockroachModelParserBolt.success", platformSuccessMetric, 1);

        badFormattedJsonErrorMetric = new CountMetric();
        context.registerMetric("CockroachModelParserBolt.error.badjson", badFormattedJsonErrorMetric, 1);

        exceptionErrorMetric = new CountMetric();
        context.registerMetric("CockroachModelParserBolt.error.exception", exceptionErrorMetric, 1);
    }

    @Override
    public void execute(Tuple input) {
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

                platformSuccessMetric.scope(platform).incr();
            } else {
                log.error("Invalid cockroach log received");

                badFormattedJsonErrorMetric.incr();
            }
        } catch ( Exception ex ) {
            // @TODO Treat Exception nicely
            ex.printStackTrace();

            exceptionErrorMetric.incr();
        }

        outputCollector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("activity", "log", "startEvent", "recordCreationTime"));
    }

}
