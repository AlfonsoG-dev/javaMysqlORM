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
    private static QueryUtils query_util;
    /**
     * nombre de la tabla en base de datos
     */
    private String tb_name;
    /**
     * metodo constructor
     * @param nTableName: nombre de la tabla que se utiliza para la creación de la query
     */
    public QueryBuilder(String nTableName) {
        query_util = new QueryUtils();
        tb_name = nTableName;
    }
    /**
     * crea la query para la sentencia de FindOne
     * @param: options: las columnas a buscar por key, value
     * @return: el usuario buscado
     */
    public String CreateFindQuery(String options) {
        String clean_values = query_util.GetPrepareConditional(options, "and");
        String sql = "select *" + " from "+ tb_name+ " where " + clean_values;
        return sql;
    }
    /**
     * crea la query para la sentencia FindByColumnName
     * @param options: las columnas a buscar por key, value
     * @return: el usuario buscado
     */
    public String CreateFindByColumnQuery(String options) {
        String clean_values = query_util.GetNormalConditional(options, "and");
        String sql = "select *" +" from " + tb_name + " where " + clean_values.stripIndent();
        return sql;
    }
    /**
     * crea la query para la sentencia GetValueOfColumnName
     * @param options: los valores de condicion
     * @param column: los valores a retornar
     * @return la sentencia sql con los valores a retornar y los valores de condicion
     */
    public String CreateFindColumnValueQuery(String options, String column) {
        String sql = "";
        String clean_values = query_util.GetNormalConditional(options, "and");
        if( column == null || column.isEmpty() == true) {
            sql =  "select *" +" from " + tb_name + " where " + clean_values.stripIndent();
        }
        else if(column != null || column.isEmpty() == false) {
            sql =  "select "+ column +" from " + tb_name + " where " + clean_values.stripIndent();
        }
        return sql;
    }
    /**
     * crea la query para la buscar un registro dentro de varias posibilidades
     * @param column: columna cuyo dato se encuentra entre las opciones
     * @param options: opciones para el dato de la columna
     * @return la sentencia sql para buscar dentro de una lista de datos
     */
    public String CreateFindInQuery(String column, String[] options) {
        String res = "";
        for(String o: options) {
            res += "'" + o + "'" + ", ";
        }
        String clean_res = query_util.CleanValues(res, 2);
        String sql = "select * from " + this.tb_name + " where " + column + " in (" + clean_res + ")";
        return sql;
    }
    /**
     * crea la query para buscar un registro usando un patron
     * @param pattern: patrón a buscar
     * @param columns: columnas con los datos
     * @return la sentencia sql para buscar por patrón
     */
    public String CreateFindPatternQuery(String pattern, String[] columns) {
        String pattern_condition = query_util.GetPatternCondition(pattern, columns);
        String sql = "select * from "  + this.tb_name + " where " + pattern_condition;
        return sql;
    }
    /**
     * crea la query para buscar el min o max de la tabla
     * <br> pre: </br> solo se debe utilizar para datos numéricos
     */
    public String CreateFindMinMaxQuery() {
        //TODO: implementar select min(column) or select max(column)
        return "";
    }
    /**
     * crea la sentencia sql para el registro de datos
     * @param nObject: objeto con el método para crear la sentencia sql
     * @return la sentencia sql para registrar
     */
    public String CreateInsertRegisterQuery(ModelMethods nObject) {
        String clean_data = query_util.GetModelType(nObject.GetAllProperties(), false);
        String column = query_util.GetModelColumns(nObject.GetAllProperties(), false);
        String sql = "insert into " + tb_name + " (" + column +") values (" + clean_data + ")";
        return sql;
    }
    /**
     * crea la sentencia sql para el inner join
     * @param ref_table: tabla a la que se hace referencia
     * @param RefModel: modelo que posee los datos de referencia
     * @return la sentencia sql para inner join
     */
    public String CreateInnerJoinQuery(String ref_table, ModelMethods RefModel, ModelMethods localModel) {
        String ref_nombres = query_util.AsignTableNameToColumns("cuentas", RefModel.GetAllProperties());
        String local_nombres = query_util.AsignTableNameToColumns("users", localModel.GetAllProperties());
        String pk_fk = query_util.InnerJoinConditional(localModel, RefModel, this.tb_name, ref_table);
        String sql = "select " + local_nombres + ", " + ref_nombres + " from " + this.tb_name + " inner join " + ref_table + " on " + pk_fk;
        return sql;
    }
    /** 
     * crear la sentencia sql para modificar los datos
     * @param nObject: objeto con los métodos para crear la sentencia sql
     * @param condicional: propiedades para la condición de la sentencia sql
     * @return la sentencia sql para modificar
     */
    public String CreateModifyRegisterQuery(ModelMethods nObject, String condicional) {
        String condition = query_util.GetNormalConditional(condicional, "and");
        String clean_key_value = query_util.GetAsignModelValues(nObject.GetAllProperties());
        String sql = "update " + tb_name + " set " +  clean_key_value + " where " + condition;
        return sql;
   }
   /**
    * crear la sentencia sql para eliminar el registro
    * @param options: las columnas con los valores para el condicional
    * @return la sentencia sql
    **/
   public String CreateDeleteRegisterQuery(String options) {
       String condicional = query_util.GetNormalConditional(options, "and");
       String sql = "delete from " + tb_name + " where " + condicional;
       return sql;
   }
}
