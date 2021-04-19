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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Ben
 */
public class ViewAllCustomerController implements Initializable {

    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, Integer> idTableColumn;
    @FXML
    private TableColumn<Customer, String> nameTableColumn;
    @FXML
    private TableColumn<Customer, String> addressTableColumn;
    @FXML
    private TableColumn<Customer, Boolean> statusTableColumn;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        loadCustomerTable();
        
        //set table columns for customerTableView
        idTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressTableColumn.setCellValueFactory(new PropertyValueFactory<>("addressString"));
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
    }    
    
    private void loadCustomerTable(){
        try {
            customerList = CustomerDB.getAllCustomer();
            customerTableView.setItems(customerList);
        } catch (SQLException ex) {
            Logger.getLogger(ViewAllCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void editClick(ActionEvent event) throws IOException {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        if(customer != null){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditCustomer.fxml"));
            Parent mainView = loader.load();
            EditCustomerController controller = loader.getController();
            controller.setCustomer(customer);
            Scene mainScene = new Scene(mainView);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.show();
        }
    }

    @FXML
    private void deleteClick(ActionEvent event) throws SQLException {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        
        if(customer != null){
            ButtonType deleteButtonType = new ButtonType("Delete");
            ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText(null);
            confirm.getButtonTypes().setAll(deleteButtonType, cancelButtonType);
            confirm.setContentText("Are you sure you want to delete the customer " + customer.getName() + " and all appointments for this customer?");
            
            
            Optional<ButtonType> result = confirm.showAndWait();
            if(result.get() == deleteButtonType){
                if(CustomerDB.deleteCustomer(customer.getId())){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Customer Deleted");
                    alert.setHeaderText(null);
                    alert.setContentText("The customer has been deleted");
                    alert.showAndWait();
                    loadCustomerTable();

                }else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("The customer has not been deleted");
                    alert.showAndWait();
                }
            }
        }
    }

    @FXML
    private void custApptListClick(ActionEvent event) throws SQLException, IOException {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        
        if(customer != null){
            AppointmentDB.createCustomerApptReport(customer);
        }
    }
    
}
