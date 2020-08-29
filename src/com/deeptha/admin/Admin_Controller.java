package com.deeptha.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Admin_Controller implements Initializable {

    @FXML
    private BorderPane main;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProducts();
    }

    public void productsLoader(ActionEvent actionEvent) {
        loadProducts();
    }

    void loadProducts(){
        View_Loader object = new View_Loader();
        Pane view = object.getPage("products/products");
        main.setCenter(view);
    }

    public void billsLoader(ActionEvent actionEvent) {
        View_Loader object = new View_Loader();
        Pane view = object.getPage("bills/bills");
        main.setCenter(view);
    }

    public void cashiersLoader(ActionEvent actionEvent) {
        View_Loader object = new View_Loader();
        Pane view = object.getPage("cashier/cashiers");
        main.setCenter(view);
    }

    public void salesLoader(ActionEvent actionEvent) {
        View_Loader object = new View_Loader();
        Pane view = object.getPage("sales/sales");
        main.setCenter(view);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to logout?");
        Optional<ButtonType> answer = alert.showAndWait();
        if(answer.get() == ButtonType.OK){
            Parent parent = FXMLLoader.load(getClass().getResource("/com/deeptha/login/login.fxml"));
            Stage stage =new Stage(StageStyle.DECORATED);
            stage.setTitle("Add Form");
            stage.setScene(new Scene(parent));
            stage.show();

            Stage stage2 = (Stage) main.getScene().getWindow();
            stage2.close();
        }else{

        }
    }
}
