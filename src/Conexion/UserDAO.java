package Conexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import Model.ModelMethods;
import Mundo.User;
import Utils.UserBuilder;
import Utils.MigrationBuilder;
import Utils.QueryUtils;

public class UserDAO {
    /**
     * user builder
     */
    private UserBuilder nUserBuilder;
    /**
     */
    MigrationBuilder migrate;
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
    public UserDAO() {
        nUserBuilder = new UserBuilder();
        query_execution = new QueryExecution("users");
        query_util = new QueryUtils();
        migrate = new MigrationBuilder("users");
    }


    //métodos

    /**
     * se utiliza para dar la cantidad de datos en la tabla
     * @return cantidad de datos
     */
    public int CountData(){
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
        assert count == 0 : "deberia ser mayor a 0";
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
    public User[] ReadAll() {
        PreparedStatement pstm = null;
        ResultSet rst = null;
        User[] users = null;
        try {
            rst = query_execution.ExecuteReadAll(pstm);
            int lenght = query_util.GetMetadataColumns(rst.getMetaData().toString());
            users = new User[1];
            while(CountData() > users.length) {
                User[] nueva = Arrays.copyOf(users, CountData());
                users = nueva;
            }
            int cont = 0;
            while(rst.next()) {
                users[cont] = nUserBuilder.CreateNewUser(rst, lenght);
                cont++;
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
        //System.out.println(users.length);
        assert users == null : "deberia ser diferente de null";
        assert users.length == 0 : "deberia ser mayor a 0";
        return users;
    }

    /**
     * busca el usuario por primary key
     * @param options: las obciones de busqueda
     * @return el usuario buscado
     */
    public User FindOne(String options) {
        User buscado = null;
        ResultSet rst = null;
        PreparedStatement pstm = null;
        try {
            rst = query_execution.ExecuteFindOne(options, pstm);
            int lenght = query_util.GetMetadataColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = nUserBuilder.CreateNewUser(rst, lenght);
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
        assert buscado == null: "deberia ser diferente de null";
        return buscado;
    }
    /**
     * busca el usuario por cualquier nombre de columna
     * @param options: las opciones de busqueda
     * @return el usuario buscado
     */
    public User FindByColumnName(String options) {
        User buscado = null;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = query_execution.ExecuteFindByColumnName(options, stm);
            int lenght = query_util.GetMetadataColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = nUserBuilder.CreateNewUser(rst, lenght);
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
        assert buscado == null : "deberia ser diferente de null";
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
        assert result == null : "deberia ser diferente de null";
        return result.substring(0, result.length()-2);
    }
    /**
     * ingresar un registro de users
     * @param nUser: el usuario a registrar
     * @return true si se registra de lo contrario false
     */
    public boolean InsertNewRegister(User nUser) throws SQLException {
        boolean registrado = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            if(nUser == null) {
                throw new Exception("user no deberia ser null");
            }
            // System.out.println(sql);
            User buscado = this.FindByColumnName("nombre: " + nUser.getNombre());
            if(buscado == null) {
                rst = query_execution.ExecuteInsertNewRegister(stm, nUser);
                while(rst.next()){
                    System.out.println(nUser.GetAllProperties());
                    registrado = true;
                }
            } else {
                registrado = false;
                throw new Exception("usuario deberia ser null");
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
        assert registrado == false : "deberia ser diferente de false";
        return registrado;
    }

    /**
     * modificar 1 registro de la base de datos por nombre, email y password
     * @param nUser: usuario con los datos a modificar
     * @return true si se modifican los datos de lo contrario false
     **/
    public boolean UpdateRegister(User nUser, String conditions) throws SQLException {
        boolean registrado = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            if(nUser == null) {
                throw new Exception("user no deberia ser null");
            }
            User buscado = this.FindByColumnName(conditions.split(",")[0]);
            if(buscado != null) {
                rst = query_execution.ExecuteUpdateRegister(stm, nUser, conditions);
                while(rst.next()) {
                    System.out.println(nUser.GetAllProperties());
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
        assert registrado == false : "deberia ser diferente de false";
        return registrado;
    }
    /**
     * eliminar un registro por cualquier columna valida de la bd
     * @param options: columna con el valor para el condicional
     * @return true si elimina de lo contrario false
     * */
    public boolean EliminarRegistro(String options) throws SQLException {
        boolean eliminar = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            if(options.isEmpty() == true || options == null) {
                throw new Exception("no deberia ser null ni vacio");
            }
            User buscado = this.FindByColumnName(options.split(",")[0]);
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
        assert eliminar == false: "no deberia ser false";
        return eliminar;
    }
}
