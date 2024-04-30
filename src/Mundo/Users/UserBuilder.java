package Mundo.Users;

import java.sql.ResultSet;
import Model.ModelBuilderMethods;

/**
 * clase para crear los registros
 */
public class UserBuilder implements ModelBuilderMethods<User>{
    /**
     * crea el usuario con los datos del ResultSet
     * @param rst: el resultado de la consulta sql
     * @param capacity: tamaño de la lista de datos
     * @return usuario creado con los datos
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
     * crea el usuario con los datos que llegan por parametro en forma de String
     * @param datos: datos del usuario a crear
     * @return el usuario creado a partir de los daots
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

