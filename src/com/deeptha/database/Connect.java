package com.deeptha.database;

import java.sql.*;

public class Connect {
    private static Connect connection = null;
    private static final String dbUrl = "jdbc:mysql://localhost:3306/Cashiers";
    private static Connection conn = null;
    private static Statement stmt = null;

    private Connect(){
        getConnection();
    }

    public static Connect getInstance(){
        if(connection == null){
            connection = new Connect();
        }
        return connection;
    }
    //connection
    void getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop_management","root","1996");
            System.out.println("Connection Successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //returns some value
    public static ResultSet executeQuery(String query){
        ResultSet results;
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            results = stmt.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        return results;
    }

    //nothing get returned
    public static void executeAction(String query){
        ResultSet results;
        try {
            stmt = conn.createStatement();
            stmt.execute(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
