package Mundo.Cuentas;

import java.sql.ResultSet;

import Model.ModelBuilderMethods;

/**
 * clase para crear los registros
 */
public class CuentaBuilder implements ModelBuilderMethods<Cuenta>{
    /**
     * creates an account using rst.
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
     * creates an account using string
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

