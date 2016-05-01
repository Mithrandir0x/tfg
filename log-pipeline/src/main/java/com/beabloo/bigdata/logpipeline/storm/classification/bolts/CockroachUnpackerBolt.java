package com.beabloo.bigdata.logpipeline.storm.classification.bolts;

import com.beabloo.bigdata.cockroach.model.CockroachEventHttpRequestContainer;
import com.beabloo.bigdata.cockroach.model.ParamsContainer;
import com.beabloo.bigdata.cockroach.serdes.ParamsContainerFastDeserializer;
import org.apache.storm.metric.api.CountMetric;
import org.apache.storm.metric.api.MultiCountMetric;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CockroachUnpackerBolt extends BaseRichBolt {

    private static final Logger log = LoggerFactory.getLogger(CockroachUnpackerBolt.class);

    public static final String ID = "COCKROACH_UNPACKER_BOLT_ID";

    private static final Pattern platformPattern = Pattern.compile("^.*/activityTracking/([a-zA-Z_0-9]+).*$");
    private static final Pattern jsonParamPattern = Pattern.compile("^\\??json=(.*)$");

    private OutputCollector outputCollector;
    private ObjectMapper objectMapper;

    transient MultiCountMetric platformSuccessMetric;
    transient CountMetric badFormattedJsonErrorMetric;
    transient CountMetric exceptionErrorMetric;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        outputCollector = collector;

        // @TODO Wrap this into a class
        objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("UNK", Version.unknownVersion());
        simpleModule.addDeserializer(ParamsContainer.class, new ParamsContainerFastDeserializer());
        objectMapper.registerModule(simpleModule);

        platformSuccessMetric = new MultiCountMetric();
        context.registerMetric("CockroachUnpackerBolt.success", platformSuccessMetric, 1);

        badFormattedJsonErrorMetric = new CountMetric();
        context.registerMetric("CockroachUnpackerBolt.error.badjson", badFormattedJsonErrorMetric, 1);

        exceptionErrorMetric = new CountMetric();
        context.registerMetric("CockroachUnpackerBolt.error.exception", exceptionErrorMetric, 1);
    }

    @Override
    public void execute(Tuple input) {
        String json = getJson(input.getStringByField("data"));
        String platform = getPlatform(input.getStringByField("type"));
        if ( json != null && platform != null ) {
            try {
                log.debug(String.format("Received new raw log. json [%s]", json));

                CockroachEventHttpRequestContainer container = objectMapper.readValue(URLDecoder.decode(json, "UTF-8"), CockroachEventHttpRequestContainer.class);

                log.info(String.format("Unpacked [%s] cockroach events", container.getEvents().size()));

                for ( ParamsContainer paramsContainer : container.getEvents() ) {
                    log.debug(String.format("Emiting new event log [%s]", paramsContainer));
                    outputCollector.emit(new Values(input.getValueByField("timestamp"),
                            platform,
                            paramsContainer.getParamsValues(),
                            paramsContainer.getExtraParams()));
                }

                platformSuccessMetric.scope(platform).incrBy(container.getEvents().size());
            } catch ( Exception ex ) {
                // @TODO Treat Exception nicely
                ex.printStackTrace();

                exceptionErrorMetric.incr();
            }
        } else {
            // Notify problem to another stream
            log.error(String.format("Badly formatted data. platform [%s] json [%s]", platform, json));
            badFormattedJsonErrorMetric.incr();
        }

        outputCollector.ack(input);
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
