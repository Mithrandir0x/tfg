package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.model.WifiPresenceLog;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class CockroachModelParserBoltTest {

    @Test
    public void hashDistributionTest() throws Exception {
        /* WifiPresenceLog log = new WifiPresenceLog();

        CockroachModelParserBolt bolt = new CockroachModelParserBolt();

        Map<String, List<Long>> hashMap = new HashMap<>();

        Map conf = mock(Map.class);
        TopologyContext context = mock(TopologyContext.class);
        OutputCollector collector = mock(OutputCollector.class);

        bolt.prepare(conf, context, collector);

        // String[][] dataset = CockroachLogSamples.getDataSet("../datasets/unique_devices_1M.csv", 1000000);
        String[][] dataset = new String[][] {};

        for ( int i = 0, j = 0 ; i < dataset.length ; i++ ) {
            String[] data = dataset[i];
            log.setOrganization(Long.parseLong(data[0]));
            log.setStartEvent(Long.parseLong(data[1]));
            log.setHotspot(Long.parseLong(data[2]));
            log.setSensor(Long.parseLong(data[3]));
            log.setDevice(data[4]);

            String uuid = bolt.getUniqueCockroachLogId(log);

            List<Long> lines;
            if ( hashMap.containsKey(uuid) ) {
                System.out.println(String.format("Creating new list. i [%s] j [%s]", i, j));
                j++;
                lines = hashMap.get(uuid);
            } else {
                lines = new ArrayList<>();
                hashMap.put(uuid, lines);
            }

            lines.add(new Long(i));
        }

        System.out.println(String.format("hashMap.size [%s]", hashMap.size()));

        List<String> lines = new ArrayList<>();
        for ( Map.Entry<String, List<Long>> element :  hashMap.entrySet() ) {
            String hash = element.getKey();
            List<Long> lineList = element.getValue();
            if ( lineList.size() > 1 ) {
                System.out.println(String.format("hash [%s] total [%s]", hash, lineList.size()));
            }
            lines.add("pfadd STORM-PIPELINE-COCKROACH-MODEL-PARSER " + hash);
        }

        Files.write(Paths.get("../datasets/unique_devices_1M.hashes.log"), lines); */
    }

}
