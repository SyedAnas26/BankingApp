package connectors;

import java.sql.*;

public class DbConnector {

    private static Connection makeConnection() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/banking_app?useSSL=false", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Could not display records.", e);
        } catch (ClassNotFoundException e) {
            throw new Exception("JDBC Driver not found.", e);
        }
    }

    public static Object get(String query, DBQueryRunner runner) throws Exception {
        Connection connection = makeConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery(query);
            return runner.execute(rs);
        } finally {
            connection.close();
        }
    }

    public static void update(String query) throws Exception {
        Connection connection = makeConnection();
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);
        } finally {
            connection.close();
        }
    }

    public static int insert(String query) throws Exception {
        Connection connection = makeConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        } finally {
            connection.close();
        }
    }
}

