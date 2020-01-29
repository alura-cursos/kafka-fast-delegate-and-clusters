package br.com.alura.ecommerce;

public class Order {

    private final String email;

    public Order(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Order{" +
                "email='" + email + '\'' +
                '}';
    }
}
