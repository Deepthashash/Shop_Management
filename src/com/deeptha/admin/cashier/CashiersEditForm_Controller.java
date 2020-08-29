package com.deeptha.admin.cashier;

import com.deeptha.database.Connect;
import com.deeptha.services.Record;
import com.deeptha.services.SelectedRecord;
import com.deeptha.services.Users;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CashiersEditForm_Controller implements Initializable {

    public Label userName;
    public PasswordField password;
    public PasswordField confirmPassword;
    public Button save;


    Users rec = SelectedRecord.selectedUser;

    Connect connection = Connect.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userName.setText(rec.getUserName());
    }

    public void onSave(ActionEvent actionEvent) {
        int uid = rec.getUserID();
        String uPass = password.getText();
        String uConfirmPass = confirmPassword.getText();

        if(uPass.trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Fields are Empty!");
            alert.show();
        }else{
            if(uPass.equals(uConfirmPass)){
                Connect.executeAction("Update users set userPassword=md5("+"'"+uPass+"') where userID="+"'"+uid+"'");
                Stage stage = (Stage) password.getScene().getWindow();
                stage.close();
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Password and Confirm Password doesn't match!");
                alert.show();
            }


        }

    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) password.getScene().getWindow();
        stage.close();
    }
}
