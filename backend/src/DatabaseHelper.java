    import org.json.JSONArray;
    import org.json.JSONObject;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class DatabaseHelper {

        String url = "jdbc:sqlserver://flight-management-system.database.windows.net:1433;databaseName=Flights;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
        private final String user = "Flight";
        private final String password = "Burunduk!";

        public String fetchTable(String tableName) {
            JSONArray allRows = new JSONArray();
            String sql = "SELECT * FROM " + tableName;

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                ResultSetMetaData md = rs.getMetaData();
                int columns = md.getColumnCount();

                while (rs.next()) {
                    JSONObject row = new JSONObject();
                    for (int i = 1; i <= columns; i++) {
                        String columnName = md.getColumnName(i);
                        Object value = rs.getObject(i);
                        // Обработка null значений, чтобы JSON не "ломался"
                        row.put(columnName, value != null ? value : "");
                    }
                    allRows.put(row);
                }
            } catch (SQLException e) {
                System.err.println("Ошибка при чтении таблицы " + tableName + ": " + e.getMessage());
                return "[]";
            }
            return allRows.toString();
        }

        public List<String> getAllTableNames() {
            List<String> tables = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                DatabaseMetaData metaData = conn.getMetaData();

                try (ResultSet rs = metaData.getTables("Flights", "dbo", "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        String name = rs.getString("TABLE_NAME");
                        // Убираем системные таблицы, если они пролезли через фильтр dbo
                        if (!name.startsWith("sys") && !name.startsWith("MS")) {
                            tables.add(name);
                        }
                    }
                }

                if (tables.isEmpty()) {
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs2 = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE'")) {
                        while (rs2.next()) {
                            String name = rs2.getString("TABLE_NAME");
                            if (!name.startsWith("sys") && !name.startsWith("MS")) {
                                tables.add(name);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return tables;
        }

        public boolean deleteRow(String tableName, String idColumnName, int idValue) {
            String sql = "DELETE FROM " + tableName + " WHERE " + idColumnName + " = ?";
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idValue);
                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;

            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
                return false;
            }
        }

        public boolean addRow(String tableName, JSONObject rowData) {
            StringBuilder columns = new StringBuilder();
            StringBuilder placeholders = new StringBuilder();
            List<Object> values = new ArrayList<>();

            for (String key : rowData.keySet()) {
                Object value = rowData.get(key);

                if (value == null || value.toString().trim().isEmpty()) {
                    continue;
                }

                if (columns.length() > 0) {
                    columns.append(", ");
                    placeholders.append(", ");
                }
                columns.append("[").append(key).append("]");
                placeholders.append("?");

                String strVal = value.toString().trim();
                if (strVal.matches("-?\\d+")) { // Целое число
                    values.add(Integer.parseInt(strVal));
                } else if (strVal.matches("-?\\d+(\\.\\d+)?")) { // Дробное
                    values.add(Double.parseDouble(strVal));
                } else {
                    values.add(strVal);
                }
            }

            String sql = "INSERT INTO [" + tableName + "] (" + columns + ") VALUES (" + placeholders + ")";

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                for (int i = 0; i < values.size(); i++) {
                    pstmt.setObject(i + 1, values.get(i));
                }

                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("SQL Error (Add): " + e.getMessage());
                return false;
            }
        }
    }