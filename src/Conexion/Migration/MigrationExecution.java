package Conexion.Migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.ModelMethods;
import Utils.MigrationBuilder;

/**
 * clase para la ejecución de la migración del modelo
 */
public class MigrationExecution {
    /**
     * nombre de la tabla
     */
    private String tableName;
    /**
     * operador de la base de datos
     */
    private Connection cursor;
    /**
     * clase que crea las sentencias sql para la migración
     */
    private MigrationBuilder migrationBuilder;
    /**
     * constructo
     */
    public MigrationExecution(String nTableName, Connection miCursor) {
        tableName = nTableName;
        migrationBuilder = new MigrationBuilder(nTableName);
        cursor = miCursor;
    }
    /**
     * crea la base de datos si esta no existe
     * @param DbName: nombre de la base de datos a crear
     * @param stm: ejecutor de la sentencia sql
     * @throws SQLException error al ejecutar la sentencia sql
     * @return ejecutor de la sentencia
     */
    public Statement executeCreateDatabase(String dbName) throws SQLException {
        String sql = migrationBuilder.createDataBaseQuery(dbName);
        Statement stm = cursor.createStatement();
        stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        return stm;
    }
    /**
     * seleccionar la base de datos
     * @param DbName: nombre de la base de datos
     * @param stm: ejecutor de la sentencia sql
     * @throws SQLException: error al ejecutar la sentencia sql
     * @return el ejecutor de la sentencia
     */
    public Statement executeSelectDatabase(String dbName, Statement stm) throws SQLException {
        String sql = migrationBuilder.createSelectDatabase(dbName);
        stm = cursor.createStatement();
        stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        return stm;
    }
    /**
     * crea la tabla si esta no existe
     * @param model: modelo con los datos para crear la tabla
     * @param stm: ejecutor de la sentencia sql
     * @throws SQLException error al ejecutar la sentencia sql
     * @return resultado de la ejecución de la sentencia sql
     */
    public Statement executeCreateTable(ModelMethods model, Statement stm) throws SQLException {
        String sql = migrationBuilder.createTableQuery(model, "n");
        stm = cursor.createStatement();
        stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        return stm;
    }
    /**
     * create a temporary table from model
     * @param model: model to use for table creation
     * @param stm: query execution object
     * @throws SQLException: execution exception
     * @return executor of this query
     */
    public Statement executeCreateTemporaryTable(ModelMethods model, Statement stm) throws SQLException {
        String sql = migrationBuilder.createTableQuery(model, "t");
        stm = cursor.createStatement();
        stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        return stm;
    }
    /**
     * ejecuta la sentencia para mostrar los datos de la tabla
     * @param stm: ejecutor de la sentencias sql
     * @return resultado de la ejecución
     * @throws SQLException error al ejecutar
     */
    public ResultSet executeShowTableData(Statement stm) throws SQLException {
        String sql = "show columns from " + this.tableName;
        stm = cursor.createStatement();
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * agrega las columnas del modelo a la tabla
     * @param model: modelo con las columnas para la tabla
     * @param stm: ejecutor de la sentencia sql
     * @throws SQLException error al ejecutar la sentencia sql
     * @return el ejecutor de la sentencia
     */
    public Statement executeAddColumn(ModelMethods model, ModelMethods refModel, String refTable, boolean includePKFK, Statement stm) throws SQLException {
        ResultSet rst = executeShowTableData(stm);
        String sql =  "";              
        sql = migrationBuilder.createAddColumnQuery(model.initModel(), refModel.initModel(), refTable, includePKFK, rst);
        if(sql != "" || sql != null) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * renombra una columna de la tabla según el modelo
     * @param model: modelo con la columna a renombrar
     * @param stm: ejecutor de la sentencia sql
     * @throws SQLException: error al ejecutar la sentencia sql
     * @return el ejecutor de la sentencia
     */
    public Statement exceuteRenameColumn(ModelMethods model, Statement stm) throws SQLException {
        ResultSet rst = executeShowTableData(stm);
        String sql = migrationBuilder.createRenameColumnQuery(model.initModel(), rst);
        if(sql != "" || sql != null) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * modifica el tipo de dato una columna de la tabla con el que esta presente en el modelo
     * @param model: modelo con el tipo a modificar
     * @param stm: ejecutor de la sentencia sql
     * @throws SQLException: error al ejecutar la sentencia sql
     * @return el ejecutor de la sentencia
     */
    public Statement executeChangeColumnType(ModelMethods model, boolean includePKFK, Statement stm) throws SQLException {
        ResultSet rst = executeShowTableData(stm);
        String sql = migrationBuilder.createChangeTypeQuery(model.initModel(), includePKFK, rst);
        if(sql != "" || sql != null) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * elimina una columna de la tabla según el modelo
     * @param model: modelo con las columnas
     * @param stm: ejecutor de la sentencia sql
     * @throws SQLException: error al ejecutar la sentencia sql
     * @return el ejecutor de la sentencia
     */
    public Statement executeDeleteColumn(ModelMethods model, boolean includePKFK, Statement stm) throws SQLException {
        ResultSet rst = executeShowTableData(stm);
        String sql = migrationBuilder.createDeleteColumnQuery(model.initModel(), includePKFK, rst);
        System.out.println(sql);
        if(sql != "" || sql != null) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
}
