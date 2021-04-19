/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Utils.DBConnection;
import Utils.DBQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * UserDB handles all access to the user database
 * 
 * @author Ben
 */
public class UserDB {
    
    //Create the connection variable loaded from DBConnection
    private static final Connection CONN = DBConnection.getConnection();   
    
    //Validate the user based on the username and password
    public static User validLogin(String username, String password) throws SQLException{
        String insertStatement = "SELECT userID, userName, password From user WHERE userName=? AND password=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, username);
        ps.setString(2, password);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs.next()){
           User currentUser = new User(rs.getInt("userID"),rs.getString("userName"));
           return currentUser;
        }else{
            return null;
        }
    }
}
