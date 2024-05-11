package Conexion.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

import Model.ModelBuilderMethods;
import Model.ModelMethods;

import Utils.Formats.ParamValue;

/**
 * class to use the database operations.
 */
public class QueryDAO<T> {
    /**
     * sql query | statement execution.
     */
    private QueryExecution queryExecution;
    /**
     * model builder class.
     */
    private ModelBuilderMethods<T> modelBuilderMethods;
    /**
     * {@link Connection} instance
     */
    private Connection cursor;
    /**
     * {@link java.lang.reflect.Constructor}
     * @param myConnector: {@link Connection} instance
     * @param builder: model builder instance
     */
    public QueryDAO(String tableName, Connection cursor, ModelBuilderMethods<T> builder) {
        this.cursor              = cursor;
        queryExecution      = new QueryExecution(tableName, cursor);
        modelBuilderMethods = builder;
    }
    /**
     * {@link Connection} cursor.
     * @return {@link Connection}
     */
    public Connection getConnection() {
        return cursor;
    }
    /**
     * disconnect the cursor connection
     * <br> pre: </br> the connection is != null
     */
    public void disconnectConnection() {
        try {
            cursor.close();
            System.out.println("[ INFO ]: connection has been closed");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * uses the given string to create a statement.
     * <br> pre: </br> used only for SELECT statements
     * @param sql: row query statement
     * @return value of column name
     */
    public String anyQuery(String sql) {
        StringBuffer result = new StringBuffer();
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst     = queryExecution.executeMyQuery(stm, sql);
            int len = rst.getMetaData().getColumnCount();
            while(rst.next()) {
                for(int i=1; i<= len; i++) {
                    result.append(rst.getMetaData().getColumnName(i));
                    result.append(": ");
                    result.append(rst.getString(i));
                    result.append(",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
            if(!result.isEmpty()) {
                System.out.println("[ INFO ]: query successfully completed");
            }
        }
        if(result.length()-1 > 0) {
            result = new StringBuffer(result.substring(0, result.length()-1));
        }
        return result.toString();
    }
    /**
     * uses the given string as sql statement.
     * <br> pre: </br> used only for INSERT UPDATE DELETE statements.
     * @param sql: row query statement
     * @return true if row count is > 0 otherwise false
     */
    public boolean anyExecution(String sql) {
        Statement stm      = null;
        boolean isExecuted = false;
        try {
            int rst = queryExecution.executeMyUpdateQuery(stm, sql);
            if(rst > 0) {
                System.out.println("[ INFO ]: anyQuery successfully completed");
                isExecuted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isExecuted;
    }
    /**
     * create a view from selection.
     * <br> sample: </br> create view name_view as select operation from table;
     * @param viewName: name of the view to create
     * @param condition: condition for selection
     * @param columns: the columns to select for the view
     * @return true if its created, otherwise false
     */
    public boolean createView(String viewName, ParamValue condition, String columns) {
        boolean isCreate  = false;
        Statement stm     = null;
        try {
            int rst = queryExecution.executeCreateView(
                    stm,
                    viewName,
                    condition,
                    columns
            );
            if(rst == 1) {
                System.out.println("[ INFO ]: anyExecution successfully completed");
                isCreate = true;
            } else if(rst == -1) {
                throw new Exception(
                        "[ ERROR ]: while trying to create a view"
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isCreate; 
    }
    /**
     * delete views from database.
     * @param viewName: the view to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteView(String viewName) {
        boolean isDeleted = false;
        Statement stm     = null;
        try {
            int rst = queryExecution.executeDeleteView(stm, viewName);
            if(rst == 0) {
                System.out.println("[ INFO ]: delete view completed");
                isDeleted = true;
            } else if(rst == -1) {
                throw new Exception(
                        "[ ERROR ]: while trying to delete a view"
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isDeleted;
    }
    /**
     * count table data.
     * @return data count.
     */
    public int countData() {
        int count              = 0;
        PreparedStatement pstm = null;
        ResultSet rst          = null;
        try{
            rst = queryExecution.executeCountData(pstm);
            while(rst.next()) {
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }

            if(pstm != null) {
                try {
                    pstm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pstm = null;
            }
            if(count > 0) {
                System.out.println("[ INFO ]: count data completed");
            }
        }
        return count;
    }
    /**
     * creates a list of model from table data.
     * @param params: order: ASC | DESC, group: column to use for GROUP BY and limit
     * @return a list of models.
     */
    public ArrayList<T> readAll(ParamValue params) {
        PreparedStatement pstm  = null;
        ResultSet rst           = null;
        ArrayList<T> results = new ArrayList<T>();
        try {
            rst        = queryExecution.executeReadAll(pstm, params);
            int length = rst.getMetaData().getColumnCount();
            while(rst.next()) {
                results.add(modelBuilderMethods.createFromRST(rst, length));
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(pstm != null) {
                try {
                    pstm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pstm = null;
            }
            if(results.size() > 0) {
                System.out.println("[ INFO ]: list data completed");
            }
        }
        return results;
    }

    /**
     * search using {@link PreparedStatement}.
     * @param condition: where clause condition
     * @return the searched object
     */
    public T preparedFind(ParamValue condition) {
        T searched              = null;
        PreparedStatement pstm = null;
        ResultSet rst          = null;
        try {
            rst        = queryExecution.executePreparedFind(pstm, condition);
            int length = rst.getMetaData().getColumnCount();
            while(rst.next()) {
                searched = modelBuilderMethods.createFromRST(rst, length);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(pstm != null) {
                try {
                    pstm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pstm = null;
            }
            if(searched != null) {
                System.out.println("[ INFO ]: preparedFind completed");
            }
        }
        return searched;
    }
    /**
     * search using column name.
     * @param condition: where clause condition
     * @return the search model instance 
     */
    public T findByColumnName(ParamValue condition) {
        T searched     = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst        = queryExecution.executeFindByColumnName(stm, condition);
            int length = rst.getMetaData().getColumnCount();
            while(rst.next()) {
                searched = modelBuilderMethods.createFromRST(rst, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
            if(searched != null) {
                System.out.println("[ INFO ]: findByColumnName completed");
            }
        }
        return searched;
    }
    /**
     * search using IN operation.
     * @param returnColumns: columns to return in selection
     * @param conditionalColumns: column to search
     * @param condition: condition for the search
     * @param type: logic type for where clause
     * @return the object of the generic type
     */
    public String findIn(String returnColumns, String conditionalColumns, String condition, String type) {
        StringBuffer searched = new StringBuffer();
        Statement stm  = null;
        ResultSet rst  = null;
        try {
            int len = 0;
            rst     = queryExecution.executeFindIn(
                    stm,
                    returnColumns,
                    conditionalColumns,
                    condition,
                    type
            );
            if(returnColumns == null || returnColumns.isEmpty()) {
                len = rst.getMetaData().getColumnCount();
            } else if(returnColumns != null || !returnColumns.isEmpty()) {
                len = returnColumns.split(",").length;
            }
            while(rst.next()) {
                for(int i=1; i<=len; ++i) {
                    searched.append(rst.getMetaData().getColumnName(i));
                    searched.append(": ");
                    searched.append(rst.getString(i));
                    searched.append(",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
            if(searched != null) {
                System.out.println("[ INFO ]: findIn completed");
            }
        }
        if(searched.length()-1 > 0) {
            searched = new StringBuffer(searched.substring(0, searched.length()-1));
        }
        return searched.toString();
    }
    /**
     * search using regex or pattern.
     * @param pattern: regex o pattern to search
     * @param condition: where clause condition
     * @param type: and or not
     * @return an object of the generic type
     */
    public T findPattern(String pattern, String condition, String type) {
        T searched     = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst        = queryExecution.executeFindPattern(
                    stm,
                    pattern,
                    condition,
                    type
            );
            int length = rst.getMetaData().getColumnCount();
            while(rst.next()) {
                searched = modelBuilderMethods.createFromRST(rst, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
            if(searched != null) {
                System.out.println("[ INFO ]: findPattern completed");
            }
        }
        return searched;
    }
    /**
     * get the min or max of each of the given columns.
     * @param columns: 'min: name, max: password'
     * @param condition: condition for where clause
     * @return the min or max object data
     */
    public String getMinMax(ParamValue columns, ParamValue condition) {
        StringBuffer searched = new StringBuffer();
        Statement stm  = null;
        ResultSet rst  = null;
        try {
            rst     = queryExecution.executeFindMinMax(
                    stm,
                    columns,
                    condition
            );
            int len = columns.getColumns().length;
            while(rst.next()) {
                for(int i=1; i<=len; ++i) {
                    searched.append(rst.getMetaData().getColumnName(i));
                    searched.append(": ");
                    searched.append(rst.getString(i));
                    searched.append(",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
            if(searched != null) {
                System.out.println("[ INFO ]: getMinMax completed");
            }
        }
        return searched.substring(0, searched.length()-1);
    }
    /**
     * search for the value of the given column. 
     * @param condition: column name
     * @param column: columns to search
     * @return value of column name
     */
    public String getValueOfColumnName(ParamValue condition, String columns) {
        StringBuffer result = new StringBuffer();
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst     = queryExecution.executeGetValueOfColumnName(
                    stm,
                    condition,
                    columns
            );
            int len = 0;
            if(columns == null || columns.isEmpty() == true) {
                len = rst.getMetaData().getColumnCount();
            }
            else if(columns != null || columns.isEmpty() == false) {
                len = columns.split(",").length;
            }
            while(rst.next()) {
                for(int i=1; i<= len; ++i) {
                    result.append(rst.getMetaData().getColumnName(i));
                    result.append(": ");
                    result.append(rst.getString(i));
                    result.append(",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
            if(!result.isEmpty()) {
                System.out.println("[ INFO ]: getValueOfColumnName completed");
            }
        }
        if(result.length()-1 > 0) {
            result = new StringBuffer(result.substring(0, result.length()-1));
        }
        return result.toString();
    }
    /**
     * inner join of 2 tables.
     * @param primary: model with the fk field declaration
     * @param foreign: model with the pk field for the relationship 
     * @param foreignT: foreign table name
     * @param condition: condition for where clause
     * @return a string with the format -> source_name: source_password, reference_name: reference_password
     */
    public String innerJoin(ModelMethods primary, ModelMethods foreign, String foreignT) {
        StringBuffer result = new StringBuffer();
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.executeInnerJoin(
                    stm,
                    primary,
                    foreign,
                    foreignT
            );
            int len = rst.getMetaData().getColumnCount();
            while(rst.next()) {
                for(int i=1; i<len; ++i) {
                    result.append(rst.getMetaData().getTableName(i));
                    result.append("_");
                    result.append(rst.getMetaData().getColumnName(i));
                    result.append(": ");
                    result.append(rst.getString(i));
                    result.append(",");
                }
            }

        } catch(Exception e) {
            System.err.println(e);
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                } 
                stm = null;
            }
            if(!result.isEmpty()) {
                System.out.println("[ INFO ]: innerJoin completed");
            }
        }
        if(result.length()-1 > 0) {
            result = new StringBuffer(result.substring(0, result.length()-1));
        }
        return result.toString();
    }
    /**
     * inserts a new value to the table.
     * @param model: model with the data to insert
     * @param condition: where clause condition
     * @return true if its inserted, false otherwise
     */
    public boolean insertNewRegister(ModelMethods model, ParamValue condition) {
        boolean isInserted  = false;
        Statement stm       = null;
        try {
            if(model == null) {
                throw new Exception(
                        "[ ERROR ]: model cannot be [ NULL ]"
                );
            }
            cursor.beginRequest();
            T searched = findByColumnName(condition);
            if(searched == null) {
                cursor.endRequest();
                int rst = queryExecution.executeInsertNewRegister(stm, model);
                if(rst > 0){
                    isInserted = true;
                }
            } else {
                isInserted = false;
                throw new Exception(
                        "[ ERROR ]: find statement must be [ NULL ]"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
            if(isInserted) {
                System.out.println("[ INFO ]: insertNewRegister completed");
            }
        }
        return isInserted;
    }
    /**
     * insert register using {@link PreparedStatement}
     * @param model: model to insert
     * @param condition: where clause condition
     * @return true if its inserted, false otherwise
     */
    public boolean preparedInsert(ModelMethods model, ParamValue condition) {
        boolean isInserted = false;
        PreparedStatement pstm = null;
        try {
            if(model == null) {
                throw new Exception(
                        "[ ERROR ]: model cannot be [ NULL ]"
                );
            }
            T searched = findByColumnName(condition);
            if(searched == null) {
                int rst = queryExecution.executeInsertPreparedInsert(pstm, model);
                if(rst >0 ) {
                    isInserted = true;
                }
            } else {
                throw new Exception(
                        "[ ERROR ]: find statement must be [ NULL ]"
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(pstm != null) {
                try {
                    pstm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                pstm = null;
            }
            if(isInserted) {
                System.out.println("[ INFO ]: preparedInsert completed");
            }
        }
        return isInserted;
    }
    /**
     * insert register using options like {@link String} sentence
     * @param options: the data to insert
     * @param condition: where clause condition
     * @return true if its inserted, false otherwise
     */
    public boolean insertByColumns(ParamValue options, ParamValue condition) {
        Statement stm = null;
        boolean isInserted = false;
        try {
            T searched = findByColumnName(condition);
            if(searched == null) {
                int rst = queryExecution.executeInsertByColumns(stm, options);
                if(rst > 0) {
                    isInserted = true;
                }
            } else {
                isInserted = false;
                throw new Exception(
                        "[ ERROR ]: find statement must be [ NULL ]"
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
            if(isInserted) {
                System.out.println("[ INFO ]: insertByColumns completed");
            }
        }
        return isInserted;
    }
    /**
     * copy the columns from one table to another.
     * <br> pre: </br> you can copy all o some columns of the table
     * @param sourceT: source table
     * @param targetT: target table 
     * @param condition: condition for where clause
     * @param columns: columns to return
     * @return true if the {@link sourceT} is copied to {@link targetT}, false otherwise
     */
    public boolean insertIntoSelect(String sourceT, String targetT, ParamValue condition, String columns) {
        boolean isInserted = false;
        Statement stm      = null;
        try {
            int rst = queryExecution.executeInsertIntoSelect(
                    stm,
                    sourceT,
                    targetT,
                    condition,
                    columns
            );
            if(rst > 0) {
                isInserted = true;
            } else {
                isInserted = false;
                throw new Exception(
                        "[ ERROR ]: while trying to insert a new register"
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
            if(isInserted) {
                System.out.println("[ INFO ]: insertIntoSelect completed");
            }
        }
        return isInserted;
    }
    /**
     * update database data.
     * @param model: model with the updated data
     * @param conditions: where clause condition
     * @return true if its updated, false otherwise
     **/
    public boolean updateRegister(ModelMethods model, ParamValue conditions) {
        boolean isUpdated = false;
        Statement stm      = null;
        try {
            if(model == null) {
                throw new Exception(
                        "[ ERROR ]: model cannot be [ NULL ]"
                );
            }
            cursor.beginRequest();
            T searched = findByColumnName(conditions);
            if(searched != null) {
                cursor.endRequest();
                int result = queryExecution.executeUpdateRegister(
                        stm,
                        model,
                        conditions
                );
                if(result > 0) {
                    isUpdated = true;
                }
            } else {
                isUpdated = false;
                throw new Exception(
                        "[ ERROR ]: find statement cannot [ NULL ]"
                );
            }

        } catch( Exception e) {
            e.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();

                } catch( Exception e2) {
                    System.err.println(e2.getMessage());
                }
                stm = null;
            }
            if(isUpdated) {
                System.out.println("[ INFO ]: update completed");
            }
        }
        return isUpdated;
    }
    /**
     * prepared update version.
     * @param model: the model with the data to update
     * @param condition: where clause condition
     * @return true if the data is updated, false otherwise
     */
    public boolean preparedUpdate(ModelMethods model, ParamValue condition) {
        boolean isUpdated = false;
        PreparedStatement pstm = null;
        try {
            if(model == null) {
                throw new Exception(
                        "[ ERROR ]: model cannot be [ NULL ]"
                );
            }
            cursor.beginRequest();
            T searched = findByColumnName(condition);
            if(searched != null) {
                cursor.endRequest();
                int result = queryExecution.executePreparedUpdate(
                        pstm,
                        model,
                        condition
                );
                if(result > 0) {
                    isUpdated = true;
                }
            } else {
                isUpdated = false;
                throw new Exception(
                        "[ ERROR ]: find statement cannot [ NULL ]"
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(pstm != null) {
                try {
                    pstm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                pstm = null;
            }
            if(isUpdated) {
                System.out.println("[ INFO ]: preparedUpdated completed");
            }
        }
        return isUpdated;
    }
    /**
     * delete a table register.
     * @param condition: where clause condition
     * @return true if its deleted, false otherwise
     * */
    public boolean deleteRegister(ParamValue condition) {
        boolean isDeleted = false;
        Statement stm    = null;
        try {
            cursor.beginRequest();
            T searched = findByColumnName(condition);
            if(searched != null) {
                cursor.endRequest();
                int rst = queryExecution.executeDeleteRegister(
                        stm,
                        condition
                );
                if(rst > 0) {
                    isDeleted = true;
                }
            } else {
                isDeleted = false;
                throw new Exception(
                        "[ ERROR ]: find statement cannot [ NULL ]"
                );
            }
        } catch (Exception e ) {

            System.err.println(e);
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch ( Exception e ) {
                    System.err.println(e);
                }
                stm = null;
            }
            if(isDeleted) {
                System.out.println("[ INFO ]: delete completed");
            }
        }
        return isDeleted;
    }
    /**
     * delete operation using {@link PreparedStatement}
     * @param condition: where clause condition
     * @return true if the data is deleted, false otherwise
     */
    public boolean preparedDelete(ParamValue condition) {
        boolean isDeleted = false;
        PreparedStatement pstm = null;
        try {
            T searched = findByColumnName(condition);
            if(searched != null) {
                int rst = queryExecution.executePreparedDelete(
                        pstm,
                        condition
                );
                if(rst > 0) {
                    isDeleted = true;
                }
            } else {
                isDeleted = false;
                throw new Exception(
                        "[ ERROR ]: find statement cannot [ NULL ]"
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(pstm != null) {
                try {
                    pstm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                pstm = null;
            }
            if(isDeleted) {
                System.out.println("[ INFO ]: preparedDelete completed");
            }
        }
        return isDeleted;
    }
}

