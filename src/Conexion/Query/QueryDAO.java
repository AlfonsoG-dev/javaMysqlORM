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
        tbName              = tableName;
        cursor              = miConector;
        queryExecution      = new QueryExecution(tableName, miConector);
        queryUtil           = new QueryUtils();
        modelBuilderMethods = builder;
    }
    /**
     * {@link Connection} cursor.
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
     * busca y retorna el valor de la sentencia.
     * @param sql: row query statement
     * @return value of column name
     */
    public String anyQuery(String sql) {
        String result = "";
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst     = queryExecution.executeMyQuery(stm, sql);
            int len = queryUtil.getMetadataNumColumns(rst.getMetaData());
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
     * INSERT UPDATE DELETE statements.
     * @param sql: row query statement
     * @return true if row count is > 0 othewise false
     */
    public boolean anyExecution(String sql) {
        Statement stm      = null;
        boolean isExecuted = false;
        try {
            int rst = queryExecution.executeMyUpdateQuery(stm, sql);
            if(rst > 0) {
                isExecuted = true;
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
        return isExecuted;
    }
    /**
     * create a view from selection.
     * <br> sample: </br> create view name_view as select operation from table;
     * @param viewName: name of the view to create
     * @param condition: condition for selection
     * @param columns: the columns to select for the view
     * @param type: type logic for where clause
     * @return true if its created, otherwise false
     */
    public boolean createView(String viewName, String condition, String columns, String type) {
        boolean isCreate  = false;
        Statement stm     = null;
        try {
            int rst = queryExecution.executeCreateView(
                    stm,
                    viewName,
                    condition,
                    columns,
                    type
            );
            if(rst == 1) {
                isCreate = true;
            } else if(rst == -1) {
                throw new Exception("nothing happen when trying to create a new view");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isCreate; 
    }
    /**
     * delete views from database.
     * @param viewName: the view to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteView(String viewName) {
        boolean isDeleted = false;
        Statement stm     = null;
        try {
            int rst = queryExecution.executeDeleteView(stm, viewName);
            if(rst == 0) {
                isDeleted = true;
            } else if(rst == -1) {
                throw new Exception("nothing happen when trying to delete a view");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isDeleted;
    }
    /**
     * se utiliza para dar la cantidad de datos en la tabla.
     * @return cantidad de datos
     */
    public int countData() {
        int count              = 0;
        PreparedStatement pstm = null;
        ResultSet rst          = null;
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
     * crea una lista de registros con los datos de la bd.
     * @return lista de registros
     */
    public ArrayList<T> readAll() {
        PreparedStatement pstm  = null;
        ResultSet rst           = null;
        ArrayList<T> resultados = new ArrayList<T>();
        try {
            rst        = queryExecution.executeReadAll(pstm);
            int lenght = queryUtil.getMetadataNumColumns(rst.getMetaData());
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
     * search using {@link PreparedStatement}.
     * @param condition: las obciones de busqueda
     * @param type: and or not
     * @return el usuario buscado
     */
    public T findOne(String condition, String type) {
        T buscado              = null;
        PreparedStatement pstm = null;
        ResultSet rst          = null;
        try {
            rst        = queryExecution.executeFindOne(pstm, condition, type);
            int lenght = queryUtil.getMetadataNumColumns(rst.getMetaData());
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
     * busca el registro por cualquier nombre de columna.
     * @param condition: las opciones de busqueda
     * @param type: tipo de condicion para la setencia sql
     * @return el registro buscado
     */
    public T findByColumnName(String condition, String type) {
        T buscado     = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst        = queryExecution.executeFindByColumnName(stm, condition, type);
            int lenght = queryUtil.getMetadataNumColumns(rst.getMetaData());
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
     * @param returnColumns: columns to return in selection
     * @param conditionalColumns: column to search
     * @param condition: condition for the search
     * @param type: logic type for where clause
     * @return the object of the generic type
     */
    public String findIn(String returnColumns, String conditionalColumns, String condition, String type) {
        String buscado = null;
        Statement stm  = null;
        ResultSet rst  = null;
        try {
            int len = 0;
            rst     = queryExecution.executeFindIn(
                    stm,
                    returnColumns,
                    conditionalColumns,
                    condition,
                    type
            );
            if(returnColumns == null || returnColumns.isEmpty()) {
                len = queryUtil.getMetadataNumColumns(rst.getMetaData());
            } else if(returnColumns != null || !returnColumns.isEmpty()) {
                len = returnColumns.split(",").length;
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
     * busca los datos con regex o patrones.
     * @param pattern: regex o patron a buscar
     * @param condition: columnas a comparar con el patron
     * @param type: and or not
     * @return an object of the generic type
     */
    public T findPattern(String pattern, String condition, String type) {
        T buscado     = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst        = queryExecution.executeFindPattern(
                    stm,
                    pattern,
                    condition,
                    type
            );
            int lenght = queryUtil.getMetadataNumColumns(rst.getMetaData());
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
     * get the min or max of each of the given columns.
     * @param columns: 'min: nombre, max: password'
     * @param condition: condition for where clause
     * @param type: and or not
     * @return the min or max object data
     */
    public String getMinMax(String columns, String condition, String type) {
        String buscado = null;
        Statement stm  = null;
        ResultSet rst  = null;
        try {
            rst     = queryExecution.executeFindMinMax(
                    stm,
                    columns,
                    condition,
                    type
            );
            int len = 0;
            if(columns == null || columns.isEmpty()) {
                len = queryUtil.getMetadataNumColumns(rst.getMetaData());
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
     * busca y retorna el valor de la columna o columnas.
     * @param condition: column name
     * @param column: las columnas a buscar el valor
     * @param type: tipo de condicion para la setencia sql
     * @return value of column name
     */
    public String getValueOfColumnName(String condition, String columns, String type) {
        String result = "";
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst     = queryExecution.executeGetValueOfColumnName(
                    stm,
                    condition,
                    columns,
                    type
            );
            int len = 0;
            if(columns == null || columns.isEmpty() == true) {
                len = queryUtil.getMetadataNumColumns(rst.getMetaData());
            }
            else if(columns != null || columns.isEmpty() == false) {
                len = columns.split(",").length;
            }
            while(rst.next()) {
                for(int i=1; i<= len; ++i) {
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
     * inner join of 2 tables.
     * @param primary: model with the fk field declaration
     * @param foreign: model with the pk field for the relationship 
     * @param foreignT: foreign table name
     * @param condition: condition for where clause
     * @param type: logic type for where clause
     * @return a string with the format -> source_name: source_password, reference_name: reference_password
     */
    public String innerJoin(ModelMethods primary, ModelMethods foreign, String foreignT, String condition, 
            String type) {
        String result = "";
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = queryExecution.executeInnerJoin(
                    stm,
                    primary,
                    foreign,
                    foreignT,
                    condition,
                    type
            );
            while(rst.next()) {
                result += tbName + "->" + rst.getString(tbName + "_nombre") + ": " +
                    rst.getString(tbName + "_password") + ", " +
                    foreignT + "->" + rst.getString(foreignT + "_nombre") + ": " + 
                    rst.getString(foreignT + "_password") + ", ";
            }

        } catch(Exception e) {
            System.err.println(e);
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                } 
                stm = null;
            }
        }
        return result.substring(0, result.length()-2);
    }
    /**
     * ingresar un registro de GenericObjects.
     * @param model: model with the data to insert
     * @param condition: condición para buscar el registro
     * @param type: tipo de condicion para la setencia sql
     * @return true si se registra de lo contrario false
     */
    public boolean insertNewRegister(ModelMethods model, String condition, String type) {
        boolean isInserted  = false;
        Statement stm       = null;
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
                    isInserted = true;
                }
            } else {
                isInserted = false;
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
        return isInserted;
    }
    public boolean insertByColumns(String options, String condition, String type) {
        Statement stm = null;
        boolean isInserted = false;
        try {
            if(options == null || options.isEmpty()) {
                throw new Exception("trying to use an empty insert option");
            }
            T searched = findByColumnName(condition, type);
            if(searched == null) {
                int rst = queryExecution.executeInsertByColumns(stm, options);
                if(rst > 0) {
                    System.out.println(options);
                    isInserted = true;
                }
            } else {
                isInserted = false;
                throw new Exception("trying to insert an already existing record");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isInserted;
    }
    /**
     * copy the columns from one table to another.
     * <br> pre: </br> you can copy all o some columns of the table
     * @param sourceT: source table
     * @param targetT: target table 
     * @param condition: condition for where clause
     * @param columns: columns to return
     * @param type: logic operator for where clause
     * @return true if the {@link sourceT} is copied to {@link targetT}, false otherwise
     */
    public boolean insertIntoSelect(String sourceT, String targetT, String condition, String columns, 
            String type) {
        boolean isInserted = false;
        Statement stm      = null;
        try {
            int rst = queryExecution.executeInsertIntoSelect(
                    stm,
                    sourceT,
                    targetT,
                    condition,
                    columns,
                    type
            );
            if(rst > 0) {
                isInserted = true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isInserted;
    }
    /**
     * modificar 1 registro de la base de datos.
     * @param model: model with the updated data
     * @param conditions: columna con su valor para el condicional
     * @param type: tipo de condicion para la setencia sql
     * @return true si se modifican los datos de lo contrario false
     **/
    public boolean updateRegister(ModelMethods model, String conditions, String type) {
        boolean isUpdated = false;
        Statement stm      = null;
        try {
            if(model == null) {
                throw new Exception("model no deberia ser null");
            }
            cursor.beginRequest();
            T buscado = findByColumnName(conditions.split(",")[0], type);
            if(buscado != null) {
                cursor.endRequest();
                int result = queryExecution.executeUpdateRegister(
                        stm,
                        model,
                        conditions,
                        type
                );
                if(result > 0) {
                    System.out.println(model.getAllProperties());
                    isUpdated = true;
                }
            } else {
                isUpdated = false;
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
        return isUpdated;
    }
    /**
     * eliminar un registro por cualquier columna valida de la bd.
     * @param condition: columna con el valor para el condicional
     * @param type: tipo de condicion para la setencia sql
     * @return true si elimina de lo contrario false
     * */
    public boolean deleteRegister(String condition, String type) {
        boolean isDeleted = false;
        Statement stm    = null;
        try {
            if(condition.isEmpty() == true || condition == null) {
                throw new Exception("no deberia ser null ni vacio");
            }
            cursor.beginRequest();
            T buscado = findByColumnName(condition.split(",")[0], type);
            if(buscado != null) {
                cursor.endRequest();
                int rst = queryExecution.executeEliminarRegistro(
                        stm,
                        condition,
                        type
                );
                if(rst > 0) {
                    System.out.println(condition);
                    isDeleted = true;
                }
            } else {
                isDeleted = false;
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
        return isDeleted;
    }
}
