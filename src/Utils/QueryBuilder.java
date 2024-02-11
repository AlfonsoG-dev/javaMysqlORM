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
        tbName = nTableName;
    }
    /**
     * crea la query para la sentencia de FindOne
     * @param options: options: las columnas a buscar por key, value
     * @param type: tipo de condicion para la sentencia sql
     * @return: el usuario buscado
     */
    public String createFindQuery(String options, String type) {
        String sql = "", cleanValues = "";
        if(options == null || options.isEmpty()) {
            sql = "select *" + " from "+ tbName;
        } else {
            cleanValues = queryUtil.getPrepareConditional(options, type);
            sql = "select *" + " from "+ tbName+ " where " + cleanValues;
        }
        return sql;
    }
    /**
     * crea la query para la sentencia FindByColumnName
     * @param options: las columnas a buscar por key, value
     * @param type: tipo de condicion para la sentencia sql
     * @return: el usuario buscado
     */
    public String createFindByColumnQuery(String options, String type) {
        String sql = "", cleanValues = "";
        if(options == null || options.isEmpty()) {
            sql = "select *" +" from " + tbName;
        } else {
            cleanValues = queryUtil.getNormalConditional(options, type);
            sql = "select *" +" from " + tbName + " where " + cleanValues.stripIndent();
        }
        return sql;
    }
    /**
     * crea la query para la sentencia GetValueOfColumnName
     * @param options: los valores de condicion
     * @param column: los valores a retornar
     * @param type: tipo de condicion para la sentencia sql
     * @return la sentencia sql con los valores a retornar y los valores de condicion
     */
    public String createFindColumnValueQuery(String options, String columns, String type) {
        String sql = "";
        String cleanValues = "";
        if(columns == null || columns.isEmpty() == true) {
            if(options == null || options.isEmpty()) {
                sql =  "select *" +" from " + tbName;
            } else {
                cleanValues = queryUtil.getNormalConditional(options, type);
                sql =  "select *" +" from " + tbName + " where " + cleanValues.stripIndent();
            }
        }
        else if(columns != null || columns.isEmpty() == false) {
            if(options == null || options.isEmpty()) {
                sql =  "select "+ columns +" from " + tbName;
            } else {
                cleanValues = queryUtil.getNormalConditional(options, type);
                sql =  "select "+ columns +" from " + tbName + " where " + cleanValues.stripIndent();
            }
        }
        return sql;
    }
    /**
     * crea la query para la buscar un registro dentro de varias posibilidades
     * @param returnOptions: columns to return
     * @param columns: columns to search in condition
     * @param condition: type of condition in ('', '')
     * @return la sentencia sql para buscar dentro de una lista de datos
     */
    public String createFindInQuery(String returnOptions, String columns, String condition, String type) {
        String sql = "", inCondition = "";
        if(returnOptions == null) {
            if(condition == null || condition.isEmpty() && (columns == null || columns.isEmpty())) {
                sql = "select * from " + tbName;
            } else {
                inCondition = queryUtil.getInConditional(columns, condition, type);
                sql = "select * from " + tbName + " where " + inCondition;
            }
        } else if(returnOptions != null && !returnOptions.isEmpty()) {
            if(condition == null || condition.isEmpty() && (columns == null || columns.isEmpty())) {
                sql = "select " + returnOptions + " from " + tbName;
            } else {
                inCondition = queryUtil.getInConditional(columns, condition, type);
                sql = "select " + returnOptions + " from " + tbName + " where " + inCondition;
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
    public String createFindPatternQuery(String pattern, String[] options, String type) {
        String sql = "", patternCondition = "";
        if(pattern == null || options == null) {
            sql = "select * from "  + this.tbName;
        } else {
            patternCondition = queryUtil.getPatternCondition(pattern, options, type);
            sql = "select * from "  + this.tbName + " where " + patternCondition;
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
        String sql = "", getCondition = "";
        String minMaxSelection = queryUtil.getMinMaxSelection(columns);
        if(condition == null || condition.isEmpty()) {
            sql = "select " + minMaxSelection + " from " + tbName;
        } else {
            getCondition = queryUtil.getNormalConditional(condition, type);
            sql = "select " + minMaxSelection + " from " + tbName + " where " + getCondition;
        }
        return sql;
    }
    /**
     * crea la sentencia sql para el registro de datos
     * @param nObject: objeto con el método para crear la sentencia sql
     * @return la sentencia sql para registrar
     */
    public String createInsertRegisterQuery(ModelMethods model) {
        String[] data = queryUtil.getModelType(model.getAllProperties(), false).split(",");
        String miData = "", cleanData = "";
        for(String d: data) {
            if(!d.equals("''")) {
                miData += d +",";
            }
        }
        cleanData = miData.substring(0, miData.length()-1);
        String column = queryUtil.getModelColumns(model.getAllProperties(), false);
        String sql = "insert into " + tbName + " (" + column +") values (" + cleanData + ")";
        return sql;
    }
    /**
     * crea la sentencia sql para el inner join
     * @param ref_table: tabla a la que se hace referencia
     * @param RefModel: modelo que posee los datos de referencia
     * @return la sentencia sql para inner join
     */
    public String createInnerJoinQuery(ModelMethods localModel, ModelMethods refModel, String refTable, String condition, String type) {
        String refNombres = queryUtil.asignTableNameToColumns(refModel.getAllProperties(), refTable);
        String localNombres = queryUtil.asignTableNameToColumns(localModel.getAllProperties(), this.tbName);
        String pkfk = queryUtil.innerJoinConditional(localModel.getAllProperties(), refModel.getAllProperties(), this.tbName, refTable);
        String sql = "";
        if(condition == null || condition.isEmpty()) {
            sql = "select " + localNombres + ", " + refNombres + " from " + this.tbName + " inner join " + refTable + " on " + pkfk;
        } else {
            String conditional = queryUtil.getNormalConditional(condition, type);
            sql = "select " + localNombres + ", " + refNombres + " from " + this.tbName + " inner join " + refTable + " on " + pkfk + " where " + conditional;
        }
        return sql;
    }
    /** 
     * crear la sentencia sql para modificar los datos
     * @param nObject: objeto con los métodos para crear la sentencia sql
     * @param condicional: propiedades para la condición de la sentencia sql
     * @param type: tipo de condicion para la sentencia sql
     * @return la sentencia sql para modificar
     */
    public String createModifyRegisterQuery(ModelMethods model, String condicional, String type) {
        String sql = "", condition = "";
        String cleanKeyValue = queryUtil.getAsignModelValues(model.getAllProperties());
        if(condicional == null || condicional.isEmpty()) {
            sql = "update " + tbName + " set " +  cleanKeyValue;
        } else {
            condition = queryUtil.getNormalConditional(condicional, type);
            sql = "update " + tbName + " set " +  cleanKeyValue + " where " + condition;
        }
        return sql;
   }
   /**
    * crear la sentencia sql para eliminar el registro
    * @param options: las columnas con los valores para el condicional
    * @param type: tipo de condicion para la sentencia sql
    * @return la sentencia sql
    **/
   public String createDeleteRegisterQuery(String options, String type) {
       String sql = "", condicional = "";
       if(options == null || options.isEmpty()) {
           sql = "delete from " + tbName;
       } else {
           condicional = queryUtil.getNormalConditional(options, type);
           sql = "delete from " + tbName + " where " + condicional;
       }
       return sql;
   }
}
