package com.beabloo.bigdata.cockroach.serdes;

import com.beabloo.bigdata.cockroach.model.CockroachEventHttpRequestContainer;
import com.beabloo.bigdata.cockroach.model.ParamsContainer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.net.URLDecoder;

public class CockroachLogContainerDeserializer {

    private ObjectMapper objectMapper;

    public CockroachLogContainerDeserializer() {
        objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("UNK", Version.unknownVersion());
        simpleModule.addDeserializer(ParamsContainer.class, new ParamsContainerFastDeserializer());
        objectMapper.registerModule(simpleModule);
    }

    public CockroachEventHttpRequestContainer deserialize(String json) throws Exception {
        return objectMapper.readValue(URLDecoder.decode(json, "UTF-8"), CockroachEventHttpRequestContainer.class);
    }

}
