package Utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * record con las herramientas para crear las sentencias sql según los datos del modelo y tabla
 */
public record QueryUtils() {
    /**
     * reduce el string por un valor especifico
     * @param condition: los valores a limpiar
     * @param val: valor especifico
     * @return los valores limpios
    */
    public String cleanValues(String condition, int val) {
        if(condition.length() > 0) {
            return condition.substring(0, condition.length()-val);
        } else {
            return condition;
        }
    }
    /**
     * regresa la cantidad de columnas en la sentencia sql
     * @param metadata: datos de la sentencia sql
     * @throws SQLException: execute exception
     * @return cantidad de columnas en la sentencia sql
     */
    public int getMetadataNumColumns(ResultSetMetaData metadata) throws SQLException {
        return metadata.getColumnCount();
    }
    /**
     * obtener las columnas de los datos del modelo
     * @param modelProperties: properties of model
     * @param includePKFK: true or false to include PK or FK
     * @return las columnas del modelo
     */
    public String getModelColumns(String modelProperties, boolean includePKFK) {
        String[] data = modelProperties.split("\n");
        String 
            columName = "",
            cColumName = "";
        for(int i = 0; i < data.length; i++) {
            String myColumn = data[i].split(":")[0].stripIndent();
            if(myColumn == null || myColumn.isEmpty()) {
                myColumn = "";
            } else {
                columName += myColumn + ", ";
            }
        }
        String[] columns = columName.split(",");
        if(includePKFK == false) {
            for(int i = 1; i < columns.length; i++) {
                cColumName += columns[i].stripIndent() + ",";
            }
        }
        else {
            for(int i = 0; i < columns.length; i++) {
                cColumName += columns[i].stripIndent() + ",";
            }
        }
        return cleanValues(cColumName, 2);
    }
    /**
     * obtener las columnas de la tabla
     * @param rst: resultado de la consulta a la bd
     * @throws SQLException: error while trying to execute
     * @return la lista de columnas de la tabla
     */
    public HashMap<String, ArrayList<String>> getTableData(ResultSet rst) throws SQLException {
        HashMap<String, ArrayList<String>> datosTable = new HashMap<>();
        ArrayList<String>
            columns = new ArrayList<>(),
            types   = new ArrayList<>();
        while(rst.next()) {
            String[] data = rst.getString(1).split("\n");
            for(String k: data) {
                columns.add(k);
            }
            String[] 
                mTypes     = rst.getString(2).split("\n"),
                nullColumn = rst.getString(3).split("\n"),
                keyColumn  = rst.getString(4).split("\n"),
                extraValue = rst.getString(6).split("\n");
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
     * @param modelProperties: properties of model
     * @param includePKFK: include pk or fk
     * @return los tipos de dato por columna
     */
    public String getModelType(String modelProperties, boolean includePKFK) {
        String[] data   = modelProperties.split("\n");
        String userData = "";
        if(includePKFK == false) {
            for(int i = 1; i < data.length; i++) {
                String myType = data[i].split(":")[1].trim();
                if(myType == null || myType.isEmpty()) {
                    myType = "";
                } else {
                    userData += "'" + myType + "'" + ",";
                }
            }
        }
        else {
            for(int i = 0; i < data.length; i++) {
                String myType = data[i].split(":")[1].stripIndent();
                userData += "'" + myType + "'" + ",";
            }
        }
        return cleanValues(userData, 1);
    }
    /**
     * buscar el typo de dato de una columna
     * @param modelProperties: propiedades del modelo
     * @param column: columna a buscar el tipo de dato
     * @return el indice o index del typo de dato
     */
    public int searchColumnType(String modelProperties, String column) {
        String[] modelColumns = getModelColumns(modelProperties, true).split(",");
        int res = 0;
        for(int i=0; i<modelColumns.length; ++i) {
            if(modelColumns[i].contains(column)) {
                res = i;
            }
        }
        return res;
    }
    /**
     * combina las condition & valores en 1 solo String
     * @param condition: los valores a limpiar
     * @return los valores limpios y combinados
     */
    public String getOptionValue(String condition) {
        String[] div = condition.split(", ");
        String values = "";
        for(String val: div) {
            values += val.split(":")[1] +",";
        }
        return cleanValues(values, 1);
    }
    /**
     * combine min and max operators for each column after ':' in condition
     * @param columns: 'min: nombre, max: password'
     * @return min(column) as min_column, max(column) as max_column
     */
    public String getMinMaxSelection(String columns) {
        String build = "";
        String[] values = columns.split(",");
        for(String v: values) {
            String 
                minMax = v.split(":")[0].trim(),
                value  = v.split(":")[1].trim();
            if(minMax.equals("min")) {
                build += "min(" + value +") as min_" + value + ", ";
            } else if(minMax.equals("max")) {
                build += "max(" + value +") as max_" + value + ", ";
            }
        }
        return cleanValues(build, 2);
    }
    /**
     * clean the given {@link String} using the logic type
     * @param type: logic type
     * @param value: given string to clean
     * @return clean string
     */
    private String cleanByLogicType(String type, String value) {
        String cValue = "";
        if(type.equals("and")) {
            cValue = cleanValues(value, 4);
        } else if(type.equals("not")) {
            cValue = cleanValues(value, 4);
        } else if(type.equals("or")) {
            cValue = cleanValues(value, 3);
        }
        return cValue;
    }
    /**
     * combina la llave con el valor para el condicional sql
     * <br> pre: </br> if type is not use and for the next condition
     * @param condition:  las columnas a asignadas  el valor
     * @param type: logic operator type: and, or, not
     * @return las columnas asignadas el valor
     */
    public String getPrepareConditional(String condition, String type) {
        String build = "";
        String[] div = condition.split(",");
        for(String val: div) {
            if(type.toLowerCase().equals("not")) {
                build += "not " + val.split(":")[0] + "=" + "?" + " and";
            } else {
                build += val.split(":")[0] + "=" + "?" + " " + type;
            }
        }
        return cleanByLogicType(type, build);
    }
    /**
     * combina la llave con el valor para el condicional sql
     * @param condition: las columnas para asignar el valor
     * @return las columnas asignadas el valor
     */
    public String getNormalConditional(String condition, String type) {
        String build = "";
        String[] div = condition.split(",");
        for(String val: div) {
            if(type.toLowerCase().equals("not")) {
                build += "not " + val.split(":")[0] +
                    "="+ "'"+
                    val.split(":")[1].stripIndent()+
                    "'" + " and";
            } else {
                build += val.split(":")[0] +
                    "="+ "'"+
                    val.split(":")[1].stripIndent()+
                    "'" + " " + type;
            }
        }
        return cleanByLogicType(type, build);
    }
    /**
     * combina la llave con el valor para el condicional sql tipo in
     * @param columns: las columnas que representan la llave
     * @param condition: condicion o sentencia sql tipo SELECT
     * @param type: operador logico para la condicion
     * @return el condicional combinado tipo in
     */
    public String getInConditional(String columns, String condition, String type) {
        String 
            inCondition = "",
            build       = "";
        String[] myColumns = columns.split(",");
        if(condition.toLowerCase().startsWith("select")) {
            for(String c: myColumns) {
                if(type.toLowerCase().equals("not")) {
                    build += c + " not in (" + condition + ")" + " " + "and";
                } else {
                    build += c + " in (" + condition + ")" + " " + type;
                }
            }
        } else {
            String[] partition = condition.split(",");
            inCondition = "(";
            for(String p: partition) {
                inCondition += "'" + p.trim() + "', ";
            }
            String cCondition = cleanValues(inCondition, 3);
            for(String c: myColumns) {
                if(type.toLowerCase().equals("not")) {
                    build += c + " not in " + cCondition + "') " + "and";
                } else {
                    build += c + " in " + cCondition + "') " + type;
                }
            }
        }
        return cleanByLogicType(type, build);
    }
    /**
     * asigna los valores a las columnas del modelo separados por ","
     * @param nObject: objeto que contiene la información del modelo
     * @return los valores asignados a la columna
     */
    public String getAsignModelValues(String modelProperties) {
        String[] data = modelProperties.split("\n");
        String keyValue = "";
        for(int i = 1; i < data.length; i++) {
            String 
                key   = data[i].split(":")[0],
                value = data[i].split(":")[1];
            keyValue += key.stripIndent() +"="+ "'"+value.stripIndent()+"'"+ ", ";
        }
        return cleanValues(keyValue, 2);
    }
    /**
     * asignar el nombre de la tabla a las columnas del modelo
     * @param tb_name: nombre a asignar
     * @param model: modelo con las columnas
     * @return columnas asignadas el nombre de la tabla
     */
    public String asignTableNameToColumns(String modelProperties, String tbName) {
        String[] data = modelProperties.split("\n");
        String build = "";
        for(String l: data) {
            String key = l.split(":")[0];
            if(key.contains("pk") == false && key.contains("fk") == false) {
                build += tbName + "." + key + " as " + tbName +"_"+ key +", ";
            }
        }
        return cleanValues(build, 2);
    }
    /**
     * genera el condicional de la búsqueda por patrón
     * @param pattern: patrón a buscar
     * @param condition: columnas cuyo dato tiene el patrón
     * @param type: tipo de condicional para la sentencia sql
     * @return la condición del patrón
     */
    public String getPatternCondition(String pattern, String[] condition, String type) {
        String build = "";
        for(String k: condition) {
            if(type.toLowerCase().equals("not")) {
                build += k + " not like " + "'" + pattern + "'" + " and";
            } else {
                build += k + " like " + "'" + pattern + "'" + " " + type;
            }
        }
        return cleanByLogicType(type, build);
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
    public String innerJoinConditional(String localProperties, String refProperties, String localTable, String refTable) {
        String 
            pk    = getPkFk(refProperties).get("pk"),
            fk    = getPkFk(localProperties).get("fk"),
            build = "";
        build += localTable + "." + fk +"="+ refTable +"."+ pk;
        return build;
    }
    /**
     * obtiene la pk o fk de la lista de columnas del modelo
     * @param model: el modelo con la lista de columnas
     * @return pk o fk
     */
    public HashMap<String, String> getPkFk(String modelProperties) {
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
    public ArrayList<String> auxiliarmodelProperties(String modelProperties) {
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
    public HashMap<String, String> compareColumnName(String modelProperties, ResultSet rst) throws SQLException {
        HashMap<String, String> resultado = new HashMap<>();
        String localProperties = getModelColumns(modelProperties, true);
        ArrayList<String> 
            modelColumns = auxiliarmodelProperties(localProperties),
            tableColumns = getTableData(rst).get("columns");
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
    public HashMap<String, String> compareColumnType(String modelProperties, ResultSet rst) throws SQLException {
        String localTypes = getModelType(modelProperties, true);
        HashMap<String, String> resultado = new HashMap<>();
        ArrayList<String> 
            tableTypes = getTableData(rst).get("tipos"),
            modelTypes = auxiliarmodelProperties(localTypes);
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
