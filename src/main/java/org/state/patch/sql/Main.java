package org.state.patch.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.dbcp2.BasicDataSource;
import org.state.patch.sql.config.Configurator;
import org.state.patch.sql.config.ServiceConfig;

public class Main {
    public static void main(String[] args) {
        System.out.println("I'm state-patch-sql");

        ServiceConfig config = Configurator.extract("org.state.patch.sql", new ServiceConfig());
        try (BasicDataSource datasource = new BasicDataSource()) {
            datasource.setDriverClassName(config.database.driver);
            datasource.setUrl(config.database.url);
            datasource.setUsername(config.database.username);
            datasource.setPassword(config.database.password);
            try (Connection connection = datasource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT ?")) {
                    statement.setString(1, "Hello Postgres!");
                    try (ResultSet result = statement.executeQuery()) {
                        while (result.next()) {
                            System.out.println("Result: " + result.getString(1));
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

}
