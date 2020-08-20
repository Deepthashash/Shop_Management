package com.deeptha.login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Login_Loader extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 445, 170));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}