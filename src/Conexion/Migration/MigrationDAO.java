package Conexion.Migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.ModelMethods;
import Utils.Formats.ParamValue;

/**
 * class for migration operations
 */
public class MigrationDAO {

    /**
     * sql query execution for migration queries
     */
    private MigrationExecution migrationExecution;
    /**
     * {@link Connection} instance
     */
    private Connection cursor;
    /**
     * table name
     */
    private String tableName;
    /**
     * {@link java.lang.reflect.Constructor}
     */
    public MigrationDAO(String nTableName, Connection miCursor) {
        cursor             = miCursor;
        tableName          = nTableName;
        migrationExecution = new MigrationExecution(tableName, cursor);
    }
    /**
     * create a database schema
     * @param DbName
     * @return true if its created, false otherwise
     */
    public boolean createDataBase(String dbName) {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isCreated = false;
        try {
            stm = migrationExecution.executeCreateDatabase(dbName);
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                System.out.println("[ INFO ]:  database has been created");
                isCreated = true;
            }
        } catch(Exception e) {
            System.err.println(e);
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                }catch(Exception e) {
                    System.err.println(e);
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                }catch(Exception e) {
                    System.err.println(e);
                }
                stm = null;
            }
        }
        return isCreated;
    }
    /**
     * use or select a database
     * @param dbName: database name
     * @return true if its selected, false otherwise
     */
    public boolean selecDatabase(String dbName) {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isSelected = false;
        try {
            stm = migrationExecution.executeSelectDatabase(dbName, stm);
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                isSelected = true;
                System.out.println("[ INFO ]: se selecciona la base de datos");
            }
        } catch(Exception e) {
            System.err.println(e);
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                }catch(Exception e) {
                    System.err.println(e);
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                }catch(Exception e) {
                    System.err.println(e);
                }
                stm = null;
            }
        }
        return isSelected;
    }
    /**
     * create table operation
     * @param model: modelo to create
     * @return true if its created, false otherwise
     */
    public boolean createTable(ModelMethods model) {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isCreated = false;
        try {
            rst = migrationExecution.executeCreateTable(model, stm).getGeneratedKeys();
            if(showTableData() == true && rst.getMetaData().getColumnCount() > 0) {
                System.out.println("[ INFO ]: table has been created");
                isCreated = true;
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    System.err.println(e);
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    System.err.println(e);
                }
                stm = null;
            }
        }
        return isCreated;
    }
    /**
     * create index query
     * @param unique: if the index is unique
     * @param columns: columns to create the index
     * @param stm: executor of statements
     * @return true if its created, false otherwise
     */
    public boolean createIndex(boolean isUnique, String columns) {
        boolean isCreated = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = migrationExecution.executeCreateIndex(isUnique, columns, stm).getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                System.out.println("[ INFO ]: index has been created");
                isCreated = true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isCreated;
    }
    /**
     * delete index operation
     * @param columns: index column name
     * @return true if its deleted, false otherwise
     */
    public boolean dropIndex(String columns) {
        boolean isDeleted = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = migrationExecution.executeDropIndex(columns, stm).getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                System.out.println("[ INFO ]: index has been deleted");
                isDeleted = true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isDeleted;
    }
    /**
     * create a temporary table in this session.
     * <br> pre: </br> the temporary table name is the same as the instance creation,
     * new MigrationBuilder("user", cursor): user is the table name,
     * temporary table name: t_user.
     * @param temporaryName: temporary table name
     * @return true if its created, false otherwise
     */
    public boolean createTemporaryTable(ModelMethods model) {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isCreated = false;
        try {
            rst = migrationExecution.executeCreateTemporaryTable(model, stm).getGeneratedKeys();
            if(showTableData() == true && rst.getMetaData().getColumnCount() > 0) {
                System.out.println(
                        String.format(
                            "[ INFO ]: temporary table { %s } has been created",
                            tableName
                        )
                );
                isCreated = true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isCreated;
    }
    /**
     * helper function to print some data
     */
    protected void printTableData(ResultSet rst) throws SQLException {
        int cols = rst.getMetaData().getColumnCount();
        while(rst.next()) {
            for(int i=1; i<cols; ++i) {
                System.out.println(rst.getString(i));
            }
        }
    }
    /**
     * show table data.
     * @return true if the table can show data, false otherwise
     */
    public boolean showTableData() {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isShowing = false;
        try {
            rst = migrationExecution.executeShowTableData(stm);
            if(rst.next() == true) {
                printTableData(rst);
                isShowing = true;
            }
        } catch (Exception e) {
            System.err.println(e);
        
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                stm = null;
            }
        }
        return isShowing;
    }
    /**
     * add columns using model as reference
     * @param primaryM: model with the fk declaration of foreignM pk
     * @param foreignM: model with the pk reference in primaryM fk
     * @param foreignT: foreign table name
     * @param includePKFK: true or false to include pk or fk
     * @return true if the columns are added, false otherwise
     */
    public boolean addColumn(ModelMethods primaryM, ModelMethods foreignM, String foreignT,
            boolean includePKFK) {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isAdded = false;
        try {
            stm = migrationExecution.executeAddColumn(
                    primaryM,
                    foreignM,
                    foreignT,
                    includePKFK,
                    stm
            );
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                isAdded = true;
                System.out.println("[ INFO ]: new columns has been added");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    System.err.println(e);
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    System.err.println(e);
                }
                stm = null;
            }
        }
        return isAdded;
    }
    /**
     * add a default constraint.
     * @param options: default constraint value
     * @return true if its added, false otherwise
     */
    public boolean addDefaultConstraint(ParamValue options) {
        boolean isAdded = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = migrationExecution.executeAddDefaultConstraint(options, stm).getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                System.out.println("[ INFO ]: default constraint has been added");
                isAdded = true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isAdded;
    }
    /**
     * delete a default constraint
     * @param columns: default constraint column name
     * @return true if its deleted, false otherwise
     */
    public boolean dropDefaultConstraint(String columns) {
        boolean isDeleted = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = migrationExecution.executeDropDefaultConstraint(columns, stm).getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                System.out.println("[ INFO ]: default constriant has been deleted");
                isDeleted = true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isDeleted;
    }
    /**
     * add constraint mainly for CHECK validation
     * @param options: columns for constraint. ejm -> edad: 18, ciudad: pasto.
     * @param constraint: constraint operations -> <=, =, >=.
     * @param constraintName: name for the constraint
     * @param type: logic type for constraint operation.
     * @return true if its added, false otherwise
     */
    public boolean addCheckContraint(String[] options, String[] constraint, String constraintName,
            String type) {
        boolean isAdded = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = migrationExecution.executeAddCheckConstraint(
                    options,
                    constraint,
                    constraintName,
                    type,
                    stm
            ).getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                isAdded = true;
                System.out.println("[ INFO ]: constraint has been added");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isAdded;
    }
    /**
     * updates a check constraint
     * @param options: columns for constraint. ejm -> edad: 18, ciudad: pasto.
     * @param constraint: constraint operations -> <=, =, >=.
     * @param checkName: name for the constraint
     * @param type: logic type for constraint operation.
     * @return true if its updated, false otherwise
     */
    public boolean updateCheckConstraint(String[] options, String[] constraint, String checkName,
            String type) {
        boolean isUpdated = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            boolean isDeleted = dropCheckConstraint(checkName);
            if(isDeleted == true) {
                isUpdated = addCheckContraint(options, constraint, checkName, type);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        if(isUpdated == true) {
            System.out.println("[ INFO ]: the check constraint has been updated");
        } else {
            System.out.println("[ ERROR ]: while trying to update a check constraint");
        }
        return isUpdated;
    }
    /**
     * delete a check constraint given its name.
     * @param checkName: check contraint name
     * @return true if its deleted, false otherwise
     */
    public boolean dropCheckConstraint(String checkName) {
        boolean isDeleted = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = migrationExecution.executeDropCheckConstraint(
                    checkName,
                    stm
            ).getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                isDeleted = true;
                System.out.println("[ INFO ]: check constraint has been deleted");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                stm = null;
            }
        }
        return isDeleted;
    }
    /**
     * rename column operation.
     * @param model: model to delete
     * @return true if its deleted, false otherwise
     */
    public boolean renameColumn(ModelMethods model) {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isRenamed = false;
        try {
            stm = migrationExecution.exceuteRenameColumn(model, stm);
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                isRenamed = true;
                System.out.println("[ INFO ]: name of the column has been modified");
            }
        } catch(Exception e) {
            System.err.println(e);
        } finally {
            if(rst != null) { 
                try {
                    rst.close();
                } catch(Exception e) {
                    System.err.println(e);
                }
                rst = null;
            }
            if(stm != null) { 
                try {
                    stm.close();
                } catch(Exception e) {
                    System.err.println(e);
                }
                stm = null;
            }
        }
        return isRenamed;
    }
    /**
     * change the column type operation.
     * @param model: model with the new type
     * @param includePKFK: true or false to include pk & fk
     * @return true if its changed
     */
    public boolean changeType(ModelMethods model, boolean includePKFK) {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isChanged = false;
        try {
            stm = migrationExecution.executeChangeColumnType(model, includePKFK, stm);
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                isChanged = true;
                System.out.println("[ INFO ]: column data type has been modified");
            }
        } catch(Exception e) {
            System.err.println(e);
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    System.err.println(e);
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    System.err.println(e);
                }
                stm = null;
            }
        }
        return isChanged;
    }
    /**
     * delete a column operation.
     * @param model: model to delete
     * @param includePKFK: true or false to include pk & fk
     * @return true if its deleted, false otherwise
     */
    public boolean deleteColumn(ModelMethods model, boolean includePKFK) {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isDeleted = false;
        try {
            stm = migrationExecution.executeDeleteColumn(model, includePKFK, stm);
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                isDeleted = true;
                System.out.println("[ INFO ]: columns has been deleted");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(rst != null) {
                try {
                    rst.close();
                } catch(Exception e) {
                    System.err.println(e);
                }
                rst = null;
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch(Exception e) {
                    System.err.println(e);
                }
                stm = null;
            }
        }
        return isDeleted;
    }
}
