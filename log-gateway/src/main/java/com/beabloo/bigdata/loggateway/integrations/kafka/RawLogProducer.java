package com.beabloo.bigdata.loggateway.integrations.kafka;

import com.beabloo.bigdata.loggateway.integrations.kafka.serdes.RawLogSerDe;
import com.beabloo.bigdata.model.RawLog;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

public class RawLogProducer {

    private static RawLogProducer ourInstance = new RawLogProducer();

    private static String baseTopic = "raw-logs";

    private Producer<String, RawLog> producer;

    private RawLogProducer() {
        try {
            Properties properties = new Properties();

            String kafkaVersion = System.getProperty("kafka.version"); // -Dkafka.version=0.9
            if ( "0.9".equals(kafkaVersion) ) {
                properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("log_gateway.kafka.producer.bootstrap.servers")); // -Dlog_gateway.kafka.producer.bootstrap.servers=localhost:9092
                properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, RawLogSerDe.class.getName());
            } else if ( "0.8.2.2".equals(kafkaVersion) ) {
                properties.setProperty("metadata.broker.list", System.getProperty("log_gateway.kafka.metadata.broker.list")); // -Dlog_gateway.kafka.metadata.broker.list=localhost:9092
                properties.setProperty("key.serializer.class", StringSerializer.class.getName());
                properties.setProperty("serializer.class", RawLogSerDe.class.getName());
            }
            properties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, System.getProperty("log_gateway.kafka.producer.client.id")); // -Dlog_gateway.kafka.producer.client.id=local
            properties.setProperty(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, "60000");
            properties.setProperty(ProducerConfig.ACKS_CONFIG, "-1");

            producer = new KafkaProducer<>(properties);
        } catch ( Throwable ex ) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public static RawLogProducer getInstance() {
        return ourInstance;
    }

    public Producer<String, RawLog> getProducer() {
        return producer;
    }

    public void send(RawLog rawLog) {
        long timestamp = System.currentTimeMillis();
        long partition = timestamp % 4;
        ProducerRecord<String, RawLog> message = new ProducerRecord<>(baseTopic, "" + partition, rawLog);
        producer.send(message, (RecordMetadata metadata, Exception exception) -> {
            if ( exception == null ) {
                System.out.println(String.format("Sent message [%s] to kafka [ topic [%s] partition [%s] offset [%s] ]...",
                        timestamp,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset()
                ));
            } else {
                System.out.println(String.format("Error while trying to send message [%s] to kafka...", timestamp));
                exception.printStackTrace();
            }
        });
    }

}
