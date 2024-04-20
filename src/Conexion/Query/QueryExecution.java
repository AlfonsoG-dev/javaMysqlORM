package Conexion.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        table        = tbName;
        queryBuilder = new QueryBuilder(tbName);
        queryUtil    = new QueryUtils();
        cursor       = miConector;
    }
    /**
     * check if the view is created or not.
     * <br> pre: </br> the view is aready created
     * @param viewName: the name of the view to check the creation
     * @throws SQLException: error while tring to check view creation
     * @return 1 if is created, 0 if not and -1 if nothing happens
     */
    private int isViewCreated(String viewName) throws SQLException {
        int res       = -1;
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
        stm     = cursor.createStatement();
        int rst = stm.executeUpdate(sql);
        return rst;
    }
    /**
     * execute a sql query to create a view from selection.
     * @param stm: execute the sql statement
     * @param viewName: name of the view to create
     * @param condition: condition for selection
     * @param columns: the columns to select for the view
     * @param type: type logic for where clause
     * @throws SQLException: error of the execution
     * @return 1 if is created, 0 if is not created and -1 if nothing happens
     */
    public int executeCreateView(Statement stm, String viewName, String condition, String columns,
            String type) throws SQLException {
        stm              = cursor.createStatement();
        String
            selectSQL = queryBuilder.createFindColumnValueQuery(
                condition,
                columns,
                type
            ),
            viewSQL   = "create view " + viewName + " as " + selectSQL;
        stm.executeUpdate(viewSQL);
        return isViewCreated(viewName);
    }
    /**
     * execute a sql query to delete a view from database
     * @param viewName: view name to delete
     * @throws SQLException: error of the execution
     * @return 0 if the view doesn't exists, 1 if exists and -1 if nothing happen
     */
    public int executeDeleteView(Statement stm, String viewName) throws SQLException {
        stm        = cursor.createStatement();
        String sql = "drop view if exists " + viewName;
        stm.executeUpdate(sql);
        return isViewCreated(viewName);
    }
    /**
     * ejecuta el contador de resultados
     * @param pstm: ejecutor de sentencias sql
     * @throws SQLException error al ejecutar
     * @return {@link ResultSet} de la ejecución
    */
    public ResultSet executeCountData(PreparedStatement pstm) throws SQLException {
        String sql    = "select id_pk from " + table;
        pstm          = cursor.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    /**
     * show table data
     * @param stm: ejecutor de la sentencias sql
     * @throws SQLException error al ejecutar
     * @return {@link ResultSet} de la ejecución
     */
    public ResultSet executeShowTableData(Statement stm) throws SQLException {
        String sql    = "show columns from " + this.table;
        stm           = cursor.createStatement();
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la lectura de datos
     * @param pstm: ejecutor de sentencias sql
     * @throws SQLException error al ejecutar
     * @return {@link ResultSet} de la ejecución
    */
    public ResultSet executeReadAll(PreparedStatement pstm) throws SQLException {
        String sql    = "select * from " + table;
        pstm          = cursor.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    /**
     * ejecuta la busqueda de 1 resultado por id
     * @param condition: id del registro buscado
     * @param type: tipo de condicion para la setencia sql
     * @param pstm: ejecutor de sentencia sql
     * @throws SQLException error de la ejecución
     * @return {@link ResultSet} de la ejecución
    */
    public ResultSet executePreparedFind(PreparedStatement pstm, String condition,
            String type) throws SQLException {
        String 
            sql         = queryBuilder.createPreparedFindQuery(condition, type),
            val         = queryUtil.getValueOfCondition(condition);
        pstm            = cursor.prepareStatement(sql);
        String[] fields = val.split(",");
        for(int i=0; i<fields.length; ++i) {
            pstm.setString((i+1), fields[i].trim());
        }
        ResultSet rst   = pstm.executeQuery();
        return rst;
    }
    /**
     * ejecuta la busqueda por nombre de columna
     * puede recibir 1 o varias columnas con el valor
     * @param condition: nombre y valor de columna: "nombre: test, email: test@test"
     * @param type: tipo de condicion para la setencia sql
     * @param stm: ejecutor de sentencia sql
     * @throws SQLException error de la ejecución
     * @return {@link ResultSet} de la ejecución
    */
    public ResultSet executeFindByColumnName(Statement stm, String condition,
            String type) throws SQLException {
        stm           = cursor.createStatement();
        String sql    = queryBuilder.createFindByColumnQuery(condition, type);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la busqueda dentro de una serie de datos o una sentencia sql tipo SELECT.
     * @param stm: ejecutor de sentencias sql
     * @param returnColumns: columns to return
     * @param conditionalColumns: columns for the conditional
     * @param condition: conditional
     * @param type: and or not
     * @throws SQLException: sql exception
     * @return {@link ResultSet} of the execution
     */
    public ResultSet executeFindIn(Statement stm, String returnColumns, String conditionalColumns,
            String condition, String type)  throws SQLException {
        stm           = cursor.createStatement();
        String sql    = queryBuilder.createFindInQuery(
                returnColumns,
                conditionalColumns,
                condition,
                type
        );
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
     * @return {@link ResultSet} of the execution
     */
    public ResultSet executeFindMinMax(Statement stm, String columns, String condition,
            String type) throws SQLException {
        stm           = cursor.createStatement();
        String sql    = queryBuilder.createFindMinMaxQuery(
                columns,
                condition,
                type
        );
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * busca los datos utilizando regex o patrones
     * @param stm: ejecutor de sentencias sql
     * @param pattern: regex o patron a buscar
     * @param condition: columnas a comparar con el patron
     * @param type: and or not
     * @throws SQLException: error de ejecución
     * @return the {@link ResultSet} of the execution
     */
    public ResultSet executeFindPattern(Statement stm, String pattern, String condition,
            String type) throws SQLException {
        stm           = cursor.createStatement();
        String sql    = queryBuilder.createFindPatternQuery(
                table,
                condition.trim().split(","),
                type
        );
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la busqueda por columna y valor y retorna el valor de las columnas seleccionadas
     * puede recibir 1 o varias columnas con el valor
     * puede retornar 1 o varios valores de la columna seleccionada
     * @param condition: nombre y valor de la columna: "nombre: test, email: test@test"
     * @param column: columna a conocer el valor: "id, rol"
     * @param type: tipo de condicion para la setencia sql
     * @param stm: ejecutor de sentencia sql
     * @throws SQLException error de la ejecución
     * @return {@link ResultSet} de la ejecución
    */
    public ResultSet executeGetValueOfColumnName(Statement stm, String condition, String columns,
            String type) throws SQLException {
        stm           = cursor.createStatement();
        String sql    = queryBuilder.createFindColumnValueQuery(
                condition,
                columns,
                type
        );
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta el registro de un nuevo elemento 
     * solo puede registrar 1 elemento a la vez
     * @param stm: ejecutor de sentencia sql
     * @param model: elemento a registrar
     * @throws SQLException error de la ejecución
     * @return row count or '0' when nothing is returned
    */
    public int executeInsertNewRegister(Statement stm, ModelMethods model) throws SQLException {
        String sql = queryBuilder.createInsertRegisterQuery(model);
        stm        = cursor.createStatement();
        int rst    = stm.executeUpdate(sql);
        return rst;
    }
    /**
     * execute the {@link PreparedStatement} for insert query
     * @param pstm: {@link PreparedStatement}
     * @param model: model to insert
     * @throws SQLException: error while trying to insert
     * @return row count or '0' when nothing is returned
     */
    public int executeInsertPreparedInsert(PreparedStatement pstm, ModelMethods model) throws SQLException {
        String sql = queryBuilder.createPreparedInsert(model);
        pstm = cursor.prepareStatement(sql);
        String[] fields = queryUtil.getModelType(model.getAllProperties(), true).split(",");
        for(int i=0; i<fields.length; ++i) {
            pstm.setString((i+1), fields[i].trim().replace("'", ""));
        }
        return pstm.executeUpdate();

    }
    /**
     * executes an insert query
     * @param stm: statement executor
     * @param options: column: value insert options
     * @throws SQLException: exception when trying to execute
     * @return row count or '0' when nothing is returned
     */
    public int executeInsertByColumns(Statement stm, String options)  throws SQLException {
        String 
            cOptions = options.trim(),
            sql = queryBuilder.createInsertByColumnQuery(cOptions);
        stm = cursor.createStatement();
        return stm.executeUpdate(sql);
    }
    /**
     * executes the insert into select statement
     * @param stm: statement object 
     * @param sourceT: source table
     * @param targetT: target table 
     * @param condition: condition for where clause
     * @param columns: columns to return
     * @param type: logic operator for where clause
     * @throws SQLException: error while trying to execute
     * @return the row count or 0 when nothing is returned
     */
    public int executeInsertIntoSelect(Statement stm, String sourceT, String targetT, String condition, 
            String columns, String type) throws SQLException {
        stm           = cursor.createStatement();
        String 
            sql       = "",
            selectSQL = "";
        if(columns == null || columns == "") {
            selectSQL = new QueryBuilder(sourceT).createFindByColumnQuery(condition, type);
            sql       = "insert into " + targetT + " " + selectSQL;
        } else {
            selectSQL = new QueryBuilder(sourceT).createFindColumnValueQuery(condition, columns, type);
            sql       = "insert into " + targetT + " (" + columns + ")" + selectSQL;
        }
        int rst       = stm.executeUpdate(sql);
        return rst;
    }
    /**
     * ejecuta la consulta tipo inner join
     * @param stm: ejecutor de sentencias sql
     * @param primary: model with the declaration of fk
     * @param foreign: foreign model of the relationship
     * @param foreignT: foreign model table name
     * @param condition: where condition clause
     * @param type: logic type for where clause
     * @throws SQLException error al ejecutar la sentencia sql
     * @return el {@link ResultSet} de la ejecución
     */
    public ResultSet executeInnerJoin(Statement stm, ModelMethods primary, ModelMethods foreign,
            String foreignT, String condition, String type) throws SQLException {
        String sql    = queryBuilder.createInnerJoinQuery(
                primary,
                foreign,
                foreignT,
                condition,
                type
        );
        stm           = cursor.createStatement();
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la modificación de 1 registro
     * puede modificar cualquier valor valido del registro
     * @param stm: ejecutor de la sentencia sql
     * @param model: model with the updated data
     * @param conditions: condiciones de query; "nombre: test, email: test"
     * @param type: tipo de condicion para la setencia sql
     * @throws SQLException error de la ejecución
     * @return the row count of the statement or '0' when return nothing 
    */
    public int executeUpdateRegister(Statement stm, ModelMethods model, String conditions, String type)
            throws SQLException {
        stm         = cursor.createStatement();
        String sql  = queryBuilder.createModifyRegisterQuery(
                model,
                conditions,
                type
        );
        int retorno = stm.executeUpdate(sql);
        return retorno;
    }
    /**
     * @param pstm: execute the sql sentences
     * @param model: model with the data to update
     * @param condition: where clause condition
     * @param type: logic type for where clause
     * @throws SQLException: error while trying to update
     * @return row count or '0' when nothing is returned
     */
    public int executePreparedUpdate(PreparedStatement pstm, ModelMethods model, String condition,
            String type) throws SQLException {
        String 
            sql = queryBuilder.createPreparedUpdate(model, condition, type),
            valCondition = queryUtil.getValueOfCondition(condition),
            modelValues  = queryUtil.getModelType(model.getAllProperties(), true);
        pstm = cursor.prepareStatement(sql);
        String[] 
            setValues = modelValues.split(","),
            conditionValues   = valCondition.split(",");
        // set the condition values
        List<String> vals = new ArrayList<>();
        for(String s: setValues) {
            vals.add(s.replace("'", ""));
        }
        for(String s: conditionValues) {
            vals.add(s);
        }
        // set the update values
        for(int i=0; i<vals.size(); ++i) {
            pstm.setString((i+1), vals.get(i));
        }
        return pstm.executeUpdate();
    }
    /**
     * ejecuta la eliminación de un registro
     * puede eliminar varios registros según las condition
     * @param stm: ejecutor de la sentencia sql
     * @param condition: opciones de condición: "nombre: test, email: test@test"
     * @param type: tipo de condicion para la setencia sql
     * @throws SQLException error al ejecutar
     * @return the row count or '0' when nothing is returned
    */
    public int executeEliminarRegistro(Statement stm, String condition, String type)
            throws SQLException {
        stm        = cursor.createStatement();
        String sql = queryBuilder.createDeleteRegisterQuery(condition, type);
        int result = stm.executeUpdate(sql);
        return result;
    }
}
