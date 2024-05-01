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
    private final static String mysql_driver      = "com.mysql.cj.jdbc.Driver";

    // record con los datos de la conexión
    private DbConfig miConfig;

    /**
     * inicializa la conexión a la base de datos
     * @param nConfig: configuración para la conexión a la base de datos
     */
    public Conector(DbConfig nConfig) {
        miConfig = nConfig;
    }
    /**
     * crea la conexión a la base de datos en mysql
     * @return conexión de la base de datos
     */
    public Connection conectarMySQL() {
        Connection conn = null;
        try {
            Class.forName(mysql_driver);
            conn = DriverManager.getConnection(
                    miConfig.getMysqlUrl(),
                    miConfig.username(),
                    miConfig.password()
            );
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }
}
