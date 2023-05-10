package org.example;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySQLConnection {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "xx_development_xx";

    public static void main(String[] args) {
        try {
            Connection connection = connectToDatabase();
            System.out.println("Database connection successful!");

            List<String> databaseNames = getDatabaseNames(connection);
            System.out.println("Список баз данных: " + databaseNames);

            String selectedDatabase = "Eventerprise-main";
            useDatabase(connection, selectedDatabase);
            System.out.println("Выбрана база данных: " + selectedDatabase);

            // Получение списка таблиц и количества записей в них
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            System.out.println("\nСписок таблиц и количество записей в них:");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                int rowCount = getTableRowCount(connection, tableName);
                System.out.printf("- %s: %d%n", tableName, rowCount);
            }
            tables.close();

            connection.close();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    public static Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
    }

    public static int getTableRowCount(Connection connection, String tableName) throws SQLException {
        int rowCount = 0;

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM `" + tableName + "`");
            if (resultSet.next()) {
                rowCount = resultSet.getInt(1);
            }
            resultSet.close();
        }

        return rowCount;
    }

    public static List<String> getDatabaseNames(Connection connection) throws SQLException {
        List<String> databaseNames = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SHOW DATABASES");
            while (resultSet.next()) {
                databaseNames.add(resultSet.getString(1));
            }
            resultSet.close();
        }
        return databaseNames;
    }

    public static void useDatabase(Connection connection, String databaseName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("USE `" + databaseName + "`");
        }
    }
}
