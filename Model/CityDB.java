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
 * CityDB handles all access to the city database
 * 
 * @author Ben
 */
public class CityDB {
    
    //Create the connection vairable loaded from DBConnection
    private static final Connection CONN = DBConnection.getConnection();
    
    /**
     * 
     * Retrieves the largest ID from the city database and return the ID +.
     * If the city database is empty then return the ID 1.
     * 
     * @return
     * @throws SQLException 
     */
    private static int getNextId() throws SQLException{
        int id;
        String insertStatement = "SELECT MAX(cityId) AS maxId FROM city";
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
     * Get the city details based on passed city ID and create and return the city object
     * 
     * @param cityId
     * @return
     * @throws SQLException 
     */
    public static City getCity(int cityId) throws SQLException{
        
        String insertStatement = "SELECT cityId, city, countryId FROM city WHERE cityId=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, cityId);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs.next()){
            City city = new City(rs.getInt("cityId"), rs.getString("city"), rs.getInt("countryId"));
            
            return city;
        }else{
            return null;
        }
    }
    
    /**
     * 
     * Check if the city exists in the city database based on the passed city details
     * 
     * @param cityName
     * @param countryId
     * @return
     * @throws SQLException 
     */
    public static int checkExists(String cityName, int countryId) throws SQLException{
        
        String insertStatement = "SELECT cityId, city, CountryId FROM city WHERE city=? AND countryId=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, cityName);
        ps.setInt(2, countryId);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs.next()){
            int cityId = rs.getInt("cityId");
            
            return cityId;
        }else{
            return 0;
        } 
    }   
    
    /**
     * 
     * Add a city to the city database
     * 
     * @param city
     * @param countryId
     * @throws SQLException 
     */
    public static void addCity(String city, int countryId) throws SQLException{
        int nextId = getNextId();
        String insertStatement = "INSERT INTO city (cityId, city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) "
                               + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, nextId);
        ps.setString(2, city);
        ps.setInt(3, countryId);
        ps.setString(4, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(5, User.getCurrentUser().getName());
        ps.setString(6, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(7,User.getCurrentUser().getName());
        
        int result = ps.executeUpdate();
        if(result == 1){
            System.out.println("City Added");
        }else{
            System.out.println("ERROR, City not added");
        }
    }
}
