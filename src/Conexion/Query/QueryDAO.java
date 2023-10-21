package Conexion.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Config.DbConfig;
import Model.ModelBuilderMethods;
import Model.ModelMethods;
import Utils.QueryUtils;

/**
 * clase para realizar el DAO de las consultas sql según el modelo
 */
public class QueryDAO<T> {
    /**
     * auxiliar para la ejecución de las querys
     */
    private QueryExecution query_execution;
    /**
     * herramientas para la creacion de las querys
     */
    private QueryUtils query_util;
    /**
     * Data Acces Object of GenericObject
     * inicializa el conector de mysql
     */
    public QueryDAO(String table_name, DbConfig miConfig) {
        query_execution = new QueryExecution(table_name, miConfig);
        query_util = new QueryUtils();
    }

    //métodos

    /**
     * se utiliza para dar la cantidad de datos en la tabla
     * @return cantidad de datos
     */
    public int CountData() {
        int count = 0;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try{
            rst = query_execution.ExecuteCountData(pstm);
            while(rst.next()) {
                count++;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                rst = null;
            }

            if(pstm != null) {
                try {
                    pstm.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                pstm = null;
            }
        }
        return count;
    }
    /**
     * crea una lista de registros con los datos de la bd
     * @param model_builder_methods: opciones para utilizar los registros
     * @return lista de registros
     */
    public ArrayList<T> ReadAll(ModelBuilderMethods<T> model_builder_methods) {
        PreparedStatement pstm = null;
        ResultSet rst = null;
        ArrayList<T> resultados = new ArrayList<T>();
        try {
            rst = query_execution.ExecuteReadAll(pstm);
            int lenght = query_util.GetMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                resultados.add(model_builder_methods.CreateFromRST(rst, lenght));
            }
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                rst = null;
            }
            if(pstm != null) {
                try {
                    pstm.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                pstm = null;
            }
        }
        return resultados;
    }

    /**
     * busca el registro por primary key
     * @param options: las obciones de busqueda
     * @param type: tipo de condición para la sentencia sql
     * @param model_builder_methods: opciones para utilizar los registros
     * @return el usuario buscado
     */
    public T FindOne(String options, String type, ModelBuilderMethods<T> model_builder_methods) {
        T buscado = null;
        ResultSet rst = null;
        PreparedStatement pstm = null;
        try {
            rst = query_execution.ExecuteFindOne(pstm, options, type);
            int lenght = query_util.GetMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = model_builder_methods.CreateFromRST(rst, lenght);
            }
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                rst = null;
            }
            if(pstm != null) {
                try {
                    pstm.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                pstm = null;
            }
        }
        return buscado;
    }
    /**
     * busca el registro por cualquier nombre de columna
     * @param options: las opciones de busqueda
     * @param type: tipo de condicion para la setencia sql
     * @param model_builder_methods: opciones para utilizar los registros
     * @return el registro buscado
     */
    public T FindByColumnName(String options, String type, ModelBuilderMethods<T> model_builder_methods) {
        T buscado = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = query_execution.ExecuteFindByColumnName(stm, options, type);
            int lenght = query_util.GetMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = model_builder_methods.CreateFromRST(rst, lenght);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
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
        return buscado;
    }
    /**
     * busca y retorna el valor de la columna o columnas
     * @param options: column name
     * @param column: las columnas a buscar el valor
     * @param type: tipo de condicion para la setencia sql
     * @return value of column name
     */
    public String GetValueOfColumnName(String options, String columns, String type) {
        String result ="";
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = query_execution.ExecuteGetValueOfColumnName(stm, options, columns, type);
            int len = 0;
            if(columns == null || columns.isEmpty() == true) {
                len = query_util.GetMetadataNumColumns(rst.getMetaData().toString());
            }
            else if(columns != null || columns.isEmpty() == false) {
                len = columns.split(",").length;
            }
            while(rst.next()) {
                for(int i=1; i<= len; i++) {
                    result += rst.getString(i) + ", ";
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally{
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
        if(result == "") {
            result = null;
        }
        return result.substring(0, result.length()-2);
    }
    /**
     * ingresar un registro de GenericObjects
     * @param nObject: el objeto a registrar
     * @param condition: condición para buscar el registro
     * @param type: tipo de condicion para la setencia sql
     * @param model_builder_methods: opciones para utilizar los registros
     * @return true si se registra de lo contrario false
     */
    public boolean InsertNewRegister(ModelMethods model, String condition, String type, ModelBuilderMethods<T> model_builder_methods) throws SQLException {
        boolean registrado = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            if(model == null) {
                throw new Exception("el objeto no deberia ser null");
            }
            T buscado = this.FindByColumnName(condition, type, model_builder_methods);
            if(buscado == null) {
                rst = query_execution.ExecuteInsertNewRegister(stm, model);
                while(rst.next()){
                    System.out.println(model.GetAllProperties());
                    registrado = true;
                }
            } else {
                registrado = false;
                throw new Exception("el objeto deberia ser null");
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally{
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
        return registrado;
    }

    /**
     * modificar 1 registro de la base de datos
     * @param nObject: usuario con los datos a modificar
     * @param conditions: columna con su valor para el condicional
     * @param type: tipo de condicion para la setencia sql
     * @param model_builder_methods: opciones para utilizar los registros
     * @return true si se modifican los datos de lo contrario false
     **/
    public boolean UpdateRegister(ModelMethods model, String conditions, String type, ModelBuilderMethods<T> model_builder_methods) throws SQLException {
        boolean registrado = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            if(model == null) {
                throw new Exception("model no deberia ser null");
            }
            T buscado = this.FindByColumnName(conditions.split(",")[0], type, model_builder_methods);
            if(buscado != null) {
                rst = query_execution.ExecuteUpdateRegister(stm, model, conditions, type);
                while(rst.next()) {
                    System.out.println(model.GetAllProperties());
                    registrado = true;
                }
            } else {
                registrado = false;
                throw new Exception("nObject no deberia ser null");
            }

        } catch( Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if(rst != null){
                try {
                    rst.close();

                } catch( Exception e2) {
                    System.err.println(e2.getMessage());
                }
                rst = null;
            }

            if(stm != null) {
                try {
                    stm.close();

                } catch( Exception e2) {
                    System.err.println(e2.getMessage());
                }
                stm = null;
            }
        }
        return registrado;
    }
    /**
     * eliminar un registro por cualquier columna valida de la bd
     * @param options: columna con el valor para el condicional
     * @param type: tipo de condicion para la setencia sql
     * @param model_builder_methods: opciones para utilizar los registros
     * @return true si elimina de lo contrario false
     * */
    public boolean EliminarRegistro(String options, String type, ModelBuilderMethods<T> model_builder_methods) throws SQLException {
        boolean eliminar = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            if(options.isEmpty() == true || options == null) {
                throw new Exception("no deberia ser null ni vacio");
            }
            T buscado = this.FindByColumnName(options.split(",")[0], type, model_builder_methods);
            if(buscado != null) {
                rst = query_execution.ExecuteEliminarRegistro(stm, options, type);
                while(rst.next()) {
                    System.out.println(options);
                    eliminar = true;
                }
            } else {
                eliminar = false;
                throw new Exception("no deberia ser null");
            }
        } catch (Exception e ) {

            System.err.println(e);
        } finally {
            if(rst != null){
                try{
                    rst.close();
                } catch ( Exception e) {
                    System.err.println(e);
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch ( Exception e ) {
                    System.err.println(e);
                }
                stm = null;
            }
        }
        return eliminar;
    }
}
