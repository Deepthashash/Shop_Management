package com.deeptha.admin.products;

import com.deeptha.database.Connect;
import com.deeptha.services.Record;
import com.deeptha.services.SelectedRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class Products_Controller implements Initializable {

    @FXML
    private TableView<Record> adminProductTable;
    @FXML
    private TableColumn<Record, Integer> bar;
    @FXML
    private TableColumn<Record, String> pro;
    @FXML
    private TableColumn<Record, Double> pri;
    @FXML
    private TableColumn<Record, Integer> sto;
    @FXML
    private TableColumn<Record, Integer> rel;

    public TextField searchAdmin;

    Connect connection = Connect.getInstance();
    ResultSet results = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        getData();
    }

    void initColumns(){
        bar.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        pro.setCellValueFactory(new PropertyValueFactory<>("product"));
        pri.setCellValueFactory(new PropertyValueFactory<>("price"));
        sto.setCellValueFactory(new PropertyValueFactory<>("stock"));
        rel.setCellValueFactory(new PropertyValueFactory<>("reOrderLevel"));
    }

    void getData(){
        results = Connect.executeQuery("Select * from products");
        showData();
    }

    void showData(){
        ObservableList<Record> list2 = FXCollections.observableArrayList();
        try {
            String searchText = searchAdmin.getText().toLowerCase();
            int searchBarcode;
            String searchName;
            Double searchPrice;
            int searchStock;
            int searchRel;

            if(searchText.isEmpty() || searchText == null ){
                while (results.next()){
                    searchBarcode = results.getInt("barcode");
                    searchName = results.getString("product");
                    searchPrice = results.getDouble("price");
                    searchStock = results.getInt("stock");
                    searchRel = results.getInt("reOrderLevel");

                    list2.add(new Record(searchBarcode,searchName,searchPrice,searchStock,searchRel));
                }
            }else{
                try {
                    int barcodeVal = Integer.parseInt(searchText);
                    while (results.next()){
                        if(results.getInt("barcode") == barcodeVal){

                            searchBarcode = results.getInt("barcode");
                            searchName = results.getString("product");
                            searchPrice = results.getDouble("price");
                            searchStock = results.getInt("stock");
                            searchRel = results.getInt("reOrderLevel");

                            list2.add(new Record(searchBarcode,searchName,searchPrice,searchStock,searchRel));
                        }else continue;
                    }
                }catch (Exception e){

                }
                while (results.next()){
                    if(results.getString("product").toLowerCase().contains(searchText)){

                        searchBarcode = results.getInt("barcode");
                        searchName = results.getString("product");
                        searchPrice = results.getDouble("price");
                        searchStock = results.getInt("stock");
                        searchRel = results.getInt("reOrderLevel");

                        list2.add(new Record(searchBarcode,searchName,searchPrice,searchStock,searchRel));
                    }else continue;
                }
            }
            adminProductTable.getItems().setAll(list2);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(ActionEvent actionEvent) {
        Record selected = adminProductTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.NONE);
        if(selected == null){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("No field selected!");
            alert.show();
        }else{
            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Are sure you want to delete");
            Optional<ButtonType> answer = alert.showAndWait();
            if(answer.get() == ButtonType.OK){
                Connect.executeAction("Delete from products where barcode="+"'"+selected.getBarcode()+"'");
                getData();
            }else{

            }
        }




    }

    public void edit(ActionEvent actionEvent) throws IOException {
        Record selected = adminProductTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.NONE);
        if(selected == null){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("No field selected!");
            alert.show();
        }else {
            SelectedRecord.rec = selected;
            Parent parent = FXMLLoader.load(getClass().getResource("/com/deeptha/admin/products/productsEditForm.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Form");
            stage.setScene(new Scene(parent));
            stage.show();
        }
    }

    public void onSearch(ActionEvent actionEvent) {
        getData();
    }

    public void onAdd(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/com/deeptha/admin/products/productsAddForm.fxml"));
        Stage stage =new Stage(StageStyle.DECORATED);
        stage.setTitle("Add Form");
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public void refresh(ActionEvent actionEvent) {
        getData();
    }

}
