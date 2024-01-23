package Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ModelBuilderMethods<T>{
    /**
     * validar los datos del resultado de la consulta sql
     * @param rst: resultados de la consulta sql
     * @param capacity: tamaño de la lista de resultados
     * @throws SQLException: error al ejecutar la consulta sql
     * @return la lista de resultados
     */
    public String[] resultDataValidator(ResultSet rst, int capacity) throws SQLException;
    /**
     * crea el usuario con los datos del ResultSet
     * @param rst: resultados de la consulta sql
     * @param capacity: tamaño de la lista de resultados
     * @throws SQLException: error el ejecutar la consulta sql
     * @return el objeto T con los datos de la consulta sql
     */
    public T createFromRST(ResultSet rst, int capacity) throws SQLException;
    /**
     * crea el usuario con los datos de un String
     * @param datos: String con los datos
     * @return el objeto T con los datos del String
     */
    public T createFromSTR(String datos);
}
