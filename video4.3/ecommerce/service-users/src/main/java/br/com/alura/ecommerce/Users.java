package br.com.alura.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class Users {

    private final Connection connection;

    Users() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            this.connection.createStatement().execute("create table Users (" +
                    "uuid varchar(200) primary key," +
                    "email varchar(200))");
        } catch(SQLException ex) {
            // not the best solution for database schema evolution.
            // but this is not our goal here.
            ex.printStackTrace();
        }
    }

    public boolean isNew(String email) throws SQLException {
        var statement = connection.prepareStatement("select uuid from Users " +
                "where email = ? limit 1");
        statement.setString(1, email);
        var results = statement.executeQuery();
        return !results.next();
    }

    public void insertNew(String email) throws SQLException {
        var statement = connection.prepareStatement("insert into Users (uuid, email) " +
                "values (?,?)");
        statement.setString(1, UUID.randomUUID().toString());
        statement.setString(2, email);
        statement.execute();
        System.out.println("User " + email + " inserted.");
    }
}






