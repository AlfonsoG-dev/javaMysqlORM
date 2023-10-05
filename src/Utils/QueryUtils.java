package Utils;

import java.util.HashMap;

import Model.ModelMethods;


public record QueryUtils() {
    /**
     * obtener las columnas de los datos del modelo
     * @param nObject: objeto con los datos del modelo
     * @return las columnas del modelo
     */
    public String GetModelColumns(String ModelProperties) {
        String[] data = ModelProperties.split("\n");
        String column_name = "";
        for(int i = 0; i < data.length; i++) {
            column_name += data[i].split(":")[0].stripIndent() + ", ";
        }
        String[] columns = column_name.split(",");
        String clean_colunm_name = "";
        for(int i = 1; i < columns.length; i++) {
            clean_colunm_name += columns[i].stripIndent() + ", ";
        }
        return this.CleanValues(clean_colunm_name, 4);
    }
    /**
     * obtener el tipo por cada columna del modelo
     * @param nObject: objeto con los datos del modelo
     * @return los tipos de dato por columna
     */
    public String GetModelType(String ModelProperties){
        String[] data = ModelProperties.split("\n");
        String user_data = "";
        for(int i = 1; i < data.length; i++) {
            user_data += "'"+data[i].split(":")[1].stripIndent()+ "'" + ",";
        }
        return this.CleanValues(user_data, 1);
    }
    /**
     * reduce el string por un valor especifico
     * @param options: los valores a limpiar
     * @param val: valor especifico
     * @return los valores limpios
    */
    public String CleanValues(String options, int val) {
        return options.substring(0, options.length()-val);
    }
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
        String clean_values = this.CleanValues(values, 1);
        return clean_values;
    }
    /**
     * combina la llave con el valor para el condicional sql
     * @param values:  las columnas a asignadas  el valor
     * @return las columnas asignadas el valor
     */
    public String GetPrepareConditional(String values) {
        String conditionalValue = "";
        String[] div = values.split(",");
        for(String val: div) {
            conditionalValue += val.split(":")[0] + "=" + "?" + " and";
        }
        String clean_values = this.CleanValues(conditionalValue, 3);
        return clean_values;
    }
    /**
     * combina la llave con el valor para el condicional sql
     * @param values: las columnas para asignar el valor
     * @return las columnas asignadas el valor
     */
    public String GetNormalConditional(String values) {
        String conditionalValue = "";
        String[] div = values.split(",");
        for(String val: div) {
            conditionalValue += val.split(":")[0] +
            "="+ "'"+
            val.split(":")[1].stripIndent()+
            "'" + " and";
        }
        String clean_values = this.CleanValues(conditionalValue, 3);
        return clean_values;
    }
    /**
     * asigna los valores a las columnas del modelo separados por ","
     * @param nObject: objeto que contiene la información del modelo
     * @return los valores asignados a la columna
     */
    public String GetAsignModelValues(String ModelProperties) {
        String[] data = ModelProperties.split("\n");
        String key_value = "";
        for(int i = 1; i < data.length; i++) {
            String key = data[i].split(":")[0];
            String value = data[i].split(":")[1];
            key_value += key.stripIndent() +"="+ "'"+value.stripIndent()+"'"+ ", ";
        }
        String clean_values = this.CleanValues(key_value, 2);
        return clean_values;
    }
    /**
     * asignar el nombre de la tabla a las columnas del modelo
     * @param tb_name: nombre a asignar
     * @param model: modelo con las columnas
     * @return columnas asignadas el nombre de la tabla
     */
    public String AsignTableNameToColumns(String tb_name, String ModelProperties) {
        String[] data = ModelProperties.split("\n");
        String build = "";
        for(String l: data) {
            String key = l.split(":")[0];
            if(key.contains("pk") == false && key.contains("fk") == false) {
                build += tb_name + "." + key + " as " + tb_name +"_"+ key +", ";
            }
        }
        String clean_values = this.CleanValues(build, 2);
        return clean_values;
    }
    /**
     * genera el condicional para innerjoin utilizando la fk del modelo de referencia y la pk del modelo primario
     * @param local: modelo primario
     * @param ref: modelo de referencia
     * @param local_tb: tabla primaria
     * @param ref_tb: tabla de referencia
     * @return el condicional del inner join
     */
    public String InnerJoinConditional(ModelMethods local, ModelMethods ref, String local_tb, String ref_tb) {
        String pk = "", fk = "", build = "";
        pk = this.GetPkFk(local.GetAllProperties()).get("pk");
        fk = this.GetPkFk(ref.GetAllProperties()).get("fk");
        build += ref_tb + "." + fk +"="+ local_tb +"."+ pk;
        return build;
    }
    /**
     * obtiene la pk o fk de la lista de columnas del modelo
     * @param model: el modelo con la lista de columnas
     * @return pk o fk
     */
    public HashMap<String, String> GetPkFk(String ModelProperties) {
        HashMap<String, String> pkfk = new HashMap<String, String>();
        String[] data = ModelProperties.split("\n");
        for(int i=0; i<data.length; ++i) {
            String key = data[i].split(":")[0];
            if(key.contains("pk")) {
                pkfk.put("pk", key);
            }
            if(key.contains("fk")) {
                pkfk.put("fk", key);
            }
        }
        return pkfk;
    }
}
