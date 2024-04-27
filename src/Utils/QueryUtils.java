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
     * @param value: los valores a limpiar
     * @param spaces: valor especifico
     * @return los valores limpios
    */
    public String cleanValues(String value, int spaces) {
        StringBuffer buffer = new StringBuffer(value);
        if(buffer.length() > 0) {
            return buffer.substring(0, buffer.length()-spaces);
        } else {
            return buffer.toString();
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
        StringBuffer
            colName = new StringBuffer(),
            modelCol = new StringBuffer();
        for(int i=0; i<data.length; ++i) {
            StringBuffer col = new StringBuffer(data[i].split(":")[0].trim());
            if(!col.isEmpty()) {
                colName.append(col + ", ");
            }
        }
        String[] cols = colName.toString().split(",");
        if(includePKFK == false) {
            for(int i=1; i<cols.length; ++i) {
                modelCol.append(cols[i].stripIndent() + ",");
            }
        }
        else {
            for(int i=0; i<cols.length; ++i) {
                modelCol.append(cols[i].stripIndent() + ",");
            }
        }
        return cleanValues(modelCol.toString(), 2);
    }
    protected Thread combineTableProperties(ResultSet rst, ArrayList<String> cols, ArrayList<String> types) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    while(rst.next()) {
                        String[] data = rst.getString(1).split("\n");
                        for(String k: data) {
                            cols.add(k);
                        }
                        String[] 
                            tTypes     = rst.getString(2).split("\n"),
                            tNull      = rst.getString(3).split("\n"),
                            tKeyCol    = rst.getString(4).split("\n"),
                            tExtra     = rst.getString(6).split("\n");
                        for(int k=0; k<tTypes.length; ++k) {
                            StringBuffer type = new StringBuffer();
                            if(tTypes[k] != null) {
                                type .append(tTypes[k]);
                            }
                            if(tNull[k] != null && tNull[k].contains("NO")) {
                                type.append(" not null");
                            }
                            if(tKeyCol[k] != null) {
                                if(tKeyCol[k].contains("PRI")) {
                                    type.append(" unique primary key");
                                }
                                if(tKeyCol[k].contains("MUL")) {
                                    type.append(" foreign key");
                                }
                                if(tKeyCol[k].contains("UNI")) {
                                    type.append(" unique");
                                }
                            }
                            if(tExtra[k] != null) {
                                type .append(" " + tExtra[k]);
                            }
                            types.add(type.toString().trim());
                        }

                    }
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        return t;
    }
    /**
     * obtener las columnas de la tabla
     * @param rst: resultado de la consulta a la bd
     * @throws SQLException: error while trying to execute
     * @return la lista de columnas de la tabla
     */
    public HashMap<String, ArrayList<String>> getTableData(ResultSet rst) throws SQLException {
        HashMap<String, ArrayList<String>> tableData = new HashMap<>();
        ArrayList<String>
            cols    = new ArrayList<>(),
            types   = new ArrayList<>();
        Thread t = this.combineTableProperties(rst, cols, types);
        try {
            t.start();
            t.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        tableData.put("columns", cols);
        tableData.put("tipos", types);
        return tableData;
    }
    /**
     * obtener el tipo por cada columna del modelo
     * @param modelProperties: properties of model
     * @param includePKFK: include pk or fk
     * @return los tipos de dato por columna
     */
    public String getModelType(String modelProperties, boolean includePKFK) {
        String[] data   = modelProperties.split("\n");
        StringBuffer types = new StringBuffer();
        if(includePKFK == false) {
            for(int i = 1; i < data.length; i++) {
                StringBuffer type = new StringBuffer(data[i].split(":")[1].trim());
                if(!type.isEmpty()) {
                    types.append("'" + type + "'" + ",");
                }
            }
        }
        else {
            for(int i = 0; i < data.length; i++) {
                String myType = data[i].split(":")[1].stripIndent();
                types.append("'" + myType + "'" + ",");
            }
        }
        return cleanValues(types.toString(), 1);
    }
    /**
     * buscar el typo de dato de una columna
     * @param modelProperties: propiedades del modelo
     * @param column: columna a buscar el tipo de dato
     * @return el indice o index del typo de dato
     */
    public int searchColumnType(String modelProperties, String column) {
        String[] modelCols = getModelColumns(modelProperties, true).split(",");
        int res = 0;
        for(int i=0; i<modelCols.length; ++i) {
            if(modelCols[i].contains(column)) {
                res = i;
            }
        }
        return res;
    }
    /**
     * get the value of -> name: value
     * @param condition: los valores a limpiar
     * @return los valores limpios y combinados
     */
    public String getValueOfCondition(String condition) {
        String[] properties = condition.split(", ");
        StringBuffer val = new StringBuffer();
        for(String p: properties) {
            val.append(p.split(":")[1].trim() +",");
        }
        return cleanValues(val.toString(), 1);
    }
    /**
     * combine min and max operators for each column after ':' in condition
     * @param columns: 'min: nombre, max: password'
     * @return min(column) as min_column, max(column) as max_column
     */
    public String getMinMaxSelection(String columns) {
        StringBuffer build = new StringBuffer();
        String[] properties = columns.split(",");
        for(String v: properties) {
            StringBuffer 
                minMax = new StringBuffer(v.split(":")[0].trim()),
                value  = new StringBuffer(v.split(":")[1].trim());
            if(minMax.indexOf("min") != -1) {
                build.append( "min(" + value +") as min_" + value + ", ");
            } else if(minMax.indexOf("max") != -1) {
                build.append("max(" + value +") as max_" + value + ", ");
            }
        }
        return cleanValues(build.toString(), 2);
    }
    /**
     * clean the given {@link String} using the logic type
     * @param type: logic type
     * @param value: given string to clean
     * @return clean string
     */
    public String cleanByLogicType(String type, String value) {
        if(type.equals("and")) {
            value = cleanValues(value, 4);
        } else if(type.equals("not")) {
            value = cleanValues(value, 4);
        } else if(type.equals("or")) {
            value = cleanValues(value, 3);
        }
        return value;
    }
    /**
     * combina la llave con el valor para el condicional sql
     * <br> pre: </br> if type is not use and for the next condition
     * @param condition:  las columnas a asignadas  el valor
     * @param type: logic operator type: and, or, not
     * @return las columnas asignadas el valor
     */
    public String getPrepareConditional(String condition, String type) {
        StringBuffer build = new StringBuffer();
        String[] properties = condition.split(",");
        for(String p: properties) {
            if(type.toLowerCase().equals("not")) {
                build.append("not " + p.split(":")[0] + "=" + "?" + " and");
            } else {
                build.append(p.split(":")[0] + "=" + "?" + " " + type);
            }
        }
        return cleanByLogicType(type, build.toString());
    }
    /**
     * creates the preparedUpdate values combination with question mark: name=?
    */
    public String getPreparedUpdateValues(String modelProperties) {
        String[] modelColumns = getModelColumns(modelProperties, true).split(",");
        String b = "";
        for(String m: modelColumns) {
            b += m + "=?" + ", ";
        }
        return b.substring(0, b.length()-2);
    }
    /**
     * combina la llave con el valor para el condicional sql
     * @param condition: las columnas para asignar el valor
     * @return las columnas asignadas el valor
     */
    public String getNormalConditional(String condition, String type) {
        StringBuffer build = new StringBuffer();
        String[] properties = condition.split(",");
        for(String p: properties) {
            if(type.toLowerCase().equals("not")) {
                build.append(
                        "not " + p.split(":")[0] +
                        "="+ "'"+
                        p.split(":")[1].stripIndent()+
                        "'" + " and"
                );
            } else {
                build.append(
                        p.split(":")[0] +
                        "="+ "'"+
                        p.split(":")[1].stripIndent()+
                        "'" + " " + type
                );
            }
        }
        return cleanByLogicType(type, build.toString());
    }
    /**
     * combina la llave con el valor para el condicional sql tipo in
     * @param columns: las columnas que representan la llave
     * @param condition: condicion o sentencia sql tipo SELECT
     * @param type: operador logico para la condicion
     * @return el condicional combinado tipo in
     */
    public String getInConditional(String columns, String condition, String type) {
        StringBuffer 
            inCondition  = new StringBuffer(),
            build        = new StringBuffer(),
            cInCondition = new StringBuffer();
        String[] cols = columns.split(",");
        if(condition.toLowerCase().startsWith("select")) {
            for(String c: cols) {
                if(type.toLowerCase().equals("not")) {
                    build.append(c + " NOT IN (" + condition + ")" + " " + "AND");
                } else {
                    build.append(c + " IN (" + condition + ")" + " " + type);
                }
            }
        } else {
            String[] properties = condition.split(",");
            inCondition.append("(");
            for(String p: properties) {
                inCondition.append("'" + p.trim() + "', ");
            }
            cInCondition.append(cleanValues(inCondition.toString(), 3));
            for(String c: cols) {
                if(type.toLowerCase().equals("not")) {
                    build.append(c + " NOT IN " + cInCondition + "') " + "AND");
                } else {
                    build.append(c + " IN " + cInCondition + "') " + type);
                }
            }
        }
        return cleanByLogicType(type, build.toString());
    }
    /**
     * asigna los valores a las columnas del modelo separados por ","
     * @param nObject: objeto que contiene la información del modelo
     * @return los valores asignados a la columna
     */
    public String getAsignModelValues(String modelProperties) {
        String[] data = modelProperties.split("\n");
        StringBuffer assing = new StringBuffer();
        for(int i=1; i<data.length; ++i) {
            String 
                key   = data[i].split(":")[0],
                value = data[i].split(":")[1];
            assing.append(
                    key.stripIndent() +"="+ "'"+value.stripIndent()+"'"+ ", "
            );
        }
        return cleanValues(assing.toString(), 2);
    }
    /**
     * asignar el nombre de la tabla a las columnas del modelo
     * @param tb_name: nombre a asignar
     * @param model: modelo con las columnas
     * @return columnas asignadas el nombre de la tabla
     */
    public String asignTableNameToColumns(String modelProperties, String tbName) {
        String[] data = modelProperties.split("\n");
        StringBuffer build = new StringBuffer();
        for(String d: data) {
            String key = d.split(":")[0];
            if(key.contains("pk") == false && key.contains("fk") == false) {
                build.append(
                        tbName + "." + key + " as " + tbName +"_"+ key +", "
                );
            }
        }
        return cleanValues(build.toString(), 2);
    }
    /**
     * genera el condicional de la búsqueda por patrón
     * @param pattern: patrón a buscar
     * @param condition: columnas cuyo dato tiene el patrón
     * @param type: tipo de condicional para la sentencia sql
     * @return la condición del patrón
     */
    public String getPatternCondition(String pattern, String[] condition, String type) {
        StringBuffer build = new StringBuffer();
        for(String k: condition) {
            if(type.toLowerCase().equals("not")) {
                build.append(k + " not like " + "'" + pattern + "'" + " and");
            } else {
                build.append(k + " like " + "'" + pattern + "'" + " " + type);
            }
        }
        return cleanByLogicType(type, build.toString());
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
    public String innerJoinConditional(String localProperties, String refProperties, String localTable,
            String refTable) {
        StringBuffer build = new StringBuffer();
        String 
            pk    = getPkFk(refProperties).get("pk"),
            fk    = getPkFk(localProperties).get("fk");
        if(!pk.isEmpty() || !fk.isEmpty()) {
            build.append(localTable + "." + fk +"="+ refTable +"."+ pk);
        }
        return build.toString();
    }
    /**
     * obtiene la pk o fk de la lista de columnas del modelo
     * @param model: el modelo con la lista de columnas
     * @return pk o fk
     */
    public HashMap<String, String> getPkFk(String modelProperties) {
        HashMap<String, String> pkfk = new HashMap<String, String>();
        String[] data = modelProperties.split("\n");
        for(String d: data) {
            String key = d.split(":")[0];
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
        String[] modelCols = modelProperties.split(",");
        for(String k: modelCols) {
            columns.add(k);
        }
        return columns;
    }
    protected Thread compareProperties(ArrayList<String> modelCols, ArrayList<String> tableCols,
            HashMap<String, StringBuffer> comparations) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                if(modelCols.size() == tableCols.size()) {
                    StringBuffer rename = new StringBuffer();
                    for(int i=0; i<modelCols.size(); ++i) {
                        if(tableCols.get(i).equals(modelCols.get(i)) == false) {
                            rename.append(
                                    modelCols.get(i) + ":" + tableCols.get(i) + ", "
                            );
                        }
                    }
                    comparations.put("rename", rename);
                }
                else if(modelCols.size() > tableCols.size()) {
                    StringBuffer agregar = new StringBuffer();
                    for(int i=0; i<modelCols.size(); i++) {
                        if(!tableCols.contains(modelCols.get(i))) {
                            agregar.append(modelCols.get(i) + ", ");
                        }
                    }
                    comparations.put("agregar", agregar);
                }
                else if(tableCols.size() > modelCols.size()) {
                    StringBuffer eliminar = new StringBuffer();
                    for(int i=0; i<tableCols.size(); i++) {
                        if(modelCols.contains(tableCols.get(i)) == false) {
                            eliminar.append(
                                    tableCols.get(i) + ":" + i + ", "
                            );
                        }
                    }
                    comparations.put("eliminar", eliminar);
                }
            }
        });
        return t;
    }
    /**
     * compara el nombre de las columnas del modelo con la tabla y ordena entre: agregar, eliminar y renombrar
     * @param model_properties: propiedades del modelo
     * @param rst: resultado de la ejecución de la sentencia sql
     * @return retorna las columnas a eliminar, agregar o renombrar
     */
    public HashMap<String, StringBuffer> compareColumnName(String modelProperties, ResultSet rst)
            throws SQLException {
        HashMap<String, StringBuffer> comparations = new HashMap<>();
        ArrayList<String> 
            modelCols = auxiliarmodelProperties(
                    getModelColumns(modelProperties, true)
            ),
            tableCols = getTableData(rst).get("columns");
        Thread t = this.compareProperties(modelCols, tableCols, comparations);
        try {
            t.start();
            t.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return comparations;
    }
    /**
     * compara los tipos de datos del modelo con la tabla y regresa el distinto
     * @param model_properties: propiedades del modelo
     * @param rst: resultado de la consulta sql 
     * @throws SQLException: error de la consulta sql
     * @return las columnas con el tipo de dato a cambiar
     */
    public HashMap<String, StringBuffer> compareColumnType(String modelProperties, ResultSet rst)
            throws SQLException {
        HashMap<String, StringBuffer> resultado = new HashMap<>();
        ArrayList<String> 
            modelTypes = auxiliarmodelProperties(
                    getModelType(modelProperties, true)
            ),
            tableTypes = getTableData(rst).get("tipos");
        if(modelTypes.size() == tableTypes.size()) {
            StringBuffer rename = new StringBuffer();
            for(int i=0; i<modelTypes.size(); ++i) {
                String cleanTypes = modelTypes.get(i).replace("'", "");
                if(tableTypes.contains(cleanTypes) == false) {
                    rename.append(cleanTypes + ":" + i + ", ");
                }
            }
            resultado.put("rename", rename);
        }
        return resultado;
    }
}
