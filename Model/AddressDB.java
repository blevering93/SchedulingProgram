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
 * AddressDB handles all access to the address database
 * 
 * @author Ben
 */
public class AddressDB {
    
    //Create the connection vairable loaded from DBConnection
    private static final Connection CONN = DBConnection.getConnection();
    
    /**
     * 
     * Retrieves the largest ID from the address database and return the ID + 1.
     * If the address database is empty then return the ID 1.
     * 
     * @return
     * @throws SQLException 
     */
    private static int getNextId() throws SQLException{
        int id;
        String insertStatement = "SELECT MAX(addressId) AS maxId FROM address";
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
     * Get the address from the address database based on a address ID.
     * 
     * @param addressId
     * @return
     * @throws SQLException 
     */
    public static Address getAddress(int addressId) throws SQLException{
        String insertStatement = "SELECT addressId, address, address2, cityId, postalCode, phone FROM address WHERE addressID=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, addressId);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs.next()){
            Address address = new Address(rs.getInt("addressId"), rs.getString("address"), rs.getString("address2"),
            rs.getInt("cityId"), rs.getString("postalCode"), rs.getString("phone"));
            
            return address;
        }else{
            return null;
        }
    }
    
    /**
     * 
     * Check if an address in the address database exists based on the address details
     * 
     * @param address
     * @param address2
     * @param postalCode
     * @param phone
     * @param cityId
     * @return
     * @throws SQLException 
     */
    public static int checkExists(String address, String address2, String postalCode, String phone, int cityId) throws SQLException{
        
        String insertStatement = "SELECT addressId, address, address2, postalCode, phone, cityId FROM address "
                + "WHERE address=? AND address2=? AND postalCode=? AND phone=? AND cityId=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
         
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, address);
        ps.setString(2, address2);
        ps.setString(3, postalCode);
        ps.setString(4, phone);
        ps.setInt(5, cityId);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs.next()){
            int addressId = rs.getInt("addressId");
            
            return addressId;
        }else{
            return 0;
        }
    }
      
    /**
     * 
     * Add an address to the address database
     * 
     * @param address
     * @param address2
     * @param postalCode
     * @param phone
     * @param cityId
     * @throws SQLException 
     */
    public static void addAddress(String address, String address2, String postalCode, String phone, int cityId) throws SQLException{
        int nextId = getNextId();
        String insertStatement = "INSERT INTO address (addressId, address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, nextId);
        ps.setString(2, address);
        ps.setString(3, address2);
        ps.setInt(4, cityId);
        ps.setString(5, postalCode);
        ps.setString(6, phone);
        ps.setString(7, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(8, User.getCurrentUser().getName());
        ps.setString(9, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(10,User.getCurrentUser().getName());
        
        int result = ps.executeUpdate();
        if(result == 1){
            System.out.println("Address Added");
        }else{
            System.out.println("ERROR, Address not added");
        }   
    }
}
