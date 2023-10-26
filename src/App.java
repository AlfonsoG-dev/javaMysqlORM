import Config.DbConfig;
import Mundo.Cuentas.Cuenta;
import Utils.MigrationBuilder;

/**
 * clase principal de la APP
 */
public class App {
    public static void main(String[] args) {
        try {
            DbConfig miConfig = new DbConfig("test_db", "localhost", "3306", "test_user", "5x5W12");
            System.out.println(miConfig.GetMysqlUrl());
            MigrationBuilder build = new MigrationBuilder("cuentas");
            Cuenta mia = new Cuenta(0, null, null, 0, null, null);
            System.out.println( build.CreateTableQuery(mia));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
