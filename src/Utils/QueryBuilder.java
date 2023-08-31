package Utils;

public record QueryBuilder(String options, String column) {
    /**
     * combina las options & valores en 1 solo String
     * @param options: los valores a limpiar
     * @return los valores limpios y combinados
     */
    public String GetOptionValue() {
        String[] div = options.split(",");
        String values = "";
        for(String val: div) {
            values += val.split(":")[1] +",";
        }
        String clean_values = values.substring(0, values.length()-1);
        return clean_values;
    }
    /**
     * crea la query para la sentencia de FindOne
     * @param: options: las columnas a buscar por key, value
     * @return: el usuario buscado
     */
    public String FindQuery() {
        String[] div = options.split(",");
        String values = "";
        for(String val: div) {
            values += val.split(":")[0] + " =" + "?" + " and";
        }
        String clean_values = values.substring(0, values.length()-3);
        String sql = "select *" + " from users where " + clean_values;
        return sql;
    }
    /**
     * crea la query para la sentencia FindByColumnName
     * @param options: las columnas a buscar por key, value
     * @return: el usuario buscado
     */
    public String FindColumnQuery() {
        String[] div = options.split(",");
        String values = "";

        for(String val: div) {
            values += val.split(":")[0] +
            "="+ "'"+
            val.split(":")[1].stripIndent()+
            "'" + " and";
        }

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
    public String FindColumnValueQuery() {
        String[] div = options.split(",");
        String values = "";
        for(String val: div) {
            values += val.split(":")[0]+ 
            "=" + "'"+
            val.split(":")[1].stripIndent() +
            "'" + " and";
        }

        String clean_values = values.substring(0, values.length()-3);
        String sql = "";

        if( this.column == null || this.column.isEmpty() == true) {
            sql =  "select *" +" from users where " + clean_values.stripIndent();
        }
        else if(this.column != null || this.column.isEmpty() == false) {
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
        return cont;
    }
}
