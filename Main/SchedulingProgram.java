/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Model.Appointment;
import Model.Customer;
import Model.CustomerDB;
import Utils.ConvertTimeZone;
import Utils.DBConnection;
import View_Controller.LoginScreenController;
import java.sql.Connection;
import java.time.LocalDateTime;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * SchedulingProgram starts the program and creates the database connection
 * then send the user to the login screen
 * @author Ben Levering
 */
public class SchedulingProgram extends Application {
    //Declare variable for database connection
    public static Connection conn = null;
    
    @Override
    public void start(Stage stage) throws Exception {
  
        //Send user to Login scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View_Controller/LoginScreen.fxml"));
        Parent loginView = loader.load();
        LoginScreenController controller = loader.getController();
        loader.setController(controller);
        Scene loginScene = new Scene(loginView);
        stage.setScene(loginScene);
        stage.setResizable(false);
        stage.show();    
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Start the database connection when the program starts
        conn = DBConnection.startConnection();        
        launch(args);
        //Close the database connection when the program ends
        DBConnection.closeConnection();
    }  
}
