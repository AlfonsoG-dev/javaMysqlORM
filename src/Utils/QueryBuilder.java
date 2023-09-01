package Utils;

import Mundo.User;

public record QueryBuilder() {
    /**
     * combina las options & valores en 1 solo String
     * @param options: los valores a limpiar
     * @return los valores limpios y combinados
     */
    public String GetOptionValue(String options) {
        String[] div = options.split(",");
        String values = "";
        for(String val: div) {
            values += val.split(":")[1] +",";
        }
        assert values == "" : "deberia ser diferente a \"\"";
        String clean_values = values.substring(0, values.length()-1);
        return clean_values;
    }
    /**
     * crea la query para la sentencia de FindOne
     * @param: options: las columnas a buscar por key, value
     * @return: el usuario buscado
     */
    public String FindQuery(String options) {
        String[] div = options.split(",");
        String values = "";
        for(String val: div) {
            values += val.split(":")[0] + " =" + "?" + " and";
        }
        assert values == "" : "deberia ser diferente a \"\"";
        String clean_values = values.substring(0, values.length()-3);
        String sql = "select *" + " from users where " + clean_values;
        return sql;
    }
    /**
     * crea la query para la sentencia FindByColumnName
     * @param options: las columnas a buscar por key, value
     * @return: el usuario buscado
     */
    public String FindColumnQuery(String options) {
        String[] div = options.split(",");
        String values = "";

        for(String val: div) {
            values += val.split(":")[0] +
            "="+ "'"+
            val.split(":")[1].stripIndent()+
            "'" + " and";
        }
        assert values == "" : "deberia ser diferente a \"\"";
        String clean_values = values.substring(0, values.length()-3);
        String sql = "select *" +" from users where " + clean_values.stripIndent();
        return sql;
    }
    /**
     * crea la query para la sentencia GetValueOfColumnName
     * @param options: los valores de condicion
     * @param column: los valores a retornar
     * @return la sentencia sql con los valores a retornar y los valores de condicion
     */
    public String FindColumnValueQuery(String options, String column) {
        String[] div = options.split(",");
        String values = "";
        for(String val: div) {
            values += val.split(":")[0]+ 
            "=" + "'"+
            val.split(":")[1].stripIndent() +
            "'" + " and";
        }
        assert values == "" : "deberia ser diferente a \"\"";
        String clean_values = values.substring(0, values.length()-3);
        String sql = "";

        if( column == null || column.isEmpty() == true) {
            sql =  "select *" +" from users where " + clean_values.stripIndent();
        }
        else if(column != null || column.isEmpty() == false) {
            sql =  "select "+ column +" from users where " + clean_values.stripIndent();
        }
        return sql;
    }
    /**
     * regresa la cantidad de columnas en la sentencia sql
     * @param metadata: datos de la sentencia sql
     * @return cantidad de columnas en la sentencia sql
     */
    public int GetMetadataColumns(String metadata){
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
        assert cont == 0 : "deberi ser mayor a 0";
        return cont;
    }
    /**
     * crea la sentencia sql para el registro de datos
     * @param nUser: el usuario a registrar
     * @return la sentencia sql para registrar
     */
    public String InsertRegisterQuery(User nUser) {
        String[] data = nUser.GetAllProperties().split("\n");
        String column_name = "";
        for(int i = 0; i < data.length; i++) {
            column_name += data[i].split(":")[0].stripIndent() + ", ";
        }
        String[] columns = column_name.split(",");
        String clean_colunm_name = "";
        for(int i = 1; i < columns.length; i++) {
            clean_colunm_name += columns[i].stripIndent() + ", ";
        }

        String user_data = "";
        for(int i = 1; i < data.length; i++) {
            user_data += "'"+data[i].split(":")[1].stripIndent()+ "'" + ",";
        }
        assert user_data == "" : "deberia tener la información del usuario nuevo";
        String clean_data = user_data.substring(0, user_data.length()-1);
        String column = clean_colunm_name.substring(0, clean_colunm_name.length()-4);
        String sql = "insert into users (" + column +") values (" + clean_data + ")";
        return sql;
    }
    /** 
     * crear la sentencia sql para modificar los datos
     * @param nUser: usuario con los datos a modificar
     * @return la sentencia sql para modificar
     */
    public String ModificarRegisterQuery(User nUser, String condicional) {
        String[] data = nUser.GetAllProperties().split("\n");
        String key_value = "";
        for(int i = 0; i < data.length; i++) {
            String key = data[i].split(":")[0];
            String value = data[i].split(":")[1];
            key_value += key.stripIndent() +"="+ "'"+value.stripIndent()+"'"+ ", ";
        }
        String[] conditions = condicional.split(",");
        String condition = "";
        for(int i = 0; i < conditions.length; i++) {
            String[] options = conditions[i].split(":");
            String key = options[0].stripIndent();
            String value = options[1].stripIndent();
            condition += key +"=" + "'"+value+"'" + " and ";
        }
        String clean_key_value = key_value.substring(0, key_value.length()-2);
        String clean_condition = condition.stripIndent().substring(0, condition.length()-5);
        String sql = "update users set " +  clean_key_value + " where " + clean_condition;
        return sql;
   }
   /**
    * crear la sentencia sql para eliminar el registro
    * @param options: las columnas con los valores para el condicional
    * @return la sentencia sql
    **/
   public String EliminarRegistroQuery(String options){
       String sql = "delete users where nombre='alfonso'";
       //TODO: crear la sentencia sql dinámica
       return sql;
   }
}
