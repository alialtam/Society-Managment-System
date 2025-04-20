package application;
import dao.FlatDAO;
import model.Flat;
import model.Flat.FlatType;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import java.sql.SQLException;
import java.util.List;
public class FlatUI {
   private FlatDAO dao = new FlatDAO();
   private ObservableList<Flat> flatList = FXCollections.observableArrayList();
  
   private TextField flatIDField = new TextField();
   private TextField flatNumberField = new TextField();
   private TextField buildingIDField = new TextField();
   private ComboBox<FlatType> flatTypeCombo = new ComboBox<>();
   private TextField slotNumberField = new TextField();
   private TableView<Flat> tableView = new TableView<>();
   public Node createContent() {
       // Initialize ComboBox
       flatTypeCombo.getItems().addAll(FlatType.values());
       flatTypeCombo.setValue(FlatType.BHK1);
       // Setup TextFormatters for integer fields
       flatIDField.setTextFormatter(new TextFormatter<>(change -> {
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
       // Setup TableView
       TableColumn<Flat, Number> flatIDCol = new TableColumn<>("Flat ID");
       flatIDCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getFlatID()));
      
       TableColumn<Flat, String> flatNumberCol = new TableColumn<>("Flat Number");
       flatNumberCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlatNumber()));
      
       TableColumn<Flat, Number> buildingIDCol = new TableColumn<>("Building ID");
       buildingIDCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBuildingID()));
      
       TableColumn<Flat, String> flatTypeCol = new TableColumn<>("Flat Type");
       flatTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlatType().getDbValue()));
      
       TableColumn<Flat, String> slotNumberCol = new TableColumn<>("Slot Number");
       slotNumberCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSlotNumber()));
      
       tableView.getColumns().addAll(flatIDCol, flatNumberCol, buildingIDCol, flatTypeCol, slotNumberCol);
       tableView.setItems(flatList);
      
       // Load initial data
       refreshTable();
       // Input Form
       GridPane form = new GridPane();
       form.setHgap(10);
       form.setVgap(10);
       form.setPadding(new Insets(10));
      
       form.add(new Label("Flat ID:"), 0, 0);
       form.add(flatIDField, 1, 0);
       form.add(new Label("Flat Number:"), 0, 1);
       form.add(flatNumberField, 1, 1);
       form.add(new Label("Building ID:"), 0, 2);
       form.add(buildingIDField, 1, 2);
       form.add(new Label("Flat Type:"), 0, 3);
       form.add(flatTypeCombo, 1, 3);
       form.add(new Label("Slot Number:"), 0, 4);
       form.add(slotNumberField, 1, 4);
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
               flatIDField.setText(String.valueOf(newSelection.getFlatID()));
               flatNumberField.setText(newSelection.getFlatNumber());
               buildingIDField.setText(String.valueOf(newSelection.getBuildingID()));
               flatTypeCombo.setValue(newSelection.getFlatType());
               slotNumberField.setText(newSelection.getSlotNumber());
           }
       });
       // Layout
       VBox content = new VBox(10, form, buttons, tableView);
       content.setPadding(new Insets(10));
      
       return content;
   }
   private void handleAdd() {
       try {
           int flatID = Integer.parseInt(flatIDField.getText());
           int buildingID = Integer.parseInt(buildingIDField.getText());
           Flat flat = new Flat(flatID, flatNumberField.getText(), buildingID, flatTypeCombo.getValue(), slotNumberField.getText());
           dao.insertFlat(flat);
           refreshTable();
           clearFields();
           showAlert(Alert.AlertType.INFORMATION, "Success", "Flat added successfully.");
       } catch (NumberFormatException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "FlatID and BuildingID must be valid integers.");
       } catch (SQLException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "Failed to add flat: " + e.getMessage());
       }
   }
   private void handleUpdate() {
       try {
           String flatIDText = flatIDField.getText().trim();
           String buildingIDText = buildingIDField.getText().trim();
           if (flatIDText.isEmpty() || !flatIDText.matches("\\d+")) {
               throw new NumberFormatException("FlatID must be a valid integer.");
           }
           if (buildingIDText.isEmpty() || !buildingIDText.matches("\\d+")) {
               throw new NumberFormatException("BuildingID must be a valid integer.");
           }
           int flatID = Integer.parseInt(flatIDText);
           int buildingID = Integer.parseInt(buildingIDText);
           System.out.println("Updating Flat - FlatID: " + flatID + ", BuildingID: " + buildingID +
                            ", FlatNumber: " + flatNumberField.getText() +
                            ", SlotNumber: " + slotNumberField.getText()); // Debug output
           Flat flat = new Flat(flatID, flatNumberField.getText(), buildingID, flatTypeCombo.getValue(), slotNumberField.getText());
           if (dao.updateFlat(flat)) {
               refreshTable();
               clearFields();
               showAlert(Alert.AlertType.INFORMATION, "Success", "Flat updated successfully.");
           } else {
               showAlert(Alert.AlertType.ERROR, "Error", "No rows updated. Check FlatID.");
           }
       } catch (NumberFormatException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "FlatID and BuildingID must be valid integers: " + e.getMessage());
       } catch (SQLException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "Failed to update flat: " + e.getMessage());
       }
   }
   private void handleDelete() {
       try {
           int flatID = Integer.parseInt(flatIDField.getText());
           dao.deleteFlat(flatID);
           refreshTable();
           clearFields();
           showAlert(Alert.AlertType.INFORMATION, "Success", "Flat deleted successfully.");
       } catch (NumberFormatException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "FlatID must be a valid integer.");
       } catch (SQLException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete flat: " + e.getMessage());
       }
   }
   private void clearFields() {
       flatIDField.clear();
       flatNumberField.clear();
       buildingIDField.clear();
       flatTypeCombo.setValue(FlatType.BHK1);
       slotNumberField.clear();
   }
   private void refreshTable() {
       try {
           flatList.clear();
           List<Flat> flats = dao.getAllFlats();
           flatList.addAll(flats);
       } catch (SQLException e) {
           showAlert(Alert.AlertType.ERROR, "Error", "Failed to load flats: " + e.getMessage());
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


