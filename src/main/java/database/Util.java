package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Util {

    private Connection connection;

    public Util() {
        if (connection == null) {
            try {
                connection = getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection != null) {
            return connection;
        }
        try (InputStream input = Util.class.getClassLoader().getResourceAsStream("db.properties")) {

            Properties prop = new Properties();
            prop.load(input);
            return DriverManager
                    .getConnection(
                            String.format("jdbc:postgresql://%s:%s/%s", prop.getProperty("hostname"), prop.getProperty("port"),
                                    prop.getProperty("db.name")),
                            prop.getProperty("db.user"), prop.getProperty("db.password"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Util(Connection connection) {
        this.connection = connection;
    }


    public boolean executeQueryOneBoolean(String query) throws SQLException {
        Statement st = getConnection().createStatement();
        ResultSet rs = st.executeQuery(query);
        rs.next();
        return rs.getBoolean(1);
    }

    public String executeQueryOneString(String query) throws SQLException {
        Statement st = getConnection().createStatement();
        ResultSet rs = st.executeQuery(query);
        rs.next();
        return rs.getString(1);
    }

    public List<String> executeQueryListString(String query) throws SQLException {
        Statement st = getConnection().createStatement();
        ResultSet rs = st.executeQuery(query);
        List<String> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getString(1));
        }
        return result;
    }

    public void executeFile(String path) throws IOException, SQLException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);


        assert inputStream != null;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

             Statement statement = getConnection().createStatement()) {

            StringBuilder builder = new StringBuilder();

            String line;
            int lineNumber = 0;
            int count = 0;

            while ((line = bufferedReader.readLine()) != null) {
                lineNumber += 1;
                //line = line.trim();

                if (line.isEmpty() || line.startsWith("--"))
                    continue;

                builder.append(line);
                if (line.endsWith(";"))
                    try {
                        statement.execute(builder.toString());
                        System.err.println(
                                ++count
                                        + " Command successfully executed : "
                                        + builder.substring(
                                        0,
                                        Math.min(builder.length(), 15))
                                        + "...");
                        builder.setLength(0);
                    } catch (SQLException e) {
                        System.err.println(
                                "At line " + lineNumber + " : "
                                        + e.getMessage() + "\n");
                        return;
                    }
            }

        }
    }

    public int executeStatement(String query) throws SQLException {
        Statement st = connection.createStatement();
        return st.executeUpdate(query);
    }

    public boolean isTableExist(String tableName) throws SQLException {
        return executeQueryOneBoolean(String.format("SELECT EXISTS ( SELECT 1 FROM pg_tables\n" +
                "            WHERE tablename = '%s');", tableName));
    }

    public String getColumnType(String tableName, String columnName) throws SQLException {
        return executeQueryOneString(String.format("SELECT data_type\n" +
                "FROM information_schema.columns WHERE table_name = '%s' and column_name = '%s';", tableName, columnName));
    }

    public List<String> getPKs(String tableName) throws SQLException {
        return executeQueryListString(String.format("SELECT pg_attribute.attname\n" +
                "    FROM pg_class, pg_attribute, pg_index\n" +
                "    WHERE pg_class.oid = pg_attribute.attrelid AND\n" +
                "    pg_class.oid = pg_index.indrelid AND\n" +
                "    pg_attribute.attnum in (pg_index.indkey[0], pg_index.indkey[1]) AND\n" +
                "    pg_index.indisprimary = 't' and pg_class.relname = '%s';", tableName));
    }

    public List<String> getFKs(java.lang.String tableName) throws SQLException {
        return executeQueryListString(java.lang.String.format("SELECT pg_catalog.pg_get_constraintdef(r.oid, true) as condef\n" +
                "FROM pg_catalog.pg_constraint r\n" +
                "WHERE r.conrelid = '%s'::regclass AND r.contype = 'f' ORDER BY 1", tableName));
    }

    public java.util.List<String> getNotNull(String tableName) throws SQLException {
        return executeQueryListString(String.format("SELECT column_name FROM information_schema.columns\n" +
                "WHERE is_nullable = 'NO' and table_name = '%s';", tableName));
    }

    public List<String> getUnique(String tableName) throws SQLException {
        return executeQueryListString(String.format("SELECT column_name\n" +
                "    FROM information_schema.constraint_column_usage\n" +
                "    WHERE table_name = '%s'\n" +
                "    AND constraint_name IN (\n" +
                "            SELECT constraint_name\n" +
                "    FROM information_schema.table_constraints\n" +
                "            WHERE constraint_type = 'UNIQUE'\n" +
                "    );", tableName));
    }

    public List<String> getCheckConstraints(String tableName) throws SQLException {
        return executeQueryListString(String.format("SELECT pg_catalog.pg_get_constraintdef(r.oid, true) as def\n" +
                "FROM pg_catalog.pg_constraint r\n" +
                "WHERE r.conrelid = '%s'::regclass AND r.contype = 'c';", tableName));
    }

    public boolean isColumnInTableExist(String tableName, String columnName) throws SQLException {
        return executeQueryOneBoolean("SELECT EXISTS (SELECT 1\n" +
                "               FROM information_schema.columns\n" +
                "               WHERE table_schema='public' AND table_name='" + tableName + "' AND column_name='" + columnName + "');");
    }

    public boolean isTypeExist(String typeName) throws SQLException {
        return executeQueryOneBoolean(String.format("select EXISTS (SELECT 1 FROM pg_type WHERE typname = '%s');", typeName));
    }



}

