package Conexion.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
    private QueryExecution queryExecution;
    /**
     * builder interface to build the objects
     */
    private ModelBuilderMethods<T> modelBuilderMethods;
    /**
     * herramientas para la creacion de las querys
     */
    private QueryUtils queryUtil;
    /**
     * local Connection cursor
     */
    private Connection cursor;
    /**
     * local table name
     */
    private String tbName;
    /**
     * Data Acces Object of GenericObject
     * inicializa el conector de mysql
     */
    public QueryDAO(String tableName, Connection miConector, ModelBuilderMethods<T> builder) {
        tbName = tableName;
        cursor = miConector;
        queryExecution = new QueryExecution(tableName, miConector);
        queryUtil = new QueryUtils();
        modelBuilderMethods = builder;
    }

    //métodos
    // TODO: add create view to the operations
    /**
     * {@link Connection} cursor
     * @return {@link Connection}
     */
    public Connection GetConnection() {
        return cursor;
    }

    /**
     * se utiliza para dar la cantidad de datos en la tabla
     * @return cantidad de datos
     */
    public int CountData() {
        int count = 0;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try{
            rst = queryExecution.ExecuteCountData(pstm);
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
    public ArrayList<T> ReadAll() {
        PreparedStatement pstm = null;
        ResultSet rst = null;
        ArrayList<T> resultados = new ArrayList<T>();
        try {
            rst = queryExecution.ExecuteReadAll(pstm);
            int lenght = queryUtil.GetMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                resultados.add(modelBuilderMethods.CreateFromRST(rst, lenght));
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
    public T FindOne(String options, String type) {
        T buscado = null;
        ResultSet rst = null;
        PreparedStatement pstm = null;
        try {
            rst = queryExecution.ExecuteFindOne(pstm, options, type);
            int lenght = queryUtil.GetMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = modelBuilderMethods.CreateFromRST(rst, lenght);
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
    public T FindByColumnName(String options, String type) {
        T buscado = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.ExecuteFindByColumnName(stm, options, type);
            int lenght = queryUtil.GetMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = modelBuilderMethods.CreateFromRST(rst, lenght);
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
     * busca el registro entre una serie de valores o una sentencia sql tipo SELECT.
     * <br> pre: </br> select * from cuenta where user_id_fk in (select id_pk from user where nombre='admin'); 
     * select * from cuenta where id_pk in ('0', '2'); 
     * @param columns: column to search
     * @param condition: condition for the search
     * @return the object of the generic type
     */
    public T FindIn(String returnOptions, String columns, String condition, String type) {
        T buscado = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.ExecuteFinInQuery(stm, returnOptions, columns, condition, type);
            int lenght = queryUtil.GetMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = modelBuilderMethods.CreateFromRST(rst, lenght);
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
            rst = queryExecution.ExecuteGetValueOfColumnName(stm, options, columns, type);
            int len = 0;
            if(columns == null || columns.isEmpty() == true) {
                len = queryUtil.GetMetadataNumColumns(rst.getMetaData().toString());
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
     * inner join of 2 tables
     * @param source: table with the fk 
     * @param reference: table with the pk == fk
     * @param ref_table: table name of the reference model 
     * @return a string with the format -> source_name: source_password, reference_name: reference_password
     */
    public String InnerJoin(ModelMethods source, ModelMethods reference, String refTable) {
        String result = "";
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.ExecuteInnerJoin(stm, source, reference, refTable);
            while(rst.next()) {
                result += tbName + "->" + rst.getString(tbName + "_nombre") + ": " +
                    rst.getString(tbName + "_password") + ", " +
                    refTable + "->" + rst.getString(refTable + "_nombre") + ": " + 
                    rst.getString(refTable + "_password") + ", ";
            }

        } catch(Exception e) {
            System.err.println(e);
        }
        String cleanResult = result.substring(0, result.length()-2);
        return cleanResult;
    }
    /**
     * ingresar un registro de GenericObjects
     * @param nObject: el objeto a registrar
     * @param condition: condición para buscar el registro
     * @param type: tipo de condicion para la setencia sql
     * @param model_builder_methods: opciones para utilizar los registros
     * @return true si se registra de lo contrario false
     */
    public boolean InsertNewRegister(ModelMethods model, String condition, String type) throws SQLException {
        boolean registrado = false;
        Statement stm = null;
        try {
            if(model == null) {
                throw new Exception("el objeto no deberia ser null");
            }
            cursor.beginRequest();
            T buscado = this.FindByColumnName(condition, type);
            if(buscado == null) {
                cursor.endRequest();
                int rst = queryExecution.ExecuteInsertNewRegister(stm, model);
                if(rst > 0){
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
    public boolean UpdateRegister(ModelMethods model, String conditions, String type) throws SQLException {
        boolean registrado = false;
        Statement stm = null;
        try {
            if(model == null) {
                throw new Exception("model no deberia ser null");
            }
            cursor.beginRequest();
            T buscado = this.FindByColumnName(conditions.split(",")[0], type);
            if(buscado != null) {
                cursor.endRequest();
                int result = queryExecution.ExecuteUpdateRegister(stm, model, conditions, type);
                if(result > 0) {
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
    public boolean EliminarRegistro(String options, String type) throws SQLException {
        boolean eliminar = false;
        Statement stm = null;
        try {
            if(options.isEmpty() == true || options == null) {
                throw new Exception("no deberia ser null ni vacio");
            }
            cursor.beginRequest();
            T buscado = this.FindByColumnName(options.split(",")[0], type);
            if(buscado != null) {
                cursor.endRequest();
                int rst = queryExecution.ExecuteEliminarRegistro(stm, options, type);
                if(rst > 0) {
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
