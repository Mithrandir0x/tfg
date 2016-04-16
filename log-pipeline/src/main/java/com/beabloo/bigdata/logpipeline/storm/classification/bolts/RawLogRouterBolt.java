package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.util.Map;

public class RawLogRouterBolt extends BaseRichBolt {

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
