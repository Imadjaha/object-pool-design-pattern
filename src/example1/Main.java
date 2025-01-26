package example1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        try {
            ConnectionPool connectionPool = ConnectionPool.create(
                    "jdbc:mysql://localhost:3306/adampos",
                    "root",
                    "AdamPos123",
                    2,
                    4
            );

            Connection connection1 = connectionPool.getConnection();
            System.out.println("Verbindung 1 bekommen, Verfuegbare Verbindungen: " + connectionPool.getAvailableConnectionsCount());

            Connection connection2 = connectionPool.getConnection();
            System.out.println("Verbindung 2 bekommen, Verfugbare Verbindungen: " + connectionPool.getAvailableConnectionsCount());


//            Connection connection4 = connectionPool.getConnection();
//            System.out.println("Verbindung 4 bekommen, Verfugbare Verbindungen: " + connectionPool.getAvailableConnectionsCount());

            // Arbeiten mit connection1
            // ... (SQL-Abfragen ausführen)
            // Execute SQL query
            Statement statement = connection1.createStatement();
            String query = "SELECT * FROM user"; // Adjust table name to your database schema
            ResultSet resultSet = statement.executeQuery(query);

            // Process the results
            while (resultSet.next()) {
                System.out.println("User ID: " + resultSet.getInt("user_id")); // Replace "id" with actual column name
                System.out.println("Username: " + resultSet.getString("username")); // Replace "username" with actual column name
            }
            // Freigeben der Verbindung
            connectionPool.releaseConnection(connection1);
            System.out.println("Verbindung 1 freigegeben. Verfügbare Verbindungen: "
                    + connectionPool.getAvailableConnectionsCount());


            // Nun könnte man erneut eine Verbindung anfordern ...
            // Connection connection3 = connectionPool.getConnection();

            Connection connection3 = connectionPool.getConnection();
            System.out.println("Verbindung 3 bekommen, Verfugbare Verbindungen: " + connectionPool.getAvailableConnectionsCount());


            // Schlussendlich sollte man auch connection2 freigeben
            connectionPool.releaseConnection(connection2);
            System.out.println("Verbindung 2 freigegeben. Verfügbare Verbindungen: "
                    + connectionPool.getAvailableConnectionsCount());


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}