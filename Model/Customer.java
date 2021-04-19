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
 * Create customer object to use with creating appointments
 * 
 * @author Ben
 */
public class Customer {
    
    //Declare customer variables
    protected int id;
    protected String name;
    protected int addressId;
    protected Address address;
    protected boolean active;
     
    //Construcors
    public Customer(int id, String name, int addressId, boolean active){
        setId(id);
        setName(name);
        setAddress(addressId);
        setActive(active);
    }
    
    //Setters
    public void setId(int id){
        this.id = id;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setAddress(int addressId){
        //Get the address from the address database table
        try {
            this.address = AddressDB.getAddress(addressId);
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    public void setActive(boolean active){
        this.active = active;
    }
    
    //Getters
    public int getId(){
        return this.id;
    }
    
    public String getName(){
        return this.name;
    }
    
    public int getAddresId(){
        return this.addressId;
    }
    
    public Address getAddress(){
        return this.address;
    }
    
    public String getAddressString(){
        String addressString = address.getAddress() + " " + address.getAddress2() + " " + address.getCityName() + " " + address.getPostalCode() + " " + address.getCountryName();
        return addressString;
    }
    
    public String getActive(){
        if(this.active){
            return "Active";
        }else{
            return "Inactive";
        }        
    }
    
    @Override
    public String toString(){
        return this.getName();
    } 
}
