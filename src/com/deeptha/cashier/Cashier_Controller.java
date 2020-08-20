package com.deeptha.cashier;

import com.deeptha.database.Connect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Cashier_Controller {

    //get instance of database connection
    Connect connection = Connect.getInstance();

    //getting an object of Record

    //scene variables
    public TextField barcode = null;
    public Label product = null;
    public Label price = null;
    public Label stock = null;
    public TextField quantity = null;
    public Button add;
    public TextField total = null;
    public TextField paid = null;
    public TextField change = null;

    //table controller
    @FXML
    private TableView<Record> billTable;
    @FXML
    private TableColumn<Record, Integer> billBarcode;
    @FXML
    private TableColumn<Record, String> billProduct;
    @FXML
    private TableColumn<Record, Double> billPrice;
    @FXML
    private TableColumn<Record, Integer> billStock;
    @FXML
    private TableColumn<Record, Integer> billQuantity;
    @FXML
    private TableColumn<Record, Double> billTotalPrice;


    //local variables
    int bar;
    String proName;
    double proPrice;
    int proStock;
    int proQuantity;

    //
    public List<Record> records = new ArrayList<>();


    //barcode search
    public void onType(ActionEvent actionEvent) {
        billBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        billPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        billProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
        billQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        billStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        billTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        bar = Integer.parseInt(barcode.getText());

        ResultSet results = Connect.executeQuery("Select * from products where barcode="+"'"+bar+"'");
        if(results != null){
//            rec = (Record) results;
            add.setDisable(false);
            try {
                while (results.next()){
                    proName = results.getString("product");
                    proPrice = results.getDouble("price");
                    proStock = results.getInt("stock");
                    product.setText(proName);
                    price.setText(Double.toString(proPrice));
                    stock.setText(Integer.toString(proStock));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            System.out.println("Error");
        }
    }

    //adding a record to bill
    public void onAdd(ActionEvent actionEvent) {
        
        int containingQuantity = 0;
        proQuantity = Integer.parseInt(quantity.getText());

        for(Record r : records){
            if(r.getBarcode() == bar){
                containingQuantity = r.getQuantity();
                proQuantity = proQuantity + containingQuantity;
                records.remove(r);
            }
        }
        double fprice = proPrice * (double) proQuantity;

        Record rec = new Record(bar,proName,proPrice,proStock,proQuantity,fprice);
        records.add(rec);

        addToTable();
        clearFields();
        add.setDisable(true); //disabling the button
    }

    public void onDelete(ActionEvent actionEvent) {

        Record selected = billTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are sure you want to delete");
        Optional<ButtonType> answer = alert.showAndWait();

        if(answer.get() == ButtonType.OK){
            records.remove(selected);
            addToTable();
        }else{

        }
    }

    void clearFields(){
        barcode.setText("");
        product.setText("");
        price.setText("");
        stock.setText("");
        quantity.setText("1");
    }

    //add records list to the table
    void addToTable(){
        ObservableList<Record> list = FXCollections.observableArrayList(records);
        billTable.getItems().setAll(list);
        calculateTotal();
    }

    public void generateBill(ActionEvent actionEvent) {
    }

    public void cancel(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to cancel");
        Optional<ButtonType> answer = alert.showAndWait();
        if(answer.get() == ButtonType.OK){
            paid.setText(" ");
            change.setText(" ");
            records.clear();
            addToTable();
        }
    }

    void calculateTotal(){

        double totalPrice = 0.0;
        if(records.isEmpty()){
            totalPrice = 0.0;
        }else{
            for(Record r : records){
                totalPrice = totalPrice + r.getTotalPrice();
            }
        }
        total.setText(Double.toString(totalPrice));
    }

    public void onEntered(ActionEvent actionEvent) {
        double paidAmount = Double.parseDouble(paid.getText());
        double changeAmount = paidAmount - Double.parseDouble(total.getText());
        change.setText(Double.toString(changeAmount));
    }
}