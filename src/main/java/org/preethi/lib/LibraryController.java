package org.preethi.lib;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class LibraryController {

    @FXML
    private ComboBox<String> searchCriteriaBox;
    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableColumn<Book, Integer> idColumn;
    @FXML
    private TableColumn<Book, String> semesterColumn;
    @FXML
    private TableColumn<Book, String> enggMbaColumn;
    @FXML
    private TableColumn<Book, Integer> yearColumn;
    @FXML
    private TableColumn<Book, String> monthColumn;
    @FXML
    private TableColumn<Book, String> dateOfInvoiceColumn;
    @FXML
    private TableColumn<Book, String> purchaseTypeColumn;
    @FXML
    private TableColumn<Book, String> invoiceNoColumn;
    @FXML
    private TableColumn<Book, String> departmentSubjectColumn;
    @FXML
    private TableColumn<Book, Integer> bookAccnNoFromColumn;
    @FXML
    private TableColumn<Book, Integer> bookAccnNoToColumn;
    @FXML
    private TableColumn<Book, Integer> noOfBooksColumn;
    @FXML
    private TableColumn<Book, Integer> noOfBooksPurchasedColumn;
    @FXML
    private TableColumn<Book, Integer> noOfBooksDonatedColumn;
    @FXML
    private TableColumn<Book, String> accRegNoColumn;
    @FXML
    private TableColumn<Book, Integer> accnRegisterPageNoFromColumn;
    @FXML
    private TableColumn<Book, Integer> accnRegisterPageNoToColumn;
    @FXML
    private TableColumn<Book, Double> discountPercentageColumn;
    @FXML
    private TableColumn<Book, Double> grossInvoiceAmountColumn;
    @FXML
    private TableColumn<Book, Double> discountAmountColumn;
    @FXML
    private TableColumn<Book, Double> netAmountColumn;

    private ObservableList<Book> searchResults = FXCollections.observableArrayList();

    private static final String URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "root";
    private static final String PASSWORD = "Admin@38";

    @FXML
    public void initialize() {
        searchCriteriaBox.setItems(FXCollections.observableArrayList("Semester", "Year", "Purchase Type", "Invoice No", "Department Subject"));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        enggMbaColumn.setCellValueFactory(new PropertyValueFactory<>("enggMba"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        dateOfInvoiceColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfInvoice"));
        purchaseTypeColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseType"));
        invoiceNoColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceNo"));
        departmentSubjectColumn.setCellValueFactory(new PropertyValueFactory<>("departmentSubject"));
        bookAccnNoFromColumn.setCellValueFactory(new PropertyValueFactory<>("bookAccnNoFrom"));
        bookAccnNoToColumn.setCellValueFactory(new PropertyValueFactory<>("bookAccnNoTo"));
        noOfBooksColumn.setCellValueFactory(new PropertyValueFactory<>("noOfBooks"));
        noOfBooksPurchasedColumn.setCellValueFactory(new PropertyValueFactory<>("noOfBooksPurchased"));
        noOfBooksDonatedColumn.setCellValueFactory(new PropertyValueFactory<>("noOfBooksDonated"));
        accRegNoColumn.setCellValueFactory(new PropertyValueFactory<>("accRegNo"));
        accnRegisterPageNoFromColumn.setCellValueFactory(new PropertyValueFactory<>("accnRegisterPageNoFrom"));
        accnRegisterPageNoToColumn.setCellValueFactory(new PropertyValueFactory<>("accnRegisterPageNoTo"));
        discountPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("discountPercentage"));
        grossInvoiceAmountColumn.setCellValueFactory(new PropertyValueFactory<>("grossInvoiceAmount"));
        discountAmountColumn.setCellValueFactory(new PropertyValueFactory<>("discountAmount"));
        netAmountColumn.setCellValueFactory(new PropertyValueFactory<>("netAmount"));
    }

    @FXML
    private void handleSearch() {
        String selectedCriteria = searchCriteriaBox.getValue();
        if (selectedCriteria == null) {
            showAlert("Please select a search criteria.");
            return;
        }

        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Search");
        inputDialog.setHeaderText("Enter " + selectedCriteria + " to search:");
        inputDialog.setContentText(selectedCriteria + ":");
        inputDialog.showAndWait().ifPresent(input -> fetchBooks(selectedCriteria, input));
    }
    @FXML
    private void openNewForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("new-book-form.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("New Form");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Error loading new form: " + e.getMessage());
        }
    }

    private void fetchBooks(String criteria, String value) {
        String columnName = switch (criteria) {
            case "Semester" -> "semester";
            case "Year" -> "year";
            case "Purchase Type" -> "purchase_type";
            case "Invoice No" -> "invoice_no";
            case "Department Subject" -> "department_subject";
            default -> throw new IllegalArgumentException("Invalid search criteria");
        };

        searchResults.clear();
        String query = "SELECT * FROM book WHERE " + columnName + " LIKE ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + value + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                searchResults.add(new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("semester"),
                        resultSet.getString("engg_mba"),
                        resultSet.getInt("year"),
                        resultSet.getString("month"),
                        resultSet.getString("date_of_invoice"),
                        resultSet.getString("purchase_type"),
                        resultSet.getString("invoice_no"),
                        resultSet.getString("department_subject"),
                        resultSet.getInt("book_accn_no_from"),
                        resultSet.getInt("book_accn_no_to"),
                        resultSet.getInt("no_of_books"),
                        resultSet.getInt("no_of_books_purchased"),
                        resultSet.getInt("no_of_books_donated"),
                        resultSet.getString("acc_reg_no"),
                        resultSet.getInt("accn_register_page_no_from"),
                        resultSet.getInt("accn_register_page_no_to"),
                        resultSet.getDouble("discount_percentage"),
                        resultSet.getDouble("gross_invoice_amount"),
                        resultSet.getDouble("discount_amount"),
                        resultSet.getDouble("net_amount")
                ));
            }

            booksTable.setItems(searchResults);
        } catch (SQLException e) {
            showAlert("Error fetching records: " + e.getMessage());
        }
    }

    @FXML
    private void generateReport() {
        if (searchResults == null || searchResults.isEmpty()) {
            showAlert("No search results to generate a report.", Alert.AlertType.WARNING);
            return;
        }

        // Get user's default Downloads folder
        String userHome = System.getProperty("user.home");
        String downloadsPath = userHome + File.separator + "Downloads";
        String fileName = "Library_Report_" + System.currentTimeMillis() + ".pdf";
        File file = new File(downloadsPath, fileName);

        try {
            System.out.println("Creating PDF file at: " + file.getAbsolutePath());

            try (PdfWriter writer = new PdfWriter(new FileOutputStream(file));
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                document.add(new Paragraph("Library Search Report")
                        .setBold()
                        .setFontSize(16)
                        .setUnderline()
                        .setMarginBottom(10));

                float[] columnWidths = {30f, 60f, 60f, 30f, 40f, 60f, 60f, 60f, 60f, 40f, 40f, 40f, 40f, 40f, 60f, 40f, 40f, 40f, 60f, 60f, 60f};
                Table table = new Table(UnitValue.createPointArray(columnWidths)).useAllAvailableWidth();

                String[] headers = {"ID", "Semester", "Engg/MBA", "Year", "Month", "Date of Invoice",
                        "Purchase Type", "Invoice No", "Department", "Accn No From", "Accn No To",
                        "Books Count", "Purchased", "Donated", "Acc Reg No", "Page No From",
                        "Page No To", "Discount %", "Gross Amount", "Discount Amt", "Net Amount"};

                for (String header : headers) {
                    table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
                }

                for (Book book : searchResults) {
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getId()))));
                    table.addCell(new Cell().add(new Paragraph(book.getSemester())));
                    table.addCell(new Cell().add(new Paragraph(book.getEnggMba())));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getYear()))));
                    table.addCell(new Cell().add(new Paragraph(book.getMonth())));
                    table.addCell(new Cell().add(new Paragraph(book.getDateOfInvoice())));
                    table.addCell(new Cell().add(new Paragraph(book.getPurchaseType())));
                    table.addCell(new Cell().add(new Paragraph(book.getInvoiceNo())));
                    table.addCell(new Cell().add(new Paragraph(book.getDepartmentSubject())));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getBookAccnNoFrom()))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getBookAccnNoTo()))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getNoOfBooks()))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getNoOfBooksPurchased()))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getNoOfBooksDonated()))));
                    table.addCell(new Cell().add(new Paragraph(book.getAccRegNo())));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getAccnRegisterPageNoFrom()))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getAccnRegisterPageNoTo()))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getDiscountPercentage()))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getGrossInvoiceAmount()))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getDiscountAmount()))));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getNetAmount()))));
                }

                document.add(table);
                System.out.println("PDF created successfully: " + file.getAbsolutePath());
            }

            // Check if the file was created successfully before displaying success message
            if (file.exists()) {
                showAlert("Report successfully saved to Downloads folder:\n" + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error: File was not created.", Alert.AlertType.ERROR);
            }

        } catch (IOException e) {
            showAlert("Error generating PDF: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Generic alert method with different alert types
    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }







    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
