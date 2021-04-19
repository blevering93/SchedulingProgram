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
import java.time.DayOfWeek;
import java.time.LocalDate;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * WeekView shows all appointments for the selected week
 * 
 * @author Ben
 */
public class WeekViewController implements Initializable {

    @FXML
    private Button viewBtn;
    @FXML
    private TableView<Appointment> weekTableView;
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
    private Label weekLbl;
    @FXML
    private Button nextWeekBtn;
    @FXML
    private Button previousWeekBtn;
    @FXML
    private Button viewEditAppointmentBtn;
    
    //Set the first day of the week as Monday
    protected LocalDate mondayDayWeek = LocalDate.now().with(DayOfWeek.MONDAY);
    protected LocalDate selectedWeek = mondayDayWeek;
    protected int weekOffset = 0;
    
    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Set appointment list with appointments from the database
        loadWeekTable();
        
        //Set table columns for monthTableView
        dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerTableColumn.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        titleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeTableColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startTableColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTableColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        
        //When the view button is clicked load the week table view
        viewBtn.setOnAction((event) -> loadWeekTable());
    }    

    //When next week is clicked add 7 days to the week offset and display the week
    @FXML
    private void nextWeekClick(ActionEvent event) {
        weekOffset +=7;
        selectedWeek = mondayDayWeek.plusDays(weekOffset);
        //If week offset is 0, the current week is selected
        if(weekOffset == 0){
            weekLbl.setText("Current Week");
        }else{
            weekLbl.setText(selectedWeek.toString() + " to " + selectedWeek.plusDays(6).toString());
        }       
    }

    //When previous week is clicked subtract 7 days from the week offset and display the week
    @FXML
    private void previousWeekClick(ActionEvent event) {
        weekOffset -=7;
        selectedWeek = mondayDayWeek.plusDays(weekOffset);
        //If week offset is 0, the current week is selected
        if(weekOffset == 0){
            weekLbl.setText("Current Week");
        }else{
            weekLbl.setText(selectedWeek.toString() + " to " + selectedWeek.plusDays(6).toString());
        }
    }
    
    //Send the selected start and end date for the selected week to the AppointmentDB, display returned appointments
    private void loadWeekTable(){
        try {
            String start = selectedWeek.toString();
            String end = selectedWeek.plusDays(6).toString();
            
            //Set the appointment list to the returned appointments for the selected week
            appointmentList = AppointmentDB.getWeekAppointment(start, end);
            weekTableView.setItems(appointmentList);
        } catch (SQLException ex) {
            Logger.getLogger(MonthViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Send the selected appointment to the view edit appointment scene
    @FXML
    private void viewEditAppointmentClick(ActionEvent event) throws IOException, SQLException {
        Appointment appointment = weekTableView.getSelectionModel().getSelectedItem();
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
        //Set appointment to the selected appointment
        Appointment appointment = weekTableView.getSelectionModel().getSelectedItem();
        
        //If a appointment is selected display a confirmation alert to user to delete the appointment
        if(appointment != null){
            ButtonType deleteButtonType = new ButtonType("Delete");
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            
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
                    loadWeekTable();

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

    //Send the selected week start and end to Appointment DB to create a type report
    @FXML
    private void typeReportClick(ActionEvent event) throws SQLException, IOException {
        String start = selectedWeek.toString();
        String end = selectedWeek.plusDays(6).toString();
        AppointmentDB.createTypeWeekReport(start, end);
    }  
}
