package Utils.Formats;

/**
 * class that represents the condition param
 */
public class ParamValue {

    private String type;
    private String[] columns; 
    private String[] values;
    /**
     */
    public ParamValue(String column, String value, String type) {
        String[] 
            c = {column},
            v = {value};
        columns = c;
        values = v;
        this.type = type;
        validationSingle();
    }
    public ParamValue(String column, String value) {
        String[] 
            c = {column},
            v = {value};
        columns = c;
        values = v;
        validationSingle();
    }
    /**
     * <br>: pre: </br> columns and values share the same length and order
     */
    public ParamValue(String[] columns, String[] values, String type) {
        this.columns = columns;
        this.values = values;
        this.type = type;
        validatePlural();
    }
    public ParamValue(String[] columns, String[] values) {
        this.columns = columns;
        this.values = values;
        validatePlural();
    }

    public String getColumn() {
        String column = "";
        if(columns.length == 1) {
            column = columns[0];
        }
        return column;
    }
    public String getValue() {
        String value = "";
        if(values.length == 1) {
            value = values[0];
        }
        return value;
    }
    public String getType() {
        return type;
    }

    public String[] getColumns() {
        return columns;
    }
    public String[] getValues() {
        return values;
    }
    public String getCombination() throws Exception {
        StringBuffer b = new StringBuffer();
        if((getColumns() != null && getColumns().length > 0) &&
                (getValues() != null && getValues().length > 0) &&
                (getColumns().length == getValues().length)) {
            int length = getColumns().length;
            StringBuffer h = new StringBuffer();
            for(int i=0; i<length; ++i) {
                if(!getColumns()[i].isEmpty() || !getValues()[i].isEmpty()) {
                    h.append(getColumns()[i]);
                    h.append(":");
                    h.append(getValues()[i]);
                    h.append(", ");
                } else {
                    throw new Exception(
                            "[ ERROR ]: columns and values/rows must have the same length and order"
                    );
                }
            }
            b.append(h.substring(0, h.length()-2));
        } else if(!getColumn().isEmpty() && !getValue().isEmpty()) {
            b.append(getColumn());
            b.append(":");
            b.append(getValue());
        }
        return b.toString();
    }
    /**
     * assert validation
     */
    private void validationSingle() {
        try {
            if(getColumn().isEmpty() || getColumn() == null) {
                throw new Exception("[ ERROR ]: column cannot be null");
            }
            if(getValue().isEmpty() || getValue() == null) {
                throw new Exception("[ ERROR ]: row or value cannot be null");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void validatePlural() {
        try {
            if(getColumns().length != getValues().length) {
                throw new Exception(
                        "[ ERROR ]: columns and values must have the same length and same order"
                );
            }
            if(getColumns().length == 0 || getColumns() == null) {
                throw new Exception("[ ERROR ]: columns cannot be null");
            }
            if(getValues().length == 0 || getValues() == null) {
                throw new Exception("[ ERROR ]: Values cannot be null");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
