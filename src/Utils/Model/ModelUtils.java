package Utils.Model;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
/**
 * record class with helper methods for models
 */
public record ModelUtils() {
    /**
     *
     * trim the string for the given value
     * @param value: {@link String} to trim
     * @param spaces: value of trimming
     * @return the {@link String} trimmed
    */
    public String cleanValues(String value, int spaces) {
        StringBuffer buffer = new StringBuffer(value);
        if(buffer.length() > 0 && buffer.length()-spaces > 0) {
            return buffer.substring(0, buffer.length()-spaces);
        } else {
            return buffer.toString();
        }
    }
    /**
     * gets the model column from model data
     * @param modelData: the model data
     * @param includeKeys: true or false to include PK or FK
     * @return the model columns
     */
    public String getModelColumns(String modelData, boolean includeKeys) {
        String[] data = modelData.split("\n");
        StringBuffer
            colName = new StringBuffer(),
            modelCol = new StringBuffer();
        for(int i=0; i<data.length; ++i) {
            StringBuffer col = new StringBuffer(data[i].split(":", 2)[0].trim());
            if(!col.isEmpty()) {
                colName.append(col + ", ");
            }
        }
        String[] cols = colName.toString().split(",");
        if(includeKeys == false) {
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
    /**
     * get the table data
     * @param rst: {@link ResultSet} of the query execution
     * @param columns: table columns
     * @param types: table rows or types
     * @return the {@link Thread}
     */
    protected Thread constructTableData(ResultSet rst, List<String> columns, List<String> rows) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    while(rst.next()) {
                        String[] data = rst.getString(1).split("\n");
                        for(String k: data) {
                            columns.add(k);
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
                            rows.add(type.toString().trim());
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
     * get the table data
     * @param rst: {@link ResultSet}
     * @throws SQLException: error while trying to execute
     * @return {@link HashMap} with table data
     */
    public HashMap<String, List<String>> getTableData(ResultSet rst) throws SQLException {
        HashMap<String, List<String>> tableData = new HashMap<>();
        List<String>
            columns    = new ArrayList<>(),
            rows   = new ArrayList<>();
        Thread t = constructTableData(rst, columns, rows);
        try {
            t.start();
            t.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        tableData.put("columns", columns);
        tableData.put("rows", rows);
        return tableData;
    }
    /**
     * get the model types or rows
     * @param modelData: model data
     * @param includeKeys: true or false to include pk or fk
     * @return the model types or rows
     */
    public String getModelTypes(String modelData, boolean includeKeys) {
        String[] data   = modelData.split("\n");
        StringBuffer types = new StringBuffer();
        if(includeKeys == false) {
            for(int i = 1; i < data.length; i++) {
                StringBuffer type = new StringBuffer(data[i].split(":", 2)[1].trim());
                if(!type.isEmpty()) {
                    types.append("'");
                    types.append(type);
                    types.append("'");
                    types.append(",");
                }
            }
        }
        else {
            for(int i = 0; i < data.length; i++) {
                if(data[i].contains(":")) {
                    String myType = data[i].split(":", 2)[1].trim();
                    types.append("'");
                    types.append(myType);
                    types.append("'");
                    types.append(",");
                }

            }
        }
        return cleanValues(types.toString(), 1);
    }
    /**
     * search the type of the given column
     * @param modelData: model data
     * @param column: column to search
     * @return the index or -1 if nothing is returned
     */
    public int getColumnType(String modelData, String column) {
        String[] modelCols = getModelColumns(modelData, true).split(",");
        int res = -1;
        for(int i=0; i<modelCols.length; ++i) {
            if(modelCols[i].contains(column)) {
                res = i;
                break;
            }
        }
        return res;
    }
    /**
     * get the pk of fk from model data
     * @param modelData: the model data
     * @return a {@link HashMap} with pk or fk
     */
    public HashMap<String, String> getPkFk(String modelData) {
        HashMap<String, String> keys = new HashMap<String, String>();
        String[] data = modelData.split("\n");
        for(String d: data) {
            String key = d.split(":", 2)[0];
            if(key.contains("_pk")) {
                keys.put("pk", key);
            }
            if(key.contains("_fk")) {
                keys.put("fk", key);
            }
        }
        return keys;
    }
    /**
     * populate a {@link HashMap} with fields to rename, add & delete
     * @param modelCols: model columns
     * @param tableCols: table columns
     * @param comparators: {@link HashMap} to populate
     * @return the {@link Thread} of execution
     */
    protected Thread compareProperties(List<String> modelCols, List<String> tableCols,
            HashMap<String, StringBuffer> comparators) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                if(modelCols.size() == tableCols.size()) {
                    StringBuffer rename = new StringBuffer();
                    for(int i=0; i<modelCols.size(); ++i) {
                        if(tableCols.get(i).equals(modelCols.get(i)) == false) {
                            rename.append(modelCols.get(i));
                            rename.append(":");
                            rename.append(tableCols.get(i));
                            rename.append(", ");
                        }
                    }
                    comparators.put("rename", rename);
                }
                else if(modelCols.size() > tableCols.size()) {
                    StringBuffer add = new StringBuffer();
                    for(int i=0; i<modelCols.size(); i++) {
                        if(!tableCols.contains(modelCols.get(i))) {
                            add.append(modelCols.get(i));
                            add.append(", ");
                        }
                    }
                    comparators.put("add", add);
                }
                else if(tableCols.size() > modelCols.size()) {
                    StringBuffer delete = new StringBuffer();
                    for(int i=0; i<tableCols.size(); i++) {
                        if(modelCols.contains(tableCols.get(i)) == false) {
                            delete.append(tableCols.get(i));
                            delete.append(":");
                            delete.append(i);
                            delete.append(", ");
                        }
                    }
                    comparators.put("delete", delete);
                }
            }
        });
        return t;
    }

    /**
     * create an array of Strings with model columns
     * @param modelData: the model data
     * @return an {@link List} with model columns
     */
    public List<String> getModelColumnList(String modelData) {
        List<String> columns =  new ArrayList<>();
        String[] modelCols = modelData.split(",");
        for(String k: modelCols) {
            columns.add(k);
        }
        return columns;
    }
    /**
     * compare the model column with the table column to find which one to add, delete or rename.
     * @param modelData: the model data.
     * @param rst: object with table data.
     * @throws SQLException: exception while trying to execute
     * @return a {@link HashMap} with the compared columns
     */
    public HashMap<String, StringBuffer> compareColumnName(String modelData, ResultSet rst)
            throws SQLException {
        HashMap<String, StringBuffer> comparators = new HashMap<>();
        List<String> 
            modelCols = getModelColumnList(getModelColumns(modelData, true)),
            tableCols = getTableData(rst).get("columns");
        Thread t = compareProperties(modelCols, tableCols, comparators);
        try {
            t.start();
            t.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return comparators;
    }
    /**
     * compare the model type with the table row to find which one to modify.
     * @param modelData: the model data
     * @param rst: object with table data
     * @throws SQLException: exception while trying to execute
     * @return a {@link HashMap} with the rows to modify
     */
    public HashMap<String, StringBuffer> compareColumnType(String modelData, ResultSet rst)
            throws SQLException {
        HashMap<String, StringBuffer> result = new HashMap<>();
        List<String> 
            modelTypes = getModelColumnList(
                    getModelTypes(modelData, true)
            ),
            tableTypes = getTableData(rst).get("rows");
        if(modelTypes.size() == tableTypes.size()) {
            StringBuffer rename = new StringBuffer();
            for(int i=0; i<modelTypes.size(); ++i) {
                String cleanTypes = modelTypes.get(i).replace("'", "");
                if(tableTypes.contains(cleanTypes) == false) {
                    rename.append(cleanTypes);
                    rename.append(":");
                    rename.append(i);
                    rename.append(", ");
                }
            }
            result.put("rename", rename);
        }
        return result;
    }
}
