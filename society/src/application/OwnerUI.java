package application;
import dao.OwnerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Owner;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
public class OwnerUI {
   private OwnerDAO ownerDAO;
   private TableView<Owner> ownerTable;
   // Constructor to initialize OwnerDAO with database connection
   public OwnerUI(Connection connection) {
       this.ownerDAO = new OwnerDAO(connection);
   }
   public VBox createContent() {
       VBox vbox = new VBox(10);
       vbox.setPadding(new Insets(10));
       // Create table
       ownerTable = createOwnerTable();
       // Create form for add/edit
       GridPane form = createOwnerForm();
       // Create buttons
       HBox buttonBox = new HBox(10);
       Button addButton = new Button("Add");
       Button updateButton = new Button("Update");
       Button deleteButton = new Button("Delete");
       Button clearButton = new Button("Clear");
       addButton.setOnAction(e -> addOwner());
       updateButton.setOnAction(e -> updateOwner());
       deleteButton.setOnAction(e -> deleteOwner());
       clearButton.setOnAction(e -> clearForm());
       buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);
       vbox.getChildren().addAll(ownerTable, form, buttonBox);
       loadOwnerData();
       return vbox;
   }
   private TableView<Owner> createOwnerTable() {
       TableView<Owner> table = new TableView<>();
       TableColumn<Owner, Integer> ownerIdCol = new TableColumn<>("Owner ID");
       ownerIdCol.setCellValueFactory(new PropertyValueFactory<>("ownerId"));
       TableColumn<Owner, Integer> residentIdCol = new TableColumn<>("Resident ID");
       residentIdCol.setCellValueFactory(new PropertyValueFactory<>("residentId"));
       TableColumn<Owner, LocalDate> startDateCol = new TableColumn<>("Owner Start Date");
       startDateCol.setCellValueFactory(new PropertyValueFactory<>("ownerStartDate"));
       TableColumn<Owner, Double> percentageCol = new TableColumn<>("Ownership %");
       percentageCol.setCellValueFactory(new PropertyValueFactory<>("ownershipPercentage"));
       table.getColumns().addAll(ownerIdCol, residentIdCol, startDateCol, percentageCol);
       table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
       // Add listener for table selection
       table.getSelectionModel().selectedItemProperty().addListener(
               (obs, oldSelection, newSelection) -> {
                   if (newSelection != null) {
                       fillFormWithSelectedOwner(newSelection);
                   }
               });
       return table;
   }
   private GridPane createOwnerForm() {
       GridPane grid = new GridPane();
       grid.setHgap(10);
       grid.setVgap(10);
       grid.setPadding(new Insets(10, 10, 10, 10));
       TextField ownerIdField = new TextField();
       ownerIdField.setDisable(true);
       TextField residentIdField = new TextField();
       DatePicker startDatePicker = new DatePicker();
       TextField percentageField = new TextField();
       grid.add(new Label("Owner ID:"), 0, 0);
       grid.add(ownerIdField, 1, 0);
       grid.add(new Label("Resident ID:"), 0, 1);
       grid.add(residentIdField, 1, 1);
       grid.add(new Label("Owner Start Date:"), 0, 2);
       grid.add(startDatePicker, 1, 2);
       grid.add(new Label("Ownership Percentage:"), 0, 3);
       grid.add(percentageField, 1, 3);
       // Store references to form fields
       grid.setUserData(new Object[]{ownerIdField, residentIdField, startDatePicker, percentageField});
       return grid;
   }
   private void loadOwnerData() {
       try {
           List<Owner> owners = ownerDAO.findAll();
           ObservableList<Owner> ownerData = FXCollections.observableArrayList(owners);
           ownerTable.setItems(ownerData);
       } catch (SQLException e) {
           showAlert("Error", "Failed to load owner data: " + e.getMessage());
       }
   }
   private void fillFormWithSelectedOwner(Owner owner) {
       Object[] fields = (Object[]) ((GridPane) ownerTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
       ((TextField) fields[0]).setText(String.valueOf(owner.getOwnerId()));
       ((TextField) fields[1]).setText(String.valueOf(owner.getResidentId()));
       ((DatePicker) fields[2]).setValue(owner.getOwnerStartDate());
       ((TextField) fields[3]).setText(owner.getOwnershipPercentage() != null ? String.valueOf(owner.getOwnershipPercentage()) : "");
   }
   private void clearForm() {
       Object[] fields = (Object[]) ((GridPane) ownerTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
       ((TextField) fields[0]).clear();
       ((TextField) fields[1]).clear();
       ((DatePicker) fields[2]).setValue(null);
       ((TextField) fields[3]).clear();
       ownerTable.getSelectionModel().clearSelection();
   }
   private void addOwner() {
       Object[] fields = (Object[]) ((GridPane) ownerTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
       String residentIdStr = ((TextField) fields[1]).getText();
       LocalDate startDate = ((DatePicker) fields[2]).getValue();
       String percentageStr = ((TextField) fields[3]).getText();
       if (residentIdStr.isEmpty() || startDate == null || percentageStr.isEmpty()) {
           showAlert("Error", "Resident ID, Owner Start Date, and Ownership Percentage are required");
           return;
       }
       try {
           int residentId = Integer.parseInt(residentIdStr);
           double ownershipPercentage = Double.parseDouble(percentageStr);
           Owner owner = new Owner();
           owner.setResidentId(residentId);
           owner.setOwnerStartDate(startDate);
           owner.setOwnershipPercentage(ownershipPercentage);
           // OwnerID is auto-generated by the database, so we don't set it
           ownerDAO.save(owner);
           showAlert("Success", "Owner added successfully");
           loadOwnerData();
           clearForm();
       } catch (NumberFormatException e) {
           showAlert("Error", "Resident ID and Ownership Percentage must be valid numbers");
       } catch (SQLException e) {
           showAlert("Error", "Failed to add owner: " + e.getMessage());
       }
   }
   private void updateOwner() {
       Object[] fields = (Object[]) ((GridPane) ownerTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
       String ownerIdStr = ((TextField) fields[0]).getText();
       String residentIdStr = ((TextField) fields[1]).getText();
       LocalDate startDate = ((DatePicker) fields[2]).getValue();
       String percentageStr = ((TextField) fields[3]).getText();
       if (ownerIdStr.isEmpty() || residentIdStr.isEmpty() || startDate == null || percentageStr.isEmpty()) {
           showAlert("Error", "Please select an owner to update and fill all required fields");
           return;
       }
       try {
           int ownerId = Integer.parseInt(ownerIdStr);
           int residentId = Integer.parseInt(residentIdStr);
           double ownershipPercentage = Double.parseDouble(percentageStr);
           Owner owner = new Owner();
           owner.setOwnerId(ownerId);
           owner.setResidentId(residentId);
           owner.setOwnerStartDate(startDate);
           owner.setOwnershipPercentage(ownershipPercentage);
           ownerDAO.update(owner);
           showAlert("Success", "Owner updated successfully");
           loadOwnerData();
           clearForm();
       } catch (NumberFormatException e) {
           showAlert("Error", "Owner ID, Resident ID, and Ownership Percentage must be valid numbers");
       } catch (SQLException e) {
           showAlert("Error", "Failed to update owner: " + e.getMessage());
       }
   }
   private void deleteOwner() {
       Object[] fields = (Object[]) ((GridPane) ownerTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
       String ownerIdStr = ((TextField) fields[0]).getText();
       if (ownerIdStr.isEmpty()) {
           showAlert("Error", "Please select an owner to delete");
           return;
       }
       try {
           int ownerId = Integer.parseInt(ownerIdStr);
           ownerDAO.delete(ownerId);
           showAlert("Success", "Owner deleted successfully");
           loadOwnerData();
           clearForm();
       } catch (NumberFormatException e) {
           showAlert("Error", "Owner ID must be a number");
       } catch (SQLException e) {
           showAlert("Error", "Failed to delete owner: " + e.getMessage());
       }
   }
   private void showAlert(String title, String message) {
       Alert alert = new Alert(Alert.AlertType.INFORMATION);
       alert.setTitle(title);
       alert.setHeaderText(null);
       alert.setContentText(message);
       alert.showAndWait();
   }
}




