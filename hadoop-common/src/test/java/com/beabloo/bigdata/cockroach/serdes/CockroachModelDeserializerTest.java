package com.beabloo.bigdata.cockroach.serdes;

import com.beabloo.bigdata.cockroach.model.CockroachLog;
import com.beabloo.bigdata.cockroach.spec.Platform;
import com.beabloo.bigdata.model.WifiPresenceLog;
import org.junit.Test;

import static org.junit.Assert.*;

public class CockroachModelDeserializerTest {

    @Test
    public void deserializeNullPlatformTest() throws Exception {
        CockroachModelDeserializer cockroachModelDeserializer = new CockroachModelDeserializer();

        Object wifiLog = cockroachModelDeserializer.deserialize(null, null, null);

        assertNull(wifiLog);
    }

    @Test
    public void deserializeNullEventTest() throws Exception {
        CockroachModelDeserializer cockroachModelDeserializer = new CockroachModelDeserializer();

        Object wifiLog = cockroachModelDeserializer.deserialize(Platform.WIFI.name(), "{}", null);

        assertNull(wifiLog);
    }

    @Test
    public void deserializeUnknownActivityDefinition() throws Exception {
        CockroachModelDeserializer cockroachModelDeserializer = new CockroachModelDeserializer();

        Object wifiLog = cockroachModelDeserializer.deserialize(Platform.DS.name(), "{\"event\": 3}", null);

        assertNull(wifiLog);
    }

    @Test
    public void badOuiFormatTest() throws Exception {
        String paramsValues = "{\n" +
                "    \"device\": \"IDV1203211232\",\n" +
                "    \"event\": 3,\n" +
                "    \"organization\": \"37929\",\n" +
                "    \"oui\": \"Ax:Bx:xC\",\n" +
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

        CockroachModelDeserializer cockroachModelDeserializer = new CockroachModelDeserializer();

        Object wifiLog = cockroachModelDeserializer.deserialize(Platform.WIFI.name(), paramsValues, extraParams);

        assertNull(wifiLog);
    }

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

    @Test
    public void getEventIdTest() {
        String paramsValue = "{\"device\":\"IDV1203211232\",\"event\":3,\"organization\":\"37929\",\"oui\":\"AA:BB:CC\",\"power\":\"-74\",\"sensor\":\"5432\",\"hotspot\":\"57432\",\"startEvent\":\"1461499200\",\"tags\":\"click1,click2,click3\"}";

        CockroachModelDeserializer cockroachModelDeserializer = new CockroachModelDeserializer();

        assertEquals("3", cockroachModelDeserializer.getEventId(paramsValue));
    }

    @Test
    public void pythonGeneratedParamsValueTest() throws Exception {
        String paramsValue = "{\"organization_id\":39159,\"hotspot_id\":57469,\"oui\":\"a0:24:50\",\"sensor_id\":5171,\"power\":-24,\"start_event\":1461827857,\"md5_mac\":\"157e430ebd1d5cd54149e36f2cd04a42\",\"event\":3}";

        CockroachModelDeserializer cockroachModelDeserializer = new CockroachModelDeserializer();
        CockroachLog cockroachLog = cockroachModelDeserializer.deserialize("wifi", paramsValue, "{}");

        assertNull(cockroachLog);
    }

}
