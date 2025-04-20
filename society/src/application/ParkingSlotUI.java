package application;


import dao.ParkingSlotDAO;
import model.ParkingSlot;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;


import java.sql.SQLException;
import java.util.List;


public class ParkingSlotUI {
    private ParkingSlotDAO dao = new ParkingSlotDAO();
    private ObservableList<ParkingSlot> slotList = FXCollections.observableArrayList();
    
    private TextField slotNumberField = new TextField();
    private ComboBox<String> vehicleTypeCombo = new ComboBox<>();
    private TableView<ParkingSlot> tableView = new TableView<>();


    public Node createContent() {
        // Initialize ComboBox
        vehicleTypeCombo.getItems().addAll("2-wheeler", "4-wheeler");
        vehicleTypeCombo.setValue("2-wheeler");


        // Setup TableView
        TableColumn<ParkingSlot, String> slotNumberCol = new TableColumn<>("Slot Number");
        slotNumberCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSlotNumber()));
        
        TableColumn<ParkingSlot, String> vehicleTypeCol = new TableColumn<>("Vehicle Type");
        vehicleTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVehicleType()));
        
        tableView.getColumns().addAll(slotNumberCol, vehicleTypeCol);
        tableView.setItems(slotList);
        
        // Load initial data
        refreshTable();


        // Input Form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));
        
        form.add(new Label("Slot Number:"), 0, 0);
        form.add(slotNumberField, 1, 0);
        form.add(new Label("Vehicle Type:"), 0, 1);
        form.add(vehicleTypeCombo, 1, 1);


        // Buttons
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> handleAdd());
        
        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> handleUpdate());
        
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> handleDelete());
        
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearFields());
        
        HBox buttons = new HBox(10, addButton, updateButton, deleteButton, clearButton);
        
        // TableView selection handler
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                slotNumberField.setText(newSelection.getSlotNumber());
                vehicleTypeCombo.setValue(newSelection.getVehicleType());
                slotNumberField.setDisable(true); // Prevent editing primary key
            }
        });


        // Layout
        VBox content = new VBox(10, form, buttons, tableView);
        content.setPadding(new Insets(10));
        
        return content;
    }


    private void handleAdd() {
        try {
            ParkingSlot slot = new ParkingSlot(slotNumberField.getText(), vehicleTypeCombo.getValue());
            dao.addParkingSlot(slot);
            refreshTable();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Parking slot added successfully.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add parking slot: " + e.getMessage());
        }
    }


    private void handleUpdate() {
        try {
            ParkingSlot slot = new ParkingSlot(slotNumberField.getText(), vehicleTypeCombo.getValue());
            dao.updateParkingSlot(slot);
            refreshTable();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Parking slot updated successfully.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update parking slot: " + e.getMessage());
        }
    }


    private void handleDelete() {
        try {
            String slotNumber = slotNumberField.getText();
            dao.deleteParkingSlot(slotNumber);
            refreshTable();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Parking slot deleted successfully.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete parking slot: " + e.getMessage());
        }
    }


    private void clearFields() {
        slotNumberField.clear();
        vehicleTypeCombo.setValue("2-wheeler");
        slotNumberField.setDisable(false);
    }


    private void refreshTable() {
        try {
            slotList.clear();
            List<ParkingSlot> slots = dao.getAllParkingSlots();
            slotList.addAll(slots);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load parking slots: " + e.getMessage());
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


