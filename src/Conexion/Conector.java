package Conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conector {

    // driver de MySQL
    private final static String mysql_driver = "com.mysql.cj.jdbc.Driver";

    // driver de postgresql 
    private final static String postgresql_driver = "org.postgresql.Driver";

    // Nombre de la base de datos
    private String database;

    // Host
    private String hostname;

    // Puerto
    private String port;

    // Ruta de nuestra base de datos (desactivamos el uso de SSL con "?useSSL=false")
    private String url;

    // Nombre de usuario
    private String username;

    // Clave de usuario
    private String password;

    /**
     * @param nDatabase
     * @param nHostname
     * @param nPort
     * @param nUrl
     * @param nUserName
     * @param nPassword
     */
    public Conector(String nDatabase, String nHostname, String nPort, String nUserName, String nPassword) {
        database = nDatabase;
        hostname = nHostname;
        port = nPort;
        username = nUserName;
        password = nPassword;
    }
    private String SetUrlConnection(String type) {
       this.url = "jdbc:" + type +"://" + this.hostname + ":" + this.port + "/" + this.database;
        return this.url;
    }
    /**
     * crea la conexión a la base de datos
     * @return conexión de la base de datos
     */
    public Connection conectarMySQL() {
        Connection conn = null;
        try {
            Class.forName(mysql_driver);
            conn = DriverManager.getConnection(this.SetUrlConnection("mysql"), this.username, this.password);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }

}
