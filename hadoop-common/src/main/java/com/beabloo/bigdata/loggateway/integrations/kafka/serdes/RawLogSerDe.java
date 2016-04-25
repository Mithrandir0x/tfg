package com.beabloo.bigdata.loggateway.integrations.kafka.serdes;

import com.beabloo.bigdata.kryo.serdes.KryoSerDe;
import com.beabloo.bigdata.model.RawLog;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RawLogSerDe implements Serializer<RawLog>, Deserializer<RawLog> {

    private KryoSerDe kryo = new KryoSerDe();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, RawLog rawLog) {
        ByteBufferOutput output = new ByteBufferOutput(1024, 1024 * 1024);
        kryo.get().writeObject(output, rawLog);
        byte[] bytes = output.toBytes();
        Logger.getLogger(RawLogSerDe.class.getName()).log(Level.SEVERE, String.format("bytes [%s]", bytesToHex(bytes)));
        return bytes;
    }

    @Override
    public RawLog deserialize(String topic, byte[] bytes) {
        try {
            Logger.getLogger(RawLogSerDe.class.getName()).log(Level.SEVERE, String.format("bytes [%s]", bytesToHex(bytes)));

            return kryo.get().readObject(new ByteBufferInput(bytes), RawLog.class);
        } catch(Exception e) {
            throw new IllegalArgumentException("Error reading bytes", e);
        }
    }

    @Override
    public void close() {
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
