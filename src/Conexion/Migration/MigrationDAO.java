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
    private MigrationExecution migration_execution;
    /**
     * database Connection cursor
     */
    private Connection cursor;
    /**
     * constructor
     */
    public MigrationDAO(String nTableName, Connection miCursor) {
        cursor = miCursor;
        migration_execution = new MigrationExecution(nTableName, cursor);
    }
    /**
     * crea la base de datos
     * @param DbName: nombre de la base de datos
     * @return true si la base de datos fue creada false de lo contrario
     */
    public boolean CreateDataBase(String DbName) {
        Statement stm = null;
        boolean resultado = false;
        try {
            stm = migration_execution.ExecuteCreateDatabase(DbName);
            if(stm.getUpdateCount() > 0) {
                System.out.println("database created");
                resultado = true;
            }
        } catch(Exception e) {
            System.err.println(e);
        } finally {
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
    public boolean SelecDatabase(String DbName) {
        Statement stm = null;
        boolean resultado = false;
        try {
            stm = migration_execution.ExecuteSelectDatabase(DbName, stm);
            if(stm.getUpdateCount() == 0) {
                resultado = true;
                System.out.println("se selecciona la base de datos");
            }
        } catch(Exception e) {
            System.err.println(e);
        } finally {
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
    public boolean CreateTable(ModelMethods model) {
        Statement stm = null;
        ResultSet rst = null;
        boolean resultado = false;
        try {
            rst = migration_execution.ExecuteCreateTable(model, stm);
            if(this.ShowTableData() == true) {
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
     * muestra los datos de la tabla
     * @param model: modelo con los datos
     * @return resultado de la consulta
     */
    public boolean ShowTableData() {
        Statement stm = null;
        ResultSet rst = null;
        boolean comprobar = false;
        try {
            rst = migration_execution.ExecuteShowTableData(stm);
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
    public boolean AddColumn(ModelMethods local_model, ModelMethods ref_model, String ref_table) {
        Statement stm = null;
        boolean resultado = false;
        try {
            stm = migration_execution.ExecuteAddColumn(local_model, ref_model, ref_table, stm);
            if(stm.getUpdateCount() == 0) {
                resultado = true;
                System.out.println("se agregaron las columnas");
            }
        } catch(Exception e) {
            System.err.println(e);
        } finally {
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
    public boolean RenameColumn(ModelMethods model) {
        Statement stm = null;
        boolean resultado = false;
        try {
            stm = migration_execution.ExceuteRenameColumn(model, stm);
            if(stm.getUpdateCount() == 0) {
                resultado = true;
                System.out.println("se modifico el nombre de la columna");
            }
        } catch(Exception e) {
            System.err.println(e);
        } finally {
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
    public boolean ChangeType(ModelMethods model) {
        Statement stm = null;
        boolean resultado = false;
        try {
            stm = migration_execution.ExecuteChangeColumnType(model, stm);
            if(stm.getUpdateCount() == 0) {
                resultado = true;
                System.out.println("se modifico el tipo de dato");
            }
        } catch(Exception e) {
            System.err.println(e);
        } finally {
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
    public boolean DeleteColumn(ModelMethods model) {
        Statement stm = null;
        boolean resultado = false;
        try {
            stm = migration_execution.ExecuteDeleteColumn(model, stm);
            if(stm.getUpdateCount() == 0) {
                resultado = true;
                System.out.println("se elimino la columna");
            }
        } catch(Exception e) {
            System.err.println(e);
        } finally {
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
