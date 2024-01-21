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
    private QueryUtils queryUtil;
    /**
     * metodo constructor
     * @param nTableName: nombre de la tabla que se utiliza para la creación de la query
     */
    public MigrationBuilder(String nTableName) {
        super(nTableName);
        tableName = nTableName;
        queryUtil = new QueryUtils();
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
        String[] columns = queryUtil.GetModelColumns(model.InitModel(), true).split(",");
        String[] types = queryUtil.GetModelType(model.InitModel(), true).split(",");
        String typeColumn = "";
        for(String t: types) {
            if(t.contains(".")) {
                typeColumn = t.replace(".", ",");
            }
        }
        String values = "(";
        for(int i = 0; i < columns.length; ++i) {
            if(types[i].contains(".")) {
                types[i] = typeColumn;
            }
            String clearTypes = types[i].replace("'", "");
            values += columns[i] + " " + clearTypes +", ";
        }
        String clearValues = queryUtil.CleanValues(values, 2)+ ")";
        String sql = "create table if not exists " + this.tableName + clearValues;
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
    public String CreateAddColumnQuery(String modelProperties, String refModelProperties, String refTable, boolean includePKFK , ResultSet rst) throws SQLException {
        String sql = "";
        String addColumns = queryUtil.CompareColumnName(modelProperties, rst).get("agregar");
        if(addColumns != "") {
            String columns = queryUtil.GetModelColumns(addColumns, true);
            String[] cleanColumns = queryUtil.CleanValues(columns, 1).split(",");
            String[] modelTypes = queryUtil.GetModelType(modelProperties, false).split(",");
            String[] modelColumns = queryUtil.GetModelColumns(modelProperties, true).split(",");
            for(String k: cleanColumns) {
                int indexType = queryUtil.SearchColumnType(modelProperties, k);
                String clearTypes = indexType < modelTypes.length ? modelTypes[indexType].replace("'", "") : "null";
                String clearModelColumns  = indexType < modelColumns.length ? modelColumns[indexType-1] : "null";
                sql += "add column " + k + " " + clearTypes + " after " + clearModelColumns + ", ";
            }
        } 
        if(addColumns != "" && includePKFK) {
            sql += this.CreateAddConstraintQuery(addColumns, refModelProperties, refTable);
        }
        String clearSql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clearSql = queryUtil.CleanValues(sql, 2);
            res = this.CreateAlterTableQuery(clearSql);
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
    public String CreateRenameColumnQuery(String modelProperties, ResultSet rst) throws SQLException {
        String sql = "";
        String renameColumns = queryUtil.CompareColumnName(modelProperties, rst).get("rename");
        if(renameColumns != "" && renameColumns != null) {
            String[] keys = renameColumns.split(", ");
            for(String co: keys) {
                String[] values = co.split(":");
                sql += "rename column " + values[1] + " to " + values[0] +  ", ";
            }
        }
        String clearSql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clearSql = queryUtil.CleanValues(sql, 2);
            res = this.CreateAlterTableQuery(clearSql);
        }
        return res;
    }
    /**
     * crea la sentencia sql para modificar el tipo de dato de una columna
     * @param model_properties: propiedades del modelo
     */
    public String CreateChangeTypeQuery(String modelProperties, boolean includePKFK,ResultSet rst) throws SQLException {
        String sql = "";
        String renameTypes = queryUtil.CompareColumnType(modelProperties, rst).get("rename");
        if(renameTypes != "" && renameTypes != null) {
            String[] types = renameTypes.split(", ");
            String[] modelColumns = queryUtil.GetModelColumns(modelProperties, includePKFK).split(",");
            for(String t: types) {
                String type = t.split(":")[0];
                int index = Integer.parseInt(t.split(":")[1]);
                sql += "modify column " + modelColumns[index-1] + " " + type + ", ";
            }
        }
        String clearSql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clearSql = queryUtil.CleanValues(sql, 2);
            res = this.CreateAlterTableQuery(clearSql);
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
    public String CreateDeleteColumnQuery(String modelProperties, boolean includePKFK, ResultSet rst) throws SQLException {
        String sql = "";
        String deleteColumns = queryUtil.CompareColumnName(modelProperties, rst).get("eliminar");
        if(deleteColumns != "" && deleteColumns != null) {
            String[] columns = deleteColumns.split(", ");
            for(String k: columns) {
                String[] datos = k.split(":");
                sql += "drop column "  + datos[0] + ", ";
                if(datos[0].contains("fk")) {
                    sql += this.CreateDeleteConstraintQuery(deleteColumns, includePKFK);
                }
            }
        }
        String clearSql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clearSql = queryUtil.CleanValues(sql, 2);
            res = this.CreateAlterTableQuery(clearSql);
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
    public String CreateAddConstraintQuery(String addColumns, String refModelProperties, String refTable) throws SQLException {
        String sql = "";
        String[] columns = addColumns.split(",");
        String refpk = queryUtil.GetPkFk(refModelProperties).get("pk");
        for(String k: columns) {
            if(k.contains("pk") == true) {
                sql += "add constraint " + k + " primary key(" + k + "), ";
            }
            if(k.contains("fk") == true) {
                sql += "add constraint " + k + " foreign key(" + k +") references " + refTable + "(" + refpk + ") on delete cascade on update cascade, ";
            }
        }
        String clearSql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clearSql = queryUtil.CleanValues(sql, 0);
            res = clearSql;
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
    public String CreateDeleteConstraintQuery(String deleteColumns, boolean includePKFK) throws SQLException {
        String sql = "";
         if(deleteColumns != "" && deleteColumns != null) {
             String k1 = deleteColumns.split(", ")[0];
             String[] k2 = k1.split(":");
             if(k2[0].contains("pk")) {
                 sql += "drop primary key , ";
             }
             if(k2[0].contains("fk")) {
                 sql += "drop foreign key " + k2[0] + ", ";

             }
         }
        String clearSql = "";
        String res = "";
        if(sql != "" && sql != null) {
            clearSql = queryUtil.CleanValues(sql, 0);
            res = clearSql;
        }
        return res;
    }

}
