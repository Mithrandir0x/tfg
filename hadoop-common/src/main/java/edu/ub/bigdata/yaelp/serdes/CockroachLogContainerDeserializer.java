package edu.ub.bigdata.yaelp.serdes;

import edu.ub.bigdata.yaelp.model.YaelpEventContainer;
import edu.ub.bigdata.yaelp.model.YaelpRawEvent;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.net.URLDecoder;

public class CockroachLogContainerDeserializer {

    private ObjectMapper objectMapper;

    public CockroachLogContainerDeserializer() {
        objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("UNK", Version.unknownVersion());
        simpleModule.addDeserializer(YaelpRawEvent.class, new YaelpRawEventFastDeserializer());
        objectMapper.registerModule(simpleModule);
    }

    public YaelpEventContainer deserialize(String json) throws Exception {
        return objectMapper.readValue(URLDecoder.decode(json, "UTF-8"), YaelpEventContainer.class);
    }

}
