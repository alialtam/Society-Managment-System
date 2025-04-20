package application;
import dao.CleanerDAO;
import model.Cleaner;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.util.List;
public class CleanerUI {
   private CleanerDAO dao = new CleanerDAO();
   private ObservableList<Cleaner> cleanerList = FXCollections.observableArrayList();
   private TextField cleanerIDField = new TextField();
   private TextField firstNameField = new TextField();
   private TextField lastNameField = new TextField();
   private TextField buildingIDField = new TextField();
   private TextField contactNumberField = new TextField();
   private TableView<Cleaner> tableView = new TableView<>();
   public Node createContent() {
       // Setup TextFormatters for integer fields
       cleanerIDField.setTextFormatter(new TextFormatter<>(change -> {
           String newText = change.getControlNewText();
           if (newText.matches("\\d*")) {
               return change;
           }
           return null; // Reject non-digit input
       }));
       buildingIDField.setTextFormatter(new TextFormatter<>(change -> {
           String newText = change.getControlNewText();
           if (newText.matches("\\d*")) {
               return change;
           }
           return null; // Reject non-digit input
       }));
       contactNumberField.setTextFormatter(new TextFormatter<>(change -> {
           String newText = change.getControlNewText();
           if (newText.matches("\\d{0,10}")) { // Allow only 0 to 10 digits
               return change;
           }
           return null; // Reject if more than 10 digits or non-digits
       }));
       firstNameField.setTextFormatter(new TextFormatter<>(change -> {
           String newText = change.getControlNewText();
           if (newText.matches("[a-zA-Z]*")) { // Allow only letters
               return change;
           }
           return null; // Reject non-letter input
       }));
       lastNameField.setTextFormatter(new TextFormatter<>(change -> {
           String newText = change.getControlNewText();
           if (newText.matches("[a-zA-Z]*")) { // Allow only letters
               return change;
           }
           return null; // Reject non-letter input
       }));
       // Setup TableView
       TableColumn<Cleaner, Number> cleanerIDCol = new TableColumn<>("Cleaner ID");
       cleanerIDCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCleanerID()));
       TableColumn<Cleaner, String> firstNameCol = new TableColumn<>("First Name");
       firstNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
       TableColumn<Cleaner, String> lastNameCol = new TableColumn<>("Last Name");
       lastNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
       TableColumn<Cleaner, Number> buildingIDCol = new TableColumn<>("Building ID");
       buildingIDCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBuildingID() != null ? cellData.getValue().getBuildingID() : 0));
       TableColumn<Cleaner, String> contactNumberCol = new TableColumn<>("Contact Number");
       contactNumberCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContactNumber()));
       tableView.getColumns().addAll(cleanerIDCol, firstNameCol, lastNameCol, buildingIDCol, contactNumberCol);
       tableView.setItems(cleanerList);
       // Load initial data
       refreshTable();
       // Input Form
       GridPane form = new GridPane();
       form.setHgap(10);
       form.setVgap(10);
       form.setPadding(new Insets(10));
       form.add(new Label("Cleaner ID:"), 0, 0);
       form.add(cleanerIDField, 1, 0);
       form.add(new Label("First Name:"), 0, 1);
       form.add(firstNameField, 1, 1);
       form.add(new Label("Last Name:"), 0, 2);
       form.add(lastNameField, 1, 2);
       form.add(new Label("Building ID:"), 0, 3);
       form.add(buildingIDField, 1, 3);
       form.add(new Label("Contact Number:"), 0, 4);
       form.add(contactNumberField, 1, 4);
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
               cleanerIDField.setText(String.valueOf(newSelection.getCleanerID()));
               firstNameField.setText(newSelection.getFirstName() != null ? newSelection.getFirstName() : "");
               lastNameField.setText(newSelection.getLastName() != null ? newSelection.getLastName() : "");
               buildingIDField.setText(newSelection.getBuildingID() != null ? String.valueOf(newSelection.getBuildingID()) : "");
               contactNumberField.setText(newSelection.getContactNumber() != null ? newSelection.getContactNumber() : "");
           }
       });
       // Layout
       VBox content = new VBox(10, form, buttons, tableView);
       content.setPadding(new Insets(10));
       return content;
   }
   private void handleAdd() {
       try {
           int cleanerID = Integer.parseInt(cleanerIDField.getText());
           Integer buildingID = buildingIDField.getText().isEmpty() ? null : Integer.parseInt(buildingIDField.getText());
           Cleaner cleaner = new Cleaner(cleanerID, firstNameField.getText(), lastNameField.getText(), buildingID, contactNumberField.getText());
           dao.insertCleaner(cleaner);
           refreshTable();
           clearFields();
           showAlert(Alert.AlertType.INFORMATION, "Success", "Cleaner added successfully.");
       } catch (NumberFormatException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "CleanerID and BuildingID must be valid integers.");
       } catch (SQLException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "Failed to add cleaner: " + e.getMessage());
       }
   }
   private void handleUpdate() {
       try {
           int cleanerID = Integer.parseInt(cleanerIDField.getText());
           Integer buildingID = buildingIDField.getText().isEmpty() ? null : Integer.parseInt(buildingIDField.getText());
           Cleaner cleaner = new Cleaner(cleanerID, firstNameField.getText(), lastNameField.getText(), buildingID, contactNumberField.getText());
           if (dao.updateCleaner(cleaner)) {
               refreshTable();
               clearFields();
               showAlert(Alert.AlertType.INFORMATION, "Success", "Cleaner updated successfully.");
           } else {
               showAlert(Alert.AlertType.ERROR, "Error", "No rows updated. Check CleanerID.");
           }
       } catch (NumberFormatException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "CleanerID and BuildingID must be valid integers.");
       } catch (SQLException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "Failed to update cleaner: " + e.getMessage());
       }
   }
   private void handleDelete() {
       try {
           int cleanerID = Integer.parseInt(cleanerIDField.getText());
           dao.deleteCleaner(cleanerID);
           refreshTable();
           clearFields();
           showAlert(Alert.AlertType.INFORMATION, "Success", "Cleaner deleted successfully.");
       } catch (NumberFormatException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "CleanerID must be a valid integer.");
       } catch (SQLException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete cleaner: " + e.getMessage());
       }
   }
   private void clearFields() {
       cleanerIDField.clear();
       firstNameField.clear();
       lastNameField.clear();
       buildingIDField.clear();
       contactNumberField.clear();
   }
   private void refreshTable() {
       try {
           cleanerList.clear();
           List<Cleaner> cleaners = dao.getAllCleaners();
           cleanerList.addAll(cleaners);
       } catch (SQLException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "Failed to load cleaners: " + e.getMessage());
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




