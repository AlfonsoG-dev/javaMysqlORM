package Samples.Normal;

import java.sql.Connection;
import java.util.ArrayList;

import Conexion.Query.QueryDAO;
import Model.ModelBuilderMethods;
import Model.ModelMethods;
import Utils.Formats.ParamValue;

public class QuerySamples<T> {
    private Connection cursor;
    private QueryDAO<T> myDAO;
    private String tableName;
    public QuerySamples(Connection nCursor, String nTableName, ModelBuilderMethods<T> builder) {
        cursor    = nCursor;
        tableName = nTableName;
        myDAO     = new QueryDAO<>(tableName, cursor, builder);
    }

    public void sampleAnyQuery() {
        String condition = " where nombre='test'";
        String sql       = "select * from " + tableName + condition;
        String result    = myDAO.anyQuery(sql);
        System.out.println(
                String.format(
                    "Result of the execution of anyQuery method: \n%s",
                    result
                )
        );
    }

    public void sampleAnyExecution() {
        String condition  = " where nombre='test'";
        String sql        = "update " + tableName + " set column='otroValue'" + condition;
        boolean isUpdated = myDAO.anyExecution(sql);
        if(isUpdated) {
            System.out.println("updated");
        }
    }

    public void sampleCreateView()  {
        String
            viewName      = "my_view_name",
            columns       = "nombre, email",
            type          = "or";
        String[]
            cols = {"nombre", "rol"},
            vals = {"test", "admin"};
        ParamValue condition = new ParamValue(cols, vals);

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
        String viewName   = "my_view_name";
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
        String
            order = "nombre: ASC",
            group = "email: DESC";
        ArrayList<T> myList = myDAO.readAll(order, group, 10);
        myList
            .parallelStream()
            .forEach(e -> System.out.println("element: " + e));
    }

    public void samplePreparedFind() {
        String type = "and";
        ParamValue condition = new ParamValue("nombre", "test");
        T myObject       = myDAO.preparedFind(condition, type);
        if(myObject != null) {
            System.out.println(myObject.getClass().getName());
        }
    }

    public void sampleFindByColumnName() {
        String type = "and";
        ParamValue condition = new ParamValue("nombre", "test");
        T myObject       = myDAO.findByColumnName(condition, type);
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
        String result          = myDAO.findIn(
                returnColumns,
                conditionalColumns,
                condition,
                type
        );
        System.out.println(result);
    }
    public void sampleFindPattern() {
        String 
            pattern   = "@gmail.com%",
            condition = "email, nombre",
            type      = "or";
        T myObject    = myDAO.findPattern(
                pattern,
                condition,
                type
        );
        if(myObject != null) {
            System.out.println(myObject.getClass().getName());
        }
    }

    public void sampleGetMinMax() {
        String type      = "and";
        String[]
            cols = {"max", "min"},
            vals = {"create_at", "nombre"};
        ParamValue columns = new ParamValue(cols, vals);
        ParamValue condition = new ParamValue("nombre", "test");
        String result = myDAO.getMinMax(
                columns,
                condition,
                type
        );
        System.out.println(result);
    }

    public void sampleGetValueOfColumn() {
        String
            columns   = "nombre, email, rol, create_at",
            type      = "or";
        String[]
            cols = {"nombre", "rol"},
            vals = {"admin", "admin"};
        ParamValue condition = new ParamValue(cols, vals);
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
            type         = "or";
        ParamValue condition = new ParamValue("nombre", "test");
        String result    = myDAO.innerJoin(
                primary,
                foreign,
                foreignT,
                condition,
                type
        );
        System.out.println(result);
    }

    public void sampleInsertNewRegister(ModelMethods model) {
        String type           = "or";
        ParamValue condition = new ParamValue("nombre", "test");
        boolean isInserted = myDAO.insertNewRegister(
                model,
                condition,
                type
        );
        if(isInserted) {
            System.out.println("inserted");
        }
    }
    public void samplePreparedInsert(ModelMethods model) {
        String type = "or";
        ParamValue condition = new ParamValue("nombre", "test");
        myDAO.preparedInsert(model, condition, type);
    }

    public void sampleInsertByColumns() {
        // options: column: value
        // condition: column: value
        String type        = "and";
        String[]
            opCols = {"nombre", "email", "password"},
            opVals = {"test", "test@gmail.com", "123asd"},
            coCols = {"nombre", "email"},
            coVals = {"test", "test@gmail.com"};
        ParamValue condition = new ParamValue(coCols, coVals);
        ParamValue options = new ParamValue(opCols, opVals);
        boolean isInserted = myDAO.insertByColumns(
                options,
                condition,
                type
        );
        if(isInserted) {
            System.out.println("inserted");
        }
    }
    public void sampleInsertIntoSelection() {
        String 
            sourceTable    = "source_table_name",
            targetTable    = "target_table_name",
            columns        = "nombre, email, rol",
            type           = "or";
        String[]
            cols = {"nombre", "rol"},
            vals = {"admin", "admin"};
        ParamValue condition = new ParamValue(cols, vals);
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
        String type          = "and";
        ParamValue condition = new ParamValue("nombre", "test");
        boolean isUpdated = myDAO.updateRegister(
                updatedModel,
                condition,
                type
        );
        if(isUpdated) {
            System.out.println("updated");
        }
    }
    public void samplePreaparedUpdate(ModelMethods updateModel) {
        String type = "and";
        ParamValue condition = new ParamValue("nombre", "test");
        myDAO.preparedUpdate(updateModel, condition, type);
    }
    public void sampleDeleteRegister() {
        String type          = "or";
        String[]
            cols = {"nombre", "rol"},
            vals = {"test", "admin"};
        ParamValue condition = new ParamValue(cols, vals);
        boolean isDeleted = myDAO.deleteRegister(condition, type);
        if(isDeleted) {
            System.out.println("deleted");
        }
    }
    public void samplePreaparedDelete() {
        String type = "and";
        ParamValue condition = new ParamValue("nombre", "test");
        myDAO.preparedDelete(condition, type);
    }

}
