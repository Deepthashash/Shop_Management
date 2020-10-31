package com.deeptha.cashier;

import com.deeptha.database.Connect;
import com.deeptha.services.Record;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.print.*;
import java.io.IOException;
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
    Double bHeight = 0.0;

    //
    public List<Record> records = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generateBill.setDisable(true);
        initColumns();
        initColumns2();
        paneTwoFunction();
    }

    void initColumns() {
        billBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        billPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        billProduct.setCellValueFactory(new PropertyValueFactory<>("product"));
        billQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        billStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        billTotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        barcode.requestFocus();
    }

    void initColumns2() {
        searchBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        searchPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        searchName.setCellValueFactory(new PropertyValueFactory<>("product"));
        searchStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    //barcode search
    public void onType(ActionEvent actionEvent) {

        bar = Integer.parseInt(barcode.getText());

        ResultSet results = Connect.executeQuery("Select * from products where barcode=" + "'" + bar + "'");
        if (results != null) {
//            rec = (Record) results;
            add.setDisable(false);
            try {
                while (results.next()) {
                    proName = results.getString("product");
                    proPrice = results.getDouble("price");
                    proStock = results.getInt("stock");
                    product.setText(proName);
                    price.setText(Double.toString(proPrice));
                    stock.setText(Integer.toString(proStock));
                }
                quantity.requestFocus();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        } else {
            System.out.println("Error");
        }
    }

    //adding a record to bill
    public void onAdd(ActionEvent actionEvent) {

        int containingQuantity = 0;
        proQuantity = Integer.parseInt(quantity.getText());

        for (Record r : records) {
            if (r.getBarcode() == bar) {
                containingQuantity = r.getQuantity();
                proQuantity = proQuantity + containingQuantity;
                records.remove(r);
            }
        }
        double fprice = proPrice * (double) proQuantity;

        Record rec = new Record(bar, proName, proPrice, proStock, proQuantity, fprice);
        records.add(rec);

        addToTable();
        clearFields();
        add.setDisable(true); //disabling the button
        barcode.requestFocus();
    }

    public void onDelete(ActionEvent actionEvent) {

        Record selected = billTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are sure you want to delete");
        Optional<ButtonType> answer = alert.showAndWait();

        if (answer.get() == ButtonType.OK) {
            records.remove(selected);
            addToTable();
        } else {

        }
    }

    void clearFields() {
        barcode.setText("");
        product.setText("");
        price.setText("");
        stock.setText("");
        quantity.setText("1");
    }

    void clearBill() {
        totalPrice = 0.0;
        paidAmount = 0.0;
        changeAmount = 0.0;
        paid.setText(" ");
        change.setText(" ");
        records.clear();
        addToTable();
    }

    //add records list to the table
    void addToTable() {
        ObservableList<Record> list = FXCollections.observableArrayList(records);
        billTable.getItems().setAll(list);
        calculateTotal();
    }

    public void generateBill(ActionEvent actionEvent) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.NONE);
        if (records.isEmpty()) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("No items to bill!");
            alert.show();
        } else {
            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Are sure you want to generate the bill?");
            Optional<ButtonType> answer = alert.showAndWait();
            if (answer.get() == ButtonType.OK) {
                Timestamp instant = Timestamp.from(Instant.now());
                bHeight = Double.valueOf(records.size());

                PrinterJob pj = PrinterJob.getPrinterJob();
                pj.setPrintable(new BillPrintable(), getPageFormat(pj));
                try {
                    pj.print();
                } catch (PrinterException ex) {
                    ex.printStackTrace();
                }
                Connect.executeAction("Insert into invoice (issuedTime,billTotal,paidAmount,changeAmount) values (" + "'" + instant + "'" + "," + "'" + totalPrice + "'" + "," + "'" + paidAmount + "'" + "," + "'" + changeAmount + "'" + ")");

                int id;
                ResultSet resultSet = Connect.executeQuery("SELECT LAST_INSERT_ID()");
                resultSet.next();
                id = resultSet.getInt(1);


                for (Record item : records) {
                    Connect.executeAction("Insert into invoice_products values (" + "'" + id + "'" + "," + "'" + item.getBarcode() + "'" + "," + "'" + item.getQuantity() + "'" + "," + "'" + item.getTotalPrice() + "'" + ")");
                    int remain = item.getStock() - item.getQuantity();
                    Connect.executeAction("Update products set stock=" + "'" + remain + "' where barcode=" + "'" + item.getBarcode() + "'");
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
        barcode.requestFocus();
    }


    public void cancel(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to cancel");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            clearBill();
            barcode.requestFocus();
        }
    }

    void calculateTotal() {

        if (records.isEmpty()) {
            totalPrice = 0.0;
        } else {
            for (Record r : records) {
                totalPrice = totalPrice + r.getTotalPrice();
            }
        }
        total.setText(Double.toString(totalPrice));
    }

    public void onEntered(ActionEvent actionEvent) {
        paidAmount = Double.parseDouble(paid.getText());
        changeAmount = paidAmount - Double.parseDouble(total.getText());
        if (changeAmount < 0) {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setHeaderText("Entered amount is less than the bill !");
            alert.show();
        } else {
            change.setText(Double.toString(changeAmount));
            generateBill.setDisable(false);
        }
    }

    //checking stocks
    void checkStocks() throws SQLException {
        ResultSet resultSet = Connect.executeQuery("Select * from products");
        List<Record> productsLow = new ArrayList<Record>();
        while (resultSet.next()) {
            if (resultSet.getInt("stock") < resultSet.getInt("reOrderLevel")) {
                Record record = new Record(resultSet.getInt("barcode"), resultSet.getString("product"), resultSet.getInt("stock"));
                productsLow.add(record);
            }
        }
        if (!productsLow.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            String p = "";
            for (Record pro : productsLow) {
                p = p + pro.getProduct() + "\n";
            }
            alert.setContentText(p);
            alert.show();
        }
    }

    //Pane 2
    void paneTwoFunction() {
        ResultSet results = Connect.executeQuery("Select * from products");
        if (results != null) {
            ObservableList<Record> list2 = FXCollections.observableArrayList();
            try {
                String searchText = search.getText().toLowerCase();
                int searchBarcode;
                String searchName;
                Double searchPrice;
                int searchStock;

                if (searchText.isEmpty() || searchText == null) {
                    while (results.next()) {
                        searchBarcode = results.getInt("barcode");
                        searchName = results.getString("product");
                        searchPrice = results.getDouble("price");
                        searchStock = results.getInt("stock");

                        list2.add(new Record(searchBarcode, searchName, searchPrice, searchStock));
                    }
                } else {
                    while (results.next()) {
                        if (results.getString("product").toLowerCase().contains(searchText)) {

                            searchBarcode = results.getInt("barcode");
                            searchName = results.getString("product");
                            searchPrice = results.getDouble("price");
                            searchStock = results.getInt("stock");

                            list2.add(new Record(searchBarcode, searchName, searchPrice, searchStock));
                        } else continue;
                    }
                }
                searchTable.getItems().setAll(list2);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
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

    public void logout(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to logout?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            Parent parent = FXMLLoader.load(getClass().getResource("/com/deeptha/login/login.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Add Form");
            stage.setScene(new Scene(parent));
            stage.show();

            Stage stage2 = (Stage) barcode.getScene().getWindow();
            stage2.close();
        } else {

        }

    }
    /////
    // Bill printing
    /////
    public PageFormat getPageFormat(PrinterJob pj) {

        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();

        double bodyHeight = bHeight;
        double headerHeight = 5.0;
        double footerHeight = 5.0;
        double width = cm_to_pp(8);
        double height = cm_to_pp(headerHeight + bodyHeight + footerHeight);
        paper.setSize(width, height);
        paper.setImageableArea(0, 10, width, height - cm_to_pp(1));

        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);

        return pf;
    }

    protected static double cm_to_pp(double cm) {
        return toPPI(cm * 0.393600787);
    }

    protected static double toPPI(double inch) {
        return inch * 72d;
    }

    public class BillPrintable implements Printable {
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
                throws PrinterException {

            int r = records.size();
//            ImageIcon icon = new ImageIcon("C:UsersccsDocumentsNetBeansProjectsvideo TestPOSInvoicesrcposinvoicemylogo.jpg");
            int result = NO_SUCH_PAGE;
            if (pageIndex == 0) {

                Graphics2D g2d = (Graphics2D) graphics;
                double width = pageFormat.getImageableWidth();
                g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());


                //  FontMetrics metrics=g2d.getFontMetrics(new Font("Arial",Font.BOLD,7));

                try {
                    int y = 20;
                    int yShift = 10;
                    int headerRectHeight = 15;
                    // int headerRectHeighta=40;


                    g2d.setFont(new Font("Monospaced", Font.PLAIN, 9));
//                    g2d.drawImage(icon.getImage(), 50, 20, 90, 30,  );
                    y += yShift + 30;
                    g2d.drawString("-------------------------------------", 12, y);
                    y += yShift;
                    g2d.drawString("         Madura Stores        ", 12, y);
                    y += yShift;
                    g2d.drawString("         Singhasiripura       ", 12, y);
                    y += yShift;
                    g2d.drawString("         Maduruketiya         ", 12, y);
                    y += yShift;
                    g2d.drawString("         Monaragala           ", 12, y);
                    y += yShift;
                    g2d.drawString("         +94553129267         ", 12, y);
                    y += yShift;
                    g2d.drawString("-------------------------------------", 12, y);
                    y += headerRectHeight;

                    g2d.drawString(" Item Name                  Price   ", 10, y);
                    y += yShift;
                    g2d.drawString("-------------------------------------", 10, y);
                    y += headerRectHeight;

                    for (int s = 0; s < r; s++) {
                        g2d.drawString(" " + records.get(s).getProduct() + "                            ", 10, y);
                        y += yShift;
                        g2d.drawString("      " + records.get(s).getQuantity() + " * " + records.get(s).getPrice(), 10, y);
                        g2d.drawString(""+records.get(s).getTotalPrice(), 160, y);
                        y += yShift;

                    }

                    g2d.drawString("-------------------------------------", 10, y);
                    y += yShift;
                    g2d.drawString(" Total amount:               " + totalPrice + "   ", 10, y);
                    y += yShift;
                    g2d.drawString("-------------------------------------", 10, y);
                    y += yShift;
                    g2d.drawString(" Cash      :                 " +  paidAmount+ "   ", 10, y);
                    y += yShift;
                    g2d.drawString("-------------------------------------", 10, y);
                    y += yShift;
                    g2d.drawString(" Balance   :                 " + changeAmount + "   ", 10, y);
                    y += yShift;

                    g2d.drawString("*************************************", 10, y);
                    y += yShift;
                    g2d.drawString("       THANK YOU COME AGAIN            ", 10, y);
                    y += yShift;
                    g2d.drawString("*************************************", 10, y);
                    y += yShift;
                    g2d.drawString("       SOFTWARE BY: Selicon          ", 10, y);
                    y += yShift;
                    g2d.drawString("   CONTACT: deepthashashimina@gmail.com       ", 10, y);
                    y += yShift;


                } catch (Exception e) {
                    e.printStackTrace();
                }

                result = PAGE_EXISTS;
            }
            return result;
        }

    }
}
