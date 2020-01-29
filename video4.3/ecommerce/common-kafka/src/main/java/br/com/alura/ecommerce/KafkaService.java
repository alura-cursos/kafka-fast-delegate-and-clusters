package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
    private final KafkaConsumer<String, T> consumer;
    private final ConsumerFunction<T> function;
    private final Class<T> expectedType;

    KafkaService(String consumerGroup, String topic, ConsumerFunction<T> function, Class<T> expectedType, Map<String,String> properties) {
        this(expectedType, function, consumerGroup, properties);
        consumer.subscribe(Collections.singletonList(topic));
    }


    KafkaService(String consumerGroup, Pattern topicPattern, ConsumerFunction<T> function, Class<T> expectedType, Map<String,String> properties) {
        this(expectedType, function, consumerGroup, properties);
        consumer.subscribe(topicPattern);
    }

    private KafkaService(Class<T> expectedType, ConsumerFunction<T> function, String consumerGroup, Map<String,String> properties) {
        this.expectedType = expectedType;
        this.function = function;
        this.consumer = new KafkaConsumer<>(consumerProperties(consumerGroup, properties));
    }


    private Properties consumerProperties(String consumerGroup, Map<String, String> extraProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, expectedType.getName());
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        properties.putAll(extraProperties);
        return properties;
    }

    public void run() {
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println("Found " + records.count() + " records");
                for (var record : records) {
                    try {
                        function.parse(record);
                    } catch (Exception e) {
                        // this is a dangerous decision (exception), but we will do it since its
                        // the frameworks job to handle it.

                        // simple implementation for now, just logging
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void close() {
        consumer.close();
    }
}
