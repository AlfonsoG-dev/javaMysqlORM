package Conexion.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.ModelMethods;

import Utils.Builder.QueryBuilder;
import Utils.Query.QueryUtils;
import Utils.Model.ModelUtils;
import Utils.Formats.ParamValue;

/**
 * class for sql query execution
 * */
public class QueryExecution {
    /**
     * connection instance for sql statement execution
     * */
    private Connection cursor;
    /**
     * table name
     * */
    private String table;
    /**
     * sql query builder
     * */
    private QueryBuilder queryBuilder;
    /**
     * sql query utils or helper
     */
    private QueryUtils queryUtil;
    /**
     * model utils or helper
     */
    private ModelUtils modelUtils;

    //constructor

    /**
     * {@link java.lang.reflect.Constructor}
     * @param tbName: table name
     * @param miConector: {@link Connection} instance
     */
    public QueryExecution(String tbName, Connection miConector) {
        table        = tbName;
        queryBuilder = new QueryBuilder(tbName);
        queryUtil    = new QueryUtils();
        modelUtils   = new ModelUtils();
        cursor       = miConector;
    }
    /**
     * check if the view is created or not.
     * <br> pre: </br> the view is aready created
     * @param viewName: the name of the view to check the creation
     * @throws SQLException: error while tring to check view creation
     * @return 1 if is created, 0 if not and -1 if nothing happens
     */
    protected int isViewCreated(String viewName) throws SQLException {
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
     * @return {@link ResultSet}
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
    public int executeCreateView(Statement stm, String viewName, ParamValue condition, String columns,
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
     * execute the sql count query
     * @param pstm: execute instance
     * @throws SQLException: error while trying to execute the statement
     * @return {@link ResultSet}
    */
    public ResultSet executeCountData(PreparedStatement pstm) throws SQLException {
        String sql    = "select id_pk from " + table;
        pstm          = cursor.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    /**
     * execute sql show table data query
     * @param stm: execute instance
     * @throws SQLException: error while trying to execute the statement
     * @return {@link ResultSet}
     */
    public ResultSet executeShowTableData(Statement stm) throws SQLException {
        String sql    = "show columns from " + this.table;
        stm           = cursor.createStatement();
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * execute sql select query
     * @param pstm: execute instance
     * @throws SQLException: error while trying to execute the statement
     * @return {@link ResultSet}
    */
    public ResultSet executeReadAll(PreparedStatement pstm, String order, String group, int limit) throws SQLException {
        String sql    = queryBuilder.readAllQuery(order, group, limit);
        pstm          = cursor.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    /**
     * ejecuta la busqueda de 1 resultado por id
     * execute sql select using {@link PreparedStatement} 
     * @param condition: where clause condition
     * @param type: logiv type for where clause
     * @param pstm: execute instance
     * @throws SQLException: error while trying to execute the statement
     * @return {@link ResultSet}
    */
    public ResultSet executePreparedFind(PreparedStatement pstm, ParamValue condition,
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
     * execute sql select by column name query 
     * @param condition: where clause condition
     * @param type: logic type for where clause
     * @param stm: execute instance
     * @throws SQLException: error while trying to execute the statement
     * @return {@link ResultSet}
    */
    public ResultSet executeFindByColumnName(Statement stm, ParamValue condition,
            String type) throws SQLException {
        stm           = cursor.createStatement();
        String sql    = queryBuilder.createFindByColumnQuery(condition, type);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * execute select IN query.
     * @param stm: execute instance
     * @param returnColumns: searched column
     * @param conditionalColumns: conditional columns for where clause
     * @param condition: condition for where clause
     * @param type: logic type for where clause
     * @throws SQLException: error while trying to execute the statement
     * @return {@link ResultSet}
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
     * execute sql select min max query
     * @param stm: execute instance
     * @param columns: 'min: nombre, max: password'
     * @param condition: condition for where clause
     * @param type: and or not
     * @throws SQLException: error while trying to execute the statement
     * @return {@link ResultSet}
     */
    public ResultSet executeFindMinMax(Statement stm, ParamValue columns, ParamValue condition,
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
     * execute sql select pattern.
     * @param stm: execute instance
     * @param pattern: regex or pattern to search
     * @param condition: condition for where clause
     * @param type: logic type for where clause
     * @throws SQLException: error while trying to execute the statement
     * @return {@link ResultSet}
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
     * execute sql select value of column query
     * @param stm: execute instance
     * @param condition: "nombre: test, email: test@test"
     * @param column: searched column
     * @param type: logic type for where clause
     * @throws SQLException: error while trying to execute the statement
     * @return {@link ResultSet}
    */
    public ResultSet executeGetValueOfColumnName(Statement stm, ParamValue condition, String columns,
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
     * execute sql insert query.
     * @param stm: execute instance
     * @param model: model data
     * @throws SQLException: error while trying to execute the statement
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
        String[] fields = modelUtils.getModelTypes(model.getAllProperties(), true).split(",");
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
    public int executeInsertByColumns(Statement stm, ParamValue options)  throws SQLException {
        String 
            cOptions = options.getCombination().trim(),
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
    public int executeInsertIntoSelect(Statement stm, String sourceT, String targetT, ParamValue condition, 
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
     * execute sql inner join query.
     * @param stm: execute instance
     * @param primary: invoke model with fk of foreign pk
     * @param foreign: foreign model with pk of primary fk
     * @param foreignT: foreign table name
     * @param condition: where condition clause
     * @param type: logic type for where clause
     * @throws SQLException: error while trying to execute the statement 
     * @return {@link ResultSet}
     */
    public ResultSet executeInnerJoin(Statement stm, ModelMethods primary, ModelMethods foreign,
            String foreignT, ParamValue condition, String type) throws SQLException {
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
     * execute sql update query.
     * @param stm: execute instance
     * @param model: model with the updated data
     * @param conditions: where clause condition
     * @param type: logic type for where clause
     * @throws SQLException: error while trying to execute the statement
     * @return the row count of the statement or '0' when return nothing 
    */
    public int executeUpdateRegister(Statement stm, ModelMethods model, ParamValue conditions, String type)
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
     * @param pstm: execute instance
     * @param model: model with the data to update
     * @param condition: where clause condition
     * @param type: logic type for where clause
     * @throws SQLException: error while trying to update
     * @return row count or '0' when nothing is returned
     */
    public int executePreparedUpdate(PreparedStatement pstm, ModelMethods model, ParamValue condition,
            String type) throws SQLException {
        String 
            sql = queryBuilder.createPreparedUpdate(model, condition, type),
            valCondition = queryUtil.getValueOfCondition(condition),
            modelValues  = modelUtils.getModelTypes(model.getAllProperties(), true);
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
     * execute sql delete query.
     * @param stm: execute instance
     * @param condition: where clause condition
     * @param type: logic type for where clause
     * @throws SQLException: error while trying to execute the statement
     * @return the row count or '0' when nothing is returned
    */
    public int executeEliminarRegistro(Statement stm, ParamValue condition, String type)
            throws SQLException {
        stm        = cursor.createStatement();
        String sql = queryBuilder.createDeleteRegisterQuery(condition, type);
        int result = stm.executeUpdate(sql);
        return result;
    }
    /**
     * execute sql delete query.
     * @param condition: where clause condition
     * @param type: logic type for where clause
     * @return row count or '0' when nothing is returned
     */
    public int executePreparedDelete(PreparedStatement pstm, ParamValue condition, String type) 
        throws SQLException {

        String 
            values = queryUtil.getValueOfCondition(condition),
            sql = queryBuilder.createPreparedDeleteQuery(condition, type);
        pstm = cursor.prepareStatement(sql);
        String[] fields = values.split(",");
        for(int i=0; i<fields.length; ++i) {
            pstm.setString((i+1), fields[i].trim());
        }
        return pstm.executeUpdate();
    }
}
