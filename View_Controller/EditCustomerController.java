/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.AddressDB;
import Model.CityDB;
import Model.CountryDB;
import Model.Customer;
import Model.CustomerDB;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * FXML Controller class
 * 
 * EditCustomer receives an existing customer and allows the user to edit customer details
 *
 * @author Ben
 */
public class EditCustomerController implements Initializable {
    private Customer customer;
    
    @FXML
    private TextField idTextbox;
    @FXML
    private TextField nameTextbox;
    @FXML
    private TextField addressTextbox;
    @FXML
    private TextField address2Textbox;
    @FXML
    private TextField cityTextbox;
    @FXML
    private TextField postalTextbox;
    @FXML
    private TextField countryTextbox;
    @FXML
    private TextField phoneTextbox;
    @FXML
    private RadioButton activeRadio;
    @FXML
    private ToggleGroup status;
    @FXML
    private RadioButton inactiveRadio;
    @FXML
    private Button saveUpdate;
    @FXML
    private Button closeBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //close the scene and return to main when the close button is clicked
        closeBtn.setOnAction((event) -> {try { //lambda is used to return to main rather than creating a seperate method for a close button click event
            returnMain(event);
            } catch (IOException ex) {
                Logger.getLogger(EditAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        //Lambdas prevent user from entering more charactes than the database data type
        nameTextbox.textProperty().addListener((observable, i, j)-> {
            if(j.length() > 45) nameTextbox.setText(i);
        });
         
        addressTextbox.textProperty().addListener((observable, i, j)-> {
            if(j.length() > 50) addressTextbox.setText(i);
        });
        
        address2Textbox.textProperty().addListener((observable, i, j)-> {
            if(j.length() > 50) address2Textbox.setText(i);
        });
        
        cityTextbox.textProperty().addListener((observable, i, j)-> {
            if(j.length() > 50) cityTextbox.setText(i);
        });
        
        postalTextbox.textProperty().addListener((observable, i, j)-> {
            if(j.length() > 10) postalTextbox.setText(i);
        });
        
        countryTextbox.textProperty().addListener((observable, i, j)-> {
            if(j.length() > 50) countryTextbox.setText(i);
        });
        
        phoneTextbox.textProperty().addListener((observable, i, j)-> {
            if(j.length() > 10) phoneTextbox.setText(i);
        });
    }  
    
    /**
     * 
     * Set the fields to match the customer object passed in
     * 
     * @param cust 
     */
    public void setCustomer(Customer cust){
        customer = cust;
        idTextbox.setText(String.valueOf(customer.getId()));
        nameTextbox.setText(customer.getName());
        addressTextbox.setText(customer.getAddress().getAddress());
        address2Textbox.setText(customer.getAddress().getAddress2());
        cityTextbox.setText(customer.getAddress().getCityName());
        postalTextbox.setText(customer.getAddress().getPostalCode());
        countryTextbox.setText(customer.getAddress().getCountryName());
        phoneTextbox.setText(customer.getAddress().getPhone());
        
        if(customer.getActive().equals("Active")){
            activeRadio.setSelected(true);
        }else{
            inactiveRadio.setSelected(true);
        } 
    }
    
    /**
     * 
     * Validate the customer information entered. Update the current customer with the
     * updated information. If the parts of the customer don't exist, add them
     * 
     * @param event
     * @throws SQLException
     * @throws IOException 
     */
    @FXML
    private void updateClick(ActionEvent event) throws SQLException, IOException {
        int customerId = customer.getId();
        String customerName = nameTextbox.getText().trim();
        String address = addressTextbox.getText().trim();
        String address2 = address2Textbox.getText().trim();
        String city = cityTextbox.getText().trim();
        String postalCode = postalTextbox.getText().trim();
        String country = countryTextbox.getText().trim();
        String phone = phoneTextbox.getText().trim();
        int active;
        
        int countryId;
        int cityId;
        int addressId;
        
        if(activeRadio.isSelected()){
            active = 1;
        }else{
            active = 0;
        }
        
        //Check that there are not any empty textboxes, if so show alert to user
        if(customerName.isEmpty() | address.isEmpty() | address2.isEmpty() | city.isEmpty() |
                postalCode.isEmpty() | country.isEmpty() | phone.isEmpty()){
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error: Customer Not Updated");
            alert.setHeaderText(null);
            alert.setContentText("Please Fill Out All Fields");
            alert.showAndWait();
        }else{
        
            //Check if the country already exists, if not create the country and check again
            do{
                countryId = CountryDB.checkExists(country);
                if(countryId != 0){
                    break;
                }else{
                    CountryDB.addCountry(country); 
                }
            }while(countryId == 0);

            //Check if the city already exists, if not create the city and check again
            do{
                cityId = CityDB.checkExists(city, countryId);
                if(cityId != 0){
                    break;
                }else{
                    CityDB.addCity(city, countryId);
                }
            }while(cityId == 0);

            //Check if the address already exists, if not create the address and check again
            do{
                addressId = AddressDB.checkExists(address, address2, postalCode, phone, cityId);
                if(addressId != 0){
                    break;
                }else{
                    AddressDB.addAddress(address, address2, postalCode, phone, cityId);
                }
            }while(addressId == 0);

            //Attempt to update customer
            if(CustomerDB.updateCustomer(customerId, customerName, addressId, active)){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Customer Updated");
                alert.setHeaderText(null);
                alert.setContentText("The customer has been updated ");
                alert.showAndWait();

                returnMain(event);
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The customer has not been updated ");
                alert.showAndWait();
            }
        }
    }
    
    /**
     * 
     * Return the user back to the main scene
     * 
     * @param event
     * @throws IOException 
     */
    private void returnMain(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent mainView = loader.load();
        MainController controller = loader.getController();
        Scene mainScene = new Scene(mainView);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainScene);
        stage.show();
    }
    
    
    
    
    
    
}
