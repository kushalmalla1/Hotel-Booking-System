package com.hotel.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centralised JDBC connection factory.
 * Reads credentials from db.properties (not committed - see .gitignore)
 * falling back to environment variables so nothing is hard-coded.
 */
public class DBConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        String url = null, user = null, password = null;

        try (InputStream in = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                url = props.getProperty("db.url");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");
            }
        } catch (IOException e) {
            System.err.println("Could not read db.properties: " + e.getMessage());
        }

        if (url == null) url = System.getenv().getOrDefault("DB_URL",
                "jdbc:mysql://localhost:3306/hotel_booking_db");
        if (user == null) user = System.getenv().getOrDefault("DB_USER", "root");
        if (password == null) password = System.getenv().getOrDefault("DB_PASSWORD", "");

        URL = url;
        USER = user;
        PASSWORD = password;
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
