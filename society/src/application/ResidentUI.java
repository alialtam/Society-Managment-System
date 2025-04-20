package application;

import dao.ResidentDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Resident;
import util.AlertUtil;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class ResidentUI {
    private final ResidentDAO residentDAO = new ResidentDAO();
    private TableView<Resident> residentTable;
    
    // Form fields
    private final TextField idField = new TextField();
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField flatIdField = new TextField();
    private final TextField phoneField = new TextField();
    private final DatePicker moveInDatePicker = new DatePicker();
    
    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern FLAT_ID_PATTERN = Pattern.compile("^\\d+$");

    public VBox createContent() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(15));
        
        // Create table with scroll pane
        residentTable = createResidentTable();
        ScrollPane scrollPane = new ScrollPane(residentTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        // Create form
        GridPane form = createResidentForm();
        
        // Create buttons
        HBox buttonBox = createButtonBox();
        
        mainContainer.getChildren().addAll(
            new Label("Resident Management"),
            scrollPane,
            form,
            buttonBox
        );
        
        loadResidentData();
        return mainContainer;
    }

    private TableView<Resident> createResidentTable() {
        TableView<Resident> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // ID Column
        TableColumn<Resident, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("residentId"));
        
        // Name Column
        TableColumn<Resident, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getFirstName() + " " + cell.getValue().getLastName())
        );
        
        // Email Column
        TableColumn<Resident, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getEmail() != null ? 
                cell.getValue().getEmail() : "")
        );
        
        // Flat ID Column
        TableColumn<Resident, String> flatCol = new TableColumn<>("Flat ID");
        flatCol.setCellValueFactory(cell ->
            new SimpleStringProperty(
                cell.getValue().getFlatId() > 0 ? 
                String.valueOf(cell.getValue().getFlatId()) : "N/A")
        );
        
        // Phone Column
        TableColumn<Resident, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getPhone() != null ? 
                cell.getValue().getPhone() : "")
        );
        
        // Move-In Date Column
        TableColumn<Resident, LocalDate> dateCol = new TableColumn<>("Move-In Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("moveInDate"));
        
        table.getColumns().addAll(idCol, nameCol, emailCol, flatCol, phoneCol, dateCol);
        
        // Selection handler
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillFormWithSelectedResident(newVal);
            }
        });
        
        return table;
    }

    private GridPane createResidentForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        
        // Configure fields
        idField.setDisable(true);
        
        // Set text formatters for validation
        firstNameField.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().matches("[\\p{L} .'-]*") ? change : null
        ));
        
        lastNameField.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().matches("[\\p{L} .'-]*") ? change : null
        ));
        
        phoneField.setTextFormatter(new TextFormatter<>(change ->
            change.getControlNewText().matches("\\d{0,10}") ? change : null
        ));
        
        flatIdField.setTextFormatter(new TextFormatter<>(change ->
            change.getControlNewText().matches("\\d*") ? change : null
        ));
        
        // Add form elements
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("First Name*:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Last Name*:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Flat ID:"), 0, 4);
        grid.add(flatIdField, 1, 4);
        grid.add(new Label("Phone* (10 digits):"), 0, 5);
        grid.add(phoneField, 1, 5);
        grid.add(new Label("Move-In Date*:"), 0, 6);
        grid.add(moveInDatePicker, 1, 6);
        
        return grid;
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        Button clearButton = new Button("Clear");
        
        // Set button actions
        addButton.setOnAction(e -> addResident());
        updateButton.setOnAction(e -> updateResident());
        deleteButton.setOnAction(e -> deleteResident());
        clearButton.setOnAction(e -> clearForm());
        
        // Style buttons
        String buttonStyle = "-fx-pref-width: 100; -fx-pref-height: 30;";
        addButton.setStyle(buttonStyle);
        updateButton.setStyle(buttonStyle);
        deleteButton.setStyle(buttonStyle);
        clearButton.setStyle(buttonStyle);
        
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);
        return buttonBox;
    }

    private void loadResidentData() {
        try {
            List<Resident> residents = residentDAO.getAllResidents();
            ObservableList<Resident> residentData = FXCollections.observableArrayList(residents);
            residentTable.setItems(residentData);
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Loading Error", "Failed to load residents: " + e.getMessage());
        }
    }

    private void fillFormWithSelectedResident(Resident resident) {
        if (resident == null) return;
        
        idField.setText(String.valueOf(resident.getResidentId()));
        firstNameField.setText(resident.getFirstName());
        lastNameField.setText(resident.getLastName());
        emailField.setText(resident.getEmail() != null ? resident.getEmail() : "");
        flatIdField.setText(resident.getFlatId() > 0 ? String.valueOf(resident.getFlatId()) : "");
        phoneField.setText(resident.getPhone() != null ? resident.getPhone() : "");
        moveInDatePicker.setValue(resident.getMoveInDate());
    }

    private boolean validateForm() {
        // Validate required fields
        if (firstNameField.getText().trim().isEmpty()) {
            AlertUtil.showErrorAlert("Error", "First name is required");
            firstNameField.requestFocus();
            return false;
        }
        
        if (lastNameField.getText().trim().isEmpty()) {
            AlertUtil.showErrorAlert("Error", "Last name is required");
            lastNameField.requestFocus();
            return false;
        }
        
        // Validate name format
        if (!NAME_PATTERN.matcher(firstNameField.getText().trim()).matches()) {
            AlertUtil.showErrorAlert("Error", "First name contains invalid characters");
            firstNameField.requestFocus();
            return false;
        }
        
        if (!NAME_PATTERN.matcher(lastNameField.getText().trim()).matches()) {
            AlertUtil.showErrorAlert("Error", "Last name contains invalid characters");
            lastNameField.requestFocus();
            return false;
        }
        
        // Validate email if provided
        if (!emailField.getText().isEmpty() && !EMAIL_PATTERN.matcher(emailField.getText().trim()).matches()) {
            AlertUtil.showErrorAlert("Error", "Invalid email format");
            emailField.requestFocus();
            return false;
        }
        
        // Validate phone (required)
        if (phoneField.getText().trim().isEmpty()) {
            AlertUtil.showErrorAlert("Error", "Phone number is required");
            phoneField.requestFocus();
            return false;
        }
        
        if (!PHONE_PATTERN.matcher(phoneField.getText().trim()).matches()) {
            AlertUtil.showErrorAlert("Error", "Phone must be exactly 10 digits");
            phoneField.requestFocus();
            return false;
        }
        
        // Validate move-in date
        if (moveInDatePicker.getValue() == null) {
            AlertUtil.showErrorAlert("Error", "Move-in date is required");
            moveInDatePicker.requestFocus();
            return false;
        }
        
        // Validate Flat ID if provided
        if (!flatIdField.getText().isEmpty()) {
            if (!FLAT_ID_PATTERN.matcher(flatIdField.getText().trim()).matches()) {
                AlertUtil.showErrorAlert("Error", "Flat ID must be a positive number");
                flatIdField.requestFocus();
                return false;
            }
            
            try {
                int flatId = Integer.parseInt(flatIdField.getText());
                if (flatId <= 0) {
                    AlertUtil.showErrorAlert("Error", "Flat ID must be positive");
                    flatIdField.requestFocus();
                    return false;
                }
                
                if (!residentDAO.flatExists(flatId)) {
                    AlertUtil.showErrorAlert("Error", "Flat ID " + flatId + " doesn't exist");
                    flatIdField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                AlertUtil.showErrorAlert("Error", "Invalid Flat ID format");
                flatIdField.requestFocus();
                return false;
            }
        }
        
        return true;
    }

    private void addResident() {
        if (!validateForm()) return;
        
        try {
            Resident resident = new Resident(
                0, // ID will be auto-generated
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                emailField.getText().trim().isEmpty() ? null : emailField.getText().trim(),
                flatIdField.getText().isEmpty() ? 0 : Integer.parseInt(flatIdField.getText()),
                moveInDatePicker.getValue(),
                phoneField.getText().trim()
            );
            
            if (residentDAO.addResident(resident)) {
                AlertUtil.showAlert("Success", "Resident added successfully");
                loadResidentData();
                clearForm();
            } else {
                AlertUtil.showErrorAlert("Error", "Failed to add resident");
            }
        } catch (Exception e) {
            handleDatabaseError(e);
        }
    }

    private void updateResident() {
        if (idField.getText().isEmpty()) {
            AlertUtil.showErrorAlert("Error", "Please select a resident to update");
            return;
        }
        
        if (!validateForm()) return;
        
        try {
            Resident resident = new Resident(
                Integer.parseInt(idField.getText()),
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                emailField.getText().trim().isEmpty() ? null : emailField.getText().trim(),
                flatIdField.getText().isEmpty() ? 0 : Integer.parseInt(flatIdField.getText()),
                moveInDatePicker.getValue(),
                phoneField.getText().trim()
            );
            
            if (residentDAO.updateResident(resident)) {
                AlertUtil.showAlert("Success", "Resident updated successfully");
                loadResidentData();
            } else {
                AlertUtil.showErrorAlert("Error", "Failed to update resident");
            }
        } catch (Exception e) {
            handleDatabaseError(e);
        }
    }

    private void deleteResident() {
        if (idField.getText().isEmpty()) {
            AlertUtil.showErrorAlert("Error", "Please select a resident to delete");
            return;
        }
        
        try {
            int residentId = Integer.parseInt(idField.getText());
            
            if (!AlertUtil.showConfirmation("Confirm Delete",
                "Are you sure you want to delete resident #" + residentId + "?")) {
                return;
            }
            
            if (residentDAO.deleteResident(residentId)) {
                AlertUtil.showAlert("Success", "Resident deleted successfully");
                loadResidentData();
                clearForm();
            } else {
                AlertUtil.showErrorAlert("Error", "Failed to delete resident");
            }
        } catch (Exception e) {
            handleDatabaseError(e);
        }
    }

    private void handleDatabaseError(Exception e) {
        String errorMsg = e.getMessage();
        
        if (errorMsg.contains("foreign key constraint")) {
            AlertUtil.showErrorAlert("Database Error",
                "The specified Flat ID doesn't exist in the system");
        }
        else if (errorMsg.contains("unique constraint")) {
            AlertUtil.showErrorAlert("Database Error",
                "Phone number or email already exists in system");
        }
        else if (errorMsg.contains("Data too long")) {
            AlertUtil.showErrorAlert("Database Error",
                "One of the fields exceeds maximum length");
        }
        else {
            AlertUtil.showErrorAlert("Database Error",
                "Operation failed: " + errorMsg);
        }
        
        e.printStackTrace();
    }

    private void clearForm() {
        idField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        flatIdField.clear();
        phoneField.clear();
        moveInDatePicker.setValue(null);
        residentTable.getSelectionModel().clearSelection();
    }
}