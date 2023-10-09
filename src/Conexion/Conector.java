package Conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conector {

    // Librería de MySQL
    private final static String driver = "com.mysql.cj.jdbc.Driver";

    // Nombre de la base de datos
    private final static String database = "consulta";

    // Host
    private final static String hostname = "localhost";

    // Puerto
    private final static String port = "3306";

    // Ruta de nuestra base de datos (desactivamos el uso de SSL con "?useSSL=false")
    private final static String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;

    // Nombre de usuario
    private final static String username = "test_user";

    // Clave de usuario
    private final static String password = "5x5W12";

    /**
     * crea la conexión a la base de datos
     * @return conexión de la base de datos
     */
    public Connection conectarMySQL() {
        Connection conn = null;

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        assert conn == null : "deberia ser diferente de null";
        return conn;
    }

}
