package org.preethi.lib;

import javafx.beans.property.*;

public class Book {
    private final IntegerProperty id;
    private final StringProperty semester;
    private final StringProperty departmentSubject;
    private final StringProperty purchaseType;

    // Constructor
    public Book(int id, String semester, String departmentSubject, String purchaseType) {
        this.id = new SimpleIntegerProperty(id);
        this.semester = new SimpleStringProperty(semester);
        this.departmentSubject = new SimpleStringProperty(departmentSubject);
        this.purchaseType = new SimpleStringProperty(purchaseType);
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

    // Property Getters for JavaFX bindings
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

    // Setters
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
}
