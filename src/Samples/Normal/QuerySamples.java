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
            condition  = "nombre: test, rol: admin",
            columns  = "nombre, email",
            type     = "or";

        boolean isCreated = myDAO.createView(
                viewName,
                condition,
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
        String condition = "nombre: test", type = "and";
        T myObject = myDAO.findOne(condition, type);
        if(myObject != null) {
            System.out.println(myObject.getClass().getName());
        }
    }

    public void sampleFindByColumnName() {
        String condition = "nombre: test", type = "and";
        T myObject = myDAO.findByColumnName(condition, type);
        if(myObject != null) {
            System.out.println(myObject.getClass().getName());
        }
    }

    public void sampleFindIn() {
        String
            returnColumns      = "nombre, email, rol",
            conditionalColumns = "nombre",
            condition          = "test, admin",
            type               = "and";
        String result = myDAO.findIn(
                returnColumns,
                conditionalColumns,
                condition,
                type
        );
        System.out.println(result);
    }
    public void sampleFindPattern() {
        String 
            pattern = "@gmail.com%",
            condition = "email, nombre",
            type    = "or";
        T myObject = myDAO.findPattern(
                pattern,
                condition,
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
            condition = "nombre: admin, rol: admin",
            columns = "nombre, email, rol, create_at",
            type    = "or";
        String result = myDAO.getValueOfColumnName(
                condition,
                columns,
                type
        );
        System.out.println(result);
    }
    public void sampleInnerJoin(ModelMethods primary, ModelMethods foreign) {
        String
            foreignT     = "foreign_table_name",
            condition    = "nombre: test",
            type         = "or";
        String result = myDAO.innerJoin(
                primary,
                foreign,
                foreignT,
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
            sourceTable  = "source_table_name",
            targetTable  = "target_table_name",
            condition    = "nombre: admin, rol: admin",
            columns      = "nombre, email, rol",
            type         = "or";
        boolean isInserted = myDAO.insertIntoSelect(
                sourceTable,
                targetTable,
                condition,
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
            condition = "nombre: test, rol: admin",
            type      = "or";
        boolean isDeleted = myDAO.deleteRegister(condition, type);
        if(isDeleted) {
            System.out.println("deleted");
        }
    }

}
