package com.beabloo.bigdata.logpipeline.storm.classification.topologies;

import com.beabloo.bigdata.logpipeline.storm.classification.bolts.CockroachModelParserBolt;
import com.beabloo.bigdata.logpipeline.storm.classification.bolts.CockroachUnpackerBolt;
import com.beabloo.bigdata.logpipeline.storm.classification.bolts.RawLogProtocolSplitterBolt;
import com.beabloo.bigdata.logpipeline.storm.classification.bolts.hdfs.HdfsLogSelector;
import com.beabloo.bigdata.logpipeline.storm.classification.spouts.RawLogKafkaSpout;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.hdfs.bolt.HdfsBolt;
import org.apache.storm.hdfs.bolt.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;
import org.apache.storm.topology.TopologyBuilder;

import static com.beabloo.bigdata.logpipeline.storm.classification.bolts.RawLogProtocolSplitterBolt.HTTP_COCKROACH_STREAM;

public class LogClassificationTopology {


    private static final String TOPOLOGY_NAME = "rawlog_consumer_topology";

    public static void main(String[] args) {

        if ( args.length != 1 ) {
            System.out.println(String.format(" usage: THE FUCK YOU'RE TALKING ABOUT?"));
            return;
        }

        try {
            TopologyBuilder builder = new TopologyBuilder();
            Config config = new Config();

            RawLogKafkaSpout rawLogKafkaSpout = new RawLogKafkaSpout();
            builder.setSpout(RawLogKafkaSpout.ID, rawLogKafkaSpout);

            RawLogProtocolSplitterBolt rawLogProtocolSplitterBolt = new RawLogProtocolSplitterBolt();
            builder.setBolt(RawLogProtocolSplitterBolt.ID, rawLogProtocolSplitterBolt)
                    .shuffleGrouping(RawLogKafkaSpout.ID);

            CockroachUnpackerBolt cockroachUnpackerBolt = new CockroachUnpackerBolt();
            builder.setBolt(CockroachUnpackerBolt.ID, cockroachUnpackerBolt)
                    .shuffleGrouping(RawLogProtocolSplitterBolt.HTTP_COCKROACH_STREAM, RawLogProtocolSplitterBolt.ID);

            CockroachModelParserBolt cockroachModelParserBolt = new CockroachModelParserBolt();
            builder.setBolt(CockroachModelParserBolt.ID, cockroachModelParserBolt)
                    .shuffleGrouping(CockroachUnpackerBolt.ID);

            // sync the filesystem after every 1k tuples
            SyncPolicy syncPolicy = new CountSyncPolicy(5);

            // rotate files when they reach 5MB
            FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(5.0f, FileSizeRotationPolicy.Units.MB);

            FileNameFormat fileNameFormat = new DefaultFileNameFormat().withPath("/engine2_big_data/cockroach/");

            HdfsBolt bolt = new HdfsBolt()
                    .withFsUrl("hdfs://nn.local.vm:54310")
                    .withFileNameFormat(fileNameFormat)
                    .withRecordFormat(HdfsLogSelector.getHdfsLogFormat())
                    .withPartitioner(HdfsLogSelector.getHdfsLogPartitioner())
                    .withSyncPolicy(syncPolicy);

            StormSubmitter.submitTopology(TOPOLOGY_NAME, config, builder.createTopology());
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

}
