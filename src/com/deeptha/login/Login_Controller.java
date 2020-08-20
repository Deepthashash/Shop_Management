package com.deeptha.login;

import com.deeptha.database.Connect;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.sql.ResultSet;

public class Login_Controller {

    public TextField uname;
    public TextField upassword;
    Connect connection = Connect.getInstance();

    public void onLogin(ActionEvent actionEvent) {
        String name = uname.getText();
        String password = upassword.getText();

        ResultSet results = Connect.executeQuery("Select upassword from Users where uname="+"'"+name+"'"+ "and upassword="+"'"+password+"'");
        if(results != null){
            System.out.println("Login Successful!");
        }else{
            System.out.println("Error");
        }
    }

    
}
