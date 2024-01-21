package Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * record con las herramientas para crear las sentencias sql según los datos del modelo y tabla
 */
public record QueryUtils() {
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
     * regresa la cantidad de columnas en la sentencia sql
     * @param metadata: datos de la sentencia sql
     * @return cantidad de columnas en la sentencia sql
     */
    public int GetMetadataNumColumns(String metadata) {
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
     * obtener las columnas de los datos del modelo
     * @param nObject: objeto con los datos del modelo
     * @return las columnas del modelo
     */
    public String GetModelColumns(String ModelProperties, boolean includePKFK) {
        String[] data = ModelProperties.split("\n");
        String column_name = "";
        for(int i = 0; i < data.length; i++) {
            column_name += data[i].split(":")[0].stripIndent() + ", ";
        }
        String[] columns = column_name.split(",");
        String clean_colunm_name = "";
        if(includePKFK == false) {
            for(int i = 1; i < columns.length; i++) {
                clean_colunm_name += columns[i].stripIndent() + ",";
            }
        }
        else {
            for(int i = 0; i < columns.length; i++) {
                clean_colunm_name += columns[i].stripIndent() + ",";
            }
        }
        return this.CleanValues(clean_colunm_name, 2);
    }
    /**
     * obtener las columnas de la tabla
     * @param rst: resultado de la consulta a la bd
     * @return la lista de columnas de la tabla
     */
    public HashMap<String, ArrayList<String>> GetTableData(ResultSet rst) throws SQLException {
        HashMap<String, ArrayList<String>> DatosTable = new HashMap<>();
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        while(rst.next()) {
            String[] data = rst.getString(1).split("\n");
            for(String k: data) {
                columns.add(k);
            }
            String[] tipos = rst.getString(2).split("\n");
            String[] null_co = rst.getString(3).split("\n");
            String[] key_co = rst.getString(4).split("\n");
            String[] extra = rst.getString(6).split("\n");
            for(int k = 0; k < tipos.length; ++k) {
                String tipo = "";
                if(tipos[k] != null) {
                    tipo += tipos[k];
                }
                if(null_co[k] != null && null_co[k].contains("NO")) {
                    tipo += " not null";
                }
                if(key_co[k] != null) {
                    if(key_co[k].contains("PRI")) {
                        tipo += " unique primary key";
                    }
                    if(key_co[k].contains("MUL")) {
                        tipo += " foreign key";
                    }
                    if(key_co[k].contains("UNI")) {
                        tipo += " unique";
                    }
                }
                if(extra[k] != null) {
                    tipo += " " + extra[k];
                }
                types.add(tipo.trim());
            }

        }
        DatosTable.put("columns", columns);
        DatosTable.put("tipos", types);
        return DatosTable;
    }
    /**
     * obtener el tipo por cada columna del modelo
     * @param nObject: objeto con los datos del modelo
     * @return los tipos de dato por columna
     */
    public String GetModelType(String ModelProperties, boolean includePKFK) {
        String[] data = ModelProperties.split("\n");
        String user_data = "";
        if(includePKFK == false) {
            for(int i = 1; i < data.length; i++) {
                user_data += "'"+data[i].split(":")[1].stripIndent()+ "'" + ",";
            }
        }
        else {
            for(int i = 0; i < data.length; i++) {
                user_data += "'"+data[i].split(":")[1].stripIndent()+ "'" + ",";
            }
        }
        return this.CleanValues(user_data, 1);
    }
    /**
     * buscar el typo de dato de una columna
     * @param column: columna a buscar el tipo de dato
     * @param model_properties: propiedades del modelo
     * @return el indice o index del typo de dato
     */
    public int SearchColumnType(String model_properties, String column) {
        String[] model_columns = this.GetModelColumns(model_properties, true).split(",");
        int res = 0;
        for(int i=0; i<model_columns.length; ++i) {
            if(model_columns[i].contains(column)) {
                res = i;
            }
        }
        return res;
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
     * @param options:  las columnas a asignadas  el valor
     * @return las columnas asignadas el valor
     */
    public String GetPrepareConditional(String options, String type) {
        String conditionalValue = "";
        String[] div = options.split(",");
        for(String val: div) {
            // TODO: add not assignment for conditional value
            conditionalValue += val.split(":")[0] + "=" + "?" + " " + type;
        }
        String clean_values = "";
        if(type.equals("and")) {
            clean_values = this.CleanValues(conditionalValue, 4);
        } else if(type.equals("not")) {
            clean_values = this.CleanValues(conditionalValue, 4);
        } else if(type.equals("or")) {
            clean_values = this.CleanValues(conditionalValue, 3);
        }
        return clean_values;
    }
    /**
     * combina la llave con el valor para el condicional sql
     * @param options: las columnas para asignar el valor
     * @return las columnas asignadas el valor
     */
    public String GetNormalConditional(String options, String type) {
        String conditionalValue = "";
        String[] div = options.split(",");
        for(String val: div) {
            // TODO: add not assignment for conditional value
            conditionalValue += val.split(":")[0] +
            "="+ "'"+
            val.split(":")[1].stripIndent()+
            "'" + " " + type;
        }
        String clean_values = "";
        if(type.equals("and")) {
            clean_values = this.CleanValues(conditionalValue, 4);
        } else if(type.equals("not")) {
            clean_values = this.CleanValues(conditionalValue, 4);
        } else if(type.equals("or")) {
            clean_values = this.CleanValues(conditionalValue, 3);
        }
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
    public String AsignTableNameToColumns(String ModelProperties, String tb_name) {
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
     * genera el condicional de la búsqueda por patrón
     * @param pattern: patrón a buscar
     * @param options: columnas cuyo dato tiene el patrón
     * @param type: tipo de condicional para la sentencia sql
     * @return la condición del patrón
     */
    public String GetPatternCondition(String pattern, String[] options, String type) {
        String res = "";
        for(String k: options) {
            res += k + " like " + "'" + pattern + "'" + " " + type + " ";
        }
        String clean_values = "";
        if(type.equals("and")) {
            clean_values = this.CleanValues(res, 4);
        } else if(type.equals("not")) {
            clean_values = this.CleanValues(res, 4);
        } else if(type.equals("or")) {
            clean_values = this.CleanValues(res, 3);
        }
        return clean_values;
    }
    /**
     * genera el condicional para innerjoin utilizando la fk del modelo de referencia y la pk del modelo primario
     * <br> pre: </br> la condición solo utiliza la fk del modelo de referencia y la pk del modelo local
     * @param local: modelo primario
     * @param ref: modelo de referencia
     * @param local_tb: tabla primaria
     * @param ref_tb: tabla de referencia
     * @return el condicional del inner join
     */
    public String InnerJoinConditional(String local_properties, String ref_properties, String local_tb, String ref_tb) {
        String pk = "", fk = "", build = "";
        pk = this.GetPkFk(ref_properties).get("pk");
        fk = this.GetPkFk(local_properties).get("fk");
        build += local_tb + "." + fk +"="+ ref_tb +"."+ pk;
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
            if(key.contains("id_pk")) {
                pkfk.put("pk", key);
            }
            if(key.contains("id_fk")) {
                pkfk.put("fk", key);
            }
        }
        return pkfk;
    }
    /**
     * crea un ArrayList en base a las columnas del modelo
     * @param model_properties: propiedades del modelo
     * @return la lista de las columnas del modelo
     */
    public ArrayList<String> AuxiliarModelProperties(String model_properties) {
        ArrayList<String> columns =  new ArrayList<>();
        String[] model_columns = model_properties.split(",");
        for(String k: model_columns) {
            columns.add(k);
        }
        return columns;
    }
    /**
     * compara el nombre de las columnas del modelo con la tabla y ordena entre: agregar, eliminar y renombrar
     * @param model_properties: propiedades del modelo
     * @param rst: resultado de la ejecución de la sentencia sql
     * @return retorna las columnas a eliminar, agregar o renombrar
     */
    public HashMap<String, String> CompareColumnName(String model_properties, ResultSet rst) throws SQLException {
        String local_p = this.GetModelColumns(model_properties, true);
        ArrayList<String> model_columns = this.AuxiliarModelProperties(local_p);
        ArrayList<String> table_columns = this.GetTableData(rst).get("columns");
        HashMap<String, String> resultado = new HashMap<>();
        if(model_columns.size() == table_columns.size()) {
            String rename = "";
            for(int i=0; i<model_columns.size(); ++i) {
                if(table_columns.get(i).equals(model_columns.get(i)) == false) {
                    rename += model_columns.get(i) + ":" + table_columns.get(i) + ", ";
                }
            }
            resultado.put("rename", rename);
        }
        else if(model_columns.size() > table_columns.size()) {
            String agregar = "";
            for(int i=0; i<model_columns.size(); i++) {
                if(table_columns.contains(model_columns.get(i)) == false) {
                    agregar += model_columns.get(i) + ", ";
                }
            }
            resultado.put("agregar", agregar);
        }
        else if(table_columns.size() > model_columns.size()) {
            String eliminar = "";
            for(int i=0; i<table_columns.size(); i++) {
                if(model_columns.contains(table_columns.get(i)) == false) {
                    eliminar += table_columns.get(i) + ":" + i + ", ";
                }
            }
            resultado.put("eliminar", eliminar);
        }
        return resultado;
    }
    /**
     * compara los tipos de datos del modelo con la tabla y regresa el distinto
     * @param model_properties: propiedades del modelo
     * @param rst: resultado de la consulta sql 
     * @throws SQLException: error de la consulta sql
     * @return las columnas con el tipo de dato a cambiar
     */
    public HashMap<String, String> CompareColumnType(String model_properties, ResultSet rst) throws SQLException {
        String local_t = this.GetModelType(model_properties, true);
        ArrayList<String> table_types = this.GetTableData(rst).get("tipos");
        ArrayList<String> model_types = this.AuxiliarModelProperties(local_t);
        HashMap<String, String> resultado = new HashMap<>();
        if(model_types.size() == table_types.size()) {
            String rename = "";
            for(int i=0; i<model_types.size(); ++i) {
                String clean_types = model_types.get(i).replace("'", "");
                if(table_types.contains(clean_types) == false) {
                    rename += clean_types + ":" + i + ", ";
                }
            }
            resultado.put("rename", rename);
        }
        return resultado;
    }
}
