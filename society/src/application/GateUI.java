package application;
import dao.GateDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Gate;
import util.AlertUtil;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
public class GateUI {
   private GateDAO gateDAO = new GateDAO();
   private TableView<Gate> gateTable;
  
   public VBox createContent() {
       VBox vbox = new VBox(10);
       vbox.setPadding(new Insets(10));
      
       // Create table
       gateTable = createGateTable();
      
       // Create form
       GridPane form = createGateForm();
      
       // Create buttons
       HBox buttonBox = createButtonBox();
      
       vbox.getChildren().addAll(gateTable, form, buttonBox);
       loadGateData();
      
       return vbox;
   }
  
   private TableView<Gate> createGateTable() {
       TableView<Gate> table = new TableView<>();
      
       TableColumn<Gate, Integer> numberCol = new TableColumn<>("Gate Number");
       numberCol.setCellValueFactory(new PropertyValueFactory<>("gateNumber"));
      
       TableColumn<Gate, String> descCol = new TableColumn<>("Description");
       descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
      
       table.getColumns().addAll(numberCol, descCol);
       table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      
       // Add listener for table selection
       table.getSelectionModel().selectedItemProperty().addListener(
           (obs, oldSelection, newSelection) -> {
               if (newSelection != null) {
                   fillFormWithSelectedGate(newSelection);
               }
           });
      
       return table;
   }
  
   private GridPane createGateForm() {
       GridPane grid = new GridPane();
       grid.setHgap(10);
       grid.setVgap(10);
       grid.setPadding(new Insets(10));
      
       TextField numberField = new TextField();
       numberField.setTextFormatter(new TextFormatter<>(change -> {
           String newText = change.getControlNewText();
           if (newText.matches("\\d*")) { // Allow only digits
               return change;
           }
           return null; // Reject non-digit input
       }));
       TextField descField = new TextField();
      
       grid.add(new Label("Gate Number:"), 0, 0);
       grid.add(numberField, 1, 0);
       grid.add(new Label("Description:"), 0, 1);
       grid.add(descField, 1, 1);
      
       // Store references to form fields
       grid.setUserData(new TextField[]{numberField, descField});
      
       return grid;
   }
  
   private HBox createButtonBox() {
       HBox buttonBox = new HBox(10);
       Button addButton = new Button("Add");
       Button updateButton = new Button("Update");
       Button deleteButton = new Button("Delete");
       Button clearButton = new Button("Clear");
      
       addButton.setOnAction(e -> addGate());
       updateButton.setOnAction(e -> updateGate());
       deleteButton.setOnAction(e -> deleteGate());
       clearButton.setOnAction(e -> clearForm());
      
       buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);
       return buttonBox;
   }
  
   private void loadGateData() {
       try {
           List<Gate> gates = gateDAO.getAllGates();
           ObservableList<Gate> gateData = FXCollections.observableArrayList(gates);
           gateTable.setItems(gateData);
       } catch (SQLException e) {
           AlertUtil.showErrorAlert("Database Error", "Failed to load gates: " + e.getMessage());
       }
   }
  
   private void fillFormWithSelectedGate(Gate gate) {
       TextField[] fields = getFormFields();
       fields[0].setText(String.valueOf(gate.getGateNumber()));
       fields[1].setText(gate.getDescription());
   }
  
   private void clearForm() {
       TextField[] fields = getFormFields();
       for (TextField field : fields) {
           field.clear();
       }
       gateTable.getSelectionModel().clearSelection();
   }
  
   private void addGate() {
       TextField[] fields = getFormFields();
       String numberStr = fields[0].getText();
       String description = fields[1].getText();
      
       if (numberStr.isEmpty() || description.isEmpty()) {
           AlertUtil.showErrorAlert("Error", "Please fill all fields");
           return;
       }
      
       try {
           int gateNumber = Integer.parseInt(numberStr);
           Gate gate = new Gate(gateNumber, description);
          
           if (gateDAO.insertGate(gate)) {
               AlertUtil.showAlert("Success", "Gate added successfully");
               loadGateData();
               clearForm();
           } else {
               AlertUtil.showErrorAlert("Error", "Failed to add gate");
           }
       } catch (NumberFormatException e) {
           AlertUtil.showErrorAlert("Error", "Gate number must be a valid number");
       } catch (IllegalArgumentException | SQLException e) {
           AlertUtil.showErrorAlert("Error", e.getMessage());
       }
   }
  
   private void updateGate() {
       TextField[] fields = getFormFields();
       String numberStr = fields[0].getText();
       String description = fields[1].getText();
      
       if (numberStr.isEmpty() || description.isEmpty()) {
           AlertUtil.showErrorAlert("Error", "Please fill all fields");
           return;
       }
      
       try {
           int gateNumber = Integer.parseInt(numberStr);
           Gate gate = new Gate(gateNumber, description);
          
           // Check if the gate exists before updating
           Optional<Gate> existingGate = gateDAO.getGateByNumber(gateNumber);
           if (existingGate == null) {
               AlertUtil.showErrorAlert("Error", "Gate number does not exist");
               return;
           }
          
           if (gateDAO.updateGate(gate)) {
               AlertUtil.showAlert("Success", "Gate updated successfully");
               loadGateData();
           } else {
               AlertUtil.showErrorAlert("Error", "Failed to update gate");
           }
       } catch (NumberFormatException e) {
           AlertUtil.showErrorAlert("Error", "Gate number must be a valid number");
       } catch (IllegalArgumentException | SQLException e) {
           AlertUtil.showErrorAlert("Error", e.getMessage());
       }
   }
  
   private void deleteGate() {
       TextField[] fields = getFormFields();
       String numberStr = fields[0].getText();
      
       if (numberStr.isEmpty()) {
           AlertUtil.showErrorAlert("Error", "Please select a gate to delete");
           return;
       }
      
       try {
           int gateNumber = Integer.parseInt(numberStr);
          
           if (gateDAO.deleteGate(gateNumber)) {
               AlertUtil.showAlert("Success", "Gate deleted successfully");
               loadGateData();
               clearForm();
           } else {
               AlertUtil.showErrorAlert("Error", "Failed to delete gate");
           }
       } catch (NumberFormatException e) {
           AlertUtil.showErrorAlert("Error", "Gate number must be a valid number");
       } catch (SQLException e) {
           AlertUtil.showErrorAlert("Database Error", "Failed to delete gate: " + e.getMessage());
       }
   }
  
   private TextField[] getFormFields() {
       return (TextField[]) ((GridPane) gateTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
   }
}




