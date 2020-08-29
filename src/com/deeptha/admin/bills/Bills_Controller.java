package com.deeptha.admin.bills;

import com.deeptha.database.Connect;
import com.deeptha.services.Bills;
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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;

public class Bills_Controller implements Initializable {

    @FXML
    private TableView<Bills> adminBillTable;
    @FXML
    private TableColumn<Bills, Integer> id;
    @FXML
    private TableColumn<Bills, Timestamp> time;
    @FXML
    private TableColumn<Bills, Double> total;
    @FXML
    private TableColumn<Bills, Double> paid;
    @FXML
    private TableColumn<Bills, Double> change;

    public TextField searchBill;

    Connect connection = Connect.getInstance();
    ResultSet results = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        getData();
    }

    void initColumns(){
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        time.setCellValueFactory(new PropertyValueFactory<>("time"));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        paid.setCellValueFactory(new PropertyValueFactory<>("paid"));
        change.setCellValueFactory(new PropertyValueFactory<>("change"));
    }

    void getData(){
        results = Connect.executeQuery("Select * from invoice");
        showData();
    }

    void showData(){
        ObservableList<Bills> list2 = FXCollections.observableArrayList();

            String searchText = searchBill.getText();
            int searchId;
            Timestamp searchTime;
            Double searchTotal;
            Double searchPaid;
            Double searchChange;

            if(searchText.isEmpty() || searchText == null ){
                try{
                    while (results.next()){
                        searchId = results.getInt("invoiceID");
                        searchTime = results.getTimestamp("issuedTime");
                        searchTotal = results.getDouble("billTotal");
                        searchPaid = results.getDouble("paidAmount");
                        searchChange = results.getDouble("changeAmount");

                        Bills obj = new Bills();
                        obj.setId(searchId);
                        obj.setTime(searchTime);
                        obj.setTotal(searchTotal);
                        obj.setPaid(searchPaid);
                        obj.setChange(searchChange);

//                        LocalDate today = searchTime.toLocalDateTime().toLocalDate();
//                        LocalDate yes = today.minusDays(1);
//                        System.out.println(yes);
                        list2.add(obj);
                    }
                }catch (Exception e){

                }

            }else{
                int billsId = Integer.parseInt(searchText);

                try{
                    while (results.next()){
                        if(results.getInt("invoiceID") == billsId){

                            searchId = results.getInt("invoiceID");
                            searchTime = results.getTimestamp("issuedTime");
                            searchTotal = results.getDouble("billTotal");
                            searchPaid = results.getDouble("paidAmount");
                            searchChange = results.getDouble("changeAmount");

                            Bills obj = new Bills();
                            obj.setId(searchId);
                            obj.setTime(searchTime);
                            obj.setTotal(searchTotal);
                            obj.setPaid(searchPaid);
                            obj.setChange(searchChange);

                            list2.add(obj);
                        }else continue;
                    }
                }catch (Exception e){

                }

    }
        adminBillTable.getItems().setAll(list2);
    }



    public void edit(ActionEvent actionEvent) throws IOException {
//

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

    public void showBill(ActionEvent actionEvent) throws IOException {
        Bills selected = adminBillTable.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.NONE);
        if(selected == null){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("No field selected!");
            alert.show();
        }else {
            SelectedRecord.billRec = selected;
            Parent parent = FXMLLoader.load(getClass().getResource("/com/deeptha/admin/bills/billForm.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Bill");
            stage.setScene(new Scene(parent));
            stage.show();
        }
    }
}
