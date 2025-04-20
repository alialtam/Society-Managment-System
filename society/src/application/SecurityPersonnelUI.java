package application;
import dao.GateDAO;
import dao.SecurityPersonnelDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.SecurityPersonnel;
import util.AlertUtil;
import java.sql.SQLException;
import java.util.List;
public class SecurityPersonnelUI {
   private TableView<SecurityPersonnel> personnelTable;
   private ComboBox<String> shiftCombo = new ComboBox<>();
   private ComboBox<Integer> gateCombo = new ComboBox<>();
   public VBox createContent() {
       VBox vbox = new VBox(10);
       vbox.setPadding(new Insets(10));
      
       // 1. Create Table
       personnelTable = createPersonnelTable();
      
       // 2. Create Form
       GridPane form = createPersonnelForm();
      
       // 3. Create Buttons
       HBox buttonBox = createButtonBox();
      
       vbox.getChildren().addAll(personnelTable, form, buttonBox);
       loadPersonnelData();
       loadGateNumbers();
      
       return vbox;
   }
   private TableView<SecurityPersonnel> createPersonnelTable() {
       TableView<SecurityPersonnel> table = new TableView<>();
      
       TableColumn<SecurityPersonnel, Integer> idCol = new TableColumn<>("ID");
       idCol.setCellValueFactory(new PropertyValueFactory<>("securityId"));
      
       TableColumn<SecurityPersonnel, String> nameCol = new TableColumn<>("Name");
       nameCol.setCellValueFactory(cell ->
           new SimpleStringProperty(
               cell.getValue().getFirstName() + " " + cell.getValue().getLastName()
           )
       );
      
       TableColumn<SecurityPersonnel, String> contactCol = new TableColumn<>("Contact");
       contactCol.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
      
       TableColumn<SecurityPersonnel, String> shiftCol = new TableColumn<>("Shift");
       shiftCol.setCellValueFactory(new PropertyValueFactory<>("shiftTimings"));
      
       TableColumn<SecurityPersonnel, Integer> gateCol = new TableColumn<>("Gate No");
       gateCol.setCellValueFactory(new PropertyValueFactory<>("gateNumber"));
       table.getColumns().addAll(idCol, nameCol, contactCol, shiftCol, gateCol);
       table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      
       table.getSelectionModel().selectedItemProperty().addListener(
           (obs, oldSelection, newSelection) -> {
               if (newSelection != null) {
                   fillFormWithSelection(newSelection);
               }
           });
      
       return table;
   }
   private GridPane createPersonnelForm() {
       GridPane grid = new GridPane();
       grid.setHgap(10);
       grid.setVgap(10);
       grid.setPadding(new Insets(10));
      
       // Initialize ComboBoxes
       shiftCombo.getItems().addAll("Morning", "Evening", "Night");
       shiftCombo.setPromptText("Select Shift");
      
       TextField idField = new TextField();
       idField.setDisable(true);
       TextField firstNameField = new TextField();
       TextField lastNameField = new TextField();
       TextField contactField = new TextField();
      
       // Add TextFormatters for validation
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
       grid.add(new Label("ID:"), 0, 0);
       grid.add(idField, 1, 0);
       grid.add(new Label("First Name:"), 0, 1);
       grid.add(firstNameField, 1, 1);
       grid.add(new Label("Last Name:"), 0, 2);
       grid.add(lastNameField, 1, 2);
       grid.add(new Label("Contact No:"), 0, 3);
       grid.add(contactField, 1, 3);
       grid.add(new Label("Shift:"), 0, 4);
       grid.add(shiftCombo, 1, 4);
       grid.add(new Label("Gate No:"), 0, 5);
       grid.add(gateCombo, 1, 5);
      
       // Store form fields as TextField array
       grid.setUserData(new TextField[]{idField, firstNameField, lastNameField, contactField});
      
       return grid;
   }
   private HBox createButtonBox() {
       HBox buttonBox = new HBox(10);
       Button addButton = new Button("Add");
       Button updateButton = new Button("Update");
       Button deleteButton = new Button("Delete");
       Button clearButton = new Button("Clear");
      
       addButton.setOnAction(e -> addPersonnel());
       updateButton.setOnAction(e -> updatePersonnel());
       deleteButton.setOnAction(e -> deletePersonnel());
       clearButton.setOnAction(e -> clearForm());
      
       buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);
       return buttonBox;
   }
   private void loadPersonnelData() {
       try {
           List<SecurityPersonnel> personnel = SecurityPersonnelDAO.getAll();
           personnelTable.setItems(FXCollections.observableArrayList(personnel));
       } catch (SQLException e) {
           AlertUtil.showErrorAlert("Database Error", "Failed to load personnel: " + e.getMessage());
       }
   }
   private void loadGateNumbers() {
       try {
           List<Integer> gateNumbers = GateDAO.getAllGateNumbers();
           gateCombo.getItems().setAll(gateNumbers);
       } catch (SQLException e) {
           AlertUtil.showErrorAlert("Database Error", "Failed to load gate numbers: " + e.getMessage());
       }
   }
   private void fillFormWithSelection(SecurityPersonnel personnel) {
       TextField[] fields = getFormFields();
       fields[0].setText(String.valueOf(personnel.getSecurityId()));
       fields[1].setText(personnel.getFirstName());
       fields[2].setText(personnel.getLastName());
       fields[3].setText(personnel.getContactNo());
       shiftCombo.setValue(personnel.getShiftTimings());
       gateCombo.setValue(personnel.getGateNumber());
   }
   private void clearForm() {
       TextField[] fields = getFormFields();
       for (TextField field : fields) {
           field.clear();
       }
       shiftCombo.getSelectionModel().clearSelection();
       gateCombo.getSelectionModel().clearSelection();
       personnelTable.getSelectionModel().clearSelection();
   }
   private void addPersonnel() {
       try {
           TextField[] fields = getFormFields();
          
           SecurityPersonnel personnel = new SecurityPersonnel(
               0, // ID will be auto-generated
               fields[1].getText(),
               fields[2].getText(),
               fields[3].getText(),
               shiftCombo.getValue(),
               gateCombo.getValue()
           );
          
           if (SecurityPersonnelDAO.insert(personnel)) {
               AlertUtil.showAlert("Success", "Personnel added successfully");
               loadPersonnelData();
               clearForm();
           }
       } catch (IllegalArgumentException | SQLException e) {
           AlertUtil.showErrorAlert("Error", e.getMessage());
       }
   }
   private void updatePersonnel() {
       try {
           TextField[] fields = getFormFields();
           String idText = fields[0].getText();
          
           if (idText.isEmpty()) {
               AlertUtil.showErrorAlert("Error", "Please select personnel to update");
               return;
           }
          
           SecurityPersonnel personnel = new SecurityPersonnel(
               Integer.parseInt(idText),
               fields[1].getText(),
               fields[2].getText(),
               fields[3].getText(),
               shiftCombo.getValue(),
               gateCombo.getValue()
           );
          
           if (SecurityPersonnelDAO.update(personnel)) {
               AlertUtil.showAlert("Success", "Personnel updated successfully");
               loadPersonnelData();
           }
       } catch (NumberFormatException e) {
           AlertUtil.showErrorAlert("Error", "Invalid ID format");
       } catch (IllegalArgumentException | SQLException e) {
           AlertUtil.showErrorAlert("Error", e.getMessage());
       }
   }
   private void deletePersonnel() {
       TextField[] fields = getFormFields();
       String idText = fields[0].getText();
      
       if (idText.isEmpty()) {
           AlertUtil.showErrorAlert("Error", "Please select personnel to delete");
           return;
       }
      
       try {
           int personnelId = Integer.parseInt(idText);
          
           if (SecurityPersonnelDAO.delete(personnelId)) {
               AlertUtil.showAlert("Success", "Personnel deleted successfully");
               loadPersonnelData();
               clearForm();
           } else {
               AlertUtil.showErrorAlert("Error", "Failed to delete personnel");
           }
       } catch (NumberFormatException e) {
           AlertUtil.showErrorAlert("Error", "Invalid ID format");
       } catch (SQLException e) {
           AlertUtil.showErrorAlert("Database Error", e.getMessage());
       }
   }
   private TextField[] getFormFields() {
       return (TextField[]) ((GridPane) personnelTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
   }
}




