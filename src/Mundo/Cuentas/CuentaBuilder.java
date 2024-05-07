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
        String[] data  = resultDataValidator(rst, capacity);
        Cuenta m = new Cuenta(
                Integer.parseInt(data[1]),
                data[2],
                data[3],
                Integer.parseInt(data[4]),
                data[5],
                data[6],
                data[7]
        );
        return m;
    }
    /**
     * creates an account using string
     */
    @Override
    public Cuenta createFromSTR(String datos) throws Exception {
        String[] data = datos.split(",");
        Cuenta m = new Cuenta(
                Integer.parseInt(data[0]),
                data[1],
                data[2],
                Integer.parseInt(data[3]),
                data[4],
                data[5],
                data[6]
        );
        return m;
    }
}

