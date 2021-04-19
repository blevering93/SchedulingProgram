/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Ben
 */
public class DBQuery {
    
    private static PreparedStatement statement;
    
    public static void setPreparedStatement(Connection conn, String sqlStmt) throws SQLException {
        
        statement = conn.prepareStatement(sqlStmt);
    }
    
    public static PreparedStatement getPreparedStatement() {
        
        return statement;
    }
}

    