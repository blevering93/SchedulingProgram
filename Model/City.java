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
 * Create city object to be used in the customers address
 * 
 * @author Ben
 */
public class City {
    
    //Declare city variables
    protected int id;
    protected String name;
    protected int countryId;
    protected Country country;
    
    //Constructor
    public City(int id, String name, int countryId){
        setId(id);
        setName(name);
        setCountry(countryId);
    }
 
    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(int countryId) {
        //get the country from the country databsae table
        try {
            this.country = CountryDB.getCountry(countryId);
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    //Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCountryId() {
        return countryId;
    }
    
    public String getCountryName(){
        return country.getName();
    } 
}
