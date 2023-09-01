package Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Mundo.User;
import Utils.QueryBuilder;
import Utils.UserBuilder;

public class UserDAO {
    //atributos


    /**
     * conector de mysql
     */
    private Connection connector;

    /**
     * user builder
     */
    private UserBuilder nUserBuilder;

    /**
     * query builder
     */
    private QueryBuilder query_util;

    //constructor


    /**
     * Data Acces Object of User
     * inicializa el conector de mysql
     */
    public UserDAO() {
        connector = new Conector().conectarMySQL();
        nUserBuilder = new UserBuilder();
        query_util = new QueryBuilder();
    }


    //métodos

    /**
     * se utiliza para dar la cantidad de datos en la tabla
     * @return cantidad de datos
     */
    public int CountData(){
        int count = 1;
        PreparedStatement stm = null;
        ResultSet rst = null;
        try{
            String sql = "select count(id) from users";
            stm = connector.prepareStatement(sql);
            rst = stm.executeQuery();
            do{
                count++;
            }
            while(rst.next());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                stm = null;
            }
        }
        assert count == 0 : "deberia ser mayor a 0";
        return count;
    }
    /**
     * crea una lista de usuarios con los datos de la bd
     * @return lista de usuarios
     */
    public User[] ReadAll() {
        PreparedStatement stm = null;
        ResultSet rst = null;
        User[] users = null;
        try {
            String sql = "select * from users";
            stm = connector.prepareStatement(sql);
            rst = stm.executeQuery();
            int lenght = query_util.GetMetadataColumns(rst.getMetaData().toString());
            users = new User[CountData()];
            int cont = 0;
            while(rst.next()) {
                users[cont] = nUserBuilder.CreateNewUser(rst, lenght);
                cont++;
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                stm = null;
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
        PreparedStatement stm = null;
        ResultSet rst = null;
        try {
            String sql = query_util.FindQuery(options);
            String val = query_util.GetOptionValue(options);
            stm = connector.prepareStatement(sql);
            stm.setString(1, val);
            rst = stm.executeQuery();
            int lenght = query_util.GetMetadataColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = nUserBuilder.CreateNewUser(rst, lenght);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                stm = null;
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
            stm = connector.createStatement();
            String sql = query_util.FindColumnQuery(options);
            rst = stm.executeQuery(sql);
            int lenght = query_util.GetMetadataColumns(rst.getMetaData().toString());
            while(rst.next()) {
                buscado = nUserBuilder.CreateNewUser(rst, lenght);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
            stm = connector.createStatement();
            String sql = query_util.FindColumnValueQuery(options, column);
            // System.out.println(sql);
            rst = stm.executeQuery(sql);
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
            System.out.println(e.getMessage());
        } finally{
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
        try {
            if(nUser == null) {
                throw new Exception("user no deberia ser null");
            }
            stm = connector.createStatement();
            // System.out.println(sql);
           User buscado = this.FindByColumnName("nombre: " + nUser.getNombre());
            if(buscado == null) {
                String sql = query_util.InsertRegisterQuery(nUser);
                System.out.println(sql);
                stm.executeUpdate(sql);
                registrado = true;
            } else {
                registrado = false;
                throw new Exception("usuario deberia ser null");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally{
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
        try {
            if(nUser == null) {
                throw new Exception("user no deberia ser null");
            }
            stm = connector.createStatement();
            User buscado = this.FindByColumnName(conditions.split(",")[0]);
            if(buscado != null) {
                String sql = query_util.ModificarRegister(nUser, conditions);
                stm.executeUpdate(sql);
                System.out.println(sql);
                registrado = true;
            } else {
                registrado = false;
                throw new Exception("usuario no deberia ser null");
            }

        } catch( Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if(stm != null) {
                try {
                    stm.close();

                } catch( Exception e2) {
                    System.out.println(e2.getMessage());
                }
                stm = null;
            }
        }
        assert registrado == false : "deberia ser diferente de false";
        return registrado;
    }
}
