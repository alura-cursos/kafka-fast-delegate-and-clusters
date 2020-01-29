package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    public static void main(String[] args) throws SQLException {
        var myService = new CreateUserService();
        try (var service = new KafkaService<>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                myService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    public CreateUserService() throws SQLException {
        this.users = new Users();
    }

    private final Users users;

    private void parse(ConsumerRecord<String, Order> record) throws CommonKafkaException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        var order = record.value();
        try {
            if (users.isNew(order.getEmail())) {
                users.insertNew(order.getEmail());
            }
        } catch (SQLException ex) {
            throw new CommonKafkaException(ex);
        }
    }

}
