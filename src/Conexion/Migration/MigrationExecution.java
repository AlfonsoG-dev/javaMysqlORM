package Conexion.Migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Conexion.Conector;
import Config.DbConfig;
import Utils.MigrationBuilder;

public class MigrationExecution {
    /**
     */
    private String tableName;
    /**
     */
    private Conector miConector;
    /**
     */
    private Connection cursor;
    /**
     */
    private MigrationBuilder migration_builder;
    /**
     */
    public MigrationExecution(String nTableName, DbConfig miConfig) {
        tableName = nTableName;
        migration_builder = new MigrationBuilder(nTableName);
        miConector = new Conector(miConfig);
        cursor = miConector.conectarMySQL();
    }
    /**
     * show table data
     * @param stm: ejecutor de la sentencias sql
     * @return resultado de la ejecución
     * @throws SQLException error al ejecutar
     */
    public ResultSet ExecuteShowTableData(Statement stm) throws SQLException {
        String sql = "show columns from " + this.tableName;
        stm = this.cursor.createStatement();
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
}
