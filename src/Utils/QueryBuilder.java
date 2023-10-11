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
        String clean_values = query_util.GetPrepareConditional(options);
        String sql = "select *" + " from "+ tb_name+ " where " + clean_values;
        return sql;
    }
    /**
     * crea la query para la sentencia FindByColumnName
     * @param options: las columnas a buscar por key, value
     * @return: el usuario buscado
     */
    public String CreateFindByColumnQuery(String options) {
        String clean_values = query_util.GetNormalConditional(options);
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
        String clean_values = query_util.GetNormalConditional(options);
        if( column == null || column.isEmpty() == true) {
            sql =  "select *" +" from " + tb_name + " where " + clean_values.stripIndent();
        }
        else if(column != null || column.isEmpty() == false) {
            sql =  "select "+ column +" from " + tb_name + " where " + clean_values.stripIndent();
        }
        return sql;
    }
    /**
     */
    public String CreateFindInQuery() {
        //TODO: implementar select * where column in (more columns)
        return "";
    }
    /**
     */
    public String CreateFindPatternQuery() {
        //TODO: implementar select * where condición like 'pattern'
        return "";
    }
    /**
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
        System.out.println(sql);
        return "";
    }
    /** 
     * crear la sentencia sql para modificar los datos
     * @param nObject: objeto con los métodos para crear la sentencia sql
     * @param condicional: propiedades para la condición de la sentencia sql
     * @return la sentencia sql para modificar
     */
    public String CreateModifyRegisterQuery(ModelMethods nObject, String condicional) {
        String condition = query_util.GetNormalConditional(condicional);
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
       String condicional = query_util.GetNormalConditional(options);
       String sql = "delete from " + tb_name + " where " + condicional;
       return sql;
   }
}
