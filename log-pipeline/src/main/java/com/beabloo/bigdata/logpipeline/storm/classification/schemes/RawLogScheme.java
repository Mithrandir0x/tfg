package com.beabloo.bigdata.logpipeline.storm.classification.schemes;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import com.beabloo.bigdata.loggateway.integrations.kafka.serdes.RawLogSerDe;
import com.beabloo.bigdata.model.RawLog;

import java.nio.ByteBuffer;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawLogScheme implements Scheme {

    private static final Logger log = LoggerFactory.getLogger(RawLogScheme.class);

    @Override
    public List<Object> deserialize(ByteBuffer payload) {
        byte[] bytes = new byte[payload.limit()];
        payload.get(bytes);

//        log.debug(String.format("bytes [%s]", bytesToHex(bytes)));

        RawLogSerDe serDe = new RawLogSerDe();
        RawLog rawLog = serDe.deserialize(null, bytes);

        return new Values(rawLog.getTimestamp(), rawLog.getType(), rawLog.getData());
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("timestamp", "type", "data");
    }

//    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();
//    private static String bytesToHex(byte[] bytes) {
//        StringBuffer buffer = new StringBuffer();
//        char l, r;
//        for ( int j = 0; j < bytes.length; j++ ) {
//            int v = bytes[j] & 0xFF;
//            l = hexArray[v >>> 4];
//            r = hexArray[v & 0x0F];
//            if ( j != bytes.length - 1 ) {
//                buffer.append(String.format("%c%c ", l, r));
//            } else {
//                buffer.append(String.format("%c%c", l, r));
//            }
//        }
//        return buffer.toString();
//    }

}
