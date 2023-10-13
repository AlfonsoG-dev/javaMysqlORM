package Mundo.Users;

import java.sql.ResultSet;
import java.sql.SQLException;
import Model.ModelBuilderMethods;

/**
 * record para crear los usuarios con los datos del ResutlSet
 * @param rst: ResultSet de la base de datos
 */
public class UserBuilder implements ModelBuilderMethods<User>{
    /**
     * valida los datos del ResultSet
     * @return lista de datos
     */
    @Override
    public String[] ResultDataValidator(ResultSet rst, int capacity) throws SQLException {
        String[] data = new String[capacity + 1];
        for(int i = 1; i < data.length; i++) {
            data[i] = rst.getString(i);
        }
        //System.out.println(data.length);
        assert data.length == 1 : "deberia ser mayor a 1";
        return data;
    }
    
    /**
     * crea el usuario con los datos del ResultSet
     * @param rst: el resultado de la consulta sql
     * @param capacity: tamaño de la lista de datos
     * @return usuario creado con los datos
     */
    @Override
    public User CreateFromRST(ResultSet rst, int capacity) throws SQLException {
        String[] data = this.ResultDataValidator(rst, capacity);
        User nUser = new User(Integer.parseInt(data[1]), data[2], data[3], data[4], data[5], data[6], data[7]);
        return nUser;
    }
    /**
     * crea el usuario con los datos que llegan por parametro en forma de String
     * @param datos: datos del usuario a crear
     * @return el usuario creado a partir de los daots
     */
    @Override
    public User CreateFromSTR(String datos) {
        String[] validate = datos.split(",");
        User nUser = new User(Integer.parseInt(validate[0]), validate[1], validate[2], validate[3], validate[4], validate[5], validate[6]);
        return nUser;
    }
}

