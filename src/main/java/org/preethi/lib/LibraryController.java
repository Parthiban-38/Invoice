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

public class LibraryController {

    @FXML
    private ComboBox<String> searchCriteriaBox, semesterTypeBox, yearBox;

    @FXML
    private MenuButton departmentMenu, purchaseMenu;

    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableColumn<Book, Integer> idColumn;
    @FXML
    private TableColumn<Book, String> semesterColumn, departmentSubjectColumn, purchaseTypeColumn;

    private ObservableList<Book> searchResults = FXCollections.observableArrayList();

    private static final String URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "root";  // Change to your actual MySQL username
    private static final String PASSWORD = "Admin@38";  // Change to your actual password


    @FXML
    public void initialize() {
        searchCriteriaBox.setItems(FXCollections.observableArrayList("SEMESTER", "PURCHASE TYPE", "DEPARTMENT"));
        semesterTypeBox.setDisable(true);
        yearBox.setDisable(true);

        searchCriteriaBox.setOnAction(event -> updateSubCriteria());
        semesterTypeBox.setOnAction(event -> updateYearOptions());
        yearBox.setOnAction(event -> handleSearch());

        // Initialize Table Columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        semesterColumn.setCellValueFactory(cellData -> cellData.getValue().semesterProperty());
        departmentSubjectColumn.setCellValueFactory(cellData -> cellData.getValue().departmentSubjectProperty());
        purchaseTypeColumn.setCellValueFactory(cellData -> cellData.getValue().purchaseTypeProperty());

        // Add event listeners to CheckMenuItems inside MenuButton
        departmentMenu.getItems().forEach(item -> ((CheckMenuItem) item).setOnAction(event -> handleSearch()));
        purchaseMenu.getItems().forEach(item -> ((CheckMenuItem) item).setOnAction(event -> handleSearch()));
    }

    @FXML
    private void updateSubCriteria() {
        String selectedCategory = searchCriteriaBox.getValue();
        semesterTypeBox.setDisable(!"SEMESTER".equals(selectedCategory));
        yearBox.setDisable(true);
        semesterTypeBox.getSelectionModel().clearSelection();
        yearBox.getSelectionModel().clearSelection();

        if ("SEMESTER".equals(selectedCategory)) {
            semesterTypeBox.setItems(FXCollections.observableArrayList("ODD", "EVEN"));
        }
    }

    @FXML
    private void updateYearOptions() {
        String selectedSemester = semesterTypeBox.getValue();
        yearBox.setDisable(selectedSemester == null);

        if (selectedSemester != null) {
            yearBox.setItems(FXCollections.observableArrayList("2024", "2025"));
        }
    }

    @FXML
    private void handleSearch() {
        List<String> conditions = new ArrayList<>();
        List<String> values = new ArrayList<>();

        // Semester filter
        String selectedSemester = semesterTypeBox.getValue();
        String selectedYear = yearBox.getValue();
        if (selectedSemester != null && selectedYear != null) {
            conditions.add("semester LIKE ?");
            values.add(selectedSemester + " (" + selectedYear + ")");
        }

        // Purchase Type filter
        List<String> selectedPurchaseTypes = getSelectedFromMenu(purchaseMenu);
        if (!selectedPurchaseTypes.isEmpty()) {
            String placeholders = String.join(",", selectedPurchaseTypes.stream().map(p -> "?").toArray(String[]::new));
            conditions.add("purchase_type IN (" + placeholders + ")");
            values.addAll(selectedPurchaseTypes);
        }

        // Department filter
        List<String> selectedDepartments = getSelectedFromMenu(departmentMenu);
        if (!selectedDepartments.isEmpty()) {
            String placeholders = String.join(",", selectedDepartments.stream().map(d -> "?").toArray(String[]::new));
            conditions.add("department_subject IN (" + placeholders + ")");
            values.addAll(selectedDepartments);
        }

        if (conditions.isEmpty()) {
            showAlert("Please select at least one filter.", Alert.AlertType.WARNING);
            return;
        }

        searchResults.clear();
        String query = "SELECT * FROM book WHERE " + String.join(" AND ", conditions);

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
                        resultSet.getString("purchase_type")
                ));
            }

            booksTable.setItems(searchResults);
        } catch (SQLException e) {
            showAlert("Error fetching records: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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

            float[] columnWidths = {50f, 100f, 150f, 100f};
            Table table = new Table(UnitValue.createPointArray(columnWidths)).useAllAvailableWidth();

            String[] headers = {"ID", "Semester", "Department", "Purchase Type"};
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
            }

            for (Book book : searchResults) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(book.getId()))));
                table.addCell(new Cell().add(new Paragraph(book.getSemester())));
                table.addCell(new Cell().add(new Paragraph(book.getDepartmentSubject())));
                table.addCell(new Cell().add(new Paragraph(book.getPurchaseType())));
            }

            document.add(table);
            showAlert("Report successfully saved: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            showAlert("Error generating PDF: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private List<String> getSelectedFromMenu(MenuButton menu) {
        List<String> selected = new ArrayList<>();
        for (MenuItem item : menu.getItems()) {
            CheckMenuItem checkItem = (CheckMenuItem) item;
            if (checkItem.isSelected()) {
                selected.add(checkItem.getText());
            }
        }
        return selected;
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
