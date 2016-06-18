package edu.ub.bigdata.logpipeline.storm.classification.bolts;

import edu.ub.bigdata.model.WifiPresenceLog;
import com.google.common.hash.HashCode;
import com.google.common.io.BaseEncoding;
import com.google.common.math.BigIntegerMath;
import com.lambdaworks.redis.RedisFuture;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;
import com.lambdaworks.redis.api.async.RedisHLLAsyncCommands;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.junit.Ignore;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

public class YaelpModelParserBoltTest {

    @Ignore
    public void verifyHyperLogLogTest() throws Exception {
        final BlockingQueue<String[]> queue = new ArrayBlockingQueue<>(1);

        final ExecutorService consumerService = Executors.newSingleThreadExecutor();

        WifiPresenceLog log = new WifiPresenceLog();

        YaelpModelParserBolt bolt = new YaelpModelParserBolt();

        Map conf = mock(Map.class);
        TopologyContext context = mock(TopologyContext.class);
        OutputCollector collector = mock(OutputCollector.class);

        bolt.prepare(conf, context, collector);

        String[][] dataset = YaelpLogSamples.getDataSet("../datasets/unique_devices_1M.csv", 1000000);

        Stream<String[]> stream = Stream.of(dataset);

        stream.forEach(data -> {
            try {
                log.setOrganization(Long.parseLong(data[0]));
                log.setStartEvent(Long.parseLong(data[1]));
                log.setHotspot(Long.parseLong(data[2]));
                log.setSensor(Long.parseLong(data[3]));
                log.setDevice(data[4]);

                String key = "test:" + bolt.getNamespaceKey(log);
                String uuid = bolt.getUniqueCockroachLogId(log);

                RedisAsyncCommands<String, String> commands = bolt.redisConnection.async();
                RedisFuture<Long> future = ((RedisHLLAsyncCommands) commands).pfadd(key, uuid);
                Long currentlyAdded = future.get();
                System.out.println(String.format("key [%s] currentlyAdded [%s]", uuid, currentlyAdded));
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
        });
    }

    @Ignore
    public void hashBucketCalculationTest() throws Exception {
        String[] data = new String[] { "39159", "1464386400", "57469", "5171", "25481b8564d204c95515e723c16188fe" };
        WifiPresenceLog log = new WifiPresenceLog();
        log.setOrganization(Long.parseLong(data[0]));
        log.setStartEvent(Long.parseLong(data[1]));
        log.setHotspot(Long.parseLong(data[2]));
        log.setSensor(Long.parseLong(data[3]));
        log.setDevice(data[4]);

        YaelpModelParserBolt bolt = new YaelpModelParserBolt();

        Map conf = mock(Map.class);
        TopologyContext context = mock(TopologyContext.class);
        OutputCollector collector = mock(OutputCollector.class);

        bolt.prepare(conf, context, collector);

        HashCode hashCode = bolt.getCockroachLogHashCode(log);

        long maxLong = 0x7fff_ffff_ffff_ffffL;
        System.out.println(String.format("%32s [%s]", "maxLong", maxLong));

        BigInteger maxBigInt = new BigInteger(new byte[] {
                (byte) 0x7f,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff,
                (byte) 0xff });
        System.out.println(String.format("%32s [%s]", "maxBigInt", maxBigInt));

        BigDecimal bigdecimal = new BigDecimal(maxBigInt);

        BigInteger bigint = new BigInteger(hashCode.asBytes());
        System.out.println(String.format("%32s [%s]", "bigint", bigint));

        System.out.println(String.format("%32s [%s]", "hashCode.bytes.len", hashCode.asBytes().length));

        bigint = bigint.abs();
        System.out.println(String.format("%32s [%s]", "bigint", bigint));

        BigInteger division = BigIntegerMath.divide(bigint, new BigInteger("10000"), RoundingMode.CEILING);
        System.out.println(String.format("%32s [%s]", "division", division));

        String uuid = BaseEncoding.base64().encode(division.toByteArray());
        System.out.println(String.format("%32s [%s]", "division.uuid", uuid));
    }

    @Ignore
    public void hashDistributionTest() throws Exception {
        WifiPresenceLog log = new WifiPresenceLog();

        YaelpModelParserBolt bolt = new YaelpModelParserBolt();

        Map<String, List<Long>> hashMap = new HashMap<>();

        Map conf = mock(Map.class);
        TopologyContext context = mock(TopologyContext.class);
        OutputCollector collector = mock(OutputCollector.class);

        bolt.prepare(conf, context, collector);

        // String[][] dataset = YaelpLogSamples.getDataSet("../datasets/unique_devices_1M.csv", 1000000);
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

        Files.write(Paths.get("../datasets/unique_devices_1M.hashes.log"), lines);
    }

}
