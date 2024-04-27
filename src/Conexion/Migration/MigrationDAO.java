package Conexion.Migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.ModelMethods;

/**
 * clase para la migración del modelo 
 */
public class MigrationDAO {

    /**
     * ejecutor de la sentencia sql
     */
    private MigrationExecution migrationExecution;
    /**
     * database Connection cursor
     */
    private Connection cursor;
    /**
     * table name
     */
    private String tableName;
    /**
     * constructor
     */
    public MigrationDAO(String nTableName, Connection miCursor) {
        cursor             = miCursor;
        tableName          = nTableName;
        migrationExecution = new MigrationExecution(tableName, cursor);
    }
    /**
     * crea la base de datos
     * @param DbName: nombre de la base de datos
     * @return true si la base de datos fue creada false de lo contrario
     */
    public boolean createDataBase(String DbName) {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isCreated = false;
        try {
            stm = migrationExecution.executeCreateDatabase(DbName);
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
     * seleccionar una base de datos
     * @param DbName: nombre de la base de datos
     * @return true si se selecciona la base de datos, false de lo contrario
     */
    public boolean selecDatabase(String DbName) {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isSelected = false;
        try {
            stm = migrationExecution.executeSelectDatabase(DbName, stm);
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
     * crea la tabla si esta no existe
     * @param model: modelo con los datos para crear la tabla
     * @return true si creo la tabla de lo contrario false
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
    protected void printTableData(ResultSet rst) throws SQLException {
        int cols = rst.getMetaData().getColumnCount();
        while(rst.next()) {
            for(int i=1; i<cols; ++i) {
                System.out.println(rst.getString(i));
            }
        }
    }
    /**
     * muestra los datos de la tabla
     * @param model: modelo con los datos
     * @return resultado de la consulta
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
     * agrega las columnas que no estan presentes en la tabla pero si en el modelo
     * @param model: modelo con las columnas a agregar
     * @return true si se agrega de lo contrario false
     */
    public boolean addColumn(ModelMethods localModel, ModelMethods refModel, String refTable,
            boolean includePKFK) {
        Statement stm     = null;
        ResultSet rst     = null;
        boolean isAdded = false;
        try {
            stm = migrationExecution.executeAddColumn(
                    localModel,
                    refModel,
                    refTable,
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
     * renombrar una columna de la tabla según el modelo
     * @param model: modelo con las columnas a renombrar
     * @return true si cambio el nombre de lo contrario false;
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
     * modificar el tipo de dato de la columna según el modelo
     * @param model: modelo con el tipo de dato a modificar
     * @return true si cambio el nombre de lo contrario false
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
     * eliminar una columna de la tabla según el modelo
     * @param model: modelo al que le falta una columna que es la que se elimina
     * @return true si se elimina la columna false de lo contrario
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
