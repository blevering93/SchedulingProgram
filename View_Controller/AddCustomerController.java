/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.AddressDB;
import Model.CityDB;
import Model.CountryDB;
import Model.CustomerDB;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 * FXML Controller class
 *
 * AddCustomer allows the user to add an customer to the database while also creating any missing
 * parts of the customers address to the appropriate database
 * 
 * @author Ben
 */
public class AddCustomerController implements Initializable {

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
    private Button clearBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private ToggleGroup status;
    @FXML
    private RadioButton activeRadio;
    @FXML
    private RadioButton inactiveRadio;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //when the clear button is clicked clear all textboxes and reset text prompts to how the scene stared
        clearBtn.setOnAction((event) -> clearAll());//lambda is used to call the clearAll() rather than creating an extra method for the cancel button on click   
        
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
     * Check that all values have been filled out then pass the values to check if each component of the 
     * customer already exists. If a part does not exist then create it and move to the next part.
     * 
     * @param event
     * @throws SQLException 
     */
    @FXML
    private void saveClick(ActionEvent event) throws SQLException {
        //Set variable to track if the customer already exists
        Boolean exists = false;
        
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
        int customerId;
        
        if(activeRadio.isSelected()){
            active = 1;
        }else{
            active = 0;
        }
        
        //Check that there are not any empty textboxes, if so show alert to user
        if(customerName.isEmpty() | address.isEmpty() | address2.isEmpty() | city.isEmpty() |
                postalCode.isEmpty() | country.isEmpty() | phone.isEmpty()){
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error: Customer Not Added");
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
                    exists = false;
                    CountryDB.addCountry(country); 
                }
            }while(countryId == 0);

            //Check if the city already exists, if not create the city and check again
            do{
                cityId = CityDB.checkExists(city, countryId);
                if(cityId != 0){
                    break;
                }else{
                    exists = false;
                    CityDB.addCity(city, countryId);
                }
            }while(cityId == 0);

            //Check if the address already exists, if not create the address and check again
            do{
                addressId = AddressDB.checkExists(address, address2, postalCode, phone, cityId);
                if(addressId != 0){
                    break;
                }else{
                    exists = false;
                    AddressDB.addAddress(address, address2, postalCode, phone, cityId);
                }
            }while(addressId == 0);

            //Check if the customer already exists, if not create the customer and check again
            do{
                customerId = CustomerDB.checkExists(customerName, addressId);
                if(customerId != 0){
                    break;
                }else{
                    exists = false;
                    CustomerDB.addCustomer(customerName, addressId, active);
                }
            }while(customerId == 0);

            //If the customer already exist alert user, else alert that the customer has been added
            if(exists){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error: Customer Already Exists");
                alert.setHeaderText(null);
                alert.setContentText("Please add a new customer");
                alert.showAndWait();
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Customer Added");
                alert.setHeaderText(null);
                alert.setContentText("Customer has been added");
                alert.showAndWait();
                clearAll();
            }
        }
    }
    
    //Clear all textboxes and set radio buttons back to active
    private void clearAll(){
        nameTextbox.clear();
        addressTextbox.clear();
        address2Textbox.clear();
        cityTextbox.clear();
        postalTextbox.clear();
        countryTextbox.clear();
        phoneTextbox.clear();
        activeRadio.setSelected(true);
    }    
}
