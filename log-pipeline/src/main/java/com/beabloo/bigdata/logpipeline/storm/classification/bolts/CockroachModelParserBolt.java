package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.serdes.CockroachModelDeserializer;
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

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;
        cockroachModelDeserialize = new CockroachModelDeserializer();
    }

    @Override
    public void execute(Tuple input) {
        try {
            String platform = input.getStringByField("platform");
            CockroachLog cockroachLog = cockroachModelDeserialize.deserialize(platform,
                    input.getStringByField("paramsValues"),
                    input.getStringByField("extraParams"));
            if ( cockroachLog != null ) {
                log.debug(String.format("Emitting value cockroachLog [%s]", cockroachLog));
                outputCollector.emit(new Values(
                        cockroachLog.getActivityDefinition().name(),
                        cockroachLog,
                        cockroachLog.getStartEvent(),
                        input.getLongByField("timestamp")));
            } else {
                log.error("Invalid cockroach log received");
            }
        } catch ( Exception ex ) {
            // @TODO Treat Excepcion
            ex.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("activity", "log", "startEvent", "recordCreationTime"));
    }

}
