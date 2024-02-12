package Samples.Normal;

import java.sql.Connection;
import java.util.ArrayList;

import Conexion.Query.QueryDAO;
import Model.ModelBuilderMethods;
import Model.ModelMethods;

public class QuerySamples<T> {
    private Connection cursor;
    private QueryDAO<T> myDAO;
    private String tableName;
    public QuerySamples(Connection nCursor, String nTableName, ModelBuilderMethods<T> builder) {
        cursor = nCursor;
        tableName = nTableName;
        myDAO = new QueryDAO<>(tableName, cursor, builder);
    }

    public void sampleAnyQuery() {
        String condition = " where nombre='test'";
        String sql = "select * from " + tableName + condition;
        String result = myDAO.anyQuery(sql);
        System.out.println(
                String.format(
                    "Result of the execution of anyQuery method: \n%s",
                    result
                )
        );
    }

    public void sampleAnyExecution() {
        String condition = " where nombre='test'";
        String sql = "update " + tableName + " set column='otroValue'" + condition;
        boolean isUpdated = myDAO.anyExecution(sql);
        if(isUpdated) {
            System.out.println("updated");
        }
    }

    public void sampleCreateView()  {
        String
            viewName = "my_view_name",
            options  = "nombre: test, rol: admin",
            columns  = "nombre, email",
            type     = "or";

        boolean isCreated = myDAO.createView(
                viewName,
                options,
                columns,
                type
        );

        if(isCreated) {
            System.out.println("created");
        }
    }

    public void sampleDeleteView() {
        String viewName = "my_view_name";
        boolean isDeleted = myDAO.deleteView(viewName);
        if(isDeleted) {
            System.out.println("deleted");
        }
    }

    public void sampleCountData() {
        int count = myDAO.countData();
        if(count > 0) {
            System.out.println("the number of data in database is: " + count);
        }
    }

    public void sampleReadAll() {
        ArrayList<T> myList = myDAO.readAll();
        if(myList.size() > 0) {
            System.out.println("the size of list data is: " + myList.size());
        }
    }

    public void sampleFindOne() {
        String options = "nombre: test", type = "and";
        T myObject = myDAO.findOne(options, type);
        if(myObject != null) {
            System.out.println(myObject.getClass().getName());
        }
    }

    public void sampleFindByColumnName() {
        String options = "nombre: test", type = "and";
        T myObject = myDAO.findByColumnName(options, type);
        if(myObject != null) {
            System.out.println(myObject.getClass().getName());
        }
    }

    public void sampleFindIn() {
        String
            returnOptions = "nombre, email, rol",
            columns       = "nombre",
            condition     = "test, admin",
            type          = "and";
        String result = myDAO.findIn(
                returnOptions,
                columns,
                condition,
                type
        );
        System.out.println(result);
    }
    public void sampleFindPattern() {
        String 
            pattern = "@gmail.com%",
            options = "email, nombre",
            type    = "or";
        T myObject = myDAO.findPattern(
                pattern,
                options,
                type
        );
        if(myObject != null) {
            System.out.println(myObject.getClass().getName());
        }
    }

    public void sampleGetMinMax() {
        String 
            columns   = "max: create_at, min: nombre",
            condition = "nombre: test",
            type      = "and";
        String result = myDAO.getMinMax(
                columns,
                condition,
                type
        );
        System.out.println(result);
    }

    public void sampleGetValueOfColumn() {
        String
            options = "nombre: admin, rol: admin",
            columns = "nombre, email, rol, create_at",
            type    = "or";
        String result = myDAO.getValueOfColumnName(
                options,
                columns,
                type
        );
        System.out.println(result);
    }
    public void sampleInnerJoin(ModelMethods sourceModel, ModelMethods targetModel) {
        String
            targetTable  = "target_table_name",
            condition    = "nombre: test",
            type         = "or";
        String result = myDAO.innerJoin(
                sourceModel,
                targetModel,
                targetTable,
                condition,
                type
        );
        System.out.println(result);
    }

    public void sampleInsertNewRegister(ModelMethods model) {
        String 
            condition = "nombre: test",
            type      = "or";
        boolean isInserted = myDAO.insertNewRegister(
                model,
                condition,
                type
        );
        if(isInserted) {
            System.out.println("inserted");
        }
    }

    public void sampleInsertIntoSelection() {
        String 
            sourceTable = "source_table_name",
            targetTable = "target_table_name",
            options     = "nombre: admin, rol: admin",
            columns     = "nombre, email, rol",
            type        = "or";
        boolean isInserted = myDAO.insertIntoSelect(
                sourceTable,
                targetTable,
                options,
                columns,
                type
        );
        if(isInserted) {
            System.out.println("inserted");
        }
    }

    public void sampleUpdateRegister(ModelMethods updatedModel) {
        String 
            conditions = "nombre: test",
            type       = "and";
        boolean isUpdated = myDAO.updateRegister(
                updatedModel,
                conditions,
                type
        );
        if(isUpdated) {
            System.out.println("updated");
        }
    }

    public void sampleDeleteRegister() {
        String
            options = "nombre: test, rol: admin",
            type    = "or";
        boolean isDeleted = myDAO.deleteRegister(options, type);
        if(isDeleted) {
            System.out.println("deleted");
        }
    }

}
