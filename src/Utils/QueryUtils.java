package Utils;

import Model.ModelMethods;

public record QueryUtils() {
    /**
     * obtener las columnas de los datos del modelo
     * @param nObject: objeto con los datos del modelo
     * @return las columnas del modelo
     */
    public String GetModelColumns(ModelMethods nObject) {
        String[] data = nObject.GetAllProperties().split("\n");
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
    public String GetModelType(ModelMethods nObject){
        String[] data = nObject.GetAllProperties().split("\n");
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
    public String GetAsignModelValues(ModelMethods nObject) {
        String[] data = nObject.GetAllProperties().split("\n");
        String key_value = "";
        for(int i = 1; i < data.length; i++) {
            String key = data[i].split(":")[0];
            String value = data[i].split(":")[1];
            key_value += key.stripIndent() +"="+ "'"+value.stripIndent()+"'"+ ", ";
        }
        String clean_values = this.CleanValues(key_value, 2);
        return clean_values;
    }
}
