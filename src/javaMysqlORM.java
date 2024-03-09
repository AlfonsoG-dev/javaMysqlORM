import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import Conexion.Conector;
import Conexion.Migration.MigrationExecution;
import Config.DbConfig;
import Mundo.Users.User;
import Utils.QueryUtils;

class javaMysqlORM {
    public static void main(String[] args) {
        DbConfig mConfig = new DbConfig(
                "contrasenias",
                "localhost",
                "3306",
                "test_user",
                "5x5W12"
        );
        Conector con = new Conector(mConfig);
        Connection cursor = con.conectarMySQL();
        try {
            QueryUtils util = new QueryUtils();
            Statement stm = cursor.createStatement();
            MigrationExecution exec = new MigrationExecution("cuenta", cursor);
            ResultSet rst = exec.executeShowTableData(stm);
            StringBuffer columns = util.compareColumnName(new User().initModel(), rst).get("rename");
            System.out.println(columns);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
