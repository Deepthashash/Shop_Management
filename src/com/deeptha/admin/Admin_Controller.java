package com.deeptha.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class Admin_Controller implements Initializable {

    @FXML
    private BorderPane main;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void productsLoader(ActionEvent actionEvent) {
        View_Loader object = new View_Loader();
        Pane view = object.getPage("products");
        main.setCenter(view);
    }

    public void billsLoader(ActionEvent actionEvent) {
    }

    public void cashiersLoader(ActionEvent actionEvent) {
    }

    public void salesLoader(ActionEvent actionEvent) {
    }

}
