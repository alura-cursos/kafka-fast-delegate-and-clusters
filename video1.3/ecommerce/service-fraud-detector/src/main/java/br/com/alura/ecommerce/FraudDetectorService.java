package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

    public static void main(String[] args) {
        var myService = new FraudDetectorService();
        try (var service = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                myService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    private final KafkaDispatcher<Order> dispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
        System.out.println("------------------------------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        var order = record.value();
        System.out.println(order);
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // ignoring because its a simulation
            e.printStackTrace();
        }
        if (order.isFraud()) {
            System.out.println("Order is a fraud");
            dispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getOrderId(), order);
        } else {
            System.out.println("Order was accepted");
            dispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getOrderId(), order);
        }
    }

}
