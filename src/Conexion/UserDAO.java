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


    //constructor


    /**
     * Data Acces Object of User
     * inicializa el conector de mysql
     */
    public UserDAO() {
        connector = new Conector().conectarMySQL();
    }


    //métodos


    /**
     * crea una lista de usuarios con los datos de la bd
     * @return lista de usuarios
     */
    public User[] ReadAll() {
        PreparedStatement stm = null;
        ResultSet rst = null;
        UserBuilder miUser = null;
        User[] users = null;
        try {
            String sql = "select * from users";
            stm = connector.prepareStatement(sql);
            rst = stm.executeQuery();
            QueryBuilder util = new QueryBuilder(null, null);
            int lenght = util.GetMetadataColumns(rst.getMetaData().toString());
            miUser = new UserBuilder(rst, lenght);
            users = new User[lenght];
            int cont = 0;
            while(rst.next()) {
                users[cont] = miUser.CreateNewUser();
                cont++;
            }
        } catch(SQLException e) {
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
        return users;
    }

    /**
     * busca el usuario por primary key
     * @param options: las obciones de busqueda
     * @return el usuario buscado
     */
    public User FindOne(String options) {
        User buscado = null;
        QueryBuilder query_util = new QueryBuilder(options, null);
        PreparedStatement stm = null;
        ResultSet rst = null;
        try {
            String sql = query_util.FindQuery();
            String val = query_util.GetOptionValue();
            stm = connector.prepareStatement(sql);
            stm.setString(1, val);
            rst = stm.executeQuery();
            int lenght = query_util.GetMetadataColumns(rst.getMetaData().toString());
            UserBuilder nUserBuilder = new UserBuilder(rst, lenght);
            while(rst.next()) {
                buscado = nUserBuilder.CreateNewUser();
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
        QueryBuilder query_util = new QueryBuilder(options, null);
        try {
            stm = connector.createStatement();
            String sql = query_util.FindColumnQuery();
            rst = stm.executeQuery(sql);
            int lenght = query_util.GetMetadataColumns(rst.getMetaData().toString());
            UserBuilder nUserBuilder = new UserBuilder(rst, lenght);
            while(rst.next()) {
                buscado = nUserBuilder.CreateNewUser();
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
        QueryBuilder query_util = new QueryBuilder(options, column);
        try {
            stm = connector.createStatement();
            String sql = query_util.FindColumnValueQuery();
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
        return result.substring(0, result.length()-2);
    }
    /**
     * ingresar un registro de users
     * @param nUser: el usuario a registrar
     * @return true si se registra de lo contrario false
     */
    public boolean InsertNewRegister(User nUser) {
        boolean registrado = false;
        return registrado;
    }   
}
