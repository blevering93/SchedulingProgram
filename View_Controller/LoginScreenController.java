/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.AppointmentDB;
import Model.User;
import Model.UserDB;
import Utils.ConvertTimeZone;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * LoginScreen allows the user to enter their username and password, it compares those values
 * to the values in the database. If the values match the user is set to the currentUser 
 * and sent to the main screen
 * 
 * @author Ben
 */
public class LoginScreenController implements Initializable {

    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordTextbox;
    @FXML
    private Label usernameLabel;
    @FXML
    private TextField usernameTextbox;
    @FXML
    private Button loginButton;

    
    private ResourceBundle resource;
    protected static User currentUser = null;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Check the users current default location, and set the login language to Spanish or English
        if(Locale.getDefault().getLanguage().equals("es")){
            resource = ResourceBundle.getBundle("lan/LoginScreen", Locale.getDefault());
        }else{
            resource = ResourceBundle.getBundle("lan/LoginScreen", Locale.ENGLISH);
    
        }
        
        //Set the Username, and Password labels to display with the correct language
        usernameLabel.setText(resource.getString("username"));
        passwordLabel.setText(resource.getString("password"));
  
    }    

    /**
     * 
     * When login is clicked, check the username and password and find a matching user 
     * in the user database
     * @param event
     * @throws IOException 
     */
    @FXML
    private void login(ActionEvent event) throws IOException {
        
        //Set the username and password based on what text is in the textboxes
        String username = usernameTextbox.getText();
        String password = passwordTextbox.getText();
        
        //Create a loginLog.txt file if it doesn't already exist
        File loginLog = new File("loginLog.txt");
        if(loginLog.createNewFile()){
            System.out.println("Log file created");
        }else{
            System.out.println("Log file already exists");
        }
        //Create a file writer to write to the login log file
        FileWriter logWriter = new FileWriter("loginLog.txt", true);

        try {
            //Validate the username and password entered with a match in the database
            currentUser = UserDB.validLogin(username,password);
            
            //If a match was found, log it and send the user to the main screen
            if(currentUser!= null){
                
                //Log the succssful login
                logWriter.write("--Successful login-- User: " + currentUser.getId() + " " + currentUser.getName() + " -- Time: " + ConvertTimeZone.getUTCDateTime(LocalDateTime.now()) + System.lineSeparator());
                logWriter.close();
                
                //Check and alert the user if there is an appointment starting in the next 15 minutes of the users login
                if(AppointmentDB.getAppointment15min()){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointment Starting Soon");
                    alert.setHeaderText(null);
                    alert.setContentText("An appointment is starting within the next 15 minutes");
                    alert.showAndWait();
                }
                
                //Set the current user to the user object
                User.setCurrentUser(currentUser);
                
                //Send the user to the main screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                Parent mainView = loader.load();
                MainController controller = loader.getController();
                Scene mainScene = new Scene(mainView);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(mainScene);
                stage.show();
             
            //If the user login information did not match the database, log the attempt and alert the user
            }else{
                
                //Log the failed login attempt
                logWriter.write("--Failed login-- Attempted User: " + username + " -- Time: " + ConvertTimeZone.getUTCDateTime(LocalDateTime.now()) + System.lineSeparator());
                logWriter.close();
                
                //Alert the user of the incorrect login information
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(resource.getString("loginError"));
                alert.setHeaderText(null);
                alert.setContentText(resource.getString("invalidLogin"));
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
