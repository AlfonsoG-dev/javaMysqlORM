package Conexion.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.ModelMethods;
import Utils.QueryBuilder;
import Utils.QueryUtils;

/**
 * clase para crear la ejecución de las sentencias sql
 * */
public class QueryExecution {
    /**
     * instancia del this.cursor de mysql
     * es único, constante e inmutable
     * */
    private Connection cursor;
    /**
     * nombre de la tabla en donde se ejecutan las sentencias sql
     * no puede ser null ni Empty
     * */
    private String table;
    /**
     * record que crea las sentencias sql
     * */
    private QueryBuilder queryBuilder;
    /**
     * clase con las herramientas para crear las querys
     */
    private QueryUtils queryUtil;

    //constructor

    /**
     * constructor de la clase
     * @param tb_name: nombre de la tabla; tb_name != null && tb_name != ""
     */
    public QueryExecution(String tbName, Connection miConector) {
        table = tbName;
        queryBuilder = new QueryBuilder(tbName);
        queryUtil = new QueryUtils();
        cursor = miConector;
    }

    //métodos

    /**
     * ejecuta el contador de resultados
     * @param pstm: ejecutor de sentencias sql
     * @return Resultado de la ejecución
     * @throws SQLException error al ejecutar
    */
    public ResultSet ExecuteCountData(PreparedStatement pstm) throws SQLException {
        String sql = "select id_pk from " + table;
        pstm = this.cursor.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    /**
     * show table data
     * @param stm: ejecutor de la sentencias sql
     * @return resultado de la ejecución
     * @throws SQLException error al ejecutar
     */
    public ResultSet ExecuteShowTableData(Statement stm) throws SQLException {
        String sql = "show columns from " + this.table;
        stm = this.cursor.createStatement();
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la lectura de datos
     * @param pstm: ejecutor de sentencias sql
     * @return resultado de la ejecución
     * @throws SQLException error al ejecutar
    */
    public ResultSet ExecuteReadAll(PreparedStatement pstm) throws SQLException {
        String sql = "select * from " + table;
        pstm = this.cursor.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    /**
     * ejecuta la busqueda de 1 resultado por id
     * @param options: id del registro buscado
     * @param type: tipo de condicion para la setencia sql
     * @param pstm: ejecutor de sentencia sql
     * @return resultado de la ejecución
     * @throws SQLException error de la ejecución
    */
    public ResultSet ExecuteFindOne(PreparedStatement pstm, String options, String type) throws SQLException {
        String sql = this.queryBuilder.CreateFindQuery(options, type);
        String val = this.queryUtil.GetOptionValue(options);
        pstm = this.cursor.prepareStatement(sql);
        pstm.setString(1, val);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    /**
     * ejecuta la busqueda por nombre de columna
     * puede recibir 1 o varias columnas con el valor
     * @param options: nombre y valor de columna: "nombre: test, email: test@test"
     * @param type: tipo de condicion para la setencia sql
     * @param stm: ejecutor de sentencia sql
     * @return resultado de la ejecución
     * @throws SQLException error de la ejecución
    */
    public ResultSet ExecuteFindByColumnName(Statement stm, String options, String type) throws SQLException {
        stm = this.cursor.createStatement();
        String sql = this.queryBuilder.CreateFindByColumnQuery(options, type);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la busqueda dentro de una serie de datos o una sentencia sql tipo SELECT.
     * @param stm: ejecutor de sentencias sql
     * @param returnOptions: columns to return
     * @param columns: columns for the conditional
     * @param condition: conditional
     * @param type: and or not
     * @throws SQLException: sql exception
     * @return result of the execution
     */
    public ResultSet ExecuteFinIn(Statement stm, String returnOptions, String columns, String condition, String type)  throws SQLException {
        stm = this.cursor.createStatement();
        String sql = queryBuilder.CreateFindInQuery(returnOptions, columns, condition, type);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la busqueda retornando min or max
     * @param stm: ejecutor de sentencias sql
     * @param columns: 'min: nombre, max: password'
     * @param condition: condition for where clausule
     * @param type: and or not
     * @throws SQLException: error de ejecución
     * @return result of the executio
     */
    public ResultSet ExecuteFindMinMax(Statement stm, String columns, String condition, String type) throws SQLException {
        stm = this.cursor.createStatement();
        String sql = queryBuilder.CreateFindMinMaxQuery(columns, condition, type);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * busca los datos utilizando regex o patrones
     * @param stm: ejecutor de sentencias sql
     * @param pattern: regex o patron a buscar
     * @param options: columnas a comparar con el patron
     * @param type: and or not
     * @throws SQLException: error de ejecución
     */
    public ResultSet ExecuteFindPattern(Statement stm, String pattern, String options, String type) throws SQLException {
        stm = this.cursor.createStatement();
        String sql = queryBuilder.CreateFindPatternQuery(table, options.trim().split(","), type);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la busqueda por columna y valor y retorna el valor de las columnas seleccionadas
     * puede recibir 1 o varias columnas con el valor
     * puede retornar 1 o varios valores de la columna seleccionada
     * @param options: nombre y valor de la columna: "nombre: test, email: test@test"
     * @param column: columna a conocer el valor: "id, rol"
     * @param type: tipo de condicion para la setencia sql
     * @param stm: ejecutor de sentencia sql
     * @return resultado de la ejecución
     * @throws SQLException error de la ejecución
    */
    public ResultSet ExecuteGetValueOfColumnName(Statement stm, String options, String columns, String type) throws SQLException {
        stm = this.cursor.createStatement();
        String sql = this.queryBuilder.CreateFindColumnValueQuery(options, columns, type);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta el registro de un nuevo elemento 
     * solo puede registrar 1 elemento a la vez
     * @param stm: ejecutor de sentencia sql
     * @param nObject: elemento a registrar
     * @return row count or '0' when nothing is returned
     * @throws SQLException error de la ejecución
    */
    public int ExecuteInsertNewRegister(Statement stm, ModelMethods model) throws SQLException {
        String sql = this.queryBuilder.CreateInsertRegisterQuery(model);
        stm = this.cursor.createStatement();
        int rst = stm.executeUpdate(sql);
        return rst;
    }
    /**
     * ejecuta la consulta tipo inner join
     * @param tb_name: nombre de la tabla local
     * @param stm: ejecutor de sentencias sql
     * @param refObject: modelo de referencia
     * @param localObject: modelo local
     * @param ref_table: table name of the ref_model
     * @throws SQLException error al ejecutar la sentencia sql
     * @return el resultado de la ejecución
     */
    public ResultSet ExecuteInnerJoin(Statement stm, ModelMethods localModel, ModelMethods refModel, String refTable) throws SQLException {
        String sql = this.queryBuilder.CreateInnerJoinQuery(localModel, refModel, refTable);
        stm = this.cursor.createStatement();
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la modificación de 1 registro
     * puede modificar cualquier valor valido del registro
     * @param stm: ejecutor de la sentencia sql
     * @param nObject: elemento a modificar
     * @param conditions: condiciones de query; "nombre: test, email: test"
     * @param type: tipo de condicion para la setencia sql
     * @return the row count of the statement or '0' when return nothing 
     * @throws SQLException error de la ejecución
    */
    public int ExecuteUpdateRegister(Statement stm, ModelMethods model, String conditions, String type) throws SQLException {
        stm = this.cursor.createStatement();
        String sql = this.queryBuilder.CreateModifyRegisterQuery(model, conditions, type);
        int retorno = stm.executeUpdate(sql);
        return retorno;
    }
    /**
     * ejecuta la eliminación de un registro
     * puede eliminar varios registros según las options
     * @param stm: ejecutor de la sentencia sql
     * @param options: opciones de condición: "nombre: test, email: test@test"
     * @param type: tipo de condicion para la setencia sql
     * @return the row count or '0' when nothing is returned
     * @throws SQLException error al ejecutar
    */
    public int ExecuteEliminarRegistro(Statement stm, String options, String type) throws SQLException {
        stm = this.cursor.createStatement();
        String sql = this.queryBuilder.CreateDeleteRegisterQuery(options, type);
        int result = stm.executeUpdate(sql);
        return result;
    }
}
