package Utils.Formats;

/**
 * class that represents the condition param
 */
public class ParamValue {

    private String column;
    private String value;
    private String[] columns; 
    private String[] values;
    public ParamValue(String column, String value) {
        this.column = column;
        this.value = value;
    }
    /**
     * <br>: pre: </br> columns & values share the same length & share the same order
     */
    public ParamValue(String[] columns, String[] values) {
        this.columns = columns;
        this.values = values;
    }

    public String getColumn() {
        return column;
    }
    public String getValue() {
        return value;
    }

    public String[] getColumns() {
        return columns;
    }
    public String[] getValues() {
        return values;
    }
    public String getCombination() {
        StringBuffer b = new StringBuffer();
        if((getColumns() != null && getColumns().length > 0) &&
                (getValues() != null && getValues().length > 0) &&
                (getColumns().length == getValues().length)) {
            int length = getColumns().length;
            String h = "";
            for(int i=0; i<length; ++i) {
                h += getColumns()[i] + ": " + getValues()[i] + ", ";
            }
            b.append(h.substring(0, h.length()-2));
        } else if(!getColumn().isEmpty() && !getValue().isEmpty()) {
            b.append(getColumn());
            b.append(": ");
            b.append(getValue());
        }
        return b.toString();
    }
}
