package Conexion.Migration;

import java.sql.Connection;
import java.sql.ResultSet;
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
        cursor = miCursor;
        tableName = nTableName;
        migrationExecution = new MigrationExecution(tableName, cursor);
    }
    /**
     * crea la base de datos
     * @param DbName: nombre de la base de datos
     * @return true si la base de datos fue creada false de lo contrario
     */
    public boolean createDataBase(String DbName) {
        Statement stm = null;
        ResultSet rst = null;
        boolean resultado = false;
        try {
            stm = migrationExecution.executeCreateDatabase(DbName);
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                System.out.println("database created");
                resultado = true;
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
        return resultado;
    }
    /**
     * seleccionar una base de datos
     * @param DbName: nombre de la base de datos
     * @return true si se selecciona la base de datos, false de lo contrario
     */
    public boolean selecDatabase(String DbName) {
        Statement stm = null;
        ResultSet rst = null;
        boolean resultado = false;
        try {
            stm = migrationExecution.executeSelectDatabase(DbName, stm);
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                resultado = true;
                System.out.println("se selecciona la base de datos");
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
        return resultado;
    }
    /**
     * crea la tabla si esta no existe
     * @param model: modelo con los datos para crear la tabla
     * @return true si creo la tabla de lo contrario false
     */
    public boolean createTable(ModelMethods model) {
        Statement stm = null;
        ResultSet rst = null;
        boolean resultado = false;
        try {
            rst = migrationExecution.executeCreateTable(model, stm).getGeneratedKeys();
            if(showTableData() == true && rst.getMetaData().getColumnCount() > 0) {
                System.out.println("tabla creada");
                resultado = true;
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
        return resultado;
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
        boolean isCreated = false;
        Statement stm = null;
        ResultSet rst = null;
        try {
            rst = migrationExecution.executeCreateTemporaryTable(model, stm).getGeneratedKeys();
            if(showTableData() == true && rst.getMetaData().getColumnCount() > 0) {
                System.out.println(String.format("temporary table { %s } has been created", tableName));
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
     * muestra los datos de la tabla
     * @param model: modelo con los datos
     * @return resultado de la consulta
     */
    public boolean showTableData() {
        Statement stm = null;
        ResultSet rst = null;
        boolean comprobar = false;
        try {
            rst = migrationExecution.executeShowTableData(stm);
            if(rst.next() == true) {
                comprobar = true;
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
        return comprobar;
    }
    /**
     * agrega las columnas que no estan presentes en la tabla pero si en el modelo
     * @param model: modelo con las columnas a agregar
     * @return true si se agrega de lo contrario false
     */
    public boolean addColumn(ModelMethods localModel, ModelMethods refModel, String refTable, boolean includePKFK) {
        Statement stm = null;
        ResultSet rst = null;
        boolean resultado = false;
        try {
            stm = migrationExecution.executeAddColumn(localModel, refModel, refTable, includePKFK, stm);
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                resultado = true;
                System.out.println("se agregaron las columnas");
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
        return resultado;
    }
    /**
     * renombrar una columna de la tabla según el modelo
     * @param model: modelo con las columnas a renombrar
     * @return true si cambio el nombre de lo contrario false;
     */
    public boolean renameColumn(ModelMethods model) {
        Statement stm = null;
        ResultSet rst = null;
        boolean resultado = false;
        try {
            stm = migrationExecution.exceuteRenameColumn(model, stm);
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                resultado = true;
                System.out.println("se modifico el nombre de la columna");
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
        return resultado;
    }
    /**
     * modificar el tipo de dato de la columna según el modelo
     * @param model: modelo con el tipo de dato a modificar
     * @return true si cambio el nombre de lo contrario false
     */
    public boolean changeType(ModelMethods model, boolean includePKFK) {
        Statement stm = null;
        ResultSet rst = null;
        boolean resultado = false;
        try {
            stm = migrationExecution.executeChangeColumnType(model, includePKFK, stm);
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                resultado = true;
                System.out.println("se modifico el tipo de dato");
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
        return resultado;
    }
    /**
     * eliminar una columna de la tabla según el modelo
     * @param model: modelo al que le falta una columna que es la que se elimina
     * @return true si se elimina la columna false de lo contrario
     */
    public boolean deleteColumn(ModelMethods model, boolean includePKFK) {
        Statement stm = null;
        ResultSet rst = null;
        boolean resultado = false;
        try {
            stm = migrationExecution.executeDeleteColumn(model, includePKFK, stm);
            rst = stm.getGeneratedKeys();
            if(rst.getMetaData().getColumnCount() > 0) {
                resultado = true;
                System.out.println("se elimino la columna");
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
        return resultado;
    }
}
