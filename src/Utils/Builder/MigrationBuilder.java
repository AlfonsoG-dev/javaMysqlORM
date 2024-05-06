package Utils.Builder;

import java.sql.ResultSet;
import java.sql.SQLException;

import Model.ModelMethods;

import Utils.Query.QueryUtils;
import Utils.Formats.ParamValue;
import Utils.Model.ModelUtils;

/**
 * class for sql query migration building.
 */
public class MigrationBuilder extends QueryBuilder {
    /**
     * table name
     */
    private String tableName;
    /**
     * query utils
     */
    private QueryUtils queryUtil;
    /**
     * model utils
     */
    private ModelUtils modelUtils;
    /**
     * {@link java.lang.reflect.Constructor}
     * @param nTableName: table name
     */
    public MigrationBuilder(String nTableName) {
        super(nTableName);
        tableName = nTableName;
        queryUtil = new QueryUtils();
        modelUtils = new ModelUtils();
    }
    /**
     * create the database schema.
     * @param dbName: database schema name.
     * @return the sql query.
     */
    public String createDataBaseQuery(String dbName) {
        return "CREATE DATABASE IF NOT EXISTS " + dbName;
    }
    /**
     * select or use a database schema.
     * @param dbName: database schema to use.
     * @return the sql query
     */
    public String createSelectDatabase(String dbName) {
        return "USE " + dbName;
    }
    /**
     * create table from model query.
     * @param model: model with the table data
     * @param type: n = normal, t = temporary
     * @return the sql query.
     */
    public String createTableQuery(ModelMethods model, String type) {
        String typeColumn = "", modelInit = model.initModel();
        String[] columns = modelUtils.getModelColumns(
                modelInit,
                true
        ).split(",");
        String[] types = modelUtils.getModelTypes(
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
     * create index query
     * @param unique: if the index is unique
     * @param columns: columns to create the index
     * @return the create index query
     */
    public String createIndexQuery(boolean unique, String columns) {
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE ");
        if(unique == true) {
            sql.append("UNIQUE INDEX ");
        } else {
            sql.append("INDEX ");
        }
        if(!columns.isEmpty()) {
            sql.append("(");
            sql.append(columns);
            sql.append(")");
        }
        return sql.toString();
    }
    /**
     * creates the sql delete index query.
     * @param columns: index name
     * @return the sql query
     */
    public String createDropIndexQuery(String columns) {
        String sql = "";
        if(columns.contains(",")) {
            String[] spaces = columns.trim().split(",");
            String b = "";
            for(String s: spaces) {
                b += "DROP INDEX " + s + ", ";
            }
            sql = queryUtil.cleanValues(b, 2);
        } else {
            sql += "DROP INDEX " + columns;
        }
        return createAlterTableQuery(sql);
    }
    /**
     * creates the alter table query.
     * @param AlterOperation: sql alter operations
     * @return the sql query
     */
    protected String createAlterTableQuery(String alterOperations) {
        return "ALTER TABLE " + this.tableName + " " + alterOperations;
    }
    /**
     * created sql add column query.
     * @param primaryM: primary model data with the fk of foreignM pk
     * @param foreignM: foreign model data with the pk of primaryM fk
     * @param foreignT: foreign table name
     * @param rst: {@link ResultSet}
     * @throws SQLException: error while trying to execute the statement
     * @return the sql query
     */
    public String createAddColumnQuery(String primaryM, String foreignM, String foreignT,
            boolean includeKeys , ResultSet rst) throws SQLException {
        StringBuffer 
            addColumns = modelUtils.compareColumnName(primaryM, rst).get("add"),
            sql        = new StringBuffer();
        if(!addColumns.isEmpty()) {
            String columns = modelUtils.getModelColumns(addColumns.toString(), includeKeys);
            String[] 
                cleanColumns = queryUtil.cleanValues(columns, 1).split(","),
                modelTypes   = modelUtils.getModelTypes(primaryM, includeKeys).split(","),
                modelColumns = modelUtils.getModelColumns(primaryM, includeKeys).split(",");
            for(String k: cleanColumns) {
                int indexType              = modelUtils.getColumnType(primaryM, k);
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
                if(includeKeys == true) {
                    sql.append(
                            getPkFKConstraintQuery(
                                addColumns.toString(),
                                foreignM,
                                foreignT
                            )
                    );
                }
            }

        } 
        String 
            clearSql = "",
            res      = "";
        if(!sql.isEmpty()) {
            if(includeKeys == true) {
                clearSql = queryUtil.cleanValues(sql.toString(), 2);
            } else {
                clearSql = queryUtil.cleanValues(sql.toString(), 0);
            }
            res = createAlterTableQuery(clearSql);
        }
        return res;
    }
    /**
     * create sql rename column query.
     * @param modelData
     * @param rst: {@link ResultSet}
     * @throws SQLException: error while trying to execute the statement
     * @return the sql query
     */
    public String createRenameColumnQuery(String modelData, ResultSet rst) throws SQLException {
        StringBuffer 
            renameColumns = modelUtils.compareColumnName(modelData, rst).get("rename"),
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
     * create sql change type query.
     * @param modelData
     * @param include keys: true or false to include pk or fk
     * @param rst: {@link ResultSet}
     * @throws SQLException: error while trying to execute the statement
     * @return the sql query.
     */
    public String createChangeTypeQuery(String modelData, boolean includeKeys, ResultSet rst) 
            throws SQLException {
            StringBuffer 
                renameTypes = modelUtils.compareColumnType(modelData, rst).get("rename"),
                sql = new StringBuffer();
        if(!renameTypes.isEmpty()) {
            String[] 
                types        = renameTypes.toString().split(", "),
                modelColumns = modelUtils.getModelColumns(modelData, includeKeys).split(",");
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
     * create sql delete column query.
     * @param modelData
     * @param includeKeys: true or false to include pk && fk
     * @param rst: {@link ResultSet}
     * @throws SQLException: error while trying to execute the statement
     * @return the sql query
     */
    public String createDeleteColumnQuery(String modelData, boolean includeKeys, ResultSet rst)
            throws SQLException {
        StringBuffer 
            deleteColumns = modelUtils.compareColumnName(modelData, rst).get("delete"),
            sql = new StringBuffer();
        if(!deleteColumns.isEmpty()) {
            String[] columns = deleteColumns.toString().split(", ");
            for(String k: columns) {
                String[] data = k.split(":");
                if(data[0].contains("fk")) {
                    sql.append(
                            createDeleteConstraintQuery(
                            deleteColumns.toString(),
                            includeKeys) + "DROP COLUMN " + data[0] + ", "
                    );
                } else {
                    sql.append("DROP COLUMN "  + data[0] + ", ");
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
     * create sql add pk or fk constraint query.
     * @param addColumns: the column with pk or fk
     * @param foreignM: foreign model data
     * @param foreignT: foreign table name
     * @return the sql query
     */
    public String getPkFKConstraintQuery(String addColumns, String foreignM, String foreignT) {
        String fk = modelUtils.getPkFk(foreignM).get("pk");
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
                        foreignT + "(" + fk + ") ON DELETE CASCADE ON UPDATE CASCADE, "
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
     * create sql add default constraint query.
     * @param options: default constraint value
     * @return the sql query
     */
    public String getDefaultConstraintQuery(ParamValue options) throws Exception {
        StringBuffer sql = new StringBuffer();
        if(!options.getCombination().contains(":")) {
            throw new Exception(
                    "[ ERROR ]: default options must have !column: value¡ format"
            );
        }
        sql.append(" ALTER ");
        if(options.getCombination().contains(",")) {
            String[] others = options.getCombination().trim().split(",");
            String b = "";
            for(String o: others) {
                String[] spaces = o.trim().split(":");
                b += spaces[0].trim();
                b += " SET DEFAULT '";
                b += spaces[1].trim();
                b += "', ";
            }
            sql.append(queryUtil.cleanValues(b, 2));
        } else {
            String[] spaces = options.getCombination().trim().split(":");
            sql.append(spaces[0].trim());
            sql.append(" SET DEFAULT '");
            sql.append(spaces[1].trim());
            sql.append("'");
        }
        return createAlterTableQuery(sql.toString());
    }
    /**
     * create sql delete default constraint query.
     * @param columns: default column name
     * @return the sql query
     */
    public String getDropDefaultConstraintQuery(String columns) {
        StringBuffer sql = new StringBuffer();
        sql.append("ALTER ");
        if(columns.contains(",")) {
            String[] cols = columns.trim().split(",");
            String b = "";
            for(String c: cols) {
                b += c.trim();
                b += " DROP DEFAULT, ";
            }
            sql.append(queryUtil.cleanValues(b, 2));
        } else {
            sql.append(columns);
            sql.append(" DROP DEFAULT");
        }
        return createAlterTableQuery(sql.toString());
    }
    /**
     * create sql add check constraint query.
     * @param options: columns for constraint. ejm -> years: 18, city: pasto.
     * @param constraint: constraint operations -> <=, =, >=.
     * @param constraintName: name for the constraint
     * @param type: logic type for constraint operation.
     * @return the sql query or an empty value.
     */
    public String getCheckQuery(String[] options, String[] constraint, String checkName, String type) {
        String sql = "";
        // ALTER TABLE Persons
        // ADD CONSTRAINT CHK_PersonAge CHECK (Age>=18 AND City='Sadness');
        // ADD CONSTRAINT CHK_rol CHECK (rol IN ('', ''))
        if(options.length == constraint.length) {
            sql = "ADD CONSTRAINT " + checkName + " CHECK (";
            for(int i=0; i<options.length; ++i) {
                if(options[i].contains(":") && options[i].split(":").length > 0) {
                    String
                        column = options[i].split(":")[0].trim(),
                        value  = options[i].split(":")[1].trim();
                    if(constraint[i].contains(":") && constraint[i].split(":").length > 0) {
                        String cVal = constraint[i].split(":")[1];
                        sql += queryUtil.getInConditional(column, cVal, type) + " ";
                    } else {
                        sql += column + constraint[i] + "'" + value + "' " + type + " ";
                        sql = queryUtil.cleanByLogicType(type, sql);
                    }
                } else {
                    System.err.println("[ ERROR ]: while trying to create getCheckQuery");
                }
            }
        }
        return createAlterTableQuery(sql).trim() + ")";
    }
    /**
     * create sql delete check constraint query.
     * @param checkName: name of the check to drop
     * @return the sql query
     */
    public String getDeleteCheckQuery(String checkName) {
        String sql = "";
        if(checkName.contains(",")) {
            String[] names = checkName.split(",");
            for(String n: names) {
                sql += "DROP CHECK " + n + ", ";
            }
            sql = queryUtil.cleanValues(sql, 2);
        } else {
            sql += "DROP CHECK " + checkName;
        }
        return createAlterTableQuery(sql);
    }
    /**
     * create sql delete constraint query.
     * @param columns: column name to of constraint
     * @param includeKeys: true or false to include pk & fk
     * @return the sql query
     */
    public String createDeleteConstraintQuery(String columns, boolean includeKeys) throws SQLException {
        StringBuffer sql = new StringBuffer();
         if(columns != "" && columns != null) {
             String k1   = columns.split(", ")[0];
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
