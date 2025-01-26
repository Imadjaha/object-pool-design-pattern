package example1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {

    private String url;
    private String user;
    private String password;
    private int maxPoolSize;
    private List<Connection> availableConnections;
    private List<Connection> usedConnections;

    public static ConnectionPool create(
            String url,
            String user,
            String password,
            int initialPoolSize,
            int maxPoolSize) throws SQLException {
        List<Connection> connections = new ArrayList<>(initialPoolSize);

        for (int i = 0; i < initialPoolSize; i++) {
            connections.add(createConnection(url, user, password));
        }

        return new ConnectionPool(url, user, password, connections, maxPoolSize);
    }

    private ConnectionPool(String url, String user, String password, List<Connection> pool, int maxPoolSize) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.availableConnections = pool;
        this.usedConnections = new ArrayList<>();
    }

    public synchronized Connection getConnection() throws SQLException {
        // Falls es verfuegbare Verbindungen gibt
        if (!availableConnections.isEmpty()) {
            Connection connection = createConnection(url, user, password);
            usedConnections.add(connection);
            return connection;
        }

        // Falls wir noch nicht die maximale Poolgroesse erreicht haben
        if (usedConnections.size() < maxPoolSize) {
            Connection connection = createConnection(url, user, password);
            usedConnections.add(connection);
            return connection;
        }

        // Warten bis eine Connection frei wird
        throw new SQLException("Maximale Poolgroesse erreicht, keine Verbindung verfuegbar.");
    }

    public synchronized void releaseConnection(Connection connection) {
        if (connection != null) {
            usedConnections.remove(connection);
            availableConnections.add(connection);
        }
    }


    private static Connection createConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public int getAvailableConnectionsCount() {
        return availableConnections.size();
    }
}
