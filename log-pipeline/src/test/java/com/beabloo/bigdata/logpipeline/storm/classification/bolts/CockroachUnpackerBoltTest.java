package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

import java.net.URLDecoder;

import static org.junit.Assert.*;

public class CockroachUnpackerBoltTest {

    @Test
    public void testJsonSerialization() {
        String url = "http://localhost:8080/log-gateway/activityTracking/wifi";
        String queryString = "?json={\"events\": [{\"extraParams\": {\"extra1\": \"10\", \"extra2\": \"20\"}, " +
                "\"paramsValues\": {\"device\": \"IDV1203211232\", \"event\": 3, \"organization\": \"37929\", " +
                "\"oui\": \"AA:BB:CC\", \"power\": \"-74\", \"sensor\": \"5432\", \"hotspot\": \"57432\", " +
                "\"startEvent\": \"1426101101\", \"tags\": \"click1,click2,click3\"} } ] }&_t=1455599484";

        CockroachUnpackerBolt bolt = new CockroachUnpackerBolt();

        String platform = bolt.getPlatform(url);
        String json = bolt.getJson(queryString);

        assertNotNull(platform);
        assertNotNull(json);

        assertEquals("wifi", platform);
        assertEquals("{\"events\": [{\"extraParams\": {\"extra1\": \"10\", \"extra2\": \"20\"}, \"paramsValues\": " +
                "{\"device\": \"IDV1203211232\", \"event\": 3, \"organization\": \"37929\", \"oui\": \"AA:BB:CC\", " +
                "\"power\": \"-74\", \"sensor\": \"5432\", \"hotspot\": \"57432\", \"startEvent\": \"1426101101\", " +
                "\"tags\": \"click1,click2,click3\"} } ] }", json);
    }

    @Test
    public void testAnotherJsonSerialization() {
        String queryString = "?_t=1455599484&_another_stoopid_tag=teeeest&json={\"events\": [{\"extraParams\": {\"extra1\": \"10\", \"extra2\": \"20\"}, " +
                "\"paramsValues\": {\"device\": \"IDV1203211232\", \"event\": 3, \"organization\": \"37929\", " +
                "\"oui\": \"AA:BB:CC\", \"power\": \"-74\", \"sensor\": \"5432\", \"hotspot\": \"57432\", " +
                "\"startEvent\": \"1426101101\", \"tags\": \"click1,click2,click3\"} } ] }&more_arguments=teeest";

        CockroachUnpackerBolt bolt = new CockroachUnpackerBolt();

        String json = bolt.getJson(queryString);

        assertNotNull(json);
        assertEquals("{\"events\": [{\"extraParams\": {\"extra1\": \"10\", \"extra2\": \"20\"}, \"paramsValues\": " +
                "{\"device\": \"IDV1203211232\", \"event\": 3, \"organization\": \"37929\", \"oui\": \"AA:BB:CC\", " +
                "\"power\": \"-74\", \"sensor\": \"5432\", \"hotspot\": \"57432\", \"startEvent\": \"1426101101\", " +
                "\"tags\": \"click1,click2,click3\"} } ] }", json);
    }

    @Test
    public void unescapeHtmlEntitiesTest() throws Exception {
        String json = "json={%22events%22:[{%22extraParams%22:{%22extra1%22:%2210%22,%22extra2%22:%2220%22},%22paramsValues%22:{%22device%22:%22IDV1203211232%22,%22event%22:3,%22organization%22:%2237929%22,%22oui%22:%22AA:BB:CC%22,%22power%22:%22-74%22,%22sensor%22:%225432%22,%22hotspot%22:%2257432%22,%22startEvent%22:%221461499200%22,%22tags%22:%22click1,click2,click3%22}}]}";
        String unescapedJson = "json={\"events\":[{\"extraParams\":{\"extra1\":\"10\",\"extra2\":\"20\"},\"paramsValues\":{\"device\":\"IDV1203211232\",\"event\":3,\"organization\":\"37929\",\"oui\":\"AA:BB:CC\",\"power\":\"-74\",\"sensor\":\"5432\",\"hotspot\":\"57432\",\"startEvent\":\"1461499200\",\"tags\":\"click1,click2,click3\"}}]}";

        assertEquals(unescapedJson, URLDecoder.decode(json, "UTF-8"));
    }

}