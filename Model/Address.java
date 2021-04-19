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
 * Create the address object used for customer to create appointments
 * 
 * @author Ben
 */
public class Address {
    
    //declare address variables
    protected int id;
    protected String address;
    protected String address2;
    protected int cityId;
    protected City city;
    protected String postalCode;
    protected String phone;

    
    //constructor
    public Address(int id, String address, String address2, int cityId, String postalCode, String phone) {
        setId(id);
        setAddress(address);
        setAddress2(address2);
        setCity(cityId);
        setPostalCode(postalCode);
        setPhone(phone);
    }

    //setters
    public void setId(int id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setCity(int cityId) {
        //get the city from the city database table
        try {
            this.city = CityDB.getCity(cityId);
        } catch (SQLException ex) {
            Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
     
    //getters
    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress2() {
        return address2;
    }

    public int getCityId() {
        return cityId;
    }
    
    public String getCityName(){
        return city.getName();
    }
    
    public String getCountryName(){
        return city.getCountryName();
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }
    
    
    
    
    
    
    
    
}
