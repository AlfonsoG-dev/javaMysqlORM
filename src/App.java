import Conexion.Migration.MigrationDAO;
import Config.DbConfig;
import Mundo.Cuentas.Cuenta;

/**
 * clase principal de la APP
 */
public class App {
    public static void main(String[] args) {
        try {
            DbConfig miConfig = new DbConfig("consulta", "localhost", "3306", "test_user", "5x5W12");
            MigrationDAO<Cuenta> miCuentaDAO = new MigrationDAO<>("cuentas", miConfig);
            Cuenta mia = new Cuenta(0, null, null, 0, null, null);
            miCuentaDAO.DeleteColumn(mia);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
