package Utils.Query;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import Utils.Formats.ParamValue;
import Utils.Model.ModelUtils;

/**
 * record class with helper methods for query
 */
public record QueryUtils() {
    private final static ModelUtils modelUtils = new ModelUtils();
    /**
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
     * gets the query or {@link ResultSet} metadata colum count
     * @param metadata: query or {@link ResultSet} metadata
     * @throws SQLException: exception while trying to execute
     * @return the column count
     */
    public int getMetadataNumColumns(ResultSetMetaData metadata) throws SQLException {
        return metadata.getColumnCount();
    }
    /**
     * get the value of -> name: value.
     * @param condition: where clause condition
     * @return clean condition for where clause
     */
    public String getValueOfCondition(ParamValue condition) {
        String[] properties = condition.getCombination().split(", ");
        StringBuffer val = new StringBuffer();
        for(String p: properties) {
            val.append(p.split(":")[1].trim());
            val.append(",");
        }
        return cleanValues(val.toString(), 1);
    }
    /**
     * combine min and max operators for each column after ':' in condition
     * @param columns: 'min: name, max: password'
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
                build.append( "min(");
                build.append(value);
                build.append(") as min_");
                build.append(value);
                build.append(", ");
            } else if(minMax.indexOf("max") != -1) {
                build.append("max(");
                build.append(value);
                build.append(") as max_");
                build.append(value);
                build.append(", ");
            }
        }
        return cleanValues(build.toString(), 2);
    }
    /**
     * creates the conditional for a prepared statement
     * <br> pre: </br> if type is not use and for the next condition
     * @param condition: the condition value
     * @param type: logic operator type: and, or, not
     * @return the condition for where clause
     */
    public String getPrepareConditional(ParamValue params) {
        StringBuffer build = new StringBuffer();
        String
            condition = params.getCombination(),
            type = params.getType();
        String[] properties = condition.split(",");
        for(String p: properties) {
            if(type.toLowerCase().equals("not")) {
                build.append("not ");
                build.append(p.split(":")[0]);
                build.append("=");
                build.append("?");
                build.append(" and");
            } else {
                build.append(p.split(":")[0]);
                build.append("=");
                build.append("? ");
                build.append(type);
            }
        }
        return cleanByLogicType(type, build.toString());
    }
    /**
     * get the update set values with question mark for the prepared update statement
     * @param modelData: model data
     * @return the set values
    */
    public String getPreparedUpdateValues(String modelData) {
        String[] modelColumns = modelUtils.getModelColumns(
                modelData, true).split(",");
        StringBuffer b = new StringBuffer();
        for(String m: modelColumns) {
            b.append(m);
            b.append("=?");
            b.append(", ");
        }
        return b.substring(0, b.length()-2);
    }
    /**
     * creates the conditional for where clause for a statement
     * @param condition: the condition value
     * @return the condition for where clause
     */
    public String getNormalConditional(ParamValue params) {
        StringBuffer build = new StringBuffer();
        String 
            condition = params.getCombination(),
            type = params.getType();
        String[] properties = condition.split(",");
        for(String p: properties) {
            if(type.toLowerCase().equals("not")) {
                build.append("not ");
                build.append(p.split(":")[0]);
                build.append("='");
                build.append(p.split(":")[1].trim());
                build.append("' and");
            } else {
                build.append(p.split(":")[0]);
                build.append("='");
                build.append(p.split(":")[1].trim());
                build.append("' ");
                build.append(type);
            }
        }
        return cleanByLogicType(type, build.toString());
    }
    /**
     * creates the in conditional for where clause
     * @param columns: the search columns
     * @param condition: the conditional value
     * @param type: logic type for where clause
     * @return the in conditional for where clause
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
                    build.append(c);
                    build.append(" NOT IN (");
                    build.append(condition);
                    build.append(") AND");
                } else {
                    build.append(c);
                    build.append(" IN (");
                    build.append(condition);
                    build.append(") ");
                    build.append(type);
                }
            }
        } else {
            String[] properties = condition.split(",");
            inCondition.append("(");
            for(String p: properties) {
                inCondition.append("'");
                inCondition.append(p.trim());
                inCondition.append("', ");
            }
            cInCondition.append(cleanValues(inCondition.toString(), 3));
            for(String c: cols) {
                if(type.toLowerCase().equals("not")) {
                    build.append(c);
                    build.append(" NOT IN ");
                    build.append(cInCondition);
                    build.append("') AND");
                } else {
                    build.append(c);
                    build.append(" IN ");
                    build.append(cInCondition);
                    build.append("') "); 
                    build.append(type);
                }
            }
        }
        return cleanByLogicType(type, build.toString());
    }
    /**
     * assign the type to the column for the query statement
     * @return the model types or values
     */
    public String getAssignModelValues(String modelData) {
        String[] data = modelData.split("\n");
        StringBuffer assign = new StringBuffer();
        for(int i=1; i<data.length; ++i) {
            String 
                key   = data[i].split(":")[0],
                value = data[i].split(":")[1];
            assign.append(key.trim());
            assign.append("='");
            assign.append(value.trim());
            assign.append("', ");
        }
        return cleanValues(assign.toString(), 2);
    }
    /**
     * creates the query statement alias using table_name
     * @param tbName: table name to create alias
     * @return a {@link String} with query statement using alias
     */
    public String assignTableNameToColumns(String modelData, String tbName) {
        String[] data = modelData.split("\n");
        StringBuffer build = new StringBuffer();
        for(String d: data) {
            String key = d.split(":")[0];
            if(key.contains("pk") == false && key.contains("fk") == false) {
                build.append(tbName);
                build.append(".");
                build.append(key);
                build.append(" as ");
                build.append(tbName);
                build.append("_");
                build.append(key);
                build.append(", ");
            }
        }
        return cleanValues(build.toString(), 2);
    }
    /**
     * creates the pattern for select statement
     * @param pattern: pattern to search
     * @param condition: condition value
     * @param type: logic type for where clause
     * @return the pattern
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
     * creates the inner join conditional for where clause.
     * <br> pre: </br> the condition uses the pk from foreign model and fk from the invoke model
     * @param invokedM: model that has the fk that references the foreign model pk
     * @param foreignM: foreign model that has the pk references the invokedM model fk
     * @param foreignT: foreign table name
     * @return inner join conditional
     */
    public String getInnerJoinConditional(String invokedM, String foreignM, String invokedT, String foreignT) {
        StringBuffer build = new StringBuffer();
        String 
            pk    = modelUtils.getPkFk(foreignM).get("pk"),
            fk    = modelUtils.getPkFk(invokedM).get("fk");
        if(!pk.isEmpty() || !fk.isEmpty()) {
            build.append(invokedT);
            build.append(".");
            build.append(fk); 
            build.append("=");
            build.append(foreignT);
            build.append(".");
            build.append(pk);
        }
        return build.toString();
    }
}
