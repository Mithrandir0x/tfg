package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import org.apache.storm.metric.api.CountMetric;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Pattern;

public class RawLogProtocolSplitterBolt extends BaseRichBolt {

    private static final Logger log = LoggerFactory.getLogger(RawLogProtocolSplitterBolt.class);

    public static final String ID = "RAWLOG_PROTOCOL_SPLITTER_BOLT_ID";

    private static Pattern cockroachUri = Pattern.compile("^.*/activityTracking/[a-zA-Z_0-9]*.*$");

    public static String HTTP_COCKROACH_STREAM = "http.cockroach";
    public static String HTTP_EXANDS_STREAM = "http.exands";

    private OutputCollector outputCollector;

    // @TODO This metric should be done for each protocol
    transient CountMetric successCountMetric;
    transient CountMetric errorCountMetric;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;

        successCountMetric = new CountMetric();
        context.registerMetric("rawlog_success", successCountMetric, 1);

        errorCountMetric = new CountMetric();
        context.registerMetric("rawlog_error", successCountMetric, 1);
    }

    @Override
    public void execute(Tuple input) {
        String type = input.getStringByField("type");
        if ( type.startsWith("http") && cockroachUri.matcher(type).matches() ) {
            log.info(String.format("Found new raw log for cockroach..."));
            outputCollector.emit(HTTP_COCKROACH_STREAM, input.getValues());

            successCountMetric.incr();
        } else {
            log.error(String.format("Unknown protocol [%s]", type));

            errorCountMetric.incr();
        }
        outputCollector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream(HTTP_COCKROACH_STREAM, new Fields("timestamp", "type", "data"));
        declarer.declareStream(HTTP_EXANDS_STREAM, new Fields("timestamp", "type", "data"));
    }

}
