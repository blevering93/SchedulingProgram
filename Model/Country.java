/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 * 
 * Create country object to be used for the city address
 * 
 * @author Ben
 */
public class Country {
    
    //Set country variables
    protected int id;
    protected String name;

    //Constructor
    public Country(int id, String name){
        setId(id);
        setName(name);
    }

    //Setters
    public void setId(int id){
        this.id = id;
    }
    
    public void setName(String name){
        this.name = name;
    }
       
    //Getters
    public int getId(){
        return this.id;
    }
    
    public String getName(){
        return this.name;
    }  
}
