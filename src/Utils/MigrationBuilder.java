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
    public String createDataBaseQuery(String dbName) {
        return "CREATE DATABASE IF NOT EXISTS " + dbName;
    }
    /**
     * seleccionar la base de datos a utilizar
     * @param DbName: nombre de la base de datos a utilizar
     * @return la sentencia sql para cambiar de base de datos
     */
    public String createSelectDatabase(String dbName) {
        return "USE " + dbName;
    }
    /**
     * crear la tabla de datos si no existe
     * @param model: modelo con los datos de las columnas y typo de dato para la tabla
     * @param type: temporary or normal
     * @return la sentencia sql para crear la tabla de datos
     */
    public String createTableQuery(ModelMethods model, String type) {
        String typeColumn = "", modelInit = model.initModel();
        String[] columns = queryUtil.getModelColumns(
                modelInit,
                true
        ).split(",");
        String[] types = queryUtil.getModelType(
                modelInit,
                true
        ).split(",");
        for(String t: types) {
            if(t.contains(".")) {
                typeColumn = t.replace(".", ",");
            }
        }
        StringBuffer values = new StringBuffer();
        values.append("(");
        for(int i = 0; i < columns.length; ++i) {
            if(types[i].contains(".")) {
                types[i] = typeColumn;
            }
            String clearTypes = types[i].replace("'", "");
            values.append(columns[i] + " " + clearTypes +", ");
        }
        String clearValues = queryUtil.cleanValues(values.toString(), 2)+ ")";
        String sql = "";
        // n = normal
        // t = temporary
        if(type.equals("n")) {
            sql = "CREATE TABLE IF NOT EXISTS " + tableName + clearValues;
        } else if(type.equals("t")) {
            sql = "CREATE TEMPORARY TABLE " + "t_" + tableName + clearValues;
        }
        return sql;
    }
    /**
     * crear la sentencia para alterar la tabla
     * @param AlterOperation: tipo de operacíon a realizar
     * @return sentencia para alterar la tabla
     */
    private String createAlterTableQuery(String alterOperations) {
        return "ALTER TABLE " + this.tableName + " " + alterOperations;
    }
    /**
     * crea la sentencia sql para agregar una columna a la tabla
     * @param model_properties: propiedades del modelo
     * @param rst: resultado de la consulta sql
     * @throws SQLException: error de la sentencia sql
     * @return la sentencia sql para agregar una columna a la tabla
     */
    public String createAddColumnQuery(String modelProperties, String refModelProperties, String refTable,
            boolean includePKFK , ResultSet rst) throws SQLException {
        StringBuffer 
            addColumns = queryUtil.compareColumnName(modelProperties, rst).get("agregar"),
            sql        = new StringBuffer();
        if(!addColumns.isEmpty()) {
            String columns = queryUtil.getModelColumns(addColumns.toString(), includePKFK);
            String[] 
                cleanColumns = queryUtil.cleanValues(columns, 1).split(","),
                modelTypes   = queryUtil.getModelType(modelProperties, includePKFK).split(","),
                modelColumns = queryUtil.getModelColumns(modelProperties, includePKFK).split(",");
            for(String k: cleanColumns) {
                int indexType              = queryUtil.searchColumnType(modelProperties, k);
                String clearTypes          = indexType < modelTypes.length ?
                    modelTypes[indexType].replace("'", "") : "null";
                String clearModelColumns  = indexType < modelColumns.length ?
                    modelColumns[indexType-1] : "null";
                if(clearTypes.contains(".")) {
                    clearTypes = clearTypes.split("\\.")[0];
                }
                sql.append(
                        "ADD COLUMN " + k + " " + clearTypes + " AFTER " + clearModelColumns + ", "
                );
                if(includePKFK == true) {
                    sql.append(
                            createAddConstraintQuery(
                                addColumns.toString(),
                                refModelProperties,
                                refTable
                            )
                    );
                }
            }

        } 
        String 
            clearSql = "",
            res      = "";
        if(!sql.isEmpty()) {
            if(includePKFK == true) {
                clearSql = queryUtil.cleanValues(sql.toString(), 2);
            } else {
                clearSql = queryUtil.cleanValues(sql.toString(), 0);
            }
            res = createAlterTableQuery(clearSql);
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
    public String createRenameColumnQuery(String modelProperties, ResultSet rst) throws SQLException {
        StringBuffer 
            renameColumns = queryUtil.compareColumnName(modelProperties, rst).get("rename"),
            sql           = new StringBuffer();
        if(!renameColumns.isEmpty()) {
            String[] keys = renameColumns.toString().split(", ");
            for(String co: keys) {
                String[] values = co.split(":");
                sql.append(
                        "RENAME COLUMN " + values[1] + " TO " + values[0] +  ", "
                );
            }
        }
        String 
            clearSql = "",
            res      = "";
        if(!sql.isEmpty()) {
            clearSql = queryUtil.cleanValues(sql.toString(), 2);
            res      = createAlterTableQuery(clearSql);
        }
        return res;
    }
    /**
     * crea la sentencia sql para modificar el tipo de dato de una columna
     * @param model_properties: propiedades del modelo
     */
    public String createChangeTypeQuery(String modelProperties, boolean includePKFK,ResultSet rst) 
            throws SQLException {
            StringBuffer 
                renameTypes = queryUtil.compareColumnType(modelProperties, rst).get("rename"),
                sql = new StringBuffer();
        if(!renameTypes.isEmpty()) {
            String[] 
                types        = renameTypes.toString().split(", "),
                modelColumns = queryUtil.getModelColumns(modelProperties, includePKFK).split(",");
            for(String t: types) {
                String type = t.split(":")[0];
                int index   = Integer.parseInt(t.split(":")[1]);
                sql.append(
                        "MODIFY COLUMN " + modelColumns[index] + " " + type + ", "
                );
            }
        }
        String 
            clearSql = "",
            res      = "";
        if(!sql.isEmpty()) {
            clearSql = queryUtil.cleanValues(sql.toString(), 2);
            res      = createAlterTableQuery(clearSql);
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
    public String createDeleteColumnQuery(String modelProperties, boolean includePKFK, ResultSet rst)
            throws SQLException {
        StringBuffer 
            deleteColumns = queryUtil.compareColumnName(modelProperties, rst).get("eliminar"),
            sql = new StringBuffer();
        if(!deleteColumns.isEmpty()) {
            String[] columns = deleteColumns.toString().split(", ");
            for(String k: columns) {
                String[] datos = k.split(":");
                if(datos[0].contains("fk")) {
                    sql.append(
                            createDeleteConstraintQuery(
                            deleteColumns.toString(),
                            includePKFK) + "DROP COLUMN " + datos[0] + ", "
                    );
                } else {
                    sql.append("DROP COLUMN "  + datos[0] + ", ");
                }
            }
        }
        String clearSql = "";
        String res = "";
        if(!sql.isEmpty()) {
            clearSql = queryUtil.cleanValues(sql.toString(), 2);
            res = createAlterTableQuery(clearSql);
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
    public String createAddConstraintQuery(String addColumns, String refModelProperties, String refTable)
            throws SQLException {
        String refpk     = queryUtil.getPkFk(refModelProperties).get("pk");
        StringBuffer sql = new StringBuffer();
        String[] columns = addColumns.split(",");
        for(String k: columns) {
            if(k.contains("pk") == true) {
                sql.append(
                        "ADD CONSTRAINT " + k + " PRIMARY KEY(" + k + "), "
                );
            }
            if(k.contains("fk") == true) {
                sql.append(
                        "ADD CONSTRAINT " + k + " FOREIGN KEY(" + k +") REFERENCES " +
                        refTable + "(" + refpk + ") ON DELETE CASCADE ON UPDATE CASCADE, "
                );
            }
        }
        String 
            clearSql = "",
            res      = "";
        if(!sql.isEmpty()) {
            clearSql = queryUtil.cleanValues(sql.toString(), 0);
            res      = clearSql;
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
    public String createDeleteConstraintQuery(String deleteColumns, boolean includePKFK) throws SQLException {
        StringBuffer sql = new StringBuffer();
         if(deleteColumns != "" && deleteColumns != null) {
             String k1   = deleteColumns.split(", ")[0];
             String[] k2 = k1.split(":");
             if(k2[0].contains("pk")) {
                 sql.append("DROP PRIMARY KEY , ");
             }
             if(k2[0].contains("fk")) {
                 sql.append("DROP FOREIGN KEY " + k2[0] + ", ");

             }
         }
        String 
            clearSql = "",
            res      = "";
        if(!sql.isEmpty()) {
            clearSql = queryUtil.cleanValues(sql.toString(), 0);
            res      = clearSql;
        }
        return res;
    }

}
