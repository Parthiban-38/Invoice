package org.preethi.lib;

import javafx.beans.property.*;

public class Book {
    private final IntegerProperty id;
    private final StringProperty semester;
    private final StringProperty departmentSubject;
    private final StringProperty purchaseType;
    private final StringProperty invoiceNo;
    private final StringProperty supplier;
    private final DoubleProperty netAmount;

    // Updated Constructor with new fields
    public Book(int id, String semester, String departmentSubject, String purchaseType,
                String invoiceNo, String supplier, double netAmount) {
        this.id = new SimpleIntegerProperty(id);
        this.semester = new SimpleStringProperty(semester);
        this.departmentSubject = new SimpleStringProperty(departmentSubject);
        this.purchaseType = new SimpleStringProperty(purchaseType);
        this.invoiceNo = new SimpleStringProperty(invoiceNo);
        this.supplier = new SimpleStringProperty(supplier);
        this.netAmount = new SimpleDoubleProperty(netAmount);
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public String getSemester() {
        return semester.get();
    }

    public String getDepartmentSubject() {
        return departmentSubject.get();
    }

    public String getPurchaseType() {
        return purchaseType.get();
    }

    public String getInvoiceNo() {
        return invoiceNo.get();
    }

    public String getSupplier() {
        return supplier.get();
    }

    public double getNetAmount() {
        return netAmount.get();
    }

    // Property Getters for TableView Binding
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty semesterProperty() {
        return semester;
    }

    public StringProperty departmentSubjectProperty() {
        return departmentSubject;
    }

    public StringProperty purchaseTypeProperty() {
        return purchaseType;
    }

    public StringProperty invoiceNoProperty() {
        return invoiceNo;
    }

    public StringProperty supplierProperty() {
        return supplier;
    }

    public DoubleProperty netAmountProperty() {
        return netAmount;
    }

    // Setters (if needed)
    public void setId(int id) {
        this.id.set(id);
    }

    public void setSemester(String semester) {
        this.semester.set(semester);
    }

    public void setDepartmentSubject(String departmentSubject) {
        this.departmentSubject.set(departmentSubject);
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType.set(purchaseType);
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo.set(invoiceNo);
    }

    public void setSupplier(String supplier) {
        this.supplier.set(supplier);
    }

    public void setNetAmount(double netAmount) {
        this.netAmount.set(netAmount);
    }
}
