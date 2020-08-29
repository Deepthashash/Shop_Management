package com.deeptha.admin.cashier;

import com.deeptha.database.Connect;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CashiersAddForm_Controller implements Initializable {

    public TextField userName;
    public PasswordField password;
    public PasswordField confirmPassword;

    Connect connection = Connect.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onSave(ActionEvent actionEvent) {

        if(userName.getText() == null || userName.getText().trim().isEmpty() || password.getText() == null || password.getText().trim().isEmpty() || confirmPassword.getText() == null || confirmPassword.getText().trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Some Fields are empty!");
            alert.show();
        }else{
            String uName = userName.getText();
            String uPass = password.getText();
            String uConfirmPass = confirmPassword.getText();

            if(uPass.equals(uConfirmPass)){
                Connect.executeAction("Insert into users(userName,userPassword,userType) values ("+"'"+uName+"',md5("+"'"+uPass+"'),0)");
                Stage stage = (Stage) userName.getScene().getWindow();
                stage.close();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Passwords doesn't match!");
                alert.show();
            }


        }

    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) userName.getScene().getWindow();
        stage.close();
    }
}
