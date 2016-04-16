package com.beabloo.bigdata.logpipeline.storm.classification.spouts;

import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;

public class RawLogKafkaSpout extends KafkaSpout {

    public RawLogKafkaSpout() {
        super(new SpoutConfig(null, null, null, null));
    }

}
