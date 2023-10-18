package Mundo.Cuentas;

import java.sql.ResultSet;
import java.sql.SQLException;

import Model.ModelBuilderMethods;

/**
 * clase para crear los registros
 */
public class CuentaBuilder implements ModelBuilderMethods<Cuenta>{
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
    public Cuenta CreateFromRST(ResultSet rst, int capacity) throws SQLException {
        String[] data = this.ResultDataValidator(rst, capacity);
        Cuenta nCuenta = new Cuenta(Integer.parseInt(data[1]), data[2], data[3], Integer.parseInt(data[4]), data[5], data[6]);
        return nCuenta;
    }
    /**
     * crea el usuario con los datos que llegan por parametro en forma de String
     * @param datos: datos del usuario a crear
     * @return el usuario creado a partir de los daots
     */
    @Override
    public Cuenta CreateFromSTR(String datos) {
        String[] validate = datos.split(",");
        Cuenta nCuenta = new Cuenta(Integer.parseInt(validate[0]), validate[1], validate[2], Integer.parseInt(validate[3]), validate[4], validate[5]);
        return nCuenta;
    }
}

