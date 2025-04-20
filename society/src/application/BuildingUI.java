package application;

import dao.BuildingDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Building;
import util.AlertUtil;
import java.util.List;
import java.util.regex.Pattern;

public class BuildingUI {
    private final BuildingDAO buildingDAO = new BuildingDAO();
    private TableView<Building> buildingTable;
    
    // Form fields
    private final TextField idField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField flatsField = new TextField();
    
    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}0-9 .'-]+$");
    private static final Pattern FLATS_PATTERN = Pattern.compile("^[1-9]\\d*$"); // Positive integers only

    public VBox createContent() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        // Create table
        buildingTable = createBuildingTable();
        
        // Create form for add/edit
        GridPane form = createBuildingForm();
        
        // Create buttons
        HBox buttonBox = createButtonBox();
        
        vbox.getChildren().addAll(buildingTable, form, buttonBox);
        loadBuildingData();
        
        return vbox;
    }
    
    private TableView<Building> createBuildingTable() {
        TableView<Building> table = new TableView<>();
        
        TableColumn<Building, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("buildingId"));
        
        TableColumn<Building, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Building, Integer> flatsCol = new TableColumn<>("Total Flats");
        flatsCol.setCellValueFactory(new PropertyValueFactory<>("totalFlats"));
        
        table.getColumns().addAll(idCol, nameCol, flatsCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Add listener for table selection
        table.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    fillFormWithSelectedBuilding(newSelection);
                }
            });
        
        return table;
    }
    
    private GridPane createBuildingForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        
        // Configure fields
        idField.setDisable(true);
        
        // Set text formatters for validation
        nameField.setTextFormatter(new TextFormatter<>(change -> 
            change.getControlNewText().matches("[\\p{L}0-9 .'-]*") ? change : null
        ));
        
        flatsField.setTextFormatter(new TextFormatter<>(change ->
            change.getControlNewText().matches("\\d*") ? change : null
        ));
        
        // Add form elements
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name*:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Total Flats*:"), 0, 2);
        grid.add(flatsField, 1, 2);
        
        return grid;
    }
    
    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        Button clearButton = new Button("Clear");
        
        // Set button styles
        String buttonStyle = "-fx-pref-width: 80; -fx-pref-height: 30;";
        addButton.setStyle(buttonStyle);
        updateButton.setStyle(buttonStyle);
        deleteButton.setStyle(buttonStyle);
        clearButton.setStyle(buttonStyle);
        
        // Set button actions
        addButton.setOnAction(e -> addBuilding());
        updateButton.setOnAction(e -> updateBuilding());
        deleteButton.setOnAction(e -> deleteBuilding());
        clearButton.setOnAction(e -> clearForm());
        
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);
        return buttonBox;
    }
    
    private void loadBuildingData() {
        try {
            List<Building> buildings = buildingDAO.getAllBuildings();
            ObservableList<Building> buildingData = FXCollections.observableArrayList(buildings);
            buildingTable.setItems(buildingData);
        } catch (Exception e) {
            AlertUtil.showErrorAlert("Loading Error", "Failed to load buildings: " + e.getMessage());
        }
    }
    
    private void fillFormWithSelectedBuilding(Building building) {
        idField.setText(String.valueOf(building.getBuildingId()));
        nameField.setText(building.getName());
        flatsField.setText(String.valueOf(building.getTotalFlats()));
    }
    
    private boolean validateForm() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty()) {
            AlertUtil.showErrorAlert("Error", "Building name is required");
            nameField.requestFocus();
            return false;
        }
        
        if (flatsField.getText().trim().isEmpty()) {
            AlertUtil.showErrorAlert("Error", "Total flats is required");
            flatsField.requestFocus();
            return false;
        }
        
        // Validate name format
        if (!NAME_PATTERN.matcher(nameField.getText().trim()).matches()) {
            AlertUtil.showErrorAlert("Error", "Building name contains invalid characters");
            nameField.requestFocus();
            return false;
        }
        
        // Validate total flats
        if (!FLATS_PATTERN.matcher(flatsField.getText().trim()).matches()) {
            AlertUtil.showErrorAlert("Error", "Total flats must be a positive number");
            flatsField.requestFocus();
            return false;
        }
        
        try {
            int flats = Integer.parseInt(flatsField.getText().trim());
            if (flats <= 0) {
                AlertUtil.showErrorAlert("Error", "Total flats must be greater than 0");
                flatsField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Error", "Invalid number format for total flats");
            flatsField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void addBuilding() {
        if (!validateForm()) return;
        
        try {
            Building building = new Building(
                0, // ID will be auto-generated
                nameField.getText().trim(),
                Integer.parseInt(flatsField.getText().trim())
            );
            
            boolean success = buildingDAO.addBuilding(building);
            
            if (success) {
                AlertUtil.showAlert("Success", "Building added successfully");
                loadBuildingData();
                clearForm();
            } else {
                AlertUtil.showErrorAlert("Error", "Failed to add building");
            }
        } catch (Exception e) {
            handleDatabaseError(e);
        }
    }
    
    private void updateBuilding() {
        if (idField.getText().isEmpty()) {
            AlertUtil.showErrorAlert("Error", "Please select a building to update");
            return;
        }
        
        if (!validateForm()) return;
        
        try {
            Building building = new Building(
                Integer.parseInt(idField.getText()),
                nameField.getText().trim(),
                Integer.parseInt(flatsField.getText().trim())
            );
            
            boolean success = buildingDAO.updateBuilding(building);
            
            if (success) {
                AlertUtil.showAlert("Success", "Building updated successfully");
                loadBuildingData();
                clearForm();
            } else {
                AlertUtil.showErrorAlert("Error", "Failed to update building");
            }
        } catch (Exception e) {
            handleDatabaseError(e);
        }
    }
    
    private void deleteBuilding() {
        if (idField.getText().isEmpty()) {
            AlertUtil.showErrorAlert("Error", "Please select a building to delete");
            return;
        }
        
        try {
            int id = Integer.parseInt(idField.getText());
            
            if (!AlertUtil.showConfirmation("Confirm Delete",
                "Are you sure you want to delete building #" + id + "?")) {
                return;
            }
            
            boolean success = buildingDAO.deleteBuilding(id);
            
            if (success) {
                AlertUtil.showAlert("Success", "Building deleted successfully");
                loadBuildingData();
                clearForm();
            } else {
                AlertUtil.showErrorAlert("Error", "Failed to delete building");
            }
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Error", "Invalid building ID");
        } catch (Exception e) {
            handleDatabaseError(e);
        }
    }
    
    private void handleDatabaseError(Exception e) {
        String errorMsg = e.getMessage();
        
        if (errorMsg.contains("unique constraint")) {
            AlertUtil.showErrorAlert("Database Error",
                "A building with this name already exists");
        }
        else if (errorMsg.contains("foreign key constraint")) {
            AlertUtil.showErrorAlert("Database Error",
                "Cannot delete building - it contains existing flats");
        }
        else {
            AlertUtil.showErrorAlert("Database Error",
                "Operation failed: " + errorMsg);
        }
    }
    
    private void clearForm() {
        idField.clear();
        nameField.clear();
        flatsField.clear();
        buildingTable.getSelectionModel().clearSelection();
    }
}