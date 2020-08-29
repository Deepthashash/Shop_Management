package com.deeptha.login;

import com.deeptha.admin.View_Loader;
import com.deeptha.cashier.Cashier_Loader;
import com.deeptha.database.Connect;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login_Controller {

    public TextField uname;
    public PasswordField upassword;
    Connect connection = Connect.getInstance();

    public void onLogin(ActionEvent actionEvent) throws SQLException, IOException {
        String name = uname.getText();
        String password = upassword.getText();

        ResultSet results = Connect.executeQuery("Select * from Users where userName="+"'"+name+"'"+ "and userPassword=md5("+"'"+password+"')");
        if(results.next()){
            System.out.println("Login Successful!");
            if(results.getInt("userType") == 0){
                Parent parent = FXMLLoader.load(getClass().getResource("/com/deeptha/cashier/cashier.fxml"));
                Stage stage =new Stage(StageStyle.DECORATED);
                stage.setTitle("Add Form");
                stage.setMaximized(true);
                stage.setScene(new Scene(parent));
                stage.show();

                Stage stage2 = (Stage) uname.getScene().getWindow();
                stage2.close();
            }else{
                Parent parent = FXMLLoader.load(getClass().getResource("/com/deeptha/admin/admin.fxml"));
                Stage stage =new Stage(StageStyle.DECORATED);
                stage.setTitle("Add Form");
                stage.setMaximized(true);
                stage.setScene(new Scene(parent));
                stage.show();

                Stage stage2 = (Stage) uname.getScene().getWindow();
                stage2.close();

            }
        }else{
            System.out.println("Error");
        }
    }

    
}
