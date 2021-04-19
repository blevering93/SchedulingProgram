/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;


import Model.User;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 * Main allows the user to switch between the MonthView, WeekView, AddApointment, ViewAllCustomer, AddCustomer views
 * @author Ben
 */
public class MainController implements Initializable {

    @FXML
    private Button monthViewBtn;
    @FXML
    private Button weekViewBtn;
    @FXML
    private Button customerViewBtn;
    @FXML
    private BorderPane mainView;
    @FXML
    private Button addCustomerBtn;
    @FXML
    private Button addAppointmentBtn;
    
    private Pane view;
    
    //Start the user at the MonthView
    private String selected = "month";
    
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Try to load the pane with the MonthView for the user to start at
        try {
            view = FXMLLoader.load(MainController.class.getResource("MonthView.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        mainView.setCenter(view);
    } 

    /**
     * 
     * If the user clicks on the Month View Button load the MonthView if the user is not currently on the 
     * month view screen. If the user is already on the MonthView screen, do noting.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    private void monthViewClicked(ActionEvent event) throws IOException {
        if(!"month".equals(selected)){
            selected = "month";
            view = FXMLLoader.load(MainController.class.getResource("MonthView.fxml"));
            mainView.setCenter(view);
        }    
    }

    /**
     * 
     * If the user clicks on the Week View Button load the WeekView if the user is not currently on the 
     * week view screen. If the user is already on the WeekView screen, do noting.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    private void weekViewClicked(ActionEvent event) throws IOException {
        if(!"week".equals(selected)){
            selected = "week";
            view = FXMLLoader.load(MainController.class.getResource("WeekView.fxml"));
            mainView.setCenter(view);
        }
    }

    /**
     * 
     * If the user clicks on the Add Appointment Button load AddAppointment if the user is not currently on the 
     * Add Appointment screen. If the user is already on the AddAppointment screen, do noting.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    private void addAppointmentClick(ActionEvent event) throws IOException {
        if(!"addAppointment".equals(selected)){
            selected = "addAppointment";
            view = FXMLLoader.load(MainController.class.getResource("AddAppointment.fxml"));
            System.out.println(User.getCurrentUser().getName());
            mainView.setCenter(view);
        }  
    }
    
    /**
     * 
     * If the user clicks on the Customer View Button load the ViewAllCustomer if the user is not currently on the 
     * customer view screen. If the user is already on the ViewAllCustomer screen, do noting.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    private void customerViewClick(ActionEvent event) throws IOException {
        if(!"customer".equals(selected)){
            selected = "customer";
            view = FXMLLoader.load(MainController.class.getResource("ViewAllCustomer.fxml"));
            mainView.setCenter(view);
        }
    }

    /**
     * 
     * If the user clicks on the Add Customer Button load AddCustomer if the user is not currently on the
     * Add Customer screen. If the user is already on the AddCustomer screen, do noting.
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    private void addCustomerClick(ActionEvent event) throws IOException {
        if(!"addCustomer".equals(selected)){
            selected = "addCustomer";
            view = FXMLLoader.load(MainController.class.getResource("AddCustomer.fxml"));
            System.out.println(User.getCurrentUser().getName());
            mainView.setCenter(view);
        }
    }

    /**
     * 
     * If the user clicks the Logout button, give the user a confirmation alert.
     * If the user continues clear the current user from the user object and return the user to
     * the login screen
     * 
     * @param event
     * @throws IOException 
     */
    @FXML
    private void logoutClick(ActionEvent event) throws IOException {
        ButtonType logoutButtonType = new ButtonType("Logout");
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        //Give the user a chance to confirm or continue logging out
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.getButtonTypes().setAll(logoutButtonType, cancelButtonType);
        confirm.setContentText("Are you sure you want to log out?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if(result.get() == logoutButtonType){
            //Clear the current user from the user object
            User.clearCurrentUser();
            
            //Return the user to the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
            Parent loginView = loader.load();
            Scene mainScene = new Scene(loginView);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.show();
        }
    }
}
