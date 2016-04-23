package com.beabloo.bigdata.kryo.serdes;

import com.beabloo.bigdata.model.RawLog;
import com.beabloo.bigdata.model.WifiPresenceLog;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoSerDe extends ThreadLocal<Kryo> {

    protected Kryo initialValue() {
        Kryo kryo = new Kryo();
        kryo.register(WifiPresenceLog.class, 1);
        kryo.register(RawLog.class, new KryoInternalSerializer(), 2);
        return kryo;
    }

    private static class KryoInternalSerializer extends Serializer<RawLog> {

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
