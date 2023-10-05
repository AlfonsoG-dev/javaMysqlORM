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
     * crear la base de datos si no existe
     * @param DbName: nombre de la base de datos
     * @return la sentencia sql para crear la base de datos
     */
    public String CreateDataBaseQuery(String DbName) {
        String sql = "create database if not exists " + DbName;
        return sql;
    }
    /**
     * crear la tabla de datos si no existe
     * @param model: modelo con los datos de las columnas y typo de dato para la tabla
     * @return la sentencia sql para crear la tabla de datos
     */
    public String CreateTableQuery(ModelMethods model) {
        String[] columns = query_util.GetModelColumns(model.InitModel(), true).split(", ");
        String[] types = query_util.GetModelType(model.InitModel(), true).split(",");
        String values = "(";
        for(int i = 0; i < columns.length; ++i) {
            String clear_types = types[i].replace("'", "");
            values += columns[i] + " " + clear_types +", ";
        }
        String clear_values = query_util.CleanValues(values, 2)+ ")";
        String sql = "create table if not exists " + this.tableName + clear_values ;
        return sql;
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
