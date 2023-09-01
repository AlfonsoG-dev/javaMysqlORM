package Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.ModelMethods;
import Utils.QueryBuilder;

public class QueryExecution {

    private final static Conector con = new Conector();
    private final static Connection connector = con.conectarMySQL();

    private String table;
    private QueryBuilder query_util;

    public QueryExecution(String tb_name) {
        table = tb_name;
        query_util = new QueryBuilder(tb_name);
    }
    public ResultSet CountData(PreparedStatement pstm) throws SQLException {
        String sql = "select count(id) from " + table;
        pstm = connector.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }
    public ResultSet ReadAll(PreparedStatement pstm) throws SQLException {
        String sql = "select * from " + table;
        pstm = connector.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }

    public ResultSet FindOne(String options, PreparedStatement pstm) throws SQLException {
        String sql = query_util.FindQuery(options);
        String val = query_util.GetOptionValue(options);
        pstm = connector.prepareStatement(sql);
        pstm.setString(1, val);
        ResultSet rst = pstm.executeQuery();
        return rst;
    }

    public ResultSet FindByColumnName(String options, Statement stm) throws SQLException {
        stm = connector.createStatement();
        String sql = query_util.FindColumnQuery(options);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }

    public ResultSet GetValueOfColumnName (String options, String column, Statement stm) throws SQLException {
        stm = connector.createStatement();
        String sql = query_util.FindColumnValueQuery(options, column);
        ResultSet rst = stm.executeQuery(sql);
        return rst;
    }

    public ResultSet InsertNewRegister(Statement stm, ModelMethods nObject) throws SQLException {
        String sql = query_util.InsertRegisterQuery(nObject);
        String[] columns = {"id"};
        stm = connector.createStatement();
        stm.executeUpdate(sql, columns);
        ResultSet rst = stm.getGeneratedKeys();
        return rst;
    }
    public ResultSet UpdateRegister(Statement stm, ModelMethods nObject, String conditions) throws SQLException {
        stm = connector.createStatement();
        String sql = query_util.ModificarRegisterQuery(nObject, conditions);
        String[] columns = {"id"};
        stm.executeUpdate(sql, columns);
        ResultSet rst = stm.getGeneratedKeys();
        return rst;
    }

    public ResultSet EliminarRegistro(Statement stm ,String options) throws SQLException {
        stm = connector.createStatement();
        String sql = query_util.EliminarRegistroQuery(options);
        String[] columns = {"id"};
        stm.executeUpdate(sql, columns);
        ResultSet rst = stm.getGeneratedKeys();
        return rst;
    }
}
