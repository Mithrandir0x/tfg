package edu.ub.bigdata.logpipeline.storm.classification.topologies;

import edu.ub.bigdata.logpipeline.storm.classification.bolts.YaelpModelParserBolt;
import edu.ub.bigdata.logpipeline.storm.classification.bolts.YaelpUnpackerBolt;
import edu.ub.bigdata.logpipeline.storm.classification.bolts.LogHdfsBolt;
import edu.ub.bigdata.logpipeline.storm.classification.bolts.RawLogProtocolSplitterBolt;
import edu.ub.bigdata.logpipeline.storm.classification.spouts.RawLogKafkaSpout;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;

public class LogClassificationTopology {

    public static void main(String[] args) {

        if ( args.length != 1 ) {
            System.out.println(String.format(" usage: To be disclosed"));
            return;
        }

        try {
            TopologyBuilder builder = new TopologyBuilder();
            Config config = new Config();

            RawLogKafkaSpout rawLogKafkaSpout = new RawLogKafkaSpout();
            builder.setSpout(RawLogKafkaSpout.ID, rawLogKafkaSpout, 4);

            RawLogProtocolSplitterBolt rawLogProtocolSplitterBolt = new RawLogProtocolSplitterBolt();
            builder.setBolt(RawLogProtocolSplitterBolt.ID, rawLogProtocolSplitterBolt)
                    .shuffleGrouping(RawLogKafkaSpout.ID);

            YaelpUnpackerBolt yaelpUnpackerBolt = new YaelpUnpackerBolt();
            builder.setBolt(YaelpUnpackerBolt.ID, yaelpUnpackerBolt)
                    .shuffleGrouping(RawLogProtocolSplitterBolt.ID, RawLogProtocolSplitterBolt.HTTP_COCKROACH_STREAM);

            YaelpModelParserBolt yaelpModelParserBolt = new YaelpModelParserBolt();
            builder.setBolt(YaelpModelParserBolt.ID, yaelpModelParserBolt)
                    .shuffleGrouping(YaelpUnpackerBolt.ID);

            LogHdfsBolt hdfsBolt = new LogHdfsBolt("/engine2_big_data/yaelp/");
            builder.setBolt("YAELP_HDFS_BOLT_ID", hdfsBolt)
                    .shuffleGrouping(YaelpModelParserBolt.ID);

            config.setNumWorkers(4);

            StormSubmitter.submitTopology(args[0], config, builder.createTopology());
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

}
