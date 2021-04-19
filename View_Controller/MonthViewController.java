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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 * 
 * MonthView shows all appointments for the selected month
 *
 * @author Ben
 */
public class MonthViewController implements Initializable {

    @FXML
    private Spinner<String> monthsSpinner;
    @FXML
    private Spinner<Integer> yearsSpinner;
    @FXML
    private TableView<Appointment> monthTableView;
    @FXML
    private TableColumn<Appointment, String> dateTableColumn;
    @FXML
    private TableColumn<Appointment, String> customerTableColumn;
    @FXML
    private TableColumn<Appointment, String> titleTableColumn;
    @FXML
    private TableColumn<Appointment, String> typeTableColumn;
    @FXML
    private TableColumn<Appointment, String> startTableColumn;
    @FXML
    private TableColumn<Appointment, String> endTableColumn;
    @FXML
    private Button viewBtn;
    
    
    protected Calendar cal = Calendar.getInstance();   
    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    
    
    /**
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Initialize the months spinner to display the months of the year starting at the current month
        ObservableList<String> months = FXCollections.observableArrayList("January", "February", "March", "April",
                "May", "June", "July", "August", "September", "October", "November", "December");
        
        SpinnerValueFactory<String> monthsFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(months);
        monthsFactory.setValue(new SimpleDateFormat("MMMM").format(cal.getTime()));
        monthsSpinner.setValueFactory(monthsFactory);
        
        //Initialize the year spinner to display the past and future 5 years starting at the current year
        int cYear = Calendar.getInstance().get(Calendar.YEAR);
        SpinnerValueFactory<Integer> yearFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(cYear - 5,cYear + 5, cYear);
        yearsSpinner.setValueFactory(yearFactory);
        
        
        
        //Set appointment list with appointments from the database
        loadMonthTable();
        
        
        //Set table columns for monthTableView
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerTableColumn.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        titleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeTableColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startTableColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTableColumn.setCellValueFactory(new PropertyValueFactory<>("endTime")); 
        
        //When the view button is clicked call the loadMonthTable method.
        viewBtn.setOnAction((event) -> loadMonthTable());
    }    
    
    //Take the month and year selected and retrieve all appointments for that time period
    private void loadMonthTable(){
        try {
            String month = monthsSpinner.getValue();
            String year = yearsSpinner.getValue().toString();
            appointmentList = AppointmentDB.getMonthAppointment(month, year);
            monthTableView.setItems(appointmentList);
        } catch (SQLException ex) {
            Logger.getLogger(MonthViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Send the selected appointment to the view edit appointment scene
    @FXML
    private void viewEditAppointmentClick(ActionEvent event) throws IOException, SQLException{
        Appointment appointment = monthTableView.getSelectionModel().getSelectedItem();
        if(appointment != null){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditAppointment.fxml"));
            Parent mainView = loader.load();
            EditAppointmentController controller = loader.getController();
            controller.setAppointment(appointment);
            Scene mainScene = new Scene(mainView);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.show();
        }
    }

    //Delete the selected appointment
    @FXML
    private void DeleteAppointmentClick(ActionEvent event) throws SQLException {
        //Set appintment to the selected appointment
        Appointment appointment = monthTableView.getSelectionModel().getSelectedItem();
        
        //If a appointment is selected display a confirmation alert to user to delete the appointment
        if(appointment != null){
            ButtonType deleteButtonType = new ButtonType("Delete");
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText(null);
            confirm.getButtonTypes().setAll(deleteButtonType, cancelButtonType);
            confirm.setContentText("Are you sure you want to delete the appointment for " + appointment.getCustomerName() + " on " + appointment.getDate() + " from " + appointment.getStartTime() + " to " + appointment.getEndTime() + "?");
            
            //If the user selects Delete, attempt to delete the appointment
            Optional<ButtonType> result = confirm.showAndWait();
            if(result.get() == deleteButtonType){
                if(AppointmentDB.deleteAppointment(appointment.getId())){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointment Deleted");
                    alert.setHeaderText(null);
                    alert.setContentText("The appointment has been deleted");
                    alert.showAndWait();
                    loadMonthTable();

                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("The appointment has not been deleted");
                    alert.showAndWait();
                }
            }
        }   
    }

    //Send the selected month and year to AppointmentDB to create a type report 
    @FXML
    private void typeReportClick(ActionEvent event) throws SQLException, IOException {
        String month = monthsSpinner.getValue();
        String year = yearsSpinner.getValue().toString();
        AppointmentDB.createTypeMonthReport(month, year);
    } 
}
