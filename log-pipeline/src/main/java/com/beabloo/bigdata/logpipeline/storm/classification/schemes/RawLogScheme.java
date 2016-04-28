package com.beabloo.bigdata.logpipeline.storm.classification.schemes;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import com.beabloo.bigdata.loggateway.integrations.kafka.serdes.RawLogSerDe;
import com.beabloo.bigdata.model.RawLog;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RawLogScheme implements Scheme {

//    @Override
//    public List<Object> deserialize(byte[] ser) {
//        RawLogSerDe serDe = new RawLogSerDe();
//        RawLog rawLog = serDe.deserialize(null, ser);
//        return new Values(rawLog.getTimestamp(), rawLog.getType(), rawLog.getData());
//    }

    @Override
    public List<Object> deserialize(ByteBuffer buffer) {
        RawLogSerDe serDe = new RawLogSerDe();
        byte[] bufferBytes = buffer.array();
        byte[] bytes = Arrays.copyOfRange(bufferBytes, 71, bufferBytes.length);;
        //Logger.getLogger(RawLogScheme.class.getName()).log(Level.SEVERE, String.format("rawlogscheme bytes [%s]", bytesToHex(bytes)));

        RawLog rawLog = serDe.deserialize(null, bytes);

        return new Values(rawLog.getTimestamp(), rawLog.getType(), rawLog.getData());
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("timestamp", "type", "data");
    }

    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        char l, r;
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            l = hexArray[v >>> 4];
            r = hexArray[v & 0x0F];
            if ( j != bytes.length - 1 ) {
                buffer.append(String.format("%c%c ", l, r));
            } else {
                buffer.append(String.format("%c%c", l, r));
            }
        }
        return buffer.toString();
    }

}
