package Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ModelBuilderMethods<T>{
    /**
     * used to get the {@link ResultSet} data
     */
    public default String[] resultDataValidator(ResultSet rst, int capacity) throws SQLException {
        String[] data = new String[capacity + 1];
        for(int i = 1; i < data.length; i++) {
            data[i] = rst.getString(i);
        }
        if(data.length == 1) {
            System.err.println("[ ERROR ]: must bee greater than 1");
        }
        return data;
    }
    /**
     * creates an user from {@link ResultSet} data
     */
    public T createFromRST(ResultSet rst, int capacity) throws Exception;
    /**
     * crea el usuario con los datos de un String
     * @param datos: String con los datos
     * @return el objeto T con los datos del String
     */
    public T createFromSTR(String datos) throws Exception;
}
