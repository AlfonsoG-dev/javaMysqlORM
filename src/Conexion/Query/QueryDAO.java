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
    public Connection getConnection() {
        return cursor;
    }
    /**
     * disconnect the cursor connection
     */
    public void disconnectConnection() {
        try {
            cursor.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * busca y retorna el valor de la sentencia
     * @param sql: row query statement
     * @return value of column name
     */
    public String anyQuery(String sql) {
        String result ="";
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.executeMyQuery(stm, sql);
            int len = queryUtil.getMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                for(int i=1; i<= len; i++) {
                    String columnName = rst.getMetaData().getColumnName(i);
                    result += columnName + ": " + rst.getString(i) + ", ";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        return result.substring(0, result.length()-2);
    }
    /**
     * INSERT UPDATE DELETE statements
     * @param sql: row query statement
     * @return true if row count is > 0 othewise false
     */
    public boolean anyExecution(String sql) {
        boolean result = false;
        Statement stm = null;
        try {
            int rst = queryExecution.executeMyUpdateQuery(stm, sql);
            if(rst > 0) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        return result;
    }
    /**
     * se utiliza para dar la cantidad de datos en la tabla
     * @return cantidad de datos
     */
    public int countData() {
        int count = 0;
        PreparedStatement pstm = null;
        ResultSet rst = null;
        try{
            rst = queryExecution.executeCountData(pstm);
            while(rst.next()) {
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
    public ArrayList<T> readAll() {
        PreparedStatement pstm = null;
        ResultSet rst = null;
        ArrayList<T> resultados = new ArrayList<T>();
        try {
            rst = queryExecution.executeReadAll(pstm);
            int lenght = queryUtil.getMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                resultados.add(modelBuilderMethods.createFromRST(rst, lenght));
            }
        } catch(SQLException e) {
            e.printStackTrace();
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
    public T findOne(String options, String type) {
        T buscado = null;
        ResultSet rst = null;
        PreparedStatement pstm = null;
        try {
            rst = queryExecution.executeFindOne(pstm, options, type);
            int lenght = queryUtil.getMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = modelBuilderMethods.createFromRST(rst, lenght);
            }
        } catch(SQLException e) {
            e.printStackTrace();
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
    public T findByColumnName(String options, String type) {
        T buscado = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.executeFindByColumnName(stm, options, type);
            int lenght = queryUtil.getMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = modelBuilderMethods.createFromRST(rst, lenght);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public String findIn(String returnOptions, String columns, String condition, String type) {
        String buscado = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.executeFinIn(stm, returnOptions, columns, condition, type);
            int len = 0;
            if(returnOptions == null || returnOptions.isEmpty()) {
                len = queryUtil.getMetadataNumColumns(rst.getMetaData().toString());
            } else if(returnOptions != null || !returnOptions.isEmpty()) {
                len = returnOptions.split(",").length;
            }
            while(rst.next()) {
                for(int i=1; i<=len; ++i) {
                    buscado += rst.getString(i).replaceAll("null", "") + ", ";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        return buscado.substring(0, buscado.length()-2);
    }
    /**
     * busca los datos con regex o patrones
     * @param pattern: regex o patron a buscar
     * @param options: columnas a comparar con el patron
     * @param type: and or not
     * @return an object of the generic type
     */
    public T findPattern(String pattern, String options, String type) {
        T buscado = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.executeFindPattern(stm, pattern, options, type);
            int lenght = queryUtil.getMetadataNumColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = modelBuilderMethods.createFromRST(rst, lenght);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
     * get the min or max of each of the given columns 
     * @param columns: 'min: nombre, max: password'
     * @param condition: condition for where clausule
     * @param type: and or not
     * @return the min or max object data
     */
    public String getMinMax( String columns, String condition, String type) {
        String buscado = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.executeFindMinMax(stm, columns, condition, type);
            int len = 0;
            if(columns == null || columns.isEmpty()) {
                len = queryUtil.getMetadataNumColumns(rst.getMetaData().toString());
            } else if(columns != null || !columns.isEmpty()) {
                len = columns.split(",").length;
            }
            while(rst.next()) {
                for(int i=1; i<=len; ++i) {
                    buscado += rst.getString(i).replaceAll("null", "") + ", ";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        return buscado.substring(0, buscado.length()-2);
    }
    /**
     * busca y retorna el valor de la columna o columnas
     * @param options: column name
     * @param column: las columnas a buscar el valor
     * @param type: tipo de condicion para la setencia sql
     * @return value of column name
     */
    public String getValueOfColumnName(String options, String columns, String type) {
        String result ="";
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.executeGetValueOfColumnName(stm, options, columns, type);
            int len = 0;
            if(columns == null || columns.isEmpty() == true) {
                len = queryUtil.getMetadataNumColumns(rst.getMetaData().toString());
            }
            else if(columns != null || columns.isEmpty() == false) {
                len = columns.split(",").length;
            }
            while(rst.next()) {
                for(int i=1; i<= len; i++) {
                    result += rst.getString(i).replaceAll("null", "") + ", ";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        return result.substring(0, result.length()-2);
    }
    /**
     * inner join of 2 tables
     * @param source: table with the fk 
     * @param reference: table with the pk == fk
     * @param ref_table: table name of the reference model 
     * @return a string with the format -> source_name: source_password, reference_name: reference_password
     */
    public String innerJoin(ModelMethods source, ModelMethods reference, String refTable, String condition, String type) {
        String result = "";
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.executeInnerJoin(stm, source, reference, refTable, condition, type);
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
    public boolean insertNewRegister(ModelMethods model, String condition, String type) {
        boolean registrado = false;
        Statement stm = null;
        try {
            if(model == null) {
                throw new Exception("el objeto no deberia ser null");
            }
            cursor.beginRequest();
            T buscado = findByColumnName(condition, type);
            if(buscado == null) {
                cursor.endRequest();
                int rst = queryExecution.executeInsertNewRegister(stm, model);
                if(rst > 0){
                    System.out.println(model.getAllProperties());
                    registrado = true;
                }
            } else {
                registrado = false;
                throw new Exception("el objeto deberia ser null");
            }

        } catch (Exception e) {
            e.printStackTrace();
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
    public boolean updateRegister(ModelMethods model, String conditions, String type) {
        boolean registrado = false;
        Statement stm = null;
        try {
            if(model == null) {
                throw new Exception("model no deberia ser null");
            }
            cursor.beginRequest();
            T buscado = findByColumnName(conditions.split(",")[0], type);
            if(buscado != null) {
                cursor.endRequest();
                int result = queryExecution.executeUpdateRegister(stm, model, conditions, type);
                if(result > 0) {
                    System.out.println(model.getAllProperties());
                    registrado = true;
                }
            } else {
                registrado = false;
                throw new Exception("nObject no deberia ser null");
            }

        } catch( Exception e) {
            e.printStackTrace();
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
    public boolean eliminarRegistro(String options, String type) {
        boolean eliminar = false;
        Statement stm = null;
        try {
            if(options.isEmpty() == true || options == null) {
                throw new Exception("no deberia ser null ni vacio");
            }
            cursor.beginRequest();
            T buscado = findByColumnName(options.split(",")[0], type);
            if(buscado != null) {
                cursor.endRequest();
                int rst = queryExecution.executeEliminarRegistro(stm, options, type);
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
