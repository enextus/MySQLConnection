package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MySQLConnectionTest {
    private Connection connection;

    @BeforeEach
    void setUp() {
        try {
            connection = MySQLConnection.connectToDatabase();
            assertNotNull(connection, "Database connection should be established");
        } catch (SQLException e) {
            fail("Failed to connect to the database");
        }
    }

    @AfterEach
    void tearDown() {
        try {
            connection.close();
        } catch (SQLException e) {
            fail("Failed to close the database connection");
        }
    }

    @Test
    void testGetTables() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            assertNotNull(tables, "Tables ResultSet should not be null");
            assertTrue(tables.next(), "There should be at least one table in the database");
        } catch (SQLException e) {
            fail("Failed to get tables from the database");
        }
    }
    @Test
    void testTableRowCount() {
        try {
            String tableName = "event_contributor";
            int rowCount = MySQLConnection.getTableRowCount(connection, tableName);
            assertTrue(rowCount >= 0, "Row count should be greater than or equal to 0");
        } catch (SQLException e) {
            e.printStackTrace(); // Добавьте эту строку для вывода информации об исключении
            fail("Failed to get row count for the specified table");
        }
    }


    @Test
    void testGetDatabaseNames() {
        try {
            List<String> databaseNames = MySQLConnection.getDatabaseNames(connection);
            assertNotNull(databaseNames, "Database names list should not be null");
            assertFalse(databaseNames.isEmpty(), "There should be at least one database");
        } catch (SQLException e) {
            fail("Failed to get database names");
        }
    }

    @Test
    void testUseDatabase() {
        try {
            String expectedDatabase = "Eventerprise-main";
            MySQLConnection.useDatabase(connection, expectedDatabase);
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT DATABASE()");
            assertTrue(resultSet.next(), "Current database should be selected");
            String currentDatabase = resultSet.getString(1);
            assertEquals(expectedDatabase, currentDatabase, "Current database should match the selected one");
        } catch (SQLException e) {
            fail("Failed to use the specified database");
        }
    }

}
