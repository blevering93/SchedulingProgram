/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import Model.AppointmentDB;
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
 * EditAppointment receives an existing appointment and allows the user to edit appointment details
 * 
 * @author Ben
 */
public class EditAppointmentController implements Initializable {
    
    //Declare the observable lists for the customers and appointments
    private ObservableList<String> availableAppointmentTimes = FXCollections.observableArrayList();
    private ObservableList<String> takenAppointmentTimes = FXCollections.observableArrayList();
    private LocalTime open;
    private LocalTime close = LocalTime.of(17,0);
    private Appointment appointment;
    
    @FXML
    private TextField apptTitleTextbox;
    @FXML
    private TextField descriptionTextbox;
    @FXML
    private TextField apptTypeTextbox;
    @FXML
    private DatePicker dateSelectDate;
    @FXML
    private ComboBox<String> apptTimeCombobox;
    @FXML
    private TextField idTextbox;
    @FXML
    private Button closeBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private TextField customerTextbox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Close the scene and return to main when the close button is clicked
        closeBtn.setOnAction((event) -> {try { //lambda is used to return to main rather than creating a seperate method for a close button click event
            returnMain(event);
            } catch (IOException ex) {
                Logger.getLogger(EditAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
            }});
        
        //When the date is changed, update the times to show avialable time slots for the updated date
        dateSelectDate.setOnAction((event) -> {//lambda is used to call the getAvailableApptTimes rather than creating an extra method for the date change
            try {
                getAvailableApptTimes(dateSelectDate.getValue().toString());
            } catch (SQLException ex) {
                Logger.getLogger(EditAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
            }});
        
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
     * Load the scene with the passed in appointments details
     * 
     * @param appt
     * @throws SQLException 
     */
    public void setAppointment(Appointment appt) throws SQLException{
        appointment = appt;
        idTextbox.setText(String.valueOf(appointment.getId()));
        customerTextbox.setText(appointment.getCustomerName());
        apptTitleTextbox.setText(appointment.getTitle());
        descriptionTextbox.setText(appointment.getDescription());
        apptTypeTextbox.setText(appointment.getType());
        dateSelectDate.setValue(LocalDate.parse(appointment.getDate()));
        getAvailableApptTimes(appointment.getDate());
        apptTimeCombobox.setValue(appointment.getStartTime() + " to " + appointment.getEndTime());
        
    }
    
    /**
     * 
     * Return the user back to the main screen
     * 
     * @param event
     * @throws IOException 
     */
    private void returnMain(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent mainView = loader.load();
        Scene mainScene = new Scene(mainView);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainScene);
        stage.show();
    }

    /**
     * 
     * Check that the user has entered valid data and attempt to update the selected
     * appointment.
     * 
     * @param event
     * @throws SQLException
     * @throws IOException 
     */
    @FXML
    private void updateClick(ActionEvent event) throws SQLException, IOException {
        int appointmentId = appointment.getId();
        int customerId = appointment.getCustomerId();
        String apptTitle = apptTitleTextbox.getText().trim();
        String description = descriptionTextbox.getText().trim();
        String apptType = apptTypeTextbox.getText().trim();
        LocalDate date = dateSelectDate.getValue();
        String start = apptTimeCombobox.getValue().split(" to ")[0] + ":00";
        String end = apptTimeCombobox.getValue().split(" to ")[1] + ":00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(date + " " + start, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(date + " " + end, formatter);
        
        if(apptTitle.isEmpty() | description.isEmpty() | apptType.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment not updated");
            alert.setHeaderText(null);
            alert.setContentText("Please complete all fields");
            alert.showAndWait();
        }else{
            if(AppointmentDB.updateAppointment(appointmentId, customerId, apptTitle, description, apptType, startDateTime, endDateTime)){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointment Updated");
                    alert.setHeaderText(null);
                    alert.setContentText("The appointment has been updated");
                    alert.showAndWait();

                    returnMain(event);
                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("The appointment has not been updated");
                    alert.showAndWait();
                }
        }
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
        takenAppointmentTimes = AppointmentDB.getTakenTimes(date, appointment.getId());
        
        //Remove the taken appointments from the available appointment list
        takenAppointmentTimes.forEach(t -> availableAppointmentTimes.remove(t));

        //If there are no appointments available for the selected date update prompt else show available appointments and enable update button
        if(availableAppointmentTimes.isEmpty()){
            updateBtn.disableProperty().set(true);
            apptTimeCombobox.setPromptText("No Appt. Available");
        }else{
            updateBtn.disableProperty().set(false);
            apptTimeCombobox.setPromptText(null);
            apptTimeCombobox.getItems().setAll(availableAppointmentTimes);
            apptTimeCombobox.getSelectionModel().select(0);
        }
    }

}
