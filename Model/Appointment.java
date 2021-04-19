/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * Create the appointment object
 * 
 * create appointment object
 * @author Ben
 */
public class Appointment {
    
    //declare appointment variables
    protected int id;
    protected Customer customer;
    protected int userId;
    protected String title;
    protected String description;
    protected String type;
    protected String date;
    protected String startTime;
    protected String endTime;
    
    
    //constructor
    public Appointment(int id, int customerId, int userId, String title, String description, String type, String date, String start, String end){
        setId(id);
        setCustomer(customerId);
        setUserId(userId);
        setTitle(title);
        setDescription(description);
        setType(type);
        setDate(date);
        setStartTime(start);
        setEndTime(end);
    }

    //setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCustomer(int customerId){
        //get the customer from the customer database table
        try {
            this.customer = CustomerDB.getCustomer(customerId);
        } catch (SQLException ex) {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDate(String date){
        this.date = date;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    //getters
    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customer.getId();
    }
    
    public String getCustomerName() {
        return customer.getName();
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getDate(){
        return date;
    }
    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
    
    
}
