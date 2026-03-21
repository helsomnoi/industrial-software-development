package com.library.util;

import com.library.exception.LibraryException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;
import java.util.stream.Collectors;

public class DatabaseManager {
    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

    static {
        try {
            Properties props = PropertiesLoader.load("application.properties");
            DB_URL = props.getProperty("db.url");
            DB_USER = props.getProperty("db.user");
            DB_PASSWORD = props.getProperty("db.password");
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            throw new LibraryException("Не удалось загрузить конфигурацию базы данных", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", DB_USER);
        props.setProperty("password", DB_PASSWORD);
        props.setProperty("client_encoding", "UTF8");
        props.setProperty("charSet", "UTF-8");
        return DriverManager.getConnection(DB_URL, props);
    }

    public static void initializeSchema() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = new BufferedReader(new InputStreamReader(
                    DatabaseManager.class.getClassLoader().getResourceAsStream("schema.sql")))
                    .lines()
                    .collect(Collectors.joining("\n"));
            stmt.execute(sql);
            System.out.println("Схема базы данных успешно инициализирована.");
        } catch (Exception e) {
            throw new LibraryException("Ошибка инициализации схемы базы данных", e);
        }
    }

    public static void loadTestData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = new BufferedReader(new InputStreamReader(
                    DatabaseManager.class.getClassLoader().getResourceAsStream("test-data.sql")))
                    .lines()
                    .collect(Collectors.joining("\n"));
            stmt.execute(sql);
            System.out.println("Тестовые данные успешно загружены.");
        } catch (Exception e) {
            throw new LibraryException("Ошибка загрузки тестовых данных", e);
        }
    }
}