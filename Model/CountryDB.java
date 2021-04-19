/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Utils.ConvertTimeZone;
import Utils.DBConnection;
import Utils.DBQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *
 * CountryDB handles all access to the address database
 * 
 * @author Ben
 */
public class CountryDB {
    
    //Create the connection variable loaded fomr DBConnection
    private static final Connection CONN = DBConnection.getConnection();
    
    /**
     * 
     * Retrieves the largest ID from the country database and return the ID + 1.
     * If the country database is empty then return the ID 1.
     * 
     * @return
     * @throws SQLException 
     */
    private static int getNextId() throws SQLException{
        int id;
        String insertStatement = "SELECT MAX(countryId) AS maxId FROM country";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs.next()){
            id = rs.getInt("maxId") + 1;
            return id;
        }else{
            return 1;
        }
    }
    
    /**
     * 
     * Get the country from the country database based on the country ID
     * 
     * @param countryId
     * @return
     * @throws SQLException 
     */
    public static Country getCountry(int countryId) throws SQLException{
        String insertStatement = "SELECT countryId, country FROM country WHERE countryId=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, countryId);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs.next()){
            Country country = new Country(rs.getInt("countryId"), rs.getString("country"));
            
            return country;
        }else{
            return null;
        }
    }
    
    /**
     * 
     * Check if a country in the country database exists based on the country details
     * 
     * @param countryName
     * @return
     * @throws SQLException 
     */
    public static int checkExists(String countryName) throws SQLException{
        String insertStatement = "SELECT countryID, country FROM country WHERE country=?"; 
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, countryName);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs.next()){
            int countryId = rs.getInt("countryID");
            return countryId;
        }else{
            return 0;
        }
    }
    
    /**
     * 
     * Add a country to the country database
     * 
     * @param countryName
     * @throws SQLException 
     */
    public static void addCountry(String countryName) throws SQLException{
        int nextId = getNextId();
        String insertStatement = "INSERT INTO country (countryId, country, createDate, createdBy, lastUpdate, lastUpdateBy) "
                               + "VALUES (?, ?, ?, ?, ?, ?)";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, nextId);
        ps.setString(2, countryName);
        ps.setString(3, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(4, User.getCurrentUser().getName());
        ps.setString(5, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(6,User.getCurrentUser().getName());
        
        int result = ps.executeUpdate();
        if(result == 1){
            System.out.println("Country Added");
        }else{
            System.out.println("ERROR, Country not added");
        }   
    }
}
