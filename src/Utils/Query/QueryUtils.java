package Utils.Query;

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
     * get the value of -> name: value.
     * @param condition: where clause condition
     * @return clean condition for where clause
     */
    public String getValueOfCondition(ParamValue condition) {
        StringBuffer val = new StringBuffer();
        String[] values = condition.getValues();
        for(int i=0; i<values.length; ++i) {
            val.append(values[i]);
            val.append(",");
        }
        return cleanValues(val.toString(), 1);
    }
    /**
     * combine min and max operators for each column after ':' in condition
     * @param columns: 'min: name, max: password'
     * @return min(column) as min_column, max(column) as max_column
     */
    public String getMinMaxSelection(ParamValue columns) {
        StringBuffer build = new StringBuffer();
        String[] 
            cols = columns.getColumns(),
            vals = columns.getValues();
        for(int i=0; i<cols.length; ++i) {
            StringBuffer 
                minMax = new StringBuffer(cols[i]),
                       value  = new StringBuffer(vals[i]);
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
        StringBuffer b =  new StringBuffer();
        String type = params.getType();
        String[] cols = params.getColumns();
        for(int i=0; i<cols.length; ++i) {
            if(type.toLowerCase().equals("not")) {
                b.append(" NOT ");
                b.append(cols[i]);
                b.append("=? AND");
            } else {
                b.append(" ");
                b.append(cols[i]);
                b.append("=? ");
                b.append(type.toUpperCase());
            }
        }
        return cleanByLogicType(
                params.getType(),
                b.toString()
        );
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
        String type = params.getType();
        String[] 
            cols = params.getColumns(),
            vals = params.getValues();
        StringBuffer b = new StringBuffer();
        for(int i=0; i<cols.length; ++i) {
            if(type.toLowerCase().equals("not")) {
                b.append(" NOT ");
                b.append(cols[i]);
                b.append("='");
                b.append(vals[i]);
                b.append("' AND ");
            } else {
                b.append(" ");
                b.append(cols[i]);
                b.append("='");
                b.append(vals[i]);
                b.append("' ");
                b.append(type.toUpperCase());
            }
        }
        return cleanByLogicType(
                params.getType(),
                b.toString()
        );
    }
    /**
     * creates the in conditional for where clause
     * @param columns: the search columns
     * @param condition: the conditional value
     * @param type: logic type for where clause
     * @return the in conditional for where clause
     */
    public String getInConditional(String columns, String condition, String type) {
        String 
            inCondition  = "",
            build        = "",
            cInCondition = "";
        String[] cols = columns.split(",");
        if(condition.toLowerCase().startsWith("select")) {
            for(String c: cols) {
                if(type.toLowerCase().equals("not")) {
                    build += c + " NOT IN (" + condition + ") AND ";
                } else {
                    build += c + " IN (" + condition + ") " + type.toUpperCase();
                }
            }
        } else {
            String[] properties = condition.split(",");
            inCondition = "('";
            for(String p: properties) {
                inCondition += p.trim() + "', " ;
            }
            cInCondition = cleanValues(inCondition.toString(), 3);
            for(String c: cols) {
                if(type.toLowerCase().equals("not")) {
                    build += " " + c + " NOT IN " + cInCondition + "') AND";
                } else {
                    build += " " + c + " IN " + cInCondition + "') " + type.toUpperCase();
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
        String build = "";
        for(String k: condition) {
            if(type.toLowerCase().equals("not")) {
                build += k + " not like " + "'" + pattern + "'" + " and";
            } else {
                build += k + " like " + "'" + pattern + "'" + " " + type;
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
        if(pk != null || fk != null) {
            build.append(invokedT);
            build.append(".");
            build.append(fk); 
            build.append("=");
            build.append(foreignT);
            build.append(".");
            build.append(pk);
        } else {
            System.out.println("[ ERROR ]:  no pk or fk in model");
        }
        return build.toString();
    }
}
