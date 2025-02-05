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

    //! sicherstellt, dass der ConnectionPool nur über die create()-Methode erstellt werden kann. (Wie Singelton)
    private ConnectionPool(String url, String user, String password, List<Connection> pool, int maxPoolSize) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.availableConnections = pool;
        this.usedConnections = new ArrayList<>();
    }

    //! erstellt einen PoolInstanz
    public static ConnectionPool create(
            String url,
            String user,
            String password,
            int initialPoolSize,
            int maxPoolSize) throws SQLException {
        List<Connection> connections = new ArrayList<>(initialPoolSize);

        try {
            for (int i = 0; i < initialPoolSize; i++) {
                connections.add(createConnection(url, user, password));
            }
        } catch (SQLException e) {
            // Bereits erstellte Verbindungen schließen
            for (Connection conn : connections) {
                if (conn != null) {
                    conn.close();
                }
            }
            throw e; // Ausnahme erneut werfen
        }

        return new ConnectionPool(url, user, password, connections, maxPoolSize);
    }


    //! Holt Datenbankverbindung aus dem Pool
    public synchronized Connection getConnection() throws SQLException {
        Connection connection = null;

        // Falls es verfügbare Verbindungen gibt, nimm eine aus dem Pool
        if (!availableConnections.isEmpty()) {
            connection = availableConnections.remove(availableConnections.size() - 1);
        }
        // Falls der Pool nicht voll ist, erstelle eine neue Verbindung
        else if (usedConnections.size() < maxPoolSize) {
            connection = createConnection(url, user, password);
        }
        // Falls keine Verbindung verfügbar ist und der Pool voll ist
        else {
            throw new SQLException("Maximale Poolgröße erreicht, keine Verbindung verfügbar.");
        }

        usedConnections.add(connection); // Markiere die Verbindung als verwendet
        return connection;
    }


    //! Methode gibt eine bereits verwendete Verbindung an den Pool zurück, sodass sie wieder verfügbar ist
    // synchronized stellt sicher, dass nur ein Thread gleichzeitig auf diese Methode zugreifen kann
    public synchronized void releaseConnection(Connection connection) {
        if (connection != null) {
            usedConnections.remove(connection);
            availableConnections.add(connection);
        }
    }

    // HilfsFunktionen

    private static Connection createConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public int getAvailableConnectionsCount() {
        return availableConnections.size();
    }
}
