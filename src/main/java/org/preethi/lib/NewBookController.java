package org.preethi.lib;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class NewBookController {

    @FXML private TextField idField;
    @FXML private TextField semesterField;
    @FXML private TextField enggMbaField;
    @FXML private TextField yearField;
    @FXML private TextField monthField;
    @FXML private DatePicker dateOfInvoiceField;
    @FXML private TextField purchaseTypeField;
    @FXML private TextField invoiceNoField;
    @FXML private TextField departmentSubjectField;
    @FXML private TextField bookAccnNoFromField;
    @FXML private TextField bookAccnNoToField;
    @FXML private TextField noOfBooksField;
    @FXML private TextField noOfBooksPurchasedField;
    @FXML private TextField noOfBooksDonatedField;
    @FXML private TextField accRegNoField;
    @FXML private TextField accnRegisterPageNoFromField;
    @FXML private TextField accnRegisterPageNoToField;
    @FXML private TextField discountPercentageField;
    @FXML private TextField grossInvoiceAmountField;
    @FXML private TextField discountAmountField;
    @FXML private TextField netAmountField;
    @FXML private Button saveButton;

    private static final String URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "root";
    private static final String PASSWORD = "Admin@38";

    @FXML
    private void handleEnter(KeyEvent event) {
        Node source = (Node) event.getSource(); // Get the current field
        if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DOWN) {
            // Move to the next field
            source.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.TAB, false, false, false, false));
        } else if (event.getCode() == KeyCode.UP) {
            // Move to the previous field
            source.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.TAB, true, false, false, false));
        }
    }


        @FXML
    private void saveBook() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO book (id, semester, engg_mba, year, month, date_of_invoice, purchase_type, invoice_no, department_subject, " +
                             "book_accn_no_from, book_accn_no_to, no_of_books, no_of_books_purchased, no_of_books_donated, acc_reg_no, " +
                             "accn_register_page_no_from, accn_register_page_no_to, discount_percentage, gross_invoice_amount, " +
                             "discount_amount, net_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            statement.setInt(1, Integer.parseInt(idField.getText()));
            statement.setString(2, semesterField.getText());
            statement.setString(3, enggMbaField.getText());
            statement.setString(4, yearField.getText());
            statement.setString(5, monthField.getText());
            statement.setString(6, dateOfInvoiceField.getValue().toString());
            statement.setString(7, purchaseTypeField.getText());
            statement.setString(8, invoiceNoField.getText());
            statement.setString(9, departmentSubjectField.getText());
            statement.setString(10, bookAccnNoFromField.getText());
            statement.setString(11, bookAccnNoToField.getText());
            statement.setInt(12, Integer.parseInt(noOfBooksField.getText()));
            statement.setInt(13, Integer.parseInt(noOfBooksPurchasedField.getText()));
            statement.setInt(14, Integer.parseInt(noOfBooksDonatedField.getText()));
            statement.setString(15, accRegNoField.getText());
            statement.setString(16, accnRegisterPageNoFromField.getText());
            statement.setString(17, accnRegisterPageNoToField.getText());
            statement.setDouble(18, Double.parseDouble(discountPercentageField.getText()));
            statement.setDouble(19, Double.parseDouble(grossInvoiceAmountField.getText()));
            statement.setDouble(20, Double.parseDouble(discountAmountField.getText()));
            statement.setDouble(21, Double.parseDouble(netAmountField.getText()));

            statement.executeUpdate();
            showAlert("Record added successfully!");
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error saving record!");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Library Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        idField.clear();
        semesterField.clear();
        enggMbaField.clear();
        yearField.clear();
        monthField.clear();
        dateOfInvoiceField.setValue(null);
        purchaseTypeField.clear();
        invoiceNoField.clear();
        departmentSubjectField.clear();
        bookAccnNoFromField.clear();
        bookAccnNoToField.clear();
        noOfBooksField.clear();
        noOfBooksPurchasedField.clear();
        noOfBooksDonatedField.clear();
        accRegNoField.clear();
        accnRegisterPageNoFromField.clear();
        accnRegisterPageNoToField.clear();
        discountPercentageField.clear();
        grossInvoiceAmountField.clear();
        discountAmountField.clear();
        netAmountField.clear();
    }
}
