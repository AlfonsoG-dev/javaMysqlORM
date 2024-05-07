package Utils.Builder;

import Model.ModelMethods;

import Utils.Query.QueryUtils;
import Utils.Formats.ParamValue;
import Utils.Model.ModelUtils;

/**
 * record class with helper methods for building sql queries
 */
public class QueryBuilder {
    /**
     * query utils for sql statement
     */
    private static QueryUtils queryUtil;
    /**
     * model utils for sql statements
     */
    private ModelUtils modelUtils;
    /**
     * table name
     */
    private String tbName;
    /**
     * {@link java.lang.reflect.Constructor}
     * @param nTableName: table name
     */
    public QueryBuilder(String nTableName) {
        queryUtil = new QueryUtils();
        modelUtils = new ModelUtils();
        tbName    = nTableName;
    }
    /**
     * validate if the sql is orderer with ASC | DESC 
     * @param validate: the sql query to validate
     * @return the validated query
     */
    protected String validationOrder(String validate) {
        String v = "";
        if(validate.toUpperCase().contains("ASC") || validate.toUpperCase().contains("DESC")) {
            v = validate;
        }
        return v;
    }
    /**
     * create the read all data query.
     * @param order: name: ASC | DESC
     * @param group:  name: ASC | DESC
     * @param limit: the number of objects to get
     * @return the read all data query
     */
    public String readAllQuery(String order, String group, int limit) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ");
        sql.append(tbName);
        if(!order.isEmpty()) {
            String[] separate = order.trim().split(":");
            sql.append(" ORDER BY ");
            sql.append(separate[0]);
            sql.append(" ");
            sql.append(validationOrder(separate[1]));
        }
        if(!group.isEmpty()) {
            sql.append(" GROUP BY ");
            String[] separate = group.trim().split(":");
            sql.append(separate[0]);
            sql.append(" ");
            sql.append(validationOrder(separate[1]));
        } 
        if(limit > 0) {
            sql.append(" LIMIT ");
            sql.append(limit);
        }
        return sql.toString();
    }
    /**
     * create the sql query using prepared statements 
     * @param condition: where clause condition
     * @param type: logic type for where clause
     * @return the sql query
     */
    public String createPreparedFindQuery(ParamValue condition) {
        StringBuffer
            sql = new StringBuffer(),
            whereClause = new StringBuffer();
        whereClause.append(" WHERE ");
        whereClause.append(queryUtil.getPrepareConditional(condition));
        sql.append("SELECT *");
        sql.append(" FROM ");
        sql.append(tbName);
        sql.append(whereClause);
        return sql.toString();
    }
    /**
     * create a sql query to select using columns
     * @param condition: where clause condition
     * @param type: logic type for where clause
     * @return a sql query
     */
    public String createFindByColumnQuery(ParamValue condition) {
        StringBuffer
            sql = new StringBuffer(),
            whereClause = new StringBuffer();
        whereClause.append(" WHERE ");
        whereClause.append(queryUtil.getNormalConditional(condition));
        sql.append("SELECT *");
        sql.append(" FROM ");
        sql.append(tbName);
        sql.append(whereClause);
        return sql.toString();
    }
    /**
     * create a sql query to select using column value
     * @param condition: where clause condition
     * @param column: the column to select
     * @param type: logic type for where clause
     * @return the sql query
     */
    public String createFindColumnValueQuery(ParamValue condition, String columns) {
        StringBuffer 
            sql = new StringBuffer(),
            whereClause = new StringBuffer();
        whereClause.append(" WHERE ");
        whereClause.append(queryUtil.getNormalConditional(condition));
        sql.append("SELECT ");
        sql.append(columns);
        sql.append(" FROM ");
        sql.append(tbName);
        sql.append(whereClause);
        return sql.toString();
    }
    /**
     * create a sql query to select using IN operator
     * @param returnColumns: columns to return
     * @param conditionalColumns: columns to search in condition
     * @param condition: type of condition in ('', '')
     * @return the sql query
     */
    public String createFindInQuery(String returnColumns, String conditionalColumns, String condition,
            String type) {
        String 
            sql         = "",
            inCondition = "";
        if(returnColumns == null) {
            if(condition == null || condition.isEmpty() && (conditionalColumns == null ||
                        conditionalColumns.isEmpty())) {
                sql = "SELECT * FROM " + tbName;
            } else {
                inCondition = queryUtil.getInConditional(
                        conditionalColumns,
                        condition,
                        type
                );
                sql = "SELECT * FROM " + tbName + " WHERE " + inCondition;
            }
        } else if(returnColumns != null && !returnColumns.isEmpty()) {
            if(condition == null || condition.isEmpty() && (conditionalColumns == null ||
                        conditionalColumns.isEmpty())) {
                sql = "SELECT " + returnColumns + " FROM " + tbName;
            } else {
                inCondition = queryUtil.getInConditional(
                        conditionalColumns,
                        condition,
                        type
                );
                sql = "SELECT " + returnColumns + " FROM " + tbName + " WHERE " + inCondition;
            }
        }
        return sql;
    }
    /**
     * create a sql query to select using patterns
     * @param pattern: the search pattern
     * @param conditions: the where clause condition
     * @param type: logic type for where clause
     * @return the sql query
     */
    public String createFindPatternQuery(String pattern, String[] conditions, String type) {
        String whereClause = "";
        if(pattern != null && (conditions != null && conditions.length > 0)) {
            whereClause = " WHERE " + queryUtil.getPatternCondition(
                    pattern,
                    conditions,
                    type
            );
        }
        return "SELECT * FROM " + tbName + whereClause;
    }
    /**
     * create a sql query to find using min max.
     * @param columns: 'min: name, max: password'
     * @param condition: condition for where clause
     * @param type: and or not
     * @return the sql query
     */
    public String createFindMinMaxQuery(ParamValue columns, ParamValue condition) {
        String 
            whereClause    = "",
            minMaxSelection = queryUtil.getMinMaxSelection(columns.getCombination());
        whereClause = " WHERE " + queryUtil.getNormalConditional(condition);
        return "SELECT " + minMaxSelection + " FROM " + tbName + whereClause;
    }
    /**
     * allows to build the insert statement query without PK and including FK
     * @param model: model with table creation data 
     * @return an Array of strings with the types and columns for the insert statement
     */
    protected String[] insertData(String modelData) {
        String[] 
            build   = new String[2],
            types   = modelUtils.getModelTypes(modelData, true).split(","),
            columns = modelUtils.getModelColumns(modelData, true).split(",");
        StringBuffer 
            type   = new StringBuffer(),
            column = new StringBuffer();
        for(int i=0; i<types.length; ++i) {
            String d = types[i];
            if(!d.equals("''")) {
                type.append(d + ",");
            }
        }
        for(int i=0; i<columns.length; ++i) {
            column.append(columns[i] + ",");
        }
        build[0] = type.toString();
        build[1] = column.toString();
        return build;
    }
    /**
     * create the sql query to insert statement
     * @param model: model with the data to insert
     * @return the sql query
     */
    public String createInsertRegisterQuery(ModelMethods model) {
        String 
            types     = insertData(model.getAllProperties())[0],
            columns   = insertData(model.getAllProperties())[1],
            cTypes    = types.substring(0, types.length()-1),
            cColumns  = queryUtil.cleanValues(columns, 1);

        return "INSERT INTO " + tbName + " (" + cColumns +") VALUES (" + cTypes + ")";
    }
    /**
     * replace the column name for question mark
     * @param columns: model columns
     * @return a {@link String} with the question marks
     */
    protected String replaceColumForQuestionMark(String columns) {
        StringBuffer b = new StringBuffer();
        String[] separate = columns.split(",");
        for(int i=0; i<separate.length; ++i) {
            b.append("?,");
        }
        return b.substring(0, b.length()-1);
    }
    /**
     * create the sql query to insert using {@link PreparedStatement}
     * @param model: model with the data to insert
     * @param the sql insert statement
     * @return the sql query
     */
    public String createPreparedInsert(ModelMethods model) {
        String 
            columns   = insertData(model.getAllProperties())[1],
            cColumns  = columns.substring(0, columns.length()-1),
            questionMark = replaceColumForQuestionMark(cColumns);
        return "INSERT INTO " + tbName + " (" + cColumns + ") VALUES (" + questionMark + ")";
    }
    /**
     * creates an insert statement using string options.
     * <br> pre: </br> format column_name: value_type, 
     * @param options: the string options to use
     * @return an insert statement
     */
    public String createInsertByColumnQuery(String options) {
        String 
            types    = insertData(options)[0],
            columns  = insertData(options)[1],
            cTypes   = types.substring(0, types.length()-1),
            cColumns = columns.substring(0, columns.length()-1);

        return "INSERT INTO " + tbName + " (" + cColumns + ") VALUES (" + cTypes + ")";
    }
    /**
     * creates the sql query for inner join
     * @param primary: model with the declaration of fk
     * @param foreign: foreign model of the relationship
     * @param foreignT: foreign table name
     * @param condition: condition for where clause
     * @param type: logic type for where clause
     * @return the sql query
     */
    public String createInnerJoinQuery(ModelMethods primary, ModelMethods foreign, String foreignT,
            ParamValue condition) {
        StringBuffer sql = new StringBuffer();
        String 
            foreignV   = queryUtil.assignTableNameToColumns(
                foreign.getAllProperties(),
                foreignT
            ),
            primaryV = queryUtil.assignTableNameToColumns(
                primary.getAllProperties(),
                this.tbName
            ),
            keys = queryUtil.getInnerJoinConditional(
                primary.getAllProperties(),
                foreign.getAllProperties(),
                this.tbName,
                foreignT
            ),
            conditional = queryUtil.getNormalConditional(condition);

        sql.append("SELECT ");
        sql.append(primaryV);
        sql.append(", ");
        sql.append(foreignV);
        sql.append(" FROM ");
        sql.append(tbName);
        sql.append(" INNER JOIN ");
        sql.append(foreignT);
        sql.append(" ON ");
        sql.append(keys);
        sql.append(" WHERE ");
        sql.append(conditional);
        return sql.toString();
    }
    /** 
     * creates the sql query to update data.
     * @param model: model with the data to insert
     * @param condition: conditional for where clause
     * @param type: logic type for where clause
     * @return the sql query
     */
    public String createModifyRegisterQuery(ModelMethods model, ParamValue condition) {
        String 
            whereClause     = "",
            cleanKeyValue = queryUtil.getAssignModelValues(model.getAllProperties());
        whereClause = " WHERE " + queryUtil.getNormalConditional(condition);
        return "UPDATE " + tbName + " SET " +  cleanKeyValue + whereClause;
   }
   /**
    * creates the sql query to update data using prepared statement
    * @param model: model with the data
    * @param conditional: where clause condition
    * @param type: logic type for where clause
    * @return the sql query
    */
   public String createPreparedUpdate(ModelMethods model, ParamValue condition) {
       String 
           pValues = queryUtil.getPreparedUpdateValues(model.getAllProperties()),
           pConditional = queryUtil.getPrepareConditional(condition);
       return "UPDATE " + tbName + " SET " + pValues + " WHERE " + pConditional;
   }
   /**
    * creates the sql query to delete data.
    * @param condition: where clause condition
    * @param type: logic type for where clause
    * @return the sql query
    **/
   public String createDeleteRegisterQuery(ParamValue condition) {
       String whereClause = "";
       if(condition != null && !condition.getCombination().isEmpty()) {
           whereClause = queryUtil.getNormalConditional(condition);
       }
       return "DELETE FROM " + tbName + " WHERE " + whereClause;
   }
   /**
    * creates the sql query to delete data.
    * @param condition: where clause condition
    * @param type: logic type for where clause
    * @return the sql query
    */
   public String createPreparedDeleteQuery(ParamValue condition) {
       String whereClause = queryUtil.getPrepareConditional(condition);
       return "DELETE FROM " + tbName + " WHERE " + whereClause;
   }
}
