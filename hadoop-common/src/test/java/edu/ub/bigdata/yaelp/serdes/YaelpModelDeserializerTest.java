package edu.ub.bigdata.yaelp.serdes;

import edu.ub.bigdata.yaelp.model.YaelpLog;
import edu.ub.bigdata.yaelp.spec.Environment;
import edu.ub.bigdata.model.WifiPresenceLog;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class YaelpModelDeserializerTest {

    @Test
    public void deserializeNullPlatformTest() throws Exception {
        YaelpModelDeserializer yaelpModelDeserializer = new YaelpModelDeserializer();

        Object wifiLog = yaelpModelDeserializer.deserialize(null, null, null);

        assertNull(wifiLog);
    }

    @Test
    public void deserializeNullEventTest() throws Exception {
        YaelpModelDeserializer yaelpModelDeserializer = new YaelpModelDeserializer();

        Object wifiLog = yaelpModelDeserializer.deserialize(Environment.WIFI.name(), "{}", null);

        assertNull(wifiLog);
    }

    @Test
    public void deserializeUnknownActivityDefinition() throws Exception {
        YaelpModelDeserializer yaelpModelDeserializer = new YaelpModelDeserializer();

        Object wifiLog = yaelpModelDeserializer.deserialize(Environment.DS.name(), "{\"trigger\": 3}", null);

        assertNull(wifiLog);
    }

    @Test
    public void badOuiFormatTest() throws Exception {
        String data = "{\n" +
                "    \"device\": \"IDV1203211232\",\n" +
                "    \"trigger\": 3,\n" +
                "    \"organization\": \"37929\",\n" +
                "    \"oui\": \"Ax:Bx:xC\",\n" +
                "    \"power\": \"-74\",\n" +
                "    \"sensor\": \"5432\",\n" +
                "    \"hotspot\": \"57432\",\n" +
                "    \"startEvent\": \"1426101101\",\n" +
                "    \"tags\": \"click1,click2,click3\"\n" +
                "}";
        String meta = "{\n" +
                "    \"extra1\": \"10\",\n" +
                "    \"extra2\": \"20\"\n" +
                "}";

        YaelpModelDeserializer yaelpModelDeserializer = new YaelpModelDeserializer();

        Object wifiLog = yaelpModelDeserializer.deserialize(Environment.WIFI.name(), data, meta);

        assertNull(wifiLog);
    }

    @Test
    public void deserializeTest() throws Exception {
        String data = "{\n" +
                "    \"device\": \"IDV1203211232\",\n" +
                "    \"trigger\": 3,\n" +
                "    \"organization\": \"37929\",\n" +
                "    \"oui\": \"AA:BB:CC\",\n" +
                "    \"power\": \"-74\",\n" +
                "    \"sensor\": \"5432\",\n" +
                "    \"hotspot\": \"57432\",\n" +
                "    \"startEvent\": \"1426101101\",\n" +
                "    \"tags\": \"click1,click2,click3\"\n" +
                "}";
        String meta = "{\n" +
                "    \"extra1\": \"10\",\n" +
                "    \"extra2\": \"20\"\n" +
                "}";

        String platform = Environment.WIFI.name();

        YaelpModelDeserializer yaelpModelDeserializer = new YaelpModelDeserializer();

        WifiPresenceLog wifiLog = (WifiPresenceLog) yaelpModelDeserializer.deserialize(platform, data, meta);

        assertNotNull(wifiLog);
        assertEquals("IDV1203211232", wifiLog.getDevice());

        Assert.assertEquals(2, wifiLog.getExtras().size());
        Assert.assertEquals("10", wifiLog.getExtras().get("extra1"));
        Assert.assertEquals("20", wifiLog.getExtras().get("extra2"));

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
        String data = "{\"device\":\"IDV1203211232\",\"trigger\":3,\"organization\":\"37929\",\"oui\":\"AA:BB:CC\",\"power\":\"-74\",\"sensor\":\"5432\",\"hotspot\":\"57432\",\"startEvent\":\"1461499200\",\"tags\":\"click1,click2,click3\"}";

        YaelpModelDeserializer yaelpModelDeserializer = new YaelpModelDeserializer();

        assertEquals("3", yaelpModelDeserializer.getEventId(data));
    }

    @Test
    public void pythonGeneratedParamsValueTest() throws Exception {
        String data = "{\"organization_id\":39159,\"hotspot_id\":57469,\"oui\":\"a0:24:50\",\"sensor_id\":5171,\"power\":-24,\"start_event\":1461827857,\"md5_mac\":\"157e430ebd1d5cd54149e36f2cd04a42\",\"trigger\":3}";

        YaelpModelDeserializer yaelpModelDeserializer = new YaelpModelDeserializer();
        YaelpLog yaelpLog = yaelpModelDeserializer.deserialize("wifi", data, "{}");

        assertNull(yaelpLog);
    }

}
