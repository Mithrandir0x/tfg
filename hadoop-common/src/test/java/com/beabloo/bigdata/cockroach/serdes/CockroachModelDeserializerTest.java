package com.beabloo.bigdata.cockroach.serdes;

import com.beabloo.bigdata.cockroach.model.CockroachEvent;
import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.model.ParamsContainer;
import com.beabloo.bigdata.cockroach.spec.ActivityDefinition;
import com.beabloo.bigdata.cockroach.spec.Platform;
import com.beabloo.bigdata.model.WifiPresenceLog;
import org.apache.storm.tuple.Values;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CockroachModelDeserializerTest {

    @Test
    public void deserializeTest() throws Exception {
        String paramsValues = "{\n" +
                "    \"device\": \"IDV1203211232\",\n" +
                "    \"event\": 3,\n" +
                "    \"organization\": \"37929\",\n" +
                "    \"oui\": \"AA:BB:CC\",\n" +
                "    \"power\": \"-74\",\n" +
                "    \"sensor\": \"5432\",\n" +
                "    \"hotspot\": \"57432\",\n" +
                "    \"startEvent\": \"1426101101\",\n" +
                "    \"tags\": \"click1,click2,click3\"\n" +
                "}";
        String extraParams = "{\n" +
                "    \"extra1\": \"10\",\n" +
                "    \"extra2\": \"20\"\n" +
                "}";

        String platform = Platform.WIFI.name();

        CockroachModelDeserializer cockroachModelDeserializer = new CockroachModelDeserializer();

        WifiPresenceLog wifiLog = (WifiPresenceLog) cockroachModelDeserializer.deserialize(platform, paramsValues, extraParams);

        assertNotNull(wifiLog);
        assertEquals("IDV1203211232", wifiLog.getDevice());

        assertEquals(2, wifiLog.getExtras().size());
        assertEquals("10", wifiLog.getExtras().get("extra1"));
        assertEquals("20", wifiLog.getExtras().get("extra2"));

        assertEquals("37929\u0001" +
                "1426101101\u0001" +
                "57432\u0001" +
                "5432\u0001" +
                "IDV1203211232\u0001" +
                "AA:BB:CC\u0001" +
                "-74\u0001" +
                "extra2\u0003" +
                "20\u0002" +
                "extra1\u0003" +
                "10", wifiLog.toString());
    }

}
