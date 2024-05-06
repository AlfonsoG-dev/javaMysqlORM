package Conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import Config.DbConfig;

/**
 * class for database connection
 */
public class Connector {

    private final static String mysql_driver = "com.mysql.cj.jdbc.Driver";

    private DbConfig miConfig;

    public Connector(DbConfig nConfig) {
        miConfig = nConfig;
    }
    public Connection mysqlConnection() {
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
