package com.beabloo.bigdata.loggateway.integrations.kafka.serdes;

import com.beabloo.bigdata.model.RawLog;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class RawLogSerDe implements Serializer<RawLog>, Deserializer<RawLog> {

    private ThreadLocal<Kryo> kryo = new ThreadLocal<Kryo>() {

        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.addDefaultSerializer(RawLog.class, new KryoInternalSerializer());
            return kryo;
        }

    };

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, RawLog rawLog) {
        ByteBufferOutput output = new ByteBufferOutput(1024, 1024 * 1024);
        kryo.get().writeObject(output, rawLog);
        return output.toBytes();
    }

    @Override
    public RawLog deserialize(String topic, byte[] bytes) {
        try {
            return kryo.get().readObject(new ByteBufferInput(bytes), RawLog.class);
        } catch(Exception e) {
            throw new IllegalArgumentException("Error reading bytes", e);
        }
    }

    @Override
    public void close() {
    }

    private static class KryoInternalSerializer extends com.esotericsoftware.kryo.Serializer<RawLog> {

        @Override
        public void write(Kryo kryo, Output output, RawLog rawLog) {
            output.writeLong(rawLog.getTimestamp());
            output.writeString(rawLog.getType());
            output.writeString(rawLog.getData());
        }

        @Override
        public RawLog read(Kryo kryo, Input input, Class<RawLog> aClass) {
            long timestamp = input.readLong();
            String type = input.readString();
            String rawData = input.readString();
            return new RawLog(timestamp, type, rawData);
        }

    }

}
