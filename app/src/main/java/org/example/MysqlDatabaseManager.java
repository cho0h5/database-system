package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertRecords(final String fileName, final List<Record> records) {
        try (Connection connection = getConnection();) {
            List<String> columnNames = getColumnNames(fileName, connection);

            String columns = String.join(", ", columnNames);
            String placeholders = columnNames.stream().map(col -> "?").collect(Collectors.joining(", "));
            String sql = "INSERT INTO `" + fileName + "` (" + columns + ") VALUES (" + placeholders + ")";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (Record record : records) {
                    for (int i = 0; i < columnNames.size(); i++) {
                        pstmt.setString(i + 1, record.fields.get(i).orElse("null"));
                    }
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void searchField(final String fileName, final String fieldName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchField'");
    }

    @Override
    public void searchRecord(final String fileName, final String fieldName, final String minValue,
            final String maxValue) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchRecord'");
    }

    private List<String> getColumnNames(String fileName, Connection connection) throws SQLException {
        List<String> columnNames = new ArrayList<>();

        try (ResultSet rs = connection.getMetaData().getColumns(null, null, fileName, null)) {
            while (rs.next()) {
                columnNames.add(rs.getString("COLUMN_NAME"));
            }
        }

        return columnNames;
    }
}
