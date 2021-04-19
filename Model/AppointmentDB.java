/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Utils.ConvertTimeZone;
import Utils.DBConnection;
import Utils.DBQuery;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * AppointmentDB handles all access to the appointment database
 * 
 * @author Ben
 */
public class AppointmentDB {
    
    //Create the connection vairable loaded from DBConnection
    private static final Connection CONN = DBConnection.getConnection();
    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private static ObservableList<String> timeList = FXCollections.observableArrayList();
    
    /**
     * 
     * Retrieves the largest ID from the appointment database and return the ID + 1.
     * If the appointment database is empty then return the ID 1.
     * 
     * @return
     * @throws SQLException 
     */
    private static int getNextId() throws SQLException{
        int id;
        String insertStatement = "SELECT MAX(appointmentId) AS maxId FROM appointment";
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
     * Get all appointments for the given month and year and return the appointment list
     * 
     * @param month
     * @param year
     * @return
     * @throws SQLException 
     */
    public static ObservableList<Appointment> getMonthAppointment(String month, String year) throws SQLException{
        
        appointmentList.clear();
        String insertStatement = "SELECT * FROM appointment WHERE monthname(start)=? AND year(start)=? ORDER BY start ASC";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, month);
        ps.setString(2, year);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            String startTime = ConvertTimeZone.getLocalTime(rs.getTimestamp("start").toLocalDateTime());
            String endTime = ConvertTimeZone.getLocalTime(rs.getTimestamp("end").toLocalDateTime());
            String date = ConvertTimeZone.getLocalDate(rs.getTimestamp("start").toLocalDateTime());
            
            
            Appointment appointment = new Appointment(rs.getInt("appointmentId"),rs.getInt("customerId"),
            rs.getInt("userId"), rs.getString("title"), rs.getString("description"), rs.getString("type"),
            date, startTime, endTime);
                 
            appointmentList.add(appointment);
        }
        
