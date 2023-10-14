package Conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import Config.DbConfig;

/**
 * clase para crear la conexión según el driver
 */
public class Conector {

    // driver de MySQL
    private final static String mysql_driver = "com.mysql.cj.jdbc.Driver";

    // driver de postgresql 
    private final static String postgresql_driver = "org.postgresql.Driver";

    // record con los datos de la conexión
    private DbConfig miConfig;

    /**
     * @param nDatabase: nombre de la base de datos
     * @param nHostname: nombre de la dirección ip de la base de datos
     * @param nPort: puerto en donde se ejecuta la base de datos
     * @param nUserName: nombre del usuario de base de datos
     * @param nPassword: contraseña del usuario de base de datos
     */
    public Conector(DbConfig nConfig) {
        miConfig = nConfig;
    }
    /**
     * crea la conexión a la base de datos
     * @return conexión de la base de datos
     */
    public Connection conectarMySQL() {
        Connection conn = null;
        try {
            Class.forName(mysql_driver);
            conn = DriverManager.getConnection(this.miConfig.GetMysqlUrl(), this.miConfig.username(), this.miConfig.password());
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }

}
