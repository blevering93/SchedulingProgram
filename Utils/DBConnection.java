/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * Create a connection to the database
 * 
 * @author Ben
 */
public class DBConnection {
    
    private static final String PROTOCOL = "jdbc";
    private static final String RDBMS = ":mysql:";
    private static final String IPADDRESS = "//3.227.166.251/U05m2E";
       
    private static final String DB_URL = PROTOCOL + RDBMS + IPADDRESS;  
    
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static Connection conn = null;
    
    private static final String USERNAME = "U05m2E";
    private static final String PASSWORD = "53688545426";
    
    public static Connection startConnection() {
        
        try{
            Class.forName(DB_DRIVER); 
            conn =  DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("Connected to database");
        }catch(ClassNotFoundException | SQLException e){
            
            System.out.println(e.getMessage());
            
        }
        return conn;
                
    }
    
    /**
     * 
     * Close the database connection
     * 
     */
    public static void closeConnection() {
        
        try{
            conn.close();
            System.out.println("Connection Closed");
        }catch(SQLException e){
            
            System.out.println("Error: "+ e.getMessage());
        }
    }
    
    /**
     * 
     * Return the connection
     * 
     * @return 
     */
    public static Connection getConnection() {
        return conn;
    }
}
