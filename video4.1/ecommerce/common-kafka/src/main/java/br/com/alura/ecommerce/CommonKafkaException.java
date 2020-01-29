package br.com.alura.ecommerce;

public class CommonKafkaException extends Exception {
    public CommonKafkaException(Exception ex) {
        super(ex);
    }
}
