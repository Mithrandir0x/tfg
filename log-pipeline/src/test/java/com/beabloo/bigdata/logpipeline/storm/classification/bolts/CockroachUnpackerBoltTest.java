package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import org.junit.Test;

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

}