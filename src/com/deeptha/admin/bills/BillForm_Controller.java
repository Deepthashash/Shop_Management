package com.deeptha.admin.bills;

import com.deeptha.database.Connect;
import com.deeptha.services.Record;
import com.deeptha.services.SelectedRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class BillForm_Controller implements Initializable {
    @FXML
    private TableView<Record> billFormTable;
    @FXML
    private TableColumn<Record, Integer> bar;
    @FXML
    private TableColumn<Record, String> pro;
    @FXML
    private TableColumn<Record, Integer> qua;
    @FXML
    private TableColumn<Record, Double> pri;

    public Label billNumber = null;
    public Label total = null;
    public Label paid = null;
    public Label change = null;

    Connect connection = Connect.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        getData();
    }

    void initColumns(){
        bar.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        pro.setCellValueFactory(new PropertyValueFactory<>("product"));
        qua.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        pri.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }

    void getData(){
        ObservableList<Record> list = FXCollections.observableArrayList();
        int billNo = SelectedRecord.billRec.getId();
        billNumber.setText(Integer.toString(billNo));
        total.setText(Double.toString(SelectedRecord.billRec.getTotal()));
        paid.setText(Double.toString(SelectedRecord.billRec.getPaid()));
        change.setText(Double.toString(SelectedRecord.billRec.getChange()));
        ResultSet resultSet = Connect.executeQuery("Select i.barcode,product,numberOfItems,i.price from invoice_products i Join products p on i.barcode = p.barcode where invoiceID = "+"'"+billNo+"' ");
        try{
            while (resultSet.next()){
                list.add(new Record(resultSet.getInt("barcode"),resultSet.getString("product"),resultSet.getInt("numberOfItems"), resultSet.getDouble("price")));
            }
        }catch (Exception e){

        }
        billFormTable.getItems().setAll(list);
    }

    public void onOk(ActionEvent actionEvent) {
        Stage stage = (Stage) billNumber.getScene().getWindow();
        stage.close();
    }
}
