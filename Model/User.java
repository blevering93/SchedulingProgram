/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 * 
 * Create user object used for users to login and access appointment data
 * 
 * @author Ben
 */
public class User {
    
    //Declare user variables
    protected int id;
    protected String name;
    protected static User currentUser;
    
    //Constructor
    public User(int id, String name){
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
    
    public static void setCurrentUser(User currentUser){
        User.currentUser = currentUser;
    }
    
    //Getters
    public int getId(){
        return this.id;
    }
    
    public String getName(){
        return this.name;
    }
    
    public static User getCurrentUser(){
        return currentUser;
    }
    
    public static void clearCurrentUser(){
        User.currentUser = null;
    }
}