        return appointmentList;
    }
    
    /**
     * 
     * Get all appointments for the given week start and end date and return the appointment list
     * 
     * @param start
     * @param end
     * @return
     * @throws SQLException 
     */
    public static ObservableList<Appointment> getWeekAppointment(String start, String end) throws SQLException{
        
        appointmentList.clear();
        String insertStatement = "SELECT * FROM appointment WHERE start BETWEEN ? AND ? ORDER BY start ASC";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, start);
        ps.setString(2, end);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){
            String startTime = ConvertTimeZone.getLocalTime(rs.getTimestamp("start").toLocalDateTime());
            String endTime = ConvertTimeZone.getLocalTime(rs.getTimestamp("end").toLocalDateTime());
            String date = ConvertTimeZone.getLocalDate(rs.getTimestamp("start").toLocalDateTime());
            
            
            Appointment appointment = new Appointment(rs.getInt("appointmentId"),rs.getInt("customerId"),
            rs.getInt("userId"), rs.getString("title"), rs.getString("description"), rs.getString("type"),
            date, startTime, endTime);
            
            appointmentList.add(appointment);
        }
        
        return appointmentList;
    }
    
    /**
     * 
     * Retrieve all appointments for the given day, return the list of all taken appointment
     * time slots
     * 
     * @param date
     * @param apptId
     * @return
     * @throws SQLException 
     */
    public static ObservableList<String> getTakenTimes(String date, int apptId) throws SQLException{
        timeList.clear();
        String insertStatement = "SELECT appointmentId, start, end FROM appointment WHERE date(start)=? ORDER BY start ASC";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, date);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
                
        while(rs.next()){
            if(rs.getInt("appointmentId") != apptId){
                timeList.add(ConvertTimeZone.getLocalTime(rs.getTimestamp("start").toLocalDateTime()) + " to " 
                    + ConvertTimeZone.getLocalTime(rs.getTimestamp("end").toLocalDateTime()));
            }
        }
        return timeList;
    }
    
    /**
     * 
     * Add the given appointment to the appointment database
     * 
     * @param customerId
     * @param title
     * @param description
     * @param type
     * @param startDateTime
     * @param endDateTime
     * @return
     * @throws SQLException 
     */
    public static boolean addAppointment(int customerId, String title, String description, String type, LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException{
        int nextId = getNextId();
        String insertStatement = "INSERT INTO appointment (appointmentId, customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) "
                               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, nextId);
        ps.setInt(2, customerId);
        ps.setInt(3, User.getCurrentUser().getId());
        ps.setString(4, title);
        ps.setString(5, description);
        ps.setString(6, "not needed");
        ps.setString(7, "not needed");
        ps.setString(8, type);
        ps.setString(9, "not needed");
        ps.setString(10, ConvertTimeZone.getUTCDateTime(startDateTime));
        ps.setString(11, ConvertTimeZone.getUTCDateTime(endDateTime));
        ps.setString(12, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(13, User.getCurrentUser().getName());
        ps.setString(14, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(15, User.getCurrentUser().getName());
        
        int result = ps.executeUpdate();
        return result == 1;
        
    }
    
    /**
     * 
     * Update the given appointment in the appointment database
     * 
     * @param appointmentId
     * @param customerId
     * @param title
     * @param description
     * @param type
     * @param startDateTime
     * @param endDateTime
     * @return
     * @throws SQLException 
     */
    public static boolean updateAppointment(int appointmentId, int customerId, String title, String description, String type, LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException{
        String insertStatement = "UPDATE appointment SET title=?, description=?, type=?, start=?, end=?, lastUpdate=?, lastUpdateBy=? WHERE appointmentId=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, type);
        ps.setString(4, ConvertTimeZone.getUTCDateTime(startDateTime));
        ps.setString(5, ConvertTimeZone.getUTCDateTime(endDateTime));
        ps.setString(6, ConvertTimeZone.getUTCDateTime(LocalDateTime.now()));
        ps.setString(7, User.getCurrentUser().getName());
        ps.setInt(8, appointmentId);
        
        int result = ps.executeUpdate();
        return result == 1;
    }
    
    /**
     * 
     * Delete the given appointment from the appointment database
     * 
     * @param appointmentId
     * @return
     * @throws SQLException 
     */
    public static boolean deleteAppointment(int appointmentId) throws SQLException{
        String insertStatement = "DELETE FROM appointment WHERE appointmentId=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, appointmentId);
        
        int result = ps.executeUpdate();
        return result == 1;
    }
    
    /**
     * 
     * Delete all appointments for the given customer ID
     * 
     * @param customerId
     * @throws SQLException 
     */
    public static void deleteCustomerAppointment(int customerId) throws SQLException{
        String insertStatement = "DELETE FROM appointment WHERE customerId=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1,customerId);
        
        ps.executeUpdate();
    }
    
    /**
     * 
     * Get all appointments within 15 minutes of the current local time
     * 
     * @return
     * @throws SQLException 
     */
    public static boolean getAppointment15min() throws SQLException{
        String insertStatement = "SELECT * FROM appointment WHERE start>=? AND start<=?";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        

        String currentUTC = ConvertTimeZone.getUTCDateTime(LocalDateTime.now());
        String current15UTC = ConvertTimeZone.getUTCDateTime(LocalDateTime.now().plusMinutes(15));
        ps.setString(1, currentUTC);
        ps.setString(2, current15UTC);
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        return rs.next();
    }
    
    /**
     * 
     * Create a report to show how many of each type of appointment for the given month and year
     * 
     * @param month
     * @param year
     * @throws SQLException
     * @throws IOException 
     */
    public static void createTypeMonthReport(String month, String year) throws SQLException, IOException{
        String insertStatement = "SELECT type, COUNT(*) FROM appointment WHERE monthname(start)=? AND year(start)=? Group By type";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, month);
        ps.setString(2, year);
        
        ps.execute();
        
        ResultSet rs = ps.getResultSet();
        
        if(rs != null){
            File typeMonthReport = new File(month + "_" + year + "_type_report.txt");
            if(typeMonthReport.createNewFile()){
                System.out.println("Report created");
            }else{
                System.out.println("Report updated");
            }
            
            try (FileWriter reportWriter = new FileWriter(month + "_" + year + "_type_report.txt", false)) {
                while(rs.next()){
                    reportWriter.write(rs.getString("type") + " : " + rs.getInt("COUNT(*)") + System.lineSeparator());  
                }
            }
        }   
    }
    
    /**
     * 
     * Create a report to show how many of each type of appointment for the given week
     * 
     * @param start
     * @param end
     * @throws SQLException
     * @throws IOException 
     */
    public static void createTypeWeekReport(String start, String end) throws SQLException, IOException{
        String insertStatement = "SELECT type, COUNT(*) FROM appointment WHERE start BETWEEN ? AND ? Group By type";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setString(1, start);
        ps.setString(2, end);
        
        ps.execute();
        
        ResultSet rs = ps.getResultSet();
        
        if(rs != null){
            File typeWeekReport = new File(start + " to " + end + "_type_report.txt");
            if(typeWeekReport.createNewFile()){
                System.out.println("Report created");
            }else{
                System.out.println("Report updated");
            }
            
            try (FileWriter reportWriter = new FileWriter(start + " to " + end + "_type_report.txt", false)) {
                while(rs.next()){
                    reportWriter.write(rs.getString("type") + " : " + rs.getInt("COUNT(*)") + System.lineSeparator()); 
                }
            }
        }
    }
    
    /**
     * 
     * Create a report to show all appointments for the given customer ID
     * 
     * @param customer
     * @throws SQLException
     * @throws IOException 
     */
    public static void createCustomerApptReport(Customer customer) throws SQLException, IOException{
        String insertStatement = "SELECT * FROM appointment WHERE customerId=? ORDER BY start ASC";
        DBQuery.setPreparedStatement(CONN, insertStatement);
        
        PreparedStatement ps = DBQuery.getPreparedStatement();
        
        ps.setInt(1, customer.getId());
        
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        if(rs != null){
            File customerApptReport = new File(customer.getId() + "_" + customer.getName() + "_appointment_report.txt");
            if(customerApptReport.createNewFile()){
                System.out.println("Report Created");
            }else{
                System.out.println("Report Updated");
            }
            
            try (FileWriter reportWriter = new FileWriter(customer.getId() + "_" + customer.getName() + "_appointment_report.txt", false)){
                while(rs.next()){
                    String startTime = ConvertTimeZone.getLocalTime(rs.getTimestamp("start").toLocalDateTime());
                    String endTime = ConvertTimeZone.getLocalTime(rs.getTimestamp("end").toLocalDateTime());
                    String date = ConvertTimeZone.getLocalDate(rs.getTimestamp("start").toLocalDateTime());
            
                    reportWriter.write("Date: " + date + " Title: " + rs.getString("title") + " Type:" + rs.getString("type") + " Start: " + startTime + " End: " + endTime + System.lineSeparator());
                }
            }
        }
    } 
}
