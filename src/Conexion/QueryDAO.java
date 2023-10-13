package Conexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import Model.ModelBuilderMethods;
import Model.ModelMethods;
import Utils.MigrationBuilder;
import Utils.QueryUtils;

public class QueryDAO<T> {
    /**
     * migración del modelo a la base de datos
     */
    private MigrationBuilder migrate;
    /**
     * query_execution
     */
    private QueryExecution query_execution;
    /**
     */
    private QueryUtils query_util;
    /**
     * Data Acces Object of User
     * inicializa el conector de mysql
     */
    public QueryDAO() {
        query_execution = new QueryExecution("users");
        query_util = new QueryUtils();
        migrate = new MigrationBuilder("users");
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
     * muestra los datos de la tabla
     * @param model: modelo con los datos
     * @return resultado de la consulta
     */
    public ResultSet ShowTableData(ModelMethods model) {
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = query_execution.ExecuteShowTableData(stm);
            migrate.CreateDeleteConstraintQuery(model.InitModel(), rst);
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
        return rst;
    }
    /**
     * crea una lista de usuarios con los datos de la bd
     * @return lista de usuarios
     */
    public ArrayList<T> ReadAll(ModelBuilderMethods<T> model_builder_methods) {
        PreparedStatement pstm = null;
        ResultSet rst = null;
        ArrayList<T> resultados = new ArrayList<T>();
        try {
            rst = query_execution.ExecuteReadAll(pstm);
            int lenght = query_util.GetMetadataColumns(rst.getMetaData().toString());
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
     * busca el usuario por primary key
     * @param options: las obciones de busqueda
     * @return el usuario buscado
     */
    public T FindOne(String options, ModelBuilderMethods<T> model_builder_methods) {
        T buscado = null;
        ResultSet rst = null;
        PreparedStatement pstm = null;
        try {
            rst = query_execution.ExecuteFindOne(options, pstm);
            int lenght = query_util.GetMetadataColumns(rst.getMetaData().toString());
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
     * busca el usuario por cualquier nombre de columna
     * @param options: las opciones de busqueda
     * @return el usuario buscado
     */
    public T FindByColumnName(String options, ModelBuilderMethods<T> model_builder_methods) {
        T buscado = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = query_execution.ExecuteFindByColumnName(options, stm);
            int lenght = query_util.GetMetadataColumns(rst.getMetaData().toString());
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
     * @return value of column name
     */
    public String GetValueOfColumnName(String options, String column) {
        String result ="";
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = query_execution.ExecuteGetValueOfColumnName(options, column, stm);
            int len = 0;
            if(column == null || column.isEmpty() == true) {
                len = query_util.GetMetadataColumns(rst.getMetaData().toString());
            }
            else if(column != null || column.isEmpty() == false) {
                len = column.split(",").length;
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
     * ingresar un registro de users
     * @param nUser: el usuario a registrar
     * @return true si se registra de lo contrario false
     */
    public boolean InsertNewRegister(ModelMethods nObject, String condition, ModelBuilderMethods<T> model_builder_methods) throws SQLException {
        boolean registrado = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            if(nObject == null) {
                throw new Exception("el objeto no deberia ser null");
            }
            T buscado = this.FindByColumnName(condition, model_builder_methods);
            if(buscado == null) {
                rst = query_execution.ExecuteInsertNewRegister(stm, nObject);
                while(rst.next()){
                    System.out.println(nObject.GetAllProperties());
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
     * modificar 1 registro de la base de datos por nombre, email y password
     * @param nUser: usuario con los datos a modificar
     * @return true si se modifican los datos de lo contrario false
     **/
    public boolean UpdateRegister(ModelMethods nObject, String conditions, ModelBuilderMethods<T> model_builder_methods) throws SQLException {
        boolean registrado = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            if(nObject == null) {
                throw new Exception("user no deberia ser null");
            }
            T buscado = this.FindByColumnName(conditions.split(",")[0], model_builder_methods);
            if(buscado != null) {
                rst = query_execution.ExecuteUpdateRegister(stm, nObject, conditions);
                while(rst.next()) {
                    System.out.println(nObject.GetAllProperties());
                    registrado = true;
                }
            } else {
                registrado = false;
                throw new Exception("usuario no deberia ser null");
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
     * @return true si elimina de lo contrario false
     * */
    public boolean EliminarRegistro(String options, ModelBuilderMethods<T> model_builder_methods) throws SQLException {
        boolean eliminar = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            if(options.isEmpty() == true || options == null) {
                throw new Exception("no deberia ser null ni vacio");
            }
            T buscado = this.FindByColumnName(options.split(",")[0], model_builder_methods);
            if(buscado != null) {
                rst = query_execution.ExecuteEliminarRegistro(stm, options);
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
