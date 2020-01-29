package br.com.alura.ecommerce;

import org.eclipse.jetty.servlet.Source;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (var dispatcher = new KafkaDispatcher()) {

            var email = req.getParameter("email");
            var amount = new BigDecimal(req.getParameter("amount"));

            var orderId = UUID.randomUUID().toString();
            var order = new Order(email, orderId, amount);

            try {

                dispatcher.send("ECOMMERCE_NEW_ORDER", email, order);
                System.out.println("The first message was sent.");

                var emailContent = "Thank you for your order! We are processing it!";
                dispatcher.send("ECOMMERCE_SEND_EMAIL", orderId, emailContent);

                System.out.println("New order sent successfully.");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println("New order sent");

            } catch (ExecutionException | InterruptedException e) {
                throw new ServletException(e);
            }

        }

    }

}
