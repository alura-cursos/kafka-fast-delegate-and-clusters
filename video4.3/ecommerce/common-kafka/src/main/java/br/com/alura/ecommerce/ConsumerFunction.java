package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public interface ConsumerFunction<T> {

    // throws Exception is usually a veryyyyy bad practice.
    // so libraries will create their own exception and tell
    // end-users (end-developers) to wrap it
    void parse(ConsumerRecord<String, T> record) throws CommonKafkaException;
}
