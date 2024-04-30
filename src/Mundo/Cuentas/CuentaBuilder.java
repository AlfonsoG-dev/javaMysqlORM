package Mundo.Cuentas;

import java.sql.ResultSet;

import Model.ModelBuilderMethods;

/**
 * clase para crear los registros
 */
public class CuentaBuilder implements ModelBuilderMethods<Cuenta>{
    /**
     * crea la cuenta con los datos del ResultSet
     * @param rst: el resultado de la consulta sql
     * @param capacity: tamaño de la lista de datos
     * @return la cuenta creada con los datos
     */
    @Override
    public Cuenta createFromRST(ResultSet rst, int capacity) throws Exception {
        Object[] data  = resultDataValidator(rst, capacity);
        Class<?> c = Cuenta.class;

        Cuenta m = (Cuenta) c.getConstructor(
                int.class,
                String.class,
                String.class,
                int.class,
                String.class,
                String.class,
                String.class
        ).newInstance(data);
        return m;
    }
    /**
     * crea la cuenta con los datos que llegan por parametro en forma de String
     * @param datos: datos del usuario a crear
     * @return la cuenta creada a partir de los daots
     */
    @Override
    public Cuenta createFromSTR(String datos) throws Exception {
        Object[] validate = datos.split(",");
        Class<?> c = Cuenta.class;
        Cuenta m = (Cuenta) c.getConstructor(
                int.class,
                String.class,
                String.class,
                int.class,
                String.class,
                String.class,
                String.class
        ).newInstance(validate);
        return m;
    }
}

