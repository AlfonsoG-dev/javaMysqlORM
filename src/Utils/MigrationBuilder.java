package Utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import Model.ModelMethods;

/**
 * clase para crear las sentencias sql de migracion de datos según el modelo
 */
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
     * seleccionar la base de datos a utilizar
     * @param DbName: nombre de la base de datos a utilizar
     * @return la sentencia sql para cambiar de base de datos
     */
    public String CreateSelectDatabase(String DbName) {
        String sql = "use " + DbName;
        return sql;
    }
    /**
     * crear la tabla de datos si no existe
     * @param model: modelo con los datos de las columnas y typo de dato para la tabla
     * @return la sentencia sql para crear la tabla de datos
     */
    public String CreateTableQuery(ModelMethods model) {
        String[] columns = query_util.GetModelColumns(model.InitModel(), true).split(",");
        String[] types = query_util.GetModelType(model.InitModel(), true).split(",");
        String t_c = "";
        for(String t: types) {
            if(t.contains(".")) {
                t_c = t.replace(".", ",");
            }
        }
        String values = "(";
        for(int i = 0; i < columns.length; ++i) {
            if(types[i].contains(".")) {
                types[i] = t_c;
            }
            String clear_types = types[i].replace("'", "");
            values += columns[i] + " " + clear_types +", ";
        }
        String clear_values = query_util.CleanValues(values, 2)+ ")";
        String sql = "create table if not exists " + this.tableName + clear_values;
        return sql;
    }
    /**
     * crear la sentencia para alterar la tabla
     * @param AlterOperation: tipo de operacíon a realizar
     * @return sentencia para alterar la tabla
     */
    private final String CreateAlterTableQuery(String alterOperations) {
        return "alter table " + this.tableName + " " + alterOperations;
    }
    /**
     * crea la sentencia sql para agregar una columna a la tabla
     * @param model_properties: propiedades del modelo
     * @param rst: resultado de la consulta sql
     * @throws SQLException: error de la sentencia sql
     * @return la sentencia sql para agregar una columna a la tabla
     */
    public String CreateAddColumnQuery(String model_properties, String ref_model_properties, String ref_table, boolean includePKFK , ResultSet rst) throws SQLException {
        String sql = "";
        String add_columns = query_util.CompareColumnName(model_properties, rst).get("agregar");
        if(add_columns != "") {
            String columns = query_util.GetModelColumns(add_columns, true);
            String[] clean_columns = query_util.CleanValues(columns, 1).split(",");
            String[] model_types = query_util.GetModelType(model_properties, false).split(",");
            String[] model_columns = query_util.GetModelColumns(model_properties, true).split(",");
            for(String k: clean_columns) {
                int index_type = query_util.SearchColumnType(model_properties, k);
                String clear_types = index_type < model_types.length ? model_types[index_type].replace("'", "") : "null";
                String clear_model_columns  = index_type < model_columns.length ? model_columns[index_type-1] : "null";
                sql += "add column " + k + " " + clear_types + " after " + clear_model_columns + ", ";
            }
        } 
        if(add_columns != "" && includePKFK) {
            sql += this.CreateAddConstraintQuery(add_columns, ref_model_properties, ref_table);
        }
        String clear_sql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clear_sql = query_util.CleanValues(sql, 2);
            res = this.CreateAlterTableQuery(clear_sql);
        }
        return res;
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
                sql += "rename column " + values[1] + " to " + values[0] +  ", ";
            }
        }
        String clear_sql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clear_sql = query_util.CleanValues(sql, 2);
            res = this.CreateAlterTableQuery(clear_sql);
        }
        return res;
    }
    /**
     * crea la sentencia sql para modificar el tipo de dato de una columna
     * @param model_properties: propiedades del modelo
     */
    public String CreateChangeTypeQuery(String model_properties, boolean includePKFK,ResultSet rst) throws SQLException {
        String sql = "";
        String rename_types = query_util.CompareColumnType(model_properties, rst).get("rename");
        if(rename_types != "" && rename_types != null) {
            String[] types = rename_types.split(", ");
            String[] model_columns = query_util.GetModelColumns(model_properties, includePKFK).split(",");
            for(String t: types) {
                String type = t.split(":")[0];
                int index = Integer.parseInt(t.split(":")[1]);
                sql += "modify column " + model_columns[index-1] + " " + type + ", ";
            }
        }
        String clear_sql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clear_sql = query_util.CleanValues(sql, 2);
            res = this.CreateAlterTableQuery(clear_sql);
        }
        return res;
    }
    /**
     * crea la sentencia sql para eliminar una columna de la tabla
     * @param model_properties: propiedades del modelo
     * @param rst: resultado de la consulta sql
     * @throws SQLException: error de la sentencia sql
     * @return la sentencia sql para eliminar columnas
     */
    public String CreateDeleteColumnQuery(String model_properties, boolean includePKFK, ResultSet rst) throws SQLException {
        String sql = "";
        String delete_columns = query_util.CompareColumnName(model_properties, rst).get("eliminar");
        if(delete_columns != "" && delete_columns != null) {
            String[] columns = delete_columns.split(", ");
            for(String k: columns) {
                String[] datos = k.split(":");
                sql += "drop column "  + datos[0] + ", ";
                if(datos[0].contains("_fk")) {
                    sql += this.CreateDeleteConstraintQuery(delete_columns, model_properties, includePKFK);
                }
            }
        }
        String clear_sql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clear_sql = query_util.CleanValues(sql, 2);
            res = this.CreateAlterTableQuery(clear_sql);
        }
        return res;
    }
    /**
     * crea la sentencia sql para agregar constraint  de la pk o fk
     * @param model_properties: propiedades del modelo
     * @param ref_model: propiedades del modelo de referencia
     * @param rst: resultado de la consulta sql
     * @param ref_table: nombre de la tabla de referencia
     * @throws SQLException error de la consulta sql
     * @return la sentencia sql para agregar constraint de la pk o fk
     */
    public String CreateAddConstraintQuery(String add_columns, String ref_model_properties, String ref_table) throws SQLException {
        String sql = "";
        String[] columns = add_columns.split(",");
        String ref_pk = query_util.GetPkFk(ref_model_properties).get("pk");
        for(String k: columns) {
            if(k.contains("pk") == true) {
                sql += "add constraint " + k + " primary key(" + k + "), ";
            }
            if(k.contains("fk") == true) {
                sql += "add constraint " + k + " foreign key(" + k +") references " + ref_table + "(" + ref_pk + ") on delete cascade on update cascade, ";
            }
        }
        String clear_sql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clear_sql = query_util.CleanValues(sql, 0);
            res = clear_sql;
        }
        return res;
    }
    /**
     * crea la sentencia sql para eliminar el constraint de la pk o fk
     * @param model_properties: propiedades del modelo
     * @param rst: resultado de la consulta sql
     * @throws SQLException: error de la consulta sql
     * @return la sentencia sql para eliminar el constraint de la pk o fk
     */
    public String CreateDeleteConstraintQuery(String delete_columns, String model_properties, boolean includePKFK) throws SQLException {
        String sql = "";
         if(delete_columns != "" && delete_columns != null) {
             String k1 = delete_columns.split(", ")[0];
             String[] k2 = k1.split(":");
             if(k2[0].contains("pk")) {
                 sql += "drop primary key , ";
             }
             if(k2[0].contains("fk")) {
                 sql += "drop foreign key " + k2[0] + ", ";

             }
         }
        String clear_sql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clear_sql = query_util.CleanValues(sql, 0);
            res = clear_sql;
        }
        return res;
    }

}
