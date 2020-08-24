package com.deeptha.cashier;

import com.deeptha.database.Connect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Cashier_Controller implements Initializable {

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
    public Button generateBill;
    public TextField total = null;
    public TextField paid = null;
    public TextField change = null;

    public TextField search = null;

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

    //table 2
    @FXML
    private TableView<Record> searchTable;
    @FXML
    private TableColumn<Record, Integer> searchBarcode;
    @FXML
    private TableColumn<Record, String> searchName;
    @FXML
    private TableColumn<Record, Double> searchPrice;
    @FXML
    private TableColumn<Record, Integer> searchStock;


    //local variables
    int bar;
    String proName;
    double proPrice;
    int proStock;
    int proQuantity;
    double totalPrice = 0.0;
    double paidAmount = 0.0;
    double changeAmount = 0.0;

    //
    public List<Record> records = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generateBill.setDisable(true);
        initColumns();
        initColumns2();
        paneTwoFunction();
    }

    void initColumns(){
        billBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        billPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        billProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
        billQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        billStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        billTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }

    void initColumns2(){
        searchBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        searchPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        searchName.setCellValueFactory(new PropertyValueFactory<>("product"));
        searchStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    //barcode search
    public void onType(ActionEvent actionEvent) {

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

    void clearBill(){
        totalPrice = 0.0;
        paidAmount = 0.0;
        changeAmount = 0.0;
        paid.setText(" ");
        change.setText(" ");
        records.clear();
        addToTable();
    }

    //add records list to the table
    void addToTable(){
        ObservableList<Record> list = FXCollections.observableArrayList(records);
        billTable.getItems().setAll(list);
        calculateTotal();
    }

    public void generateBill(ActionEvent actionEvent) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.NONE);
        if(records.isEmpty()){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("No items to bill!");
            alert.show();
        }else{
            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Are sure you want to generate the bill?");
            Optional<ButtonType> answer = alert.showAndWait();
            if(answer.get() == ButtonType.OK){
                Timestamp instant= Timestamp.from(Instant.now());
                Connect.executeAction("Insert into invoice (issuedTime,billTotal,paidAmount,changeAmount) values ("+"'"+instant+"'"+","+"'"+totalPrice+"'"+","+"'"+paidAmount+"'"+","+"'"+changeAmount+"'"+")");

                int id;
                ResultSet resultSet = Connect.executeQuery("SELECT LAST_INSERT_ID()");
                resultSet.next();
                id = resultSet.getInt(1);

                for(Record item : records){
                    Connect.executeAction("Insert into invoice_products values ("+"'"+id+"'"+","+"'"+item.getBarcode()+"'"+","+"'"+item.getQuantity()+"'"+","+"'"+item.getTotalPrice()+"'"+")");
                    int remain = item.getStock() - item.getQuantity();
                    Connect.executeAction("Update products set stock="+"'"+remain+"' where barcode="+"'"+item.getBarcode()+"'");
                }

                clearBill();
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Bill Generated");
                alert.show();
            }
        }
        generateBill.setDisable(true);
        checkStocks();
        paneTwoFunction();
    }

    public void cancel(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to cancel");
        Optional<ButtonType> answer = alert.showAndWait();
        if(answer.get() == ButtonType.OK){
            clearBill();
        }
    }

    void calculateTotal(){

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
        paidAmount = Double.parseDouble(paid.getText());
        changeAmount = paidAmount - Double.parseDouble(total.getText());
        if(changeAmount < 0){
            Alert alert = new Alert(Alert.AlertType.NONE);alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("Entered amount is less than the bill !");
            alert.show();
        }else{
            change.setText(Double.toString(changeAmount));
            generateBill.setDisable(false);
        }
    }

    //checking stocks
    void checkStocks() throws SQLException {
        ResultSet resultSet = Connect.executeQuery("Select * from products");
        List<Record> productsLow = new ArrayList<Record>();
        while(resultSet.next()){
            if(resultSet.getInt("stock") < resultSet.getInt("reOrderLevel")){
                Record record = new Record(resultSet.getInt("barcode"),resultSet.getString("product"),resultSet.getInt("stock"));
                productsLow.add(record);
            }
        }
        if(!productsLow.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String p = "";
            for(Record pro : productsLow){
                p = p + pro.getProduct() + "\n";
            }
            alert.setContentText(p);
            alert.show();
        }
    }

    //Pane 2
    void paneTwoFunction(){
        ResultSet results = Connect.executeQuery("Select * from products");
        if(results != null){
            ObservableList<Record> list2 = FXCollections.observableArrayList();
            try {
                String searchText = search.getText().toLowerCase();
                int searchBarcode;
                String searchName;
                Double searchPrice;
                int searchStock;

                if(searchText.isEmpty() || searchText == null ){
                    while (results.next()){
                        searchBarcode = results.getInt("barcode");
                        searchName = results.getString("product");
                        searchPrice = results.getDouble("price");
                        searchStock = results.getInt("stock");

                        list2.add(new Record(searchBarcode,searchName,searchPrice,searchStock));
                    }
                }else{
                    while (results.next()){
                        if(results.getString("product").toLowerCase().contains(searchText)){

                            searchBarcode = results.getInt("barcode");
                            searchName = results.getString("product");
                            searchPrice = results.getDouble("price");
                            searchStock = results.getInt("stock");

                            list2.add(new Record(searchBarcode,searchName,searchPrice,searchStock));
                        }else continue;
                    }
                }
                searchTable.getItems().setAll(list2);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            System.out.println("Error");
        }
    }


    public void search(ActionEvent actionEvent) {
        paneTwoFunction();
    }

    public void addFromSearch(ActionEvent actionEvent) {

        Record selected = searchTable.getSelectionModel().getSelectedItem();

        product.setText(selected.getProduct());
        price.setText(Double.toString(selected.getPrice()));
        stock.setText(Integer.toString(selected.getStock()));

        proName = selected.getProduct();
        proPrice = selected.getPrice();
        proStock = selected.getStock();

        add.setDisable(false);
    }
}
