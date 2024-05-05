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
        Object[] data = resultDataValidator(rst, capacity);
        Class<?> c = User.class;
        User m = (User) c.getConstructor(
                int.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class
        ).newInstance(data);
        return m;
    }
    /**
     * creates user form string
     */
    @Override
    public User createFromSTR(String datos) throws Exception {
        Object[] validate = datos.split(",");
        Class<?> c = User.class;
        User m = (User) c.getConstructor(
                int.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class
        ).newInstance(validate);
        return m;
    }
}

