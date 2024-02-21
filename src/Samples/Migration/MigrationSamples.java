package Samples.Migration;

import java.sql.Connection;

import Conexion.Migration.MigrationDAO;
import Model.ModelMethods;

public class MigrationSamples {
    private Connection cursor;
    private MigrationDAO myDAO;
    private String tableName;
    public MigrationSamples(Connection nCursor, String nTableName) {
        cursor = nCursor;
        tableName = nTableName;
        myDAO = new MigrationDAO(tableName, cursor);
    }

    public void sampleCreateDataBase(String dbName) {
        // the method already prints a message when database is created
        myDAO.createDataBase(dbName);
    }

    public void sampleCreateTable(ModelMethods newModel) {
        // the method already prints a message when table model is created
        myDAO.createTable(newModel);
    }

    public void sampleCreateTemporaryTable(ModelMethods model) {
        // the method prints a message when temporary table is created.
        // it uses the same name as the normal table only adding "t_" at the start.
        myDAO.createTemporaryTable(model);
    }
    public void sampleShowTableData() {
        boolean exists = myDAO.showTableData();
        if(exists) {
            System.out.println(
                    String.format(
                        "the table: %s exists",
                        tableName
                    )
            );
        }
    }

    public void sampleAddColumn(ModelMethods primaryModel, ModelMethods foreingModel) {
        // <pre> add a field to the model first
        // @TableProperties(miConstraint = "not null", miType = "varchar(50)")
        // private String nuevoF
        boolean added = myDAO.addColumn(
                primaryModel,
                foreingModel,
                "foreign_table_name",
                true
        );

        if(added) {
            System.out.println("a column has been added");
        }
    }

    public void sampleRenameColumn(ModelMethods model) {
        // <pre> rename a field of the table model first
        // private String renamedF
        boolean renamed = myDAO.renameColumn(model);

        if(renamed) {
            System.out.println("a field has been renamed");
        }
    }

    public void sampleChageType(ModelMethods model) {
        // <pre> change the type of the field first
        // @TableProperties(miConstraint = "not null", miType = "text")

        boolean changed = myDAO.changeType(model, true);

        if(changed) {
            System.out.println("a field type has been changed");
        }
    }

    public void sampleDeleteColumn(ModelMethods model) {
        // <pre> delete a filed from the table model first

        boolean deleted = myDAO.deleteColumn(model, true);
        if(deleted) {
            System.out.println("a field has been deleted");
        }
    }
}
