package Mundo.Cuentas;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    public Cuenta createFromRST(ResultSet rst, int capacity) throws SQLException {
        String[] data  = resultDataValidator(rst, capacity);
        Cuenta nCuenta = new Cuenta(
                Integer.parseInt(data[1]),
                data[2],
                data[3],
                Integer.parseInt(data[4]),
                data[5],
                data[6],
                data[7]
        );
        return nCuenta;
    }
    /**
     * crea la cuenta con los datos que llegan por parametro en forma de String
     * @param datos: datos del usuario a crear
     * @return la cuenta creada a partir de los daots
     */
    @Override
    public Cuenta createFromSTR(String datos) {
        String[] validate = datos.split(",");
        Cuenta nCuenta    = new Cuenta(
                Integer.parseInt(validate[0]),
                validate[1],
                validate[2],
                Integer.parseInt(validate[3]),
                validate[4],
                validate[5],
                validate[6]
        );
        return nCuenta;
    }
}

