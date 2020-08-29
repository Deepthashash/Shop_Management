package com.deeptha.admin.products;

import com.deeptha.database.Connect;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductsAddForm_Controller implements Initializable {

    public TextField formBarcode;
    public TextField formProduct;
    public TextField formPrice;
    public TextField formStock;
    public TextField formRel;

    Connect connection = Connect.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onSave(ActionEvent actionEvent) {

        if(formBarcode.getText() == null || formBarcode.getText().trim().isEmpty() || formPrice.getText() == null || formPrice.getText().trim().isEmpty() || formProduct.getText() == null || formProduct.getText().trim().isEmpty() || formStock.getText() == null || formStock.getText().trim().isEmpty() || formRel.getText() == null || formRel.getText().trim().isEmpty() ){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Some Fields are empty!");
            alert.show();
        }else{
            int barcode = Integer.parseInt(formBarcode.getText());
            String product = formProduct.getText();
            double price = Double.parseDouble(formPrice.getText());
            int stock = Integer.parseInt(formStock.getText());
            int rel = Integer.parseInt(formRel.getText());

            Connect.executeAction("Insert into products values ("+"'"+barcode+"',"+"'"+product+"',"+"'"+price+"',"+"'"+stock+"',"+"'"+rel+"')");
            Stage stage = (Stage) formRel.getScene().getWindow();
            stage.close();
        }

    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) formRel.getScene().getWindow();
        stage.close();
    }
}
