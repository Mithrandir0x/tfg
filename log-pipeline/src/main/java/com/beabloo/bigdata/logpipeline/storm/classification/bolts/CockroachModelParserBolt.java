package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.serdes.CockroachModelDeserializer;
import com.beabloo.bigdata.kryo.serdes.KryoSerDe;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CockroachModelParserBolt extends BaseRichBolt {

    public static final String ID = "COCKROACH_MODEL_PARSER_BOLT_ID";

    private OutputCollector outputCollector;
    private CockroachModelDeserializer cockroachModelDeserialize;
    private KryoSerDe kryo;

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
                Logger.getLogger(CockroachModelParserBolt.class.getName()).log(Level.INFO, String.format("Emitting value cockroachLog [%s]", cockroachLog));
                outputCollector.emit(new Values(
                        cockroachLog.getActivityDefinition().name(),
                        cockroachLog,
                        cockroachLog.getStartEvent(),
                        input.getLongByField("timestamp")));
            } else {
                Logger.getLogger(CockroachModelParserBolt.class.getName()).log(Level.SEVERE, "Invalid cockroach log received");
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
