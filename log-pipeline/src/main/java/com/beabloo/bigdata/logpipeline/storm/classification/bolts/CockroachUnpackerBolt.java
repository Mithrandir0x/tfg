package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.cockroach.model.CockroachEventHttpRequestContainer;
import com.beabloo.bigdata.cockroach.model.ParamsContainer;
import com.beabloo.bigdata.cockroach.serdes.ParamsContainerFastDeserializer;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CockroachUnpackerBolt extends BaseRichBolt {

    public static final String ID = "COCKROACH_UNPACKER_BOLT_ID";

    private static final Pattern platformPattern = Pattern.compile("^.*/activityTracking/([a-zA-Z_0-9]+).*$");
    private static final Pattern jsonParamPattern = Pattern.compile("^\\??json=(.*)$");

    private OutputCollector outputCollector;
    private ObjectMapper objectMapper;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;

        // @TODO Wrap this into a class
        objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("UNK", Version.unknownVersion());
        simpleModule.addDeserializer(ParamsContainer.class, new ParamsContainerFastDeserializer());
        objectMapper.registerModule(simpleModule);
    }

    @Override
    public void execute(Tuple input) {
        String json = getJson(input.getStringByField("data"));
        String platform = getPlatform(input.getStringByField("type"));
        if ( json != null && platform != null ) {
            try {
                CockroachEventHttpRequestContainer container = objectMapper.readValue(json, CockroachEventHttpRequestContainer.class);

                for ( ParamsContainer paramsContainer : container.getEvents() ) {
                    outputCollector.emit(new Values(input.getStringByField("timestamp"),
                            platform,
                            paramsContainer.getParamsValues(),
                            paramsContainer.getExtraParams()));
                }
            } catch ( Exception ex ) {
                // @TODO Treat Excepcion
                ex.printStackTrace();
            }
        } else {
            // Notify problem to another stream
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("timestamp", "platform", "paramsValues", "extraParams"));
    }

    String getJson(String rawData) {
        String json = null;

        String token;
        StringTokenizer paramTokenizer = new StringTokenizer(rawData, "&");

        while ( paramTokenizer.hasMoreTokens() ) {
            token = paramTokenizer.nextToken();
            Matcher matcher = jsonParamPattern.matcher(token);
            if ( matcher.matches() ) {
                json = matcher.group(1);
                break;
            }
        }

        return json;
    }

    String getPlatform(String url) {
        String platform = null;

        Matcher matcher = platformPattern.matcher(url);

        if ( matcher.matches() ) {
            platform = matcher.group(1);
        }

        return platform;
    }

}
