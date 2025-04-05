package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

public class MysqlDatabaseManager implements DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/databasesystem";
    private static final String USER = "root";
    private static final String PASSWORD = "databasesystem";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void createTable(final String fileName, final List<Field> fields) {
        String columns = fields.stream()
                .map(field -> "`" + field.getName() + "` VARCHAR(255)")
                .collect(Collectors.joining(", "));

        String sql = "CREATE TABLE IF NOT EXISTS `" + fileName + "` (" + columns + ")";

        try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertRecords(final String fileName, final List<Record> records) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertRecords'");
    }

    @Override
    public void searchField(final String fileName, final String fieldName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchField'");
    }

    @Override
    public void searchRecord(final String fileName, final String fieldName, final String minValue,
            final String maxValue) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchRecord'");
    }
}
