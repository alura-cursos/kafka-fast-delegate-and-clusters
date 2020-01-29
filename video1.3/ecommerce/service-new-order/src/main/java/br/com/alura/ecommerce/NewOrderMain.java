package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try (var dispatcher = new KafkaDispatcher()) {

            var email = UUID.randomUUID().toString() + "@email.com";

            for (var i = 0; i < 10; i++) {
                var orderId = UUID.randomUUID().toString();
                var amount = new BigDecimal(Math.random() * 5000 + 1);
                var order = new Order(email, orderId, amount);

                dispatcher.send("ECOMMERCE_NEW_ORDER", orderId, order);

                var emailContent = "Thank you for your order! We are processing it!";
                dispatcher.send("ECOMMERCE_SEND_EMAIL", orderId, emailContent);
            }
        }

    }

}
