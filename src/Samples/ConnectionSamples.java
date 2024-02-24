package Samples;

import java.sql.Connection;

import Conexion.Migration.MigrationDAO;
import Conexion.Conector;
import Config.DbConfig;
import Model.ModelMethods;
import Mundo.Cuentas.Cuenta;
import Mundo.Users.User;

public class ConnectionSamples {
    private final static DbConfig InitDB(String db_name) {
        // if the database schema is not created, do not provide its name
        DbConfig mConfig = new DbConfig(
                "",
                "host",
                "port",
                "user",
                "password"
        );
        try {
            // create the database schema
            Connection con     = new Conector(mConfig).conectarMySQL();
            MigrationDAO miDAO = new MigrationDAO("", con);
            miDAO.createDataBase(db_name);
            con.close();
            // return the new connection options with the created schema
            return new DbConfig(
                    db_name,
                    mConfig.hostname(),
                    mConfig.port(),
                    mConfig.username(),
                    mConfig.password()
            );
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private final static void InitTable(String tbName, ModelMethods model, Connection cursor) {
        // create the table from model
        MigrationDAO miDAO = new MigrationDAO(tbName, cursor);
        miDAO.createTable(model);
    }
    private final static void setDatabaseState() {
        try {
            DbConfig miConfig = InitDB("database_name");
            Connection cursor = new Conector(miConfig).conectarMySQL();
            // use the default models
            InitTable(
                    "user",
                    new User(),
                    cursor
            );
            InitTable(
                    "cuenta",
                    new Cuenta(),
                    cursor
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        setDatabaseState();
    }

}

