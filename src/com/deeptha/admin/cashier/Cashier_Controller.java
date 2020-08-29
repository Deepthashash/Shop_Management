package com.deeptha.admin.cashier;

import com.deeptha.database.Connect;
import com.deeptha.services.Bills;
import com.deeptha.services.Record;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class Cashier_Controller implements Initializable {

    @FXML
    private TableView<Users> adminCashierTable;
    @FXML
    private TableColumn<Users, Integer> userID;
    @FXML
    private TableColumn<Users, String> userName;

    public TextField searchAdmin;

    Connect connection = Connect.getInstance();
    ResultSet results = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        getData();
    }

    void initColumns(){
        userID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
    }

    void getData(){
        results = Connect.executeQuery("Select * from users");
        showData();
    }

    void showData(){
        ObservableList<Users> list2 = FXCollections.observableArrayList();
        try {
            String searchText = searchAdmin.getText().toLowerCase();
            int userID;
            String userName;

            if(searchText.isEmpty() || searchText == null ){
                while (results.next()){
                    userID = results.getInt("userID");
                    userName = results.getString("userName");
                    Users obj = new Users();
                    obj.setUserID(userID);
                    obj.setUserName(userName);

                    list2.add(obj);
                }
            }else{
                try {
                    int uid = Integer.parseInt(searchText);
                    while (results.next()){
                        if(results.getInt("userID") == uid){

                            userID = results.getInt("userID");
                            userName = results.getString("userName");
                            Users obj = new Users();
                            obj.setUserID(userID);
                            obj.setUserName(userName);

                            list2.add(obj);
                        }else continue;
                    }
                }catch (Exception e){

                }
                while (results.next()){
                    if(results.getString("userName").toLowerCase().contains(searchText)){

                        userID = results.getInt("userID");
                        userName = results.getString("userName");
                        Users obj = new Users();
                        obj.setUserID(userID);
                        obj.setUserName(userName);

                        list2.add(obj);
                    }else continue;
                }
            }
            adminCashierTable.getItems().setAll(list2);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void delete(ActionEvent actionEvent) {
        Users selected = adminCashierTable.getSelectionModel().getSelectedItem();

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
                Connect.executeAction("Delete from users where userID="+"'"+selected.getUserID()+"'");
                getData();
            }else{

            }
        }




    }

    public void edit(ActionEvent actionEvent) throws IOException {
        Users selected = adminCashierTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.NONE);
        if(selected == null){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("No field selected!");
            alert.show();
        }else {
            SelectedRecord.selectedUser = selected;
            Parent parent = FXMLLoader.load(getClass().getResource("/com/deeptha/admin/cashier/cashiersEditForm.fxml"));
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
        Parent parent = FXMLLoader.load(getClass().getResource("/com/deeptha/admin/cashier/cashiersAddForm.fxml"));
        Stage stage =new Stage(StageStyle.DECORATED);
        stage.setTitle("Add Form");
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public void refresh(ActionEvent actionEvent) {
        getData();
    }

}
