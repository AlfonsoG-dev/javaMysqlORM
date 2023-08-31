package Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import Mundo.User;

/**
 * record para crear los usuarios con los datos del ResutlSet
 * @param rst: ResultSet de la base de datos
 */
public record UserBuilder(ResultSet rst, int capacity) {

    /**
     * valida los datos del ResultSet
     * @return lista de datos
     */
    public String[] DataValidator() throws SQLException {
        String[] data = new String[capacity + 1];
        for(int i = 1; i < data.length; i++) {
            data[i] = rst.getString(i);
        }
        //System.out.println(data.length);
        return data;
    }
    
    /**
     * crea el usuario con los datos del ResultSet
     * @return usuario creado con los datos
     */
    public User CreateNewUser() throws SQLException {
        String[] data = this.DataValidator();
        User nUser = new User(Integer.parseInt(data[1]), data[2], data[3], data[4], data[5], data[6], data[7]);
        return nUser;
    }
}

