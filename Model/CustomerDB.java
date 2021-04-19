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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * CustomerDb handles all access to the customer database
 * 
 * @author Ben
 */
public class CustomerDB {
    
    //Create the connection variable loaded forom DBConnection
    private static final Connection CONN = DBConnection.getConnection();
    private static ObservableList<Customer> customerList = FXCollections.observableArrayList();
    
    /**
     * 
     * Retrieves the largest ID from the customer database and return the ID + 1.
     * If the customer database is empty then return the ID 1.
     * 
     * @return
     * @throws SQLException 
     */
    private static int getNextId() throws SQLException{
        int id;
        String insertStatement = "SELECT MAX(customerId) AS maxId FROM customer";
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
     * Get the customer from the database based on the customer ID
     * 
     * @param customerId
     * @return
     * @throws SQLException 
     */
    public static Customer getCustomer(int customerId) throws SQLException{
        String insertStatement = "SELECT customerId, customerName, addressId, active FROM customer WHERE customerId=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, customerId);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs.next()){
            Customer customer = new Customer(rs.getInt("customerId"), rs.getString("customerName"),
            rs.getInt("addressId"), rs.getBoolean("active"));
            
            return customer;
        }else{
            return null;
        }     
    }
    
    /**
     * 
     * Get and return all customers from the customer database
     * 
     * @return
     * @throws SQLException 
     */
    public static ObservableList<Customer> getAllCustomer() throws SQLException{
        customerList.clear();
        String insertStatement = "SELECT * FROM customer ORDER BY customerName ASC";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
        Customer customer = new Customer(rs.getInt("customerId"), rs.getString("customerName"),
        rs.getInt("addressId"), rs.getBoolean("active"));
        
        customerList.add(customer);
        }
        return customerList;
    }
    
    /**
     * 
     * Check if a customer in the customer database exists based on the customer details
     * 
     * @param customerName
     * @param addressId
     * @return
     * @throws SQLException 
     */
    public static int checkExists(String customerName, int addressId) throws SQLException{    
        String insertStatement = "SELECT customerId, customerName, addressId FROM customer WHERE customerName=? AND addressId=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, customerName);
        ps.setInt(2, addressId);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs.next()){
            int customerId = rs.getInt("customerId");
            
            return customerId;
        }else{
            return 0;
        }
    }
    
    /**
     * 
     * Add a customer to the customer database
     * 
     * @param customerName
     * @param addressId
     * @param active
     * @throws SQLException 
     */
    public static void addCustomer(String customerName, int addressId, int active) throws SQLException{
        int nextId = getNextId();
        String insertStatement = "INSERT INTO customer (customerId, customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, nextId);
        ps.setString(2, customerName);
        ps.setInt(3, addressId);
        ps.setInt(4, active);
        ps.setString(5, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(6, User.getCurrentUser().getName());
        ps.setString(7, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(8, User.getCurrentUser().getName());
        
        int result = ps.executeUpdate();
        if(result == 1){
            System.out.println("Customer Added");
        }else{
            System.out.println("ERROR, Customer not added");
        }   
    }
    
    /**
     * 
     * Update the customer based on the customer ID
     * 
     * @param customerId
     * @param customerName
     * @param addressId
     * @param active
     * @return
     * @throws SQLException 
     */
    public static boolean updateCustomer(int customerId, String customerName, int addressId,  int active) throws SQLException{
        String insertStatement = "UPDATE customer SET customerName=?, addressId=?, active=?, lastUpdate=?, lastUpdateBy=? "
                              + "WHERE customerId=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, customerName);
        ps.setInt(2, addressId);
        ps.setInt(3, active);
        ps.setString(4, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(5, User.getCurrentUser().getName());
        ps.setInt(6, customerId);
        
        int result = ps.executeUpdate();
        return result == 1;  
        
    }
    
    /**
     * 
     * Delete a customer based on the customer ID
     * 
     * @param customerId
     * @return
     * @throws SQLException 
     */
    public static boolean deleteCustomer(int customerId) throws SQLException{
        AppointmentDB.deleteCustomerAppointment(customerId);
        String insertStatement = "DELETE FROM customer WHERE customerId=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, customerId);
        
        int result = ps.executeUpdate();
        return result == 1;
    }
}

