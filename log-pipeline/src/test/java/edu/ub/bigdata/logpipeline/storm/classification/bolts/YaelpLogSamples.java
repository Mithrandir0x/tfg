package edu.ub.bigdata.logpipeline.storm.classification.bolts;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class YaelpLogSamples {

    public static String[][] getDataSet(String path, int sampleSize) {
        try {
            Stream<String> stream = Files.lines(Paths.get(path));
            final int[] i = {0};
            String[][] dataset = new String[sampleSize][];
            stream.forEach(line -> {
                String[] log = line.split(",");
                if ( log.length > 0  ) {
                    dataset[i[0]] = log;
                }
                i[0]++;
            });
            return dataset;
        } catch ( Exception ex ) {
            ex.printStackTrace();

            return new String[][] {  };
        }
    }

}
