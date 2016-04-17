package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

public class CockroachDataRouterBolt extends BaseRichBolt {

    private OutputCollector outputCollector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;
    }

    @Override
    public void execute(Tuple input) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

}
