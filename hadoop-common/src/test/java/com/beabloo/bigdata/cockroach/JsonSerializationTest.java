package com.beabloo.bigdata.cockroach;

import com.beabloo.bigdata.cockroach.model.CockroachEventHttpRequestContainer;
import com.beabloo.bigdata.cockroach.model.ParamsContainer;
import com.beabloo.bigdata.cockroach.serdes.ParamsContainerFastDeserializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JsonSerializationTest {

    @Test
    public void serializationTest() throws IOException {
        String json = "{\n" +
                "    \"events\": [\n" +
                "        {\n" +
                "            \"extraParams\": {\n" +
                "                \"extra1\": \"10\",\n" +
                "                \"extra2\": \"20\"\n" +
                "            },\n" +
                "            \"paramsValues\": {\n" +
                "                \"device\": \"IDV1203211232\",\n" +
                "                \"event\": 3,\n" +
                "                \"organization\": \"37929\",\n" +
                "                \"oui\": \"AA:BB:CC\",\n" +
                "                \"power\": \"-74\",\n" +
                "                \"sensor\": \"5432\",\n" +
                "                \"startevent\": \"1426101101\",\n" +
                "                \"tags\": \"click1,click2,click3\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("UNK", Version.unknownVersion());
        simpleModule.addDeserializer(ParamsContainer.class, new ParamsContainerFastDeserializer());
        objectMapper.registerModule(simpleModule);

        CockroachEventHttpRequestContainer container =  objectMapper.readValue(json, CockroachEventHttpRequestContainer.class);

        assertEquals(1, container.getEvents().size());

        ParamsContainer paramsContainer = container.getEvents().get(0);

        String paramsValue = "{\"device\":\"IDV1203211232\",\"event\":3,\"organization\":\"37929\",\"oui\":\"AA:BB:CC\",\"power\":\"-74\",\"sensor\":\"5432\",\"startevent\":\"1426101101\",\"tags\":\"click1,click2,click3\"}";
        assertEquals(paramsValue, paramsContainer.getParamsValues());

        String extraValue = "{\"extra1\":\"10\",\"extra2\":\"20\"}";
        assertEquals(extraValue, paramsContainer.getExtraParams());
    }

}
