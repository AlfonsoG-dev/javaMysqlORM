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
        String values = "(";
        for(int i = 0; i < columns.length; ++i) {
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
    public String CreateAddColumnQuery(String model_properties, String ref_model, String ref_table, ResultSet rst) throws SQLException {
        String sql = "";
        String add_columns = query_util.CompareColumnName(model_properties, rst).get("agregar");
        if(add_columns != "" && add_columns != null) {
            String columns = query_util.GetModelColumns(add_columns, true);
            String[] clean_columns = query_util.CleanValues(columns, 1).split(",");
            String[] model_types = query_util.GetModelType(model_properties, true).split(",");
            String[] model_columns = query_util.GetModelColumns(model_properties, true).split(",");
            for(String k: clean_columns) {
                int index_type = query_util.SearchColumnType(k, model_properties);
                String clear_types = model_types[index_type].replace("'", "");
                sql += "add column " + k + " " + clear_types + " after " + model_columns[index_type-1] + ", ";
                if(k.contains("fk")) {
                    sql += this.CreateAddConstraintQuery(model_properties, ref_model, rst, ref_table);
                }
            }
        }
        String clear_sql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clear_sql = query_util.CleanValues(sql, 0);
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
                for(int i=1; i < values.length; ++i) {
                    sql += "rename column " + values[1] + " to " + values[0] +  ", ";
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
     * crea la sentencia sql para modificar el tipo de dato de una columna
     * @param model_properties: propiedades del modelo
     */
    public String CreateChangeTypeQuery(String model_properties, ResultSet rst) throws SQLException {
        String sql = "";
        String rename_types = query_util.CompareColumnType(model_properties, rst).get("rename");
        if(rename_types != "" && rename_types != null) {
            String[] types = rename_types.split(", ");
            String[] model_columns = query_util.GetModelColumns(model_properties, true).split(",");
            for(String t: types) {
                String type = t.split(":")[0];
                int index = Integer.parseInt(t.split(":")[1]);
                sql += "modify column " + model_columns[index] + " " + type + ", ";
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
    public String CreateDeleteColumnQuery(String model_properties, ResultSet rst) throws SQLException {
        String sql = "";
        String delete_columns = query_util.CompareColumnName(model_properties, rst).get("eliminar");
        if(delete_columns != "" && delete_columns != null) {
            String[] columns = delete_columns.split(", ");
            for(String k: columns) {
                String[] datos = k.split(":");
                sql += "drop column "  + datos[0] + ", ";
                if(datos[0].contains("fk")) {
                    sql += this.CreateDeleteConstraintQuery(model_properties, rst);
                }
            }
        }
        String clear_sql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clear_sql = query_util.CleanValues(sql, 0);
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
    public String CreateAddConstraintQuery(String model_properties, String ref_model, ResultSet rst, String ref_table) throws SQLException {
        String sql = "";
        String add_columns = query_util.CompareColumnName(model_properties, rst).get("agregar");
        String ref_pk = query_util.GetPkFk(ref_model).get("pk");
        if(add_columns != "" && add_columns != null) {
            String[] columns = add_columns.split(", ");
            for(int i=0; i<columns.length; ++i) {
                if(columns[i].contains("pk") == true) {
                    sql += "add constraint " + columns[i] + "primary key(" + columns[i] + "), ";
                }
                if(columns[i].contains("fk") == true) {
                    sql += "add constraint " + columns[i] + " foreign key(" + columns[i] +") references " + ref_table + "(" + ref_pk + ") on delete cascade on update cascade, ";
                }
            }
        }
        String clear_sql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clear_sql = query_util.CleanValues(sql, 2);
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
    public String CreateDeleteConstraintQuery(String model_properties, ResultSet rst) throws SQLException {
        String sql = "";
        String delete_columns = query_util.CompareColumnName(model_properties, rst).get("eliminar");
        if(delete_columns != "" && delete_columns != null) {
            String k1 = delete_columns.split(", ")[0];
            String[] k2 = k1.split(":");
            String[] model_types = query_util.GetModelType(model_properties, true).split(",");
            String[] model_columns = query_util.GetModelColumns(model_properties, true).split(",");
            int index = Integer.parseInt(k2[1]);
            if(model_types[index].contains("primary key")) {
                sql += "drop primary key , ";
            }
            if(model_types[index].contains("foreign key")) {
                sql += "drop foreign key " + model_columns[index] + ", ";
            }

        }
        String clear_sql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clear_sql = query_util.CleanValues(sql, 2);
            res = clear_sql;
        }
        return res;
    }

}
