package Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
     * crear la sentencia para alterar la tabla
     * @param AlterOperation: tipo de operacíon a realizar
     * @return sentencia para alterar la tabla
     */
    private final String CreateAlterTableQuery(String AlterOperation) {
        return "alter table " + this.tableName + " " + AlterOperation;
    }
    /**
     * crea la sentencia sql para agregar una columna a la tabla
     * @param model_properties: propiedades del modelo
     * @param rst: resultado de la consulta sql
     * @throws SQLException: error de la sentencia sql
     * @return la sentencia sql para agregar una columna a la tabla
     */
    public String CreateAddColumnQuery(String model_properties, ResultSet rst) throws SQLException {
        String sql = "";
        String add_columns = query_util.CompareColumnName(model_properties, rst).get("agregar");
        if(add_columns != "" && add_columns != null) {
            String columns = query_util.GetModelColumns(add_columns, true);
            String[] clean_columns = query_util.CleanValues(columns, 2).split(",");
            String[] model_types = query_util.GetModelType(model_properties, true).split(",");
            String[] model_columns = query_util.GetModelColumns(model_properties, true).split(",");
            for(String k: clean_columns) {
                int index_type = query_util.SearchColumnType(k, model_properties);
                String clear_types = model_types[index_type].replace("'", "");
                sql += "add column " + k + " " + clear_types + " after " + model_columns[index_type-1] + ", ";
            }
        }
        String clear_sql = query_util.CleanValues(sql, 2);
        return this.CreateAlterTableQuery(clear_sql);
    }
    /**
     * crea la sentencia sql para renombrar las columnas de la tabla
     * @param model_properties: propiedades del modelo
     * @param rst: resultado de la consulta sql
     * @throws SQLException: error de la sentencia sql
     * @return la sentencia sql para renombrar columnas
     */
    public String CreateRenameColumnQuery(String model_properties, ResultSet rst) throws SQLException {
        String sql = "";
        String rename_columns = query_util.CompareColumnName(model_properties, rst).get("rename");
        if(rename_columns != "" && rename_columns != null) {
            String[] keys = rename_columns.split(", ");
            for(String co: keys) {
                String[] values = co.split(":");
                for(int i=1; i < values.length; ++i) {
                    sql += "rename column " + values[0] + " to " + values[1] +  ", ";
                }
            }
        }
        String clear_sql = query_util.CleanValues(sql, 2);
        System.out.println(this.CreateAlterTableQuery(clear_sql));
        return sql;
    }
    /**
     */
    public String CreateDeleteColumnQuery() {
        //TODO: implementar la creación de delete column
        return "";
    }
    /**
     */
    public String CreateAddPKFKColumn() {
        //TODO: implementar la obtencion de constraint
        return "";
    }

}
