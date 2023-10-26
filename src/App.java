import Config.DbConfig;

/**
 * clase principal de la APP
 */
public class App {
    public static void main(String[] args) {
        try {
            DbConfig miConfig = new DbConfig("test_db", "localhost", "3306", "test_user", "5x5W12");
            System.out.println(miConfig.GetMysqlUrl());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
