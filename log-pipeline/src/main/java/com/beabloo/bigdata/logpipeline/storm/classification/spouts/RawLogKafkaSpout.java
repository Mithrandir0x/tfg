package com.beabloo.bigdata.logpipeline.storm.classification.spouts;

import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;

public class RawLogKafkaSpout extends KafkaSpout {

    public RawLogKafkaSpout() {
        super(new SpoutConfig(null, null, null, null));
    }

}
