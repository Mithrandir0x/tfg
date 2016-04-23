package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.Map;
import java.util.regex.Pattern;

public class RawLogProtocolSplitterBolt extends BaseRichBolt {

    public static final String ID = "RAWLOG_PROTOCOL_SPLITTER_BOLT_ID";

    private static Pattern cockroachUri = Pattern.compile("^.*/activityTracking/[a-zA-Z_0-9]*.*$");

    public static String HTTP_COCKROACH_STREAM = "http.cockroach";
    public static String HTTP_EXANDS_STREAM = "http.exands";

    private OutputCollector outputCollector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;
    }

    @Override
    public void execute(Tuple input) {
        String type = input.getStringByField("type");
        if ( type.startsWith("http") && cockroachUri.matcher(type).matches() ) {
            outputCollector.emit(HTTP_COCKROACH_STREAM, input.getValues());
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream(HTTP_COCKROACH_STREAM, new Fields("timestamp", "type", "data"));
        declarer.declareStream(HTTP_EXANDS_STREAM, new Fields("timestamp", "type", "data"));
    }

}
