package com.deeptha.admin.sales;

import com.deeptha.database.Connect;
import com.deeptha.services.Bills;
import com.deeptha.services.SelectedRecord;
import com.deeptha.services.Users;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class Sales_Controller implements Initializable {

    @FXML
    private TableView<Bills> adminSalesTable;
    @FXML
    private TableColumn<Bills, LocalDate> date;
    @FXML
    private TableColumn<Bills, Double> total;

    public Label todayTotal;

    Connect connection = Connect.getInstance();
    ResultSet results = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        getData();
    }

    void initColumns(){
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    void getData() {
        results = Connect.executeQuery("Select * from invoice");
//        LocalDate today = LocalDate.now();
//        LocalDate yes = today.minusDays(1);
//        LocalDate tom = today.plusDays(1);
//        Double sum = 0.0;
//        try{
//            while(results.next()){
//                Timestamp tm = results.getTimestamp("issuedTime");
//                if(tm.toLocalDateTime().toLocalDate().equals(today) ){
//                    sum = sum + results.getDouble("billTotal");
//                }
//            }
//        }catch (Exception e){
//
//        }
//
//        todayTotal.setText(Double.toString(sum));
        showData();
    }

    void showData(){
        ObservableList<Bills> list = FXCollections.observableArrayList();
        LocalDate today = LocalDate.now();
        Double sum = 0.0;
        try{
            while(results.next()){
                System.out.println(results.getTimestamp("issuedTime"));
                if(results.getTimestamp("issuedTime").toLocalDateTime().toLocalDate().equals(today)){
                    sum = sum + results.getDouble("billTotal");
                }else{
                    Double sum2 = 0.0;
                    LocalDate temp = results.getTimestamp("issuedTime").toLocalDateTime().toLocalDate();
//                    System.out.println(temp);
                    while(results.getTimestamp("issuedTime").toLocalDateTime().toLocalDate().equals(temp) ){
                        sum2 = sum2 + results.getDouble("billTotal");
//                        System.out.println(sum2);
                        if(!results.next()){
                            break;
                        }
                    }
//                    System.out.println(sum2);
                    Bills obj = new Bills();
                    obj.setDate(temp);
                    obj.setTotal(sum2);
                    list.add(obj);
                    results.previous();
                }
            }
        }catch(Exception e){

        }
        todayTotal.setText(Double.toString(sum));
        adminSalesTable.getItems().setAll(list);
    }

}
