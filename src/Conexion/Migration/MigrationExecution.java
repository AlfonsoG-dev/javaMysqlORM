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
    private MigrationBuilder migration_builder;
    /**
     * constructo
     */
    public MigrationExecution(String nTableName, Connection miCursor) {
        tableName = nTableName;
        migration_builder = new MigrationBuilder(nTableName);
        cursor = miCursor;
    }
    /**
     * crea la base de datos si esta no existe
     * @param DbName: nombre de la base de datos a crear
     * @param stm: ejecutor de la sentencia sql
     * @throws SQLException error al ejecutar la sentencia sql
     * @return ejecutor de la sentencia
     */
    public Statement ExecuteCreateDatabase(String DbName) throws SQLException {
        String sql = migration_builder.CreateDataBaseQuery(DbName);
        Statement stm = this.cursor.createStatement();
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
    public Statement ExecuteSelectDatabase(String DbName, Statement stm) throws SQLException {
        String sql = migration_builder.CreateSelectDatabase(DbName);
        stm = this.cursor.createStatement();
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
    public Statement ExecuteCreateTable(ModelMethods model, Statement stm) throws SQLException {
        String sql = migration_builder.CreateTableQuery(model);
        stm = this.cursor.createStatement();
        stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        return stm;
    }
    /**
     * ejecuta la sentencia para mostrar los datos de la tabla
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
    /**
     * agrega las columnas del modelo a la tabla
     * @param model: modelo con las columnas para la tabla
     * @param stm: ejecutor de la sentencia sql
     * @throws SQLException error al ejecutar la sentencia sql
     * @return el ejecutor de la sentencia
     */
    public Statement ExecuteAddColumn(ModelMethods model, ModelMethods ref_model, String ref_table, boolean includePKFK, Statement stm) throws SQLException {
        ResultSet rst = this.ExecuteShowTableData(stm);
        String sql =  "";              
        sql = migration_builder.CreateAddColumnQuery(model.InitModel(), ref_model.InitModel(), ref_table, includePKFK, rst);
        if(sql != "" || sql != null) {
            stm = this.cursor.createStatement();
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
    public Statement ExceuteRenameColumn(ModelMethods model, Statement stm) throws SQLException {
        ResultSet rst = this.ExecuteShowTableData(stm);
        String sql = migration_builder.CreateRenameColumnQuery(model.InitModel(), rst);
        if(sql != "" || sql != null) {
            stm = this.cursor.createStatement();
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
    public Statement ExecuteChangeColumnType(ModelMethods model, boolean includePKFK, Statement stm) throws SQLException {
        ResultSet rst = this.ExecuteShowTableData(stm);
        String sql = migration_builder.CreateChangeTypeQuery(model.InitModel(), includePKFK, rst);
        if(sql != "" || sql != null) {
            stm = this.cursor.createStatement();
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
    public Statement ExecuteDeleteColumn(ModelMethods model, boolean includePKFK, Statement stm) throws SQLException {
        ResultSet rst = this.ExecuteShowTableData(stm);
        String sql = migration_builder.CreateDeleteColumnQuery(model.InitModel(), includePKFK, rst);
        if(sql != "" || sql != null) {
            stm = this.cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
}
