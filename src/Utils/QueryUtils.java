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
        if(options.length() > 0) {
            return options.substring(0, options.length()-val);
        } else {
            return options;
        }
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
    public String GetModelColumns(String modelProperties, boolean includePKFK) {
        String[] data = modelProperties.split("\n");
        String columName = "";
        for(int i = 0; i < data.length; i++) {
            columName += data[i].split(":")[0].stripIndent() + ", ";
        }
        String[] columns = columName.split(",");
        String cleanColumName = "";
        if(includePKFK == false) {
            for(int i = 1; i < columns.length; i++) {
                cleanColumName += columns[i].stripIndent() + ",";
            }
        }
        else {
            for(int i = 0; i < columns.length; i++) {
                cleanColumName += columns[i].stripIndent() + ",";
            }
        }
        String cleanvalues = "";
        if(includePKFK == true) {
            cleanvalues = this.CleanValues(cleanColumName, 2);
        } else {
            cleanvalues = cleanColumName;
        }
        return cleanvalues;
    }
    /**
     * obtener las columnas de la tabla
     * @param rst: resultado de la consulta a la bd
     * @return la lista de columnas de la tabla
     */
    public HashMap<String, ArrayList<String>> GetTableData(ResultSet rst) throws SQLException {
        HashMap<String, ArrayList<String>> datosTable = new HashMap<>();
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        while(rst.next()) {
            String[] data = rst.getString(1).split("\n");
            for(String k: data) {
                columns.add(k);
            }
            String[] mTypes = rst.getString(2).split("\n");
            String[] nullColumn = rst.getString(3).split("\n");
            String[] keyColumn = rst.getString(4).split("\n");
            String[] extraValue = rst.getString(6).split("\n");
            for(int k = 0; k < mTypes.length; ++k) {
                String type = "";
                if(mTypes[k] != null) {
                    type += mTypes[k];
                }
                if(nullColumn[k] != null && nullColumn[k].contains("NO")) {
                    type += " not null";
                }
                if(keyColumn[k] != null) {
                    if(keyColumn[k].contains("PRI")) {
                        type += " unique primary key";
                    }
                    if(keyColumn[k].contains("MUL")) {
                        type += " foreign key";
                    }
                    if(keyColumn[k].contains("UNI")) {
                        type += " unique";
                    }
                }
                if(extraValue[k] != null) {
                    type += " " + extraValue[k];
                }
                types.add(type.trim());
            }

        }
        datosTable.put("columns", columns);
        datosTable.put("tipos", types);
        return datosTable;
    }
    /**
     * obtener el tipo por cada columna del modelo
     * @param nObject: objeto con los datos del modelo
     * @return los tipos de dato por columna
     */
    public String GetModelType(String modelProperties, boolean includePKFK) {
        String[] data = modelProperties.split("\n");
        String userData = "";
        if(includePKFK == false) {
            for(int i = 0; i < data.length; i++) {
                String columnType = data[i].split(":")[1].stripIndent();
                String pointStrip = "";
                if(columnType.contains(".")) {
                    pointStrip = columnType.split("\\.")[0].stripIndent();
                } else {
                    pointStrip = columnType;
                }
                userData += "'" + pointStrip + "'" + ",";
            }
        }
        else {
            for(int i = 0; i < data.length; i++) {
                userData += "'"+ data[i].split(":")[1].stripIndent() + "'" + ",";
            }
        }
        return this.CleanValues(userData, 1);
    }
    /**
     * buscar el typo de dato de una columna
     * @param column: columna a buscar el tipo de dato
     * @param model_properties: propiedades del modelo
     * @return el indice o index del typo de dato
     */
    public int SearchColumnType(String modelProperties, String column) {
        String[] modelColumns = this.GetModelColumns(modelProperties, true).split(",");
        int res = 0;
        for(int i=0; i<modelColumns.length; ++i) {
            if(modelColumns[i].contains(column)) {
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
        String cleanValues = this.CleanValues(values, 1);
        return cleanValues;
    }
    /**
     * combina la llave con el valor para el condicional sql
     * <br> pre: </br> if type is not use and for the next condition
     * @param options:  las columnas a asignadas  el valor
     * @param type: logic operator type: and, or, not
     * @return las columnas asignadas el valor
     */
    public String GetPrepareConditional(String options, String type) {
        String conditionalValue = "";
        String[] div = options.split(",");
        for(String val: div) {
            if(type.toLowerCase().equals("not")) {
                conditionalValue += "not " + val.split(":")[0] + "=" + "?" + " and";
            } else {
                conditionalValue += val.split(":")[0] + "=" + "?" + " " + type;
            }
        }
        String cleanValues = "";
        if(type.equals("and")) {
            cleanValues = this.CleanValues(conditionalValue, 4);
        } else if(type.equals("not")) {
            cleanValues = this.CleanValues(conditionalValue, 4);
        } else if(type.equals("or")) {
            cleanValues = this.CleanValues(conditionalValue, 3);
        }
        return cleanValues;
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
            if(type.toLowerCase().equals("not")) {
                conditionalValue += "not" + val.split(":")[0] +
                    "="+ "'"+
                    val.split(":")[1].stripIndent()+
                    "'" + " and";
            } else {
                conditionalValue += val.split(":")[0] +
                    "="+ "'"+
                    val.split(":")[1].stripIndent()+
                    "'" + " " + type;
            }
        }
        String cleanValues = "";
        if(type.equals("and")) {
            cleanValues = this.CleanValues(conditionalValue, 4);
        } else if(type.equals("not")) {
            cleanValues = this.CleanValues(conditionalValue, 4);
        } else if(type.equals("or")) {
            cleanValues = this.CleanValues(conditionalValue, 3);
        }
        return cleanValues;
    }
    /**
     * combina la llave con el valor para el condicional sql tipo in
     * @param options: las columnas que representan la llave
     * @param condition: condicion o sentencia sql tipo SELECT
     * @param type: operador logico para la condicion
     * @return el condicional combinado tipo in
     */
    public String GetInConditional(String columns, String condition, String type) {
        String conditionValue = "", inOptions = "", res = "", cleanValues = "";
        String[] myColumns = columns.split(",");
        if(condition.toLowerCase().startsWith("select")) {
            for(String c: myColumns) {
                if(type.toLowerCase().equals("not")) {
                    res += c + " not in (" + condition + ")" + " " + type;
                } else {
                    res += c + " in (" + condition + ")" + " " + type;
                }
            }
        } else {
            String[] partition = condition.split(",");
            for(String p: partition) {
                conditionValue += "('" + p.trim() + "', ";
            }
            inOptions = conditionValue.substring(0, condition.length()-2);
            for(String c: myColumns) {
                if(type.toLowerCase().equals("not")) {
                    res += c + " not in " + inOptions + " " + type;
                } else {
                    res += c + " in " + inOptions + " " + type;
                }
            }
        }
        if(type.equals("and")) {
            cleanValues = this.CleanValues(res, 4);
        } else if(type.equals("not")) {
            cleanValues = this.CleanValues(res, 4);
        } else if(type.equals("or")) {
            cleanValues = this.CleanValues(res, 3);
        }
        return cleanValues;
    }
    /**
     * asigna los valores a las columnas del modelo separados por ","
     * @param nObject: objeto que contiene la información del modelo
     * @return los valores asignados a la columna
     */
    public String GetAsignModelValues(String modelProperties) {
        String[] data = modelProperties.split("\n");
        String keyValue = "";
        for(int i = 1; i < data.length; i++) {
            String key = data[i].split(":")[0];
            String value = data[i].split(":")[1];
            keyValue += key.stripIndent() +"="+ "'"+value.stripIndent()+"'"+ ", ";
        }
        String cleanValues = this.CleanValues(keyValue, 2);
        return cleanValues;
    }
    /**
     * asignar el nombre de la tabla a las columnas del modelo
     * @param tb_name: nombre a asignar
     * @param model: modelo con las columnas
     * @return columnas asignadas el nombre de la tabla
     */
    public String AsignTableNameToColumns(String modelProperties, String tbName) {
        String[] data = modelProperties.split("\n");
        String build = "";
        for(String l: data) {
            String key = l.split(":")[0];
            if(key.contains("pk") == false && key.contains("fk") == false) {
                build += tbName + "." + key + " as " + tbName +"_"+ key +", ";
            }
        }
        String cleanValues = this.CleanValues(build, 2);
        return cleanValues;
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
            if(type.toLowerCase().equals("not")) {
                res += "not" + k + " like " + "'" + pattern + "'" + " and";
            } else {
                res += k + " like " + "'" + pattern + "'" + " " + type;
            }
        }
        String cleanValues = "";
        if(type.equals("and")) {
            cleanValues = this.CleanValues(res, 4);
        } else if(type.equals("not")) {
            cleanValues = this.CleanValues(res, 4);
        } else if(type.equals("or")) {
            cleanValues = this.CleanValues(res, 3);
        }
        return cleanValues;
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
    public String InnerJoinConditional(String localProperties, String refProperties, String localTable, String refTable) {
        String pk = "", fk = "", build = "";
        pk = this.GetPkFk(refProperties).get("pk");
        fk = this.GetPkFk(localProperties).get("fk");
        build += localTable + "." + fk +"="+ refTable +"."+ pk;
        return build;
    }
    /**
     * obtiene la pk o fk de la lista de columnas del modelo
     * @param model: el modelo con la lista de columnas
     * @return pk o fk
     */
    public HashMap<String, String> GetPkFk(String modelProperties) {
        HashMap<String, String> pkfk = new HashMap<String, String>();
        String[] data = modelProperties.split("\n");
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
    public ArrayList<String> AuxiliarmodelProperties(String modelProperties) {
        ArrayList<String> columns =  new ArrayList<>();
        String[] modelColumns = modelProperties.split(",");
        for(String k: modelColumns) {
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
    public HashMap<String, String> CompareColumnName(String modelProperties, ResultSet rst) throws SQLException {
        String localProperties = this.GetModelColumns(modelProperties, true);
        ArrayList<String> modelColumns = this.AuxiliarmodelProperties(localProperties);
        ArrayList<String> tableColumns = this.GetTableData(rst).get("columns");
        HashMap<String, String> resultado = new HashMap<>();
        if(modelColumns.size() == tableColumns.size()) {
            String rename = "";
            for(int i=0; i<modelColumns.size(); ++i) {
                if(tableColumns.get(i).equals(modelColumns.get(i)) == false) {
                    rename += modelColumns.get(i) + ":" + tableColumns.get(i) + ", ";
                }
            }
            resultado.put("rename", rename);
        }
        else if(modelColumns.size() > tableColumns.size()) {
            String agregar = "";
            for(int i=0; i<modelColumns.size(); i++) {
                if(!tableColumns.contains(modelColumns.get(i))) {
                    agregar += modelColumns.get(i) + ", ";
                }
            }
            resultado.put("agregar", agregar);
        }
        else if(tableColumns.size() > modelColumns.size()) {
            String eliminar = "";
            for(int i=0; i<tableColumns.size(); i++) {
                if(modelColumns.contains(tableColumns.get(i)) == false) {
                    eliminar += tableColumns.get(i) + ":" + i + ", ";
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
    public HashMap<String, String> CompareColumnType(String modelProperties, ResultSet rst) throws SQLException {
        String localTypes = this.GetModelType(modelProperties, true);
        ArrayList<String> tableTypes = this.GetTableData(rst).get("tipos");
        ArrayList<String> modelTypes = this.AuxiliarmodelProperties(localTypes);
        HashMap<String, String> resultado = new HashMap<>();
        if(modelTypes.size() == tableTypes.size()) {
            String rename = "";
            for(int i=0; i<modelTypes.size(); ++i) {
                String cleanTypes = modelTypes.get(i).replace("'", "");
                if(tableTypes.contains(cleanTypes) == false) {
                    rename += cleanTypes + ":" + i + ", ";
                }
            }
            resultado.put("rename", rename);
        }
        return resultado;
    }
}
