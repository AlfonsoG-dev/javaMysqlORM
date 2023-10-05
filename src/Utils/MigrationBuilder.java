package Utils;

import Model.ModelMethods;

public class MigrationBuilder extends QueryBuilder {
    /**
     * la tabla en base de datos
     */
    private String tableName;
    /**
     * utilidades para query
     */
    private QueryUtils query_util;
    /**
     * metodo constructor
     * @param nTableName: nombre de la tabla que se utiliza para la creación de la query
     */
    public MigrationBuilder(String nTableName) {
        super(nTableName);
        tableName = nTableName;
        query_util = new QueryUtils();
    }
    /**
     */
    public String CreateDataBaseQuery(String DbName) {
        String sql = "create database if not exists " + DbName;
        return sql;
    }
    /**
     */
    public String CreateTableQuery(String TableName, ModelMethods model) {
        //TODO: implementar la creación de la tabla si no eixste
        String[] columns = query_util.GetModelColumns(model.InitModel(), true).split(", ");
        String[] types = query_util.GetModelType(model.InitModel(), true).split(",");
        for(int i = 0; i < columns.length; ++i) {
            System.out.println(columns[i]);
        }
        return "create table if not exists tablename(table properties)";
    }
    /**
     */
    public String CreateAlterTableQuery() {
        //TODO: implementar la creación de alter table
        return "";
    }
    /**
     */
    public String CreateAddColumnQuery() {
        //TODO: implementar la creación de add column
        return "";
    }
    /**
     */
    public String CreateRenameColumnQuery() {
        //TODO: implemnetar la creación de renameColumn
        return "";
    }
    /**
     */
    public String CreateDeleteColumnQuery() {
        //TODO: implementar la creación de delete column
        return "";
    }
    /**
     */
    public String GetConstraint() {
        //TODO: implementar la obtencion de constraint
        return "";
    }
    /**
     */
    public String CreateConstraintQuery() {
        //TODO: implementar la creación de constraint
        return "";
    }

}
