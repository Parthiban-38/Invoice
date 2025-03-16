package org.preethi.lib;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryController {

    @FXML
    private MenuButton semesterMenu, yearMenu, purchaseMenu, departmentMenu;

    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableColumn<Book, Integer> idColumn;
    @FXML
    private TableColumn<Book, String> semesterColumn, departmentSubjectColumn, purchaseTypeColumn;
    @FXML
    private TableColumn<Book, String> invoiceNoColumn, supplierColumn;
    @FXML
    private TableColumn<Book, Double> netAmountColumn;

    @FXML
    private Label selectedCriteriaLabel;

    private ObservableList<Book> searchResults = FXCollections.observableArrayList();

    private static final String URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "root";
    private static final String PASSWORD = "Admin@38";

    @FXML
    public void initialize() {
        semesterMenu.getItems().forEach(item -> ((CheckMenuItem) item).setOnAction(event -> handleSearch()));
        yearMenu.getItems().forEach(item -> ((CheckMenuItem) item).setOnAction(event -> handleSearch()));
        purchaseMenu.getItems().forEach(item -> ((CheckMenuItem) item).setOnAction(event -> handleSearch()));
        departmentMenu.getItems().forEach(item -> ((CheckMenuItem) item).setOnAction(event -> handleSearch()));

        // Initialize Table Columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        semesterColumn.setCellValueFactory(cellData -> cellData.getValue().semesterProperty());
        departmentSubjectColumn.setCellValueFactory(cellData -> cellData.getValue().departmentSubjectProperty());
        purchaseTypeColumn.setCellValueFactory(cellData -> cellData.getValue().purchaseTypeProperty());
        invoiceNoColumn.setCellValueFactory(cellData -> cellData.getValue().invoiceNoProperty());
        supplierColumn.setCellValueFactory(cellData -> cellData.getValue().supplierProperty());
        netAmountColumn.setCellValueFactory(cellData -> cellData.getValue().netAmountProperty().asObject());
    }

    @FXML
    private void handleSearch() {
        List<String> criteriaList = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        List<String> values = new ArrayList<>();

        // Update menu button text dynamically
        updateMenuButtonText(semesterMenu);
        updateMenuButtonText(yearMenu);
        updateMenuButtonText(purchaseMenu);
        updateMenuButtonText(departmentMenu);

        // Collect selected filters
        addSelectedCriteria(criteriaList, conditions, values, "Semester", semesterMenu, "semester");
        addSelectedCriteria(criteriaList, conditions, values, "Year", yearMenu, "year");
        addSelectedCriteria(criteriaList, conditions, values, "Purchase Type", purchaseMenu, "purchase_type");
        addSelectedCriteria(criteriaList, conditions, values, "Department", departmentMenu, "department_subject");

        selectedCriteriaLabel.setText(criteriaList.isEmpty() ? "None" : String.join(", ", criteriaList));

        if (conditions.isEmpty()) {
            showAlert("Please select at least one filter.", Alert.AlertType.WARNING);
            return;
        }

        // Construct the final SQL query
        String query = "SELECT id, semester, department_subject, purchase_type, invoiceno , nameofsupplier , netamount FROM lib2024";
        if (!conditions.isEmpty()) {
            query += " WHERE " + String.join(" AND ", conditions);
        }

        searchResults.clear();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (int i = 0; i < values.size(); i++) {
                statement.setString(i + 1, values.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                searchResults.add(new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("semester"),
                        resultSet.getString("department_subject"),
                        resultSet.getString("purchase_type"),
                        resultSet.getString("invoiceno"),
                        resultSet.getString("nameofsupplier"),
                        resultSet.getDouble("netamount")
                ));
            }

            booksTable.setItems(searchResults);
        } catch (SQLException e) {
            showAlert("Error fetching records: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void addSelectedCriteria(List<String> criteriaList, List<String> conditions, List<String> values,
                                     String label, MenuButton menu, String dbColumn) {
        List<String> selected = getSelectedFromMenu(menu);
        if (!selected.isEmpty()) {
            criteriaList.add(label + ": " + String.join("/", selected));

            // Correcting SQL IN clause formatting
            String placeholders = selected.stream().map(s -> "?").collect(Collectors.joining(", "));
            conditions.add(dbColumn + " IN (" + placeholders + ")");
            values.addAll(selected);
        }
    }

    private void updateMenuButtonText(MenuButton menuButton) {
        List<String> selectedItems = getSelectedFromMenu(menuButton);
        if (selectedItems.isEmpty()) {
            menuButton.setText("Select " + menuButton.getText().split(" ")[1]);
        } else {
            menuButton.setText(String.join(", ", selectedItems));
        }
    }

    private List<String> getSelectedFromMenu(MenuButton menu) {
        List<String> selected = new ArrayList<>();
        for (MenuItem item : menu.getItems()) {
            if (item instanceof CheckMenuItem checkItem && checkItem.isSelected()) {
                selected.add(checkItem.getText());
            }
        }
        return selected;
    }

    @FXML
    private void generateReport() {
        if (searchResults.isEmpty()) {
            showAlert("No search results to generate a report.", Alert.AlertType.WARNING);
            return;
        }

        String userHome = System.getProperty("user.home");
        String downloadsPath = userHome + File.separator + "Downloads";
        String fileName = "Library_Report_" + System.currentTimeMillis() + ".pdf";
        File file = new File(downloadsPath, fileName);

        try (PdfWriter writer = new PdfWriter(new FileOutputStream(file));
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Library Search Report")
                    .setBold()
                    .setFontSize(16)
                    .setUnderline()
                    .setMarginBottom(10));

            float[] columnWidths = {50f, 100f, 150f, 100f, 100f, 100f, 100f};
            Table table = new Table(UnitValue.createPointArray(columnWidths)).useAllAvailableWidth();

            String[] headers = {"ID", "Semester", "Department", "Purchase Type", "Invoice No", "Supplier", "Net Amount"};
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
            }

            for (Book book : searchResults) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getId()))));
                table.addCell(new Cell().add(new Paragraph(book.getSemester())));
                table.addCell(new Cell().add(new Paragraph(book.getDepartmentSubject())));
                table.addCell(new Cell().add(new Paragraph(book.getPurchaseType())));
                table.addCell(new Cell().add(new Paragraph(book.getInvoiceNo())));
                table.addCell(new Cell().add(new Paragraph(book.getSupplier())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getNetAmount()))));
            }

            document.add(table);
            showAlert("Report successfully saved: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            showAlert("Error generating PDF: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
