/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.AppointmentDB;
import Model.Customer;
import Model.CustomerDB;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



/**
 * FXML Controller class
 *
 * AddAppointment allows the user to add an appointment to the database for available times.
 * The user is restricted from scheduling over lapping appointments or appointments outside business
 * hours.
 * 
 * @author Ben
 */
public class AddAppointmentController implements Initializable {

    //Declare the observable lists for the customers and appointments
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private ObservableList<String> availableAppointmentTimes = FXCollections.observableArrayList();
    private ObservableList<String> takenAppointmentTimes = FXCollections.observableArrayList();
    
    
    private LocalTime open;
    private LocalTime close = LocalTime.of(17,0);
    
    private int apptId = -1;
            
    @FXML
    private Button clearBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private DatePicker dateSelectDate;
    @FXML
    private ComboBox<String> apptTimeCombobox;
    @FXML
    private TextField apptTitleTextbox;
    @FXML
    private TextField descriptionTextbox;
    @FXML
    private TextField apptTypeTextbox;
    @FXML
    private ComboBox<Customer> customerCombobox;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Initialize the starting promot text and disable the save button to prevent the customer from saving before a date and time are selected
        customerCombobox.setPromptText("Select Customer");
        apptTimeCombobox.setPromptText("Select Appt. Date");
        dateSelectDate.setPromptText("Select Date");
        saveBtn.disableProperty().set(true);
        
        //Load the customerList with all of the current customers so the user can select from them to create an appointment
        try {
            customerList = CustomerDB.getAllCustomer();
        } catch (SQLException ex) {
            Logger.getLogger(AddAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Load the customer combobox with all customers
        customerCombobox.getItems().setAll(customerList);
        
        //when the clear button is clicked clear all textboxes and reset text prompts to how the scene stared
        clearBtn.setOnAction((event) -> clearAll());//lambda is used to call the clearAll() rather than creating an extra method for the cancel button on click 
        
        //When the date is changed, update the times to show avialable time slots for the updated date
        dateSelectDate.setOnAction((event) -> {//lambda is used to call the getAvailableApptTimes rather than creating an extra method for the date change
            try {
                getAvailableApptTimes(dateSelectDate.getValue().toString());
            } catch (SQLException ex) {
                Logger.getLogger(EditAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
            }} );
        
        //Lambdas prevent user from entering more charactes than the database data type
        apptTitleTextbox.textProperty().addListener((observable, i, j)-> {
            if(j.length() > 255) apptTitleTextbox.setText(i);
        });
         
        descriptionTextbox.textProperty().addListener((observable, i, j)-> {
            if(j.length() > 65535) descriptionTextbox.setText(i);
        });
        
        apptTypeTextbox.textProperty().addListener((observable, i, j)-> {
            if(j.length() > 65535) apptTypeTextbox.setText(i);
        });
    }    
      

    /**
     * 
     * Check the values of the entered data, if valid pass the values to the AppointmentDB to 
     * add the appointment
     * 
     * @param event
     * @throws SQLException
     * @throws IOException 
     */
    @FXML
    private void saveClick(ActionEvent event) throws SQLException, IOException {
        String apptTitle = apptTitleTextbox.getText().trim();
        String description = descriptionTextbox.getText().trim();
        String apptType = apptTypeTextbox.getText().trim();
        LocalDate date = dateSelectDate.getValue();
        String start = apptTimeCombobox.getValue().split(" to ")[0] + ":00";
        String end = apptTimeCombobox.getValue().split(" to ")[1] + ":00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(date + " " + start , formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(date + " " + end , formatter);
        
        //Check that all textboxes are filled out. If they are then added the appointment
        if(customerCombobox.getValue() == null | apptTitle.isEmpty() | description.isEmpty() | apptType.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment not added");
            alert.setHeaderText(null);
            alert.setContentText("Please complete all fields");
            alert.showAndWait();
        }else{
            
            int customerId = customerCombobox.getValue().getId();
            
            //If the appointment was successfully added then alert the user and return to the main screen view
            if(AppointmentDB.addAppointment(customerId, apptTitle, description, apptType, startDateTime, endDateTime)){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Added");
                alert.setHeaderText(null);
                alert.setContentText("The appointment has been added");
                alert.showAndWait();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
                Parent mainView = loader.load();
                MainController controller = loader.getController();
                Scene mainScene = new Scene(mainView);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(mainScene);
                stage.show();
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The appointment has not been added");
                alert.showAndWait();
            }
        } 
    }

    
    //Clear and reset text prompts back to how the scene was started
    private void clearAll(){
        customerCombobox.getSelectionModel().select(-1);
        customerCombobox.setPromptText("Select Customer");
        apptTitleTextbox.clear();
        descriptionTextbox.clear();
        apptTypeTextbox.clear();
        dateSelectDate.getEditor().setText("");
        dateSelectDate.setPromptText("Select Date");
        apptTimeCombobox.getItems().clear();
        apptTimeCombobox.setPromptText("Select Appt. Date");
        saveBtn.disableProperty().set(true);
    }
    
    /**
     * 
     * Creates a list of all appointment slots from open to close, then checks the database
     * for the selected date for all taken appointments. The taken appointments are removed
     * from the available appointments and displayed to the user. This prevents the user from
     * being able to select a taken appointment slot.
     * 
     * @param date
     * @throws SQLException 
     */
    private void getAvailableApptTimes(String date) throws SQLException{
        
        //Set the open time to 08:00 local time
        open = LocalTime.of(8, 0);
        availableAppointmentTimes.clear();
        
        //Create a list of all appointments from open to close in 30 minute increments
        while(open.isBefore(close)){
            availableAppointmentTimes.add(open.toString() + " to " + open.plusMinutes(30));
            open = open.plusMinutes(30);
        }
        
        //Get all of the taken appointments for selected date from the appointment database
        takenAppointmentTimes = AppointmentDB.getTakenTimes(dateSelectDate.getValue().toString(), apptId);
        
        //Remove the taken appointments from the available appointment list
        takenAppointmentTimes.forEach(t -> availableAppointmentTimes.remove(t));

        //If there are no appointments available for the selected date update prompt else show available appointments and enable save button
        if(availableAppointmentTimes.isEmpty()){
            saveBtn.disableProperty().set(true);
            apptTimeCombobox.setPromptText("No Appt. Available");
        }else{
            saveBtn.disableProperty().set(false);
            apptTimeCombobox.setPromptText(null);
            apptTimeCombobox.getItems().setAll(availableAppointmentTimes);
            apptTimeCombobox.getSelectionModel().select(0);
        }
    }
}
