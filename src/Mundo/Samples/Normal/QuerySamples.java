package Mundo.Samples.Normal;

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
            columns       = "nombre, email";
        String[]
            cols = {"nombre", "rol"},
            vals = {"test", "admin"};
        ParamValue condition = new ParamValue(cols, vals, "or");

        boolean isCreated = myDAO.createView(
                viewName,
                condition,
                columns
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
        ArrayList<T> myList = myDAO.readAll();
        myList
            .parallelStream()
            .forEach(e -> System.out.println("element: " + e));
    }

    public void samplePreparedFind() {
        ParamValue condition = new ParamValue("nombre", "test", "and");
        T myObject       = myDAO.preparedFind(condition);
        if(myObject != null) {
            System.out.println(myObject.getClass().getName());
        }
    }

    public void sampleFindByColumnName() {
        ParamValue condition = new ParamValue("nombre", "test", "and");
        T myObject       = myDAO.findByColumnName(condition);
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
        String[]
            cols = {"max", "min"},
            vals = {"create_at", "nombre"};
        ParamValue columns = new ParamValue(cols, vals);
        ParamValue condition = new ParamValue("nombre", "test", "and");
        String result = myDAO.getMinMax(
                columns,
                condition
        );
        System.out.println(result);
    }

    public void sampleGetValueOfColumn() {
        String columns   = "nombre, email, rol, create_at";
        String[]
            cols = {"nombre", "rol"},
            vals = {"admin", "admin"};
        ParamValue condition = new ParamValue(cols, vals, "or");
        String result = myDAO.getValueOfColumnName(
                condition,
                columns
        );
        System.out.println(result);
    }
    public void sampleInnerJoin(ModelMethods primary, ModelMethods foreign) {
        String foreignT     = "foreign_table_name";
        ParamValue condition = new ParamValue("nombre", "test", "or");
        String result    = myDAO.innerJoin(
                primary,
                foreign,
                foreignT,
                condition
        );
        System.out.println(result);
    }

    public void sampleInsertNewRegister(ModelMethods model) {
        ParamValue condition = new ParamValue("nombre", "test", "or");
        boolean isInserted = myDAO.insertNewRegister(
                model,
                condition
        );
        if(isInserted) {
            System.out.println("inserted");
        }
    }
    public void samplePreparedInsert(ModelMethods model) {
        ParamValue condition = new ParamValue("nombre", "test", "or");
        myDAO.preparedInsert(model, condition);
    }

    public void sampleInsertByColumns() {
        // options: column: value
        // condition: column: value
        String[]
            opCols = {"nombre", "email", "password"},
            opVals = {"test", "test@gmail.com", "123asd"},
            coCols = {"nombre", "email"},
            coVals = {"test", "test@gmail.com"};
        ParamValue condition = new ParamValue(coCols, coVals, "and");
        ParamValue options = new ParamValue(opCols, opVals);
        boolean isInserted = myDAO.insertByColumns(
                options,
                condition
        );
        if(isInserted) {
            System.out.println("inserted");
        }
    }
    public void sampleInsertIntoSelection() {
        String 
            sourceTable    = "source_table_name",
            targetTable    = "target_table_name",
            columns        = "nombre, email, rol";
        String[]
            cols = {"nombre", "rol"},
            vals = {"admin", "admin"};
        ParamValue condition = new ParamValue(cols, vals, "and");
        boolean isInserted = myDAO.insertIntoSelect(
                sourceTable,
                targetTable,
                condition,
                columns
        );
        if(isInserted) {
            System.out.println("inserted");
        }
    }

    public void sampleUpdateRegister(ModelMethods updatedModel) {
        ParamValue condition = new ParamValue("nombre", "test", "and");
        boolean isUpdated = myDAO.updateRegister(
                updatedModel,
                condition
        );
        if(isUpdated) {
            System.out.println("updated");
        }
    }
    public void samplePreaparedUpdate(ModelMethods updateModel) {
        ParamValue condition = new ParamValue("nombre", "test", "and");
        myDAO.preparedUpdate(updateModel, condition);
    }
    public void sampleDeleteRegister() {
        String[]
            cols = {"nombre", "rol"},
            vals = {"test", "admin"};
        ParamValue condition = new ParamValue(cols, vals, "or");
        boolean isDeleted = myDAO.deleteRegister(condition);
        if(isDeleted) {
            System.out.println("deleted");
        }
    }
    public void samplePreaparedDelete() {
        ParamValue condition = new ParamValue("nombre", "test", "and");
        myDAO.preparedDelete(condition);
    }

}
