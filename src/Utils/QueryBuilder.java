package Utils;

import Model.ModelMethods;

/**
 * record con los métodos para crear las queries para las consultas sql
 * @param tb_name: nombre de la tabla en la cual se ejecutan las sentencias sql
 * */
public record QueryBuilder(String tb_name) {
    /**
     * utilidades para las querys
     */
    private static QueryUtils query_util = new QueryUtils();
    /**
     * crea la query para la sentencia de FindOne
     * @param: options: las columnas a buscar por key, value
     * @return: el usuario buscado
     */
    public String FindQuery(String options) {
        String clean_values = query_util.GetPrepareConditional(options);
        String sql = "select *" + " from "+ tb_name+ " where " + clean_values;
        return sql;
    }
    /**
     * crea la query para la sentencia FindByColumnName
     * @param options: las columnas a buscar por key, value
     * @return: el usuario buscado
     */
    public String FindByColumnQuery(String options) {
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
    public String FindColumnValueQuery(String options, String column) {
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
     * regresa la cantidad de columnas en la sentencia sql
     * @param metadata: datos de la sentencia sql
     * @return cantidad de columnas en la sentencia sql
     */
    public int GetMetadataColumns(String metadata) {
        String values = metadata.split(":")[1];
        String[] columns = values.split("=");
        int cont = 0;
        for(int i = 0; i < columns.length; i++) {
            String[] name = columns[i].split(",");
            for(int j = 0; j < name.length; j++) {
                //System.out.println(name[j]);
                if(name[j].equalsIgnoreCase("columnName") == true) {
                    cont++;
                }
            }
        }
        return cont;
    }
    /**
     * crea la sentencia sql para el registro de datos
     * @param nObject: objeto con el método para crear la sentencia sql
     * @return la sentencia sql para registrar
     */
    public String InsertRegisterQuery(ModelMethods nObject) {
        String clean_data = query_util.GetModelType(nObject);
        String column = query_util.GetModelColumns(nObject);
        String sql = "insert into " + tb_name + " (" + column +") values (" + clean_data + ")";
        return sql;
    }
    /**
     * crea la sentencia sql para el inner join
     * @param ref_table: tabla a la que se hace referencia
     * @param RefModel: modelo que posee los datos de referencia
     * @return la sentencia sql para inner join
     */
    public String InnerJoinQuery(String ref_table, ModelMethods RefModel, ModelMethods localModel) {
        String ref_nombres = query_util.AsignTableNameToColumns("cuentas", RefModel);
        String local_nombres = query_util.AsignTableNameToColumns("users", localModel);
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
    public String ModificarRegisterQuery(ModelMethods nObject, String condicional) {
        String condition = query_util.GetNormalConditional(condicional);
        String clean_key_value = query_util.GetAsignModelValues(nObject);
        String sql = "update " + tb_name + " set " +  clean_key_value + " where " + condition;
        return sql;
   }
   /**
    * crear la sentencia sql para eliminar el registro
    * @param options: las columnas con los valores para el condicional
    * @return la sentencia sql
    **/
   public String EliminarRegistroQuery(String options) {
       String condicional = query_util.GetNormalConditional(options);
       String sql = "delete from " + tb_name + " where " + condicional;
       return sql;
   }
}
