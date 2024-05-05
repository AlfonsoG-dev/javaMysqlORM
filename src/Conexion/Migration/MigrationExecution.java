package Conexion.Migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.ModelMethods;
import Utils.Builder.MigrationBuilder;
import Utils.Formats.ParamValue;

/**
 * class for execution of sql query
 */
public class MigrationExecution {
    /**
     * table name
     */
    private String tableName;
    /**
     * {@link Connection} instance
     */
    private Connection cursor;
    /**
     * migration sql query builder
     */
    private MigrationBuilder migrationBuilder;
    /**
     * {@link java.lang.reflect.Constructor}
     * @param nTableName: table name
     * @param miCursor: {@link Connection} instance
     */
    public MigrationExecution(String nTableName, Connection miCursor) {
        tableName        = nTableName;
        migrationBuilder = new MigrationBuilder(nTableName);
        cursor           = miCursor;
    }
    /**
     * execute create database query
     * @param dbName: database schema name
     * @param stm: {@link Statement}
     * @throws SQLException: error while trying to execute the statement
     * @return {@link Statement}
     */
    public Statement executeCreateDatabase(String dbName) throws SQLException {
        String sql    = migrationBuilder.createDataBaseQuery(dbName);
        Statement stm = cursor.createStatement();
        stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        return stm;
    }
    /**
     * execute use or select database query.
     * @param dbName: database schema name
     * @param stm: {@link Statement}
     * @throws SQLException: error while trying to execute the statement
     * @return {@link Statement}
     */
    public Statement executeSelectDatabase(String dbName, Statement stm) throws SQLException {
        String sql = migrationBuilder.createSelectDatabase(dbName);
        stm        = cursor.createStatement();
        stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        return stm;
    }
    /**
     * execute create table query.
     * @param model: model of table to create
     * @param stm: {@link Statement}
     * @throws SQLException: error while trying to execute the statement
     * @return {@link Statement}
     */
    public Statement executeCreateTable(ModelMethods model, Statement stm) throws SQLException {
        String sql = migrationBuilder.createTableQuery(model, "n");
        stm        = cursor.createStatement();
        stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        return stm;
    }
    /**
     * execute create index query
     * @param unique: if the index is unique
     * @param columns: columns to create the index
     * @param stm: executor of statements
     * @return the executor object
     */
    public Statement executeCreateIndex(boolean unique, String columns, Statement stm) throws SQLException {
        String sql = migrationBuilder.createIndexQuery(unique, columns);
        if(!sql.isEmpty()) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * execute add default constraint query
     * @param options: default constraint values
     * @param stm: {@link Statement}
     * @throws SQLException: error while trying to execute the statement
     * @return {@link Statement}
     */
    public Statement executeAddDefaultConstraint(ParamValue options, Statement stm) throws Exception {
        String sql = migrationBuilder.getDefaultConstraintQuery(options);
        if(!sql.isEmpty()) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * execute delete default constraint query
     * @param columns: default constraint column name
     */
    public Statement executeDropDefaultConstraint(String columns, Statement stm) throws SQLException {
        String sql = migrationBuilder.getDropDefaultConstraintQuery(columns);
        if(!sql.isEmpty()) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * execute drop index query
     * @param columns: index name
     */
    public Statement executeDropIndex(String columns, Statement stm) throws SQLException {
        String sql = migrationBuilder.createDropIndexQuery(columns);
        if(!sql.isEmpty()) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * create a temporary table from model
     * @param model: model to use for table creation
     * @param stm: query execution object
     * @throws SQLException: execution exception
     * @return executor of this query
     */
    public Statement executeCreateTemporaryTable(ModelMethods model, Statement stm) throws SQLException {
        String sql = migrationBuilder.createTableQuery(model, "t");
        stm        = cursor.createStatement();
        stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        return stm;
    }
    /**
     * execute show table query.
     */
    public ResultSet executeShowTableData(Statement stm) throws SQLException {
        String sql    = "show columns from " + this.tableName;
        stm           = cursor.createStatement();
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * execute add table column from model
     */
    public Statement executeAddColumn(ModelMethods model, ModelMethods refModel, String refTable,
            boolean includePKFK, Statement stm) throws SQLException {
        ResultSet rst = executeShowTableData(stm);
        String sql    = migrationBuilder.createAddColumnQuery(
                model.initModel(),
                refModel.initModel(),
                refTable,
                includePKFK,
                rst
        );
        if(sql != "" || sql != null) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * query execution for add constraint mainly for CHECK validation
     * @param options: columns for constraint. ejm -> edad: 18, ciudad: pasto.
     * @param constraint: constraint operations -> <=, =, >=.
     * @param constraintName: name for the constraint
     * @param type: logic type for constraint operation.
     * @return execution object
     */
    public Statement executeAddCheckConstraint(String[] options, String[] constraint, String checkName,
            String type, Statement stm) throws SQLException {
        String sql = migrationBuilder.getCheckQuery(
                options,
                constraint,
                checkName, 
                type
        );
        if(!sql.isEmpty()) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * query execution for drop check constraint
     * @param checkName: name of the constraint to drop
     * @return the execution object
     */
    public Statement executeDropCheckConstraint(String checkName, Statement stm) throws SQLException {
        String sql = migrationBuilder.getDeleteCheckQuery(checkName);
        if(!sql.isEmpty()) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * execute rename column from model
     */
    public Statement exceuteRenameColumn(ModelMethods model, Statement stm) throws SQLException {
        ResultSet rst = executeShowTableData(stm);
        String sql    = migrationBuilder.createRenameColumnQuery(model.initModel(), rst);
        if(sql != "" || sql != null) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * execute change table column type from model
     */
    public Statement executeChangeColumnType(ModelMethods model, boolean includePKFK, Statement stm)
            throws SQLException {
        ResultSet rst = executeShowTableData(stm);
        String sql    = migrationBuilder.createChangeTypeQuery(model.initModel(), includePKFK, rst);
        if(sql != "" || sql != null) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
    /**
     * delete table column from model.
     */
    public Statement executeDeleteColumn(ModelMethods model, boolean includePKFK, Statement stm) 
            throws SQLException {
        ResultSet rst = executeShowTableData(stm);
        String sql    = migrationBuilder.createDeleteColumnQuery(
                model.initModel(),
                includePKFK,
                rst
        );
        if(sql != "" || sql != null) {
            stm = cursor.createStatement();
            stm.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        }
        return stm;
    }
}
