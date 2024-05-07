package Mundo.Users;

import java.sql.ResultSet;
import Model.ModelBuilderMethods;

/**
 * clase para crear los registros
 */
public class UserBuilder implements ModelBuilderMethods<User>{
    /**
     * creates user from rst
     */
    @Override
    public User createFromRST(ResultSet rst, int capacity) throws Exception {
        String[] data = resultDataValidator(rst, capacity);
        User m = new User(
                Integer.parseInt(data[1]),
                data[2],
                data[3],
                data[4],
                data[5],
                data[6],
                data[7]
        );
        return m;
    }
    /**
     * creates user form string
     */
    @Override
    public User createFromSTR(String datos) throws Exception {
        String[] data = datos.split(",");
        User m = new User(
                Integer.parseInt(data[0]),
                data[1],
                data[2],
                data[3],
                data[4],
                data[5],
                data[6]
        );
        return m;
    }
}

