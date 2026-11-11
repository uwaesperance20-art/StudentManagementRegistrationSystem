package com.example.StudentRegistrationManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    private static final String SERVER_URL = "jdbc:postgresql://localhost:5432/";
    private static final String DATABASE_NAME = "student_management";   // must match DatabaseConnection

    public static void main(String[] args) {
        // Environment variables are optional now; defaults match DatabaseConnection.
        String username = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "postgres";
        String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "123";

        try {
            createDatabase(username, password);
            createTable(username, password);
            System.out.println("Setup finished successfully.");
        } catch (SQLException e) {
            System.out.println("Setup failed: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
        }
    }

    private static void createDatabase(String username, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(SERVER_URL + "postgres", username, password)) {

            String checkSql = "SELECT 1 FROM pg_database WHERE datname = ?";
            try (PreparedStatement check = connection.prepareStatement(checkSql)) {
                check.setString(1, DATABASE_NAME);
                try (ResultSet result = check.executeQuery()) {
                    if (result.next()) {
                        System.out.println("Database already exists: " + DATABASE_NAME);
                        return;
                    }
                }
            }

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE DATABASE " + DATABASE_NAME);
                System.out.println("Database created: " + DATABASE_NAME);
            }
        }
    }

    private static void createTable(String username, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(SERVER_URL + DATABASE_NAME, username, password);
             Statement statement = connection.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS students ("
                    + " id        SERIAL PRIMARY KEY,"
                    + " full_name VARCHAR(150) NOT NULL,"
                    + " age       INTEGER CHECK (age > 18),"
                    + " email     VARCHAR(150) UNIQUE NOT NULL"
                    + ")";

            statement.executeUpdate(sql);
            System.out.println("Table ready: students");
        }
    }
}