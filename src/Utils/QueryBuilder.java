package Utils;

import Model.ModelMethods;

/**
 * record con los métodos para crear las queries para las consultas sql
 * @param tb_name: nombre de la tabla en la cual se ejecutan las sentencias sql
 * */
public class QueryBuilder {
    /**
     * utilidades para las querys
     */
    private static QueryUtils queryUtil;
    /**
     * nombre de la tabla en base de datos
     */
    private String tbName;
    /**
     * metodo constructor
     * @param nTableName: nombre de la tabla que se utiliza para la creación de la query
     */
    public QueryBuilder(String nTableName) {
        queryUtil = new QueryUtils();
        tbName    = nTableName;
    }
    /**
     * crea la query para la sentencia de FindOne
     * @param condition: condition: las columnas a buscar por key, value
     * @param type: tipo de condicion para la sentencia sql
     * @return: el usuario buscado
     */
    public String createFindQuery(String condition, String type) {
        String 
            sql         = "",
            cleanValues = "";
        if(condition == null || condition.isEmpty()) {
            sql = "SELECT *" + " FROM "+ tbName;
        } else {
            cleanValues = queryUtil.getPrepareConditional(condition, type);
            sql         = "SELECT *" + " FROM "+ tbName+ " WHERE " + cleanValues;
        }
        return sql;
    }
    /**
     * crea la query para la sentencia FindByColumnName
     * @param condition: las columnas a buscar por key, value
     * @param type: tipo de condicion para la sentencia sql
     * @return: el usuario buscado
     */
    public String createFindByColumnQuery(String condition, String type) {
        String 
            sql         = "",
            cleanValues = "";
        if(condition == null || condition.isEmpty()) {
            sql = "SELECT *" +" FROM " + tbName;
        } else {
            cleanValues = queryUtil.getNormalConditional(condition, type);
            sql         = "SELECT *" +" FROM " + tbName + " WHERE " + cleanValues.stripIndent();
        }
        return sql;
    }
    /**
     * crea la query para la sentencia GetValueOfColumnName
     * @param condition: los valores de condicion
     * @param column: los valores a retornar
     * @param type: tipo de condicion para la sentencia sql
     * @return la sentencia sql con los valores a retornar y los valores de condicion
     */
    public String createFindColumnValueQuery(String condition, String columns, String type) {
        String
            sql         = "",
            cleanValues = "";
        if(columns == null || columns.isEmpty() == true) {
            if(condition == null || condition.isEmpty()) {
                sql =  "SELECT *" +" FROM " + tbName;
            } else {
                cleanValues = queryUtil.getNormalConditional(condition, type);
                sql         =  "SELECT *" +" FROM " + tbName + " where " + cleanValues.stripIndent();
            }
        }
        else if(columns != null || columns.isEmpty() == false) {
            if(condition == null || condition.isEmpty()) {
                sql =  "SELECT "+ columns +" FROM " + tbName;
            } else {
                cleanValues = queryUtil.getNormalConditional(condition, type);
                sql         =  "SELECT "+ columns +" FROM " + tbName + " WHERE " + cleanValues.stripIndent();
            }
        }
        return sql;
    }
    /**
     * crea la query para la buscar un registro dentro de varias posibilidades
     * @param returnColumns: columns to return
     * @param conditionalColumns: columns to search in condition
     * @param condition: type of condition in ('', '')
     * @return la sentencia sql para buscar dentro de una lista de datos
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
                sql         = "SELECT * FROM " + tbName + " WHERE " + inCondition;
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
                sql         = "SELECT " + returnColumns + " FROM " + tbName + " WHERE " + inCondition;
            }
        }
        return sql;
    }
    /**
     * crea la query para buscar un registro usando un patron
     * @param pattern: patrón a buscar
     * @param columns: columnas con los datos
     * @param type: tipo de condicion para la sentencia sql
     * @return la sentencia sql para buscar por patrón
     */
    public String createFindPatternQuery(String pattern, String[] conditions, String type) {
        String 
            sql              = "",
            patternCondition = "";
        if(pattern == null || conditions == null) {
            sql = "SELECT * FROM "  + this.tbName;
        } else {
            patternCondition = queryUtil.getPatternCondition(
                    pattern,
                    conditions,
                    type
            );
            sql = "SELECT * FROM "  + this.tbName + " WHERE " + patternCondition;
        }
        return sql;
    }
    /**
     * crea la query para buscar el min o max de la tabla.
     * select min(column) as alias_name from table where condition;
     * <br> pre: </br> utilizando columna tipo varchar el resultado es en orden alfabetico, de lo contrario 
     * se ordena numericamente
     * @param columns: 'min: nombre, max: password'
     * @param condition: condition for where clause
     * @param type: and or not
     */
    public String createFindMinMaxQuery(String columns, String condition, String type) {
        String 
            sql             = "",
            getCondition    = "",
            minMaxSelection = queryUtil.getMinMaxSelection(columns);
        if(condition == null || condition.isEmpty()) {
            sql = "SELECT " + minMaxSelection + " FROM " + tbName;
        } else {
            getCondition = queryUtil.getNormalConditional(condition, type);
            sql          = "SELECT " + minMaxSelection + " FROM " + tbName + " WHERE " + getCondition;
        }
        return sql;
    }
    /**
     * allows to build the insert statement query without PK and including FK
     * @param model: model with table creation data 
     * @return an Array of string with the types and columns for the insert statement
     */
    private String[] insertData(String modelData) {
        String[] 
            build   = new String[2],
            types   = queryUtil.getModelType(modelData, true).split(","),
            columns = queryUtil.getModelColumns(modelData, true).split(",");
        String 
            type   = "",
            column = "";
        for(int i=1; i<types.length; ++i) {
            String d = types[i];
            if(!d.equals("''")) {
                type += d + ",";
            }
        }
        for(int i=1; i<columns.length; ++i) {
            column += columns[i] + ",";
        }
        build[0] = type;
        build[1] = column;
        return build;
    }
    /**
     * crea la sentencia sql para el registro de datos
     * @param model: model with the data to insert
     * @return la sentencia sql para registrar
     */
    public String createInsertRegisterQuery(ModelMethods model) {
        String 
            types     = insertData(model.getAllProperties())[0],
            columns   = insertData(model.getAllProperties())[1],
            cTypes    = types.substring(0, types.length()-1),
            cColumns  = columns.substring(0, columns.length()-1);
        String sql    = "INSERT INTO " + tbName + " (" + cColumns +") VALUES (" + cTypes + ")";
        return sql;
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
            cColumns = columns.substring(0, columns.length()-1),
            sql      = "INSERT INTO " + tbName + " (" + cColumns + ") VALUES (" + cTypes + ")";
        return sql;
    }
    /**
     * crea la sentencia sql para el inner join
     * @param primary: model with the declaration of fk
     * @param foreign: foreign model of the relationship
     * @param foreignT: foreign table name
     * @param condition: condition for where clause
     * @param type: logic type for where clause
     * @return la sentencia sql para inner join
     */
    public String createInnerJoinQuery(ModelMethods primary, ModelMethods foreign, String foreignT,
            String condition, String type) {
        String 
            refNombres   = queryUtil.asignTableNameToColumns(
                foreign.getAllProperties(),
                foreignT
            ),
            localNombres = queryUtil.asignTableNameToColumns(
                primary.getAllProperties(),
                this.tbName
            ),
            pkfk         = queryUtil.innerJoinConditional(
                primary.getAllProperties(),
                foreign.getAllProperties(),
                this.tbName,
                foreignT
            ),
            sql          = "";
        if(condition == null || condition.isEmpty()) {
            sql =
                "SELECT " + localNombres + ", " +
                refNombres + " FROM " + this.tbName +
                " INNER JOIN " + foreignT + " ON " + pkfk;
        } else {
            String conditional = queryUtil.getNormalConditional(
                    condition,
                    type
            );
            sql =
                "SELECT " + localNombres + ", " +
                refNombres + " FROM " + this.tbName +
                " INNER JOIN " + foreignT + " ON " +
                pkfk + " WHERE " + conditional;
        }
        return sql;
    }
    /** 
     * crear la sentencia sql para modificar los datos
     * @param model: model with the data to insert
     * @param condicional: propiedades para la condición de la sentencia sql
     * @param type: tipo de condicion para la sentencia sql
     * @return la sentencia sql para modificar
     */
    public String createModifyRegisterQuery(ModelMethods model, String condicional, String type) {
        String 
            sql           = "",
            condition     = "",
            cleanKeyValue = queryUtil.getAsignModelValues(model.getAllProperties());
        if(condicional == null || condicional.isEmpty()) {
            sql = "UPDATE " + tbName + " SET " +  cleanKeyValue;
        } else {
            condition = queryUtil.getNormalConditional(condicional, type);
            sql       = "UPDATE " + tbName + " SET " +  cleanKeyValue + " WHERE " + condition;
        }
        return sql;
   }
   /**
    * crear la sentencia sql para eliminar el registro
    * @param condition: las columnas con los valores para el condicional
    * @param type: tipo de condicion para la sentencia sql
    * @return la sentencia sql
    **/
   public String createDeleteRegisterQuery(String condition, String type) {
       String 
           sql         = "",
           condicional = "";
       if(condition == null || condition.isEmpty()) {
           sql = "DELETE FROM " + tbName;
       } else {
           condicional = queryUtil.getNormalConditional(condition, type);
           sql         = "DELETE FROM " + tbName + " WHERE " + condicional;
       }
       return sql;
   }
}
