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
     * check if the view is created or not.
     * <br> pre: </br> the view is aready created
     * @param viewName: the name of the view to check the creation
     * @throws SQLException: error while tring to check view creation
     * @return 1 if is created, 0 if not and -1 if nothing happens
     */
    private int isViewCreated(String viewName) throws SQLException {
        int res = -1;
        ResultSet rst = cursor.getMetaData().getTables(
                null,
                null,
                viewName,
                new String[] { 
                    "VIEW" 
                }
        );
        if(rst.next()) {
            res = 1;
        } else {
            res = 0;
        }
        return res;
    }
    /**
     * execute an sql query usign only select statements
     * <br> pre: </br> INSER, UPDATE, DELETE are not supported
     * @param stm: execute the sql statement
     * @param sql: row string with the sql statement
     * @throws SQLException: error of the execution
     * @return the result of the execution
     */
    public ResultSet executeMyQuery(Statement stm, String sql) throws SQLException {
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * execute an sql query to INSER, UPDATE or DELETE statement
     * @param stm: execute the sql statement
     * @param sql: row string with the sql statement
     * @throws SQLException: error of the execution
     * @return the row count or '0' when nothing is returned
     */
    public int executeMyUpdateQuery(Statement stm, String sql) throws SQLException {
        stm = cursor.createStatement();
        int rst = stm.executeUpdate(sql);
        return rst;
    }
    /**
     * execute a sql query to create a view from selection.
     * @param stm: execute the sql statement
     * @param viewName: name of the view to create
     * @param options: options for selection
     * @param columns: the columns to select for the view
     * @param type: type logic for where clause
     * @throws SQLException: error of the execution
     * @return the row count or '0'  when nothing is returned
     */
    public int executeCreateView(Statement stm, String viewName, String options, String columns, String type) throws SQLException {
        stm = cursor.createStatement();
        String selectSQL = queryBuilder.createFindColumnValueQuery(options, columns, type);
        String viewSQL = "create view " + viewName + " as " + selectSQL;
        stm.executeUpdate(viewSQL);
        return isViewCreated(viewName);
    }
    /**
     * ejecuta el contador de resultados
     * @param pstm: ejecutor de sentencias sql
     * @return Resultado de la ejecución
     * @throws SQLException error al ejecutar
    */
    public ResultSet executeCountData(PreparedStatement pstm) throws SQLException {
        String sql = "select id_pk from " + table;
        pstm = cursor.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    /**
     * show table data
     * @param stm: ejecutor de la sentencias sql
     * @return resultado de la ejecución
     * @throws SQLException error al ejecutar
     */
    public ResultSet executeShowTableData(Statement stm) throws SQLException {
        String sql = "show columns from " + this.table;
        stm = cursor.createStatement();
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la lectura de datos
     * @param pstm: ejecutor de sentencias sql
     * @return resultado de la ejecución
     * @throws SQLException error al ejecutar
    */
    public ResultSet executeReadAll(PreparedStatement pstm) throws SQLException {
        String sql = "select * from " + table;
        pstm = cursor.prepareStatement(sql);
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
    public ResultSet executeFindOne(PreparedStatement pstm, String options, String type) throws SQLException {
        String sql = queryBuilder.createFindQuery(options, type);
        String val = queryUtil.getOptionValue(options);
        pstm = cursor.prepareStatement(sql);
        String[] fields = val.split(",");
        for(int i=0; i<fields.length; ++i) {
            pstm.setString((i+1), fields[i].trim());
        }
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
    public ResultSet executeFindByColumnName(Statement stm, String options, String type) throws SQLException {
        stm = cursor.createStatement();
        String sql = queryBuilder.createFindByColumnQuery(options, type);
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
    public ResultSet executeFinIn(Statement stm, String returnOptions, String columns, String condition, String type)  throws SQLException {
        stm = cursor.createStatement();
        String sql = queryBuilder.createFindInQuery(returnOptions, columns, condition, type);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la busqueda retornando min or max
     * @param stm: ejecutor de sentencias sql
     * @param columns: 'min: nombre, max: password'
     * @param condition: condition for where clause
     * @param type: and or not
     * @throws SQLException: error de ejecución
     * @return result of the executio
     */
    public ResultSet executeFindMinMax(Statement stm, String columns, String condition, String type) throws SQLException {
        stm = cursor.createStatement();
        String sql = queryBuilder.createFindMinMaxQuery(columns, condition, type);
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
    public ResultSet executeFindPattern(Statement stm, String pattern, String options, String type) throws SQLException {
        stm = cursor.createStatement();
        String sql = queryBuilder.createFindPatternQuery(table, options.trim().split(","), type);
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
    public ResultSet executeGetValueOfColumnName(Statement stm, String options, String columns, String type) throws SQLException {
        stm = cursor.createStatement();
        String sql = queryBuilder.createFindColumnValueQuery(options, columns, type);
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
    public int executeInsertNewRegister(Statement stm, ModelMethods model) throws SQLException {
        String sql = queryBuilder.createInsertRegisterQuery(model);
        stm = cursor.createStatement();
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
    public ResultSet executeInnerJoin(Statement stm, ModelMethods localModel, ModelMethods refModel, String refTable, String condition, String type) throws SQLException {
        String sql = queryBuilder.createInnerJoinQuery(localModel, refModel, refTable, condition, type);
        stm = cursor.createStatement();
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
    public int executeUpdateRegister(Statement stm, ModelMethods model, String conditions, String type) throws SQLException {
        stm = cursor.createStatement();
        String sql = queryBuilder.createModifyRegisterQuery(model, conditions, type);
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
    public int executeEliminarRegistro(Statement stm, String options, String type) throws SQLException {
        stm = cursor.createStatement();
        String sql = queryBuilder.createDeleteRegisterQuery(options, type);
        int result = stm.executeUpdate(sql);
        return result;
    }
}
