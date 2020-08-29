package com.deeptha.admin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Admin_Loader extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("admin.fxml"));
        primaryStage.setTitle("Admin");
        primaryStage.setScene(new Scene(root, 1080, 728));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}