package Conexion.Migration;

import java.sql.ResultSet;
import java.sql.Statement;

import Config.DbConfig;
import Model.ModelMethods;

public class MigrationDAO<T> {

    /**
     * ejecutor de la sentencia sql
     */
    private MigrationExecution migration_execution;
    /**
     * constructor
     */
    public MigrationDAO(String nTableName, DbConfig miConfig) {
        migration_execution = new MigrationExecution(nTableName, miConfig);
    }
    /**
     * muestra los datos de la tabla
     * @param model: modelo con los datos
     * @return resultado de la consulta
     */
    public String ShowTableData(ModelMethods model) {
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = migration_execution.ExecuteShowTableData(stm);
        } catch (Exception e) {
            System.err.println(e);
        
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                stm = null;
            }
        }
        return "";
    }
}
