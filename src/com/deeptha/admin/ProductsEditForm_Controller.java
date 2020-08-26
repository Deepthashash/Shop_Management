package com.deeptha.admin;

import com.deeptha.database.Connect;
import com.deeptha.services.Record;
import com.deeptha.services.SelectedRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductsEditForm_Controller implements Initializable {

    public TextField formBarcode;
    public TextField formProduct;
    public TextField formPrice;
    public TextField formStock;
    public TextField formRel;
    public Button save;


    Record rec = SelectedRecord.rec;

    Connect connection = Connect.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        formBarcode.setText(Integer.toString(rec.getBarcode()));
        formProduct.setText(rec.getProduct());
        formPrice.setText(Double.toString(rec.getPrice()));
        formStock.setText(Integer.toString(rec.getStock()));
        formRel.setText(Integer.toString(rec.getReOrderLevel()));
    }

    public void onSave(ActionEvent actionEvent) {
        int barcode = Integer.parseInt(formBarcode.getText());
        String product = formProduct.getText();
        double price = Double.parseDouble(formPrice.getText());
        int stock = Integer.parseInt(formStock.getText());
        int rel = Integer.parseInt(formRel.getText());

        if(barcode == rec.getBarcode() && product.equals(rec.getProduct()) && price == rec.getPrice() && stock == rec.getStock() && rel == rec.getReOrderLevel()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No fields Changed!");
            alert.show();
        }else{
            Connect.executeAction("Update products set product="+"'"+product+"',price="+"'"+price+"',stock="+"'"+stock+"',reOrderLevel="+"'"+rel+"' where barcode="+"'"+barcode+"'");
            Stage stage = (Stage) formRel.getScene().getWindow();
            stage.close();
        }

    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) formRel.getScene().getWindow();
        stage.close();
    }
}
