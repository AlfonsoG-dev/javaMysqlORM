package Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.ModelMethods;
import Utils.QueryBuilder;
import Utils.QueryUtils;

/**
 * clase para crear la ejecución de las sentencias sql
 * */
public class QueryExecution {

    //atributos

    /**
     * conector de mysql
     * es único constante e inmutable
     * */
    private Conector miConector;
    /**
     * instancia del this.cursor de mysql
     * es único, constante e inmutable
     * */
    private Connection cursor;
    /**
     * nombre de la tabla en donde se ejecutan las sentencias sql
     * no puede ser null ni Empty
     * */
    private String table;
    /**
     * record que crea las sentencias sql
     * */
    private QueryBuilder query_builder;
    /**
     * clase con las herramientas para crear las querys
     */
    private QueryUtils query_util;

    //constructor

    /**
     * constructor de la clase
     * @param tb_name: nombre de la tabla; tb_name != null && tb_name != ""
     */
    public QueryExecution(String tb_name, String database, String hostname, String port, String username, String password) {
        table = tb_name;
        query_builder = new QueryBuilder(tb_name);
        query_util = new QueryUtils();
        miConector = new Conector(database, hostname, port, username, password);
        cursor = miConector.conectarMySQL();
    }

    //métodos

    /**
     * ejecuta el contador de resultados
     * @param pstm: ejecutor de sentencias sql
     * @return Resultado de la ejecución
     * @throws SQLException error al ejecutar
    */
    public ResultSet ExecuteCountData(PreparedStatement pstm) throws SQLException {
        String sql = "select id_pk from " + table;
        pstm = this.cursor.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    /**
     * show table data
     * @param stm: ejecutor de la sentencias sql
     * @return resultado de la ejecución
     * @throws SQLException error al ejecutar
     */
    public ResultSet ExecuteShowTableData(Statement stm) throws SQLException {
        String sql = "show columns from " + this.table;
        stm = this.cursor.createStatement();
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la lectura de datos
     * @param pstm: ejecutor de sentencias sql
     * @return resultado de la ejecución
     * @throws SQLException error al ejecutar
    */
    public ResultSet ExecuteReadAll(PreparedStatement pstm) throws SQLException {
        String sql = "select * from " + table;
        pstm = this.cursor.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    /**
     * ejecuta la busqueda de 1 resultado por id
     * @param options: id del registro buscado
     * @param pstm: ejecutor de sentencia sql
     * @return resultado de la ejecución
     * @throws SQLException error de la ejecución
    */
    public ResultSet ExecuteFindOne(String options, PreparedStatement pstm) throws SQLException {
        String sql = this.query_builder.CreateFindQuery(options);
        String val = this.query_util.GetOptionValue(options);
        pstm = this.cursor.prepareStatement(sql);
        pstm.setString(1, val);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    /**
     * ejecuta la busqueda por nombre de columna
     * puede recibir 1 o varias columnas con el valor
     * @param options: nombre y valor de columna: "nombre: test, email: test@test"
     * @param stm: ejecutor de sentencia sql
     * @return resultado de la ejecución
     * @throws SQLException error de la ejecución
    */
    public ResultSet ExecuteFindByColumnName(String options, Statement stm) throws SQLException {
        stm = this.cursor.createStatement();
        String sql = this.query_builder.CreateFindByColumnQuery(options);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta la busqueda por columna y valor y retorna el valor de las columnas seleccionadas
     * puede recibir 1 o varias columnas con el valor
     * puede retornar 1 o varios valores de la columna seleccionada
     * @param options: nombre y valor de la columna: "nombre: test, email: test@test"
     * @param column: columna a conocer el valor: "id, rol"
     * @param stm: ejecutor de sentencia sql
     * @return resultado de la ejecución
     * @throws SQLException error de la ejecución
    */
    public ResultSet ExecuteGetValueOfColumnName (String options, String column, Statement stm) throws SQLException {
        stm = this.cursor.createStatement();
        String sql = this.query_builder.CreateFindColumnValueQuery(options, column);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }
    /**
     * ejecuta el registro de un nuevo elemento 
     * solo puede registrar 1 elemento a la vez
     * @param stm: ejecutor de sentencia sql
     * @param nObject: elemento a registrar
     * @return resultado de la ejecución
     * @throws SQLException error de la ejecución
    */
    public ResultSet ExecuteInsertNewRegister(Statement stm, ModelMethods nObject) throws SQLException {
        String sql = this.query_builder.CreateInsertRegisterQuery(nObject);
        String[] columns = {"id_pk"};
        stm = this.cursor.createStatement();
        stm.executeUpdate(sql, columns);
        ResultSet rst = stm.getGeneratedKeys();
        return rst;
    }
    /**
     * ejecuta la consulta tipo inner join
     * @param tb_name: nombre de la tabla local
     * @param stm: ejecutor de sentencias sql
     * @param refObject: modelo de referencia
     * @param localObject: modelo local
     * @throws SQLException error al ejecutar la sentencia sql
     * @return el resultado de la ejecución
     */
    public ResultSet ExecuteInnerJoin(String tb_name, Statement stm, ModelMethods refObject, ModelMethods localObject) throws SQLException {
        String sql = this.query_builder.CreateInnerJoinQuery(tb_name, refObject, localObject);
        String[] columns = {"id_pk"};
        stm = this.cursor.createStatement();
        stm.executeUpdate(sql, columns);
        ResultSet rst = stm.getGeneratedKeys();
        return rst;
    }
    /**
     * ejecuta la modificación de 1 registro
     * puede modificar cualquier valor valido del registro
     * @param stm: ejecutor de la sentencia sql
     * @param nObject: elemento a modificar
     * @param conditions: condiciones de query; "nombre: test, email: test"
     * @return resultado de la ejecución
     * @throws SQLException error de la ejecución
    */
    public ResultSet ExecuteUpdateRegister(Statement stm, ModelMethods nObject, String conditions) throws SQLException {
        stm = this.cursor.createStatement();
        String sql = this.query_builder.CreateModifyRegisterQuery(nObject, conditions);
        String[] columns = {"id_pk"};
        stm.executeUpdate(sql, columns);
        ResultSet rst = stm.getGeneratedKeys();
        return rst;
    }
    /**
     * ejecuta la eliminación de un registro
     * puede eliminar varios registros según las options
     * @param stm: ejecutor de la sentencia sql
     * @param options: opciones de condición: "nombre: test, email: test@test"
     * @return resultado de la ejecución & !por el momento no funciona ya que la ejecución no retorna un resultado
     * @throws SQLException error al ejecutar
    */
    public ResultSet ExecuteEliminarRegistro(Statement stm ,String options) throws SQLException {
        stm = this.cursor.createStatement();
        String sql = this.query_builder.CreateDeleteRegisterQuery(options);
        String[] columns = {"id_pk"};
        stm.executeUpdate(sql, columns);
        ResultSet rst = stm.getGeneratedKeys();
        return rst;
    }
}
