package application;
import dao.TenantDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Tenant;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
public class TenantUI {
   private TenantDAO tenantDAO;
   private TableView<Tenant> tenantTable;
   // Constructor to initialize TenantDAO with database connection
   public TenantUI(Connection connection) {
       this.tenantDAO = new TenantDAO(connection);
   }
   public VBox createContent() {
       VBox vbox = new VBox(10);
       vbox.setPadding(new Insets(10));
       // Create table
       tenantTable = createTenantTable();
       // Create form for add/edit
       GridPane form = createTenantForm();
       // Create buttons
       HBox buttonBox = new HBox(10);
       Button addButton = new Button("Add");
       Button updateButton = new Button("Update");
       Button deleteButton = new Button("Delete");
       Button clearButton = new Button("Clear");
       addButton.setOnAction(e -> addTenant());
       updateButton.setOnAction(e -> updateTenant());
       deleteButton.setOnAction(e -> deleteTenant());
       clearButton.setOnAction(e -> clearForm());
       buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);
       vbox.getChildren().addAll(tenantTable, form, buttonBox);
       loadTenantData();
       return vbox;
   }
   private TableView<Tenant> createTenantTable() {
       TableView<Tenant> table = new TableView<>();
       TableColumn<Tenant, Integer> tenantIdCol = new TableColumn<>("Tenant ID");
       tenantIdCol.setCellValueFactory(new PropertyValueFactory<>("tenantId"));
       TableColumn<Tenant, Integer> residentIdCol = new TableColumn<>("Resident ID");
       residentIdCol.setCellValueFactory(new PropertyValueFactory<>("residentId"));
       TableColumn<Tenant, LocalDate> leaseStartCol = new TableColumn<>("Lease Start");
       leaseStartCol.setCellValueFactory(new PropertyValueFactory<>("leaseStartDate"));
       TableColumn<Tenant, LocalDate> leaseEndCol = new TableColumn<>("Lease End");
       leaseEndCol.setCellValueFactory(new PropertyValueFactory<>("leaseEndDate"));
       TableColumn<Tenant, Integer> monthlyRentCol = new TableColumn<>("Monthly Rent");
       monthlyRentCol.setCellValueFactory(new PropertyValueFactory<>("monthlyRent"));
       table.getColumns().addAll(tenantIdCol, residentIdCol, leaseStartCol, leaseEndCol, monthlyRentCol);
       table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
       // Add listener for table selection
       table.getSelectionModel().selectedItemProperty().addListener(
               (obs, oldSelection, newSelection) -> {
                   if (newSelection != null) {
                       fillFormWithSelectedTenant(newSelection);
                   }
               });
       return table;
   }
   private GridPane createTenantForm() {
       GridPane grid = new GridPane();
       grid.setHgap(10);
       grid.setVgap(10);
       grid.setPadding(new Insets(10, 10, 10, 10));
       TextField tenantIdField = new TextField();
       tenantIdField.setDisable(true);
       TextField residentIdField = new TextField();
       DatePicker leaseStartPicker = new DatePicker();
       DatePicker leaseEndPicker = new DatePicker();
       TextField monthlyRentField = new TextField();
       grid.add(new Label("Tenant ID:"), 0, 0);
       grid.add(tenantIdField, 1, 0);
       grid.add(new Label("Resident ID:"), 0, 1);
       grid.add(residentIdField, 1, 1);
       grid.add(new Label("Lease Start Date:"), 0, 2);
       grid.add(leaseStartPicker, 1, 2);
       grid.add(new Label("Lease End Date:"), 0, 3);
       grid.add(leaseEndPicker, 1, 3);
       grid.add(new Label("Monthly Rent:"), 0, 4);
       grid.add(monthlyRentField, 1, 4);
       // Store references to form fields
       grid.setUserData(new Object[]{tenantIdField, residentIdField, leaseStartPicker, leaseEndPicker, monthlyRentField});
       return grid;
   }
   private void loadTenantData() {
       try {
           List<Tenant> tenants = tenantDAO.findAll();
           ObservableList<Tenant> tenantData = FXCollections.observableArrayList(tenants);
           tenantTable.setItems(tenantData);
       } catch (SQLException e) {
           showAlert("Error", "Failed to load tenant data: " + e.getMessage());
       }
   }
   private void fillFormWithSelectedTenant(Tenant tenant) {
       Object[] fields = (Object[]) ((GridPane) tenantTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
       ((TextField) fields[0]).setText(String.valueOf(tenant.getTenantId()));
       ((TextField) fields[1]).setText(String.valueOf(tenant.getResidentId()));
       ((DatePicker) fields[2]).setValue(tenant.getLeaseStartDate());
       ((DatePicker) fields[3]).setValue(tenant.getLeaseEndDate());
       ((TextField) fields[4]).setText(tenant.getMonthlyRent() != null ? String.valueOf(tenant.getMonthlyRent()) : "");
   }
   private void clearForm() {
       Object[] fields = (Object[]) ((GridPane) tenantTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
       ((TextField) fields[0]).clear();
       ((TextField) fields[1]).clear();
       ((DatePicker) fields[2]).setValue(null);
       ((DatePicker) fields[3]).setValue(null);
       ((TextField) fields[4]).clear();
       tenantTable.getSelectionModel().clearSelection();
   }
   private void addTenant() {
       Object[] fields = (Object[]) ((GridPane) tenantTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
       String residentIdStr = ((TextField) fields[1]).getText();
       LocalDate leaseStartDate = ((DatePicker) fields[2]).getValue();
       LocalDate leaseEndDate = ((DatePicker) fields[3]).getValue();
       String monthlyRentStr = ((TextField) fields[4]).getText();
       if (residentIdStr.isEmpty() || leaseStartDate == null) {
           showAlert("Error", "Resident ID and Lease Start Date are required");
           return;
       }
       try {
           int residentId = Integer.parseInt(residentIdStr);
           Integer monthlyRent = monthlyRentStr.isEmpty() ? null : Integer.parseInt(monthlyRentStr);
           Tenant tenant = new Tenant();
           tenant.setResidentId(residentId);
           tenant.setLeaseStartDate(leaseStartDate);
           tenant.setLeaseEndDate(leaseEndDate);
           tenant.setMonthlyRent(monthlyRent);
           // TenantID is auto-generated by the database, so we don't set it
           tenantDAO.save(tenant);
           showAlert("Success", "Tenant added successfully");
           loadTenantData();
           clearForm();
       } catch (NumberFormatException e) {
           showAlert("Error", "Resident ID and Monthly Rent must be valid numbers");
       } catch (SQLException e) {
           showAlert("Error", "Failed to add tenant: " + e.getMessage());
       }
   }
   private void updateTenant() {
       Object[] fields = (Object[]) ((GridPane) tenantTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
       String tenantIdStr = ((TextField) fields[0]).getText();
       String residentIdStr = ((TextField) fields[1]).getText();
       LocalDate leaseStartDate = ((DatePicker) fields[2]).getValue();
       LocalDate leaseEndDate = ((DatePicker) fields[3]).getValue();
       String monthlyRentStr = ((TextField) fields[4]).getText();
       if (tenantIdStr.isEmpty() || residentIdStr.isEmpty() || leaseStartDate == null) {
           showAlert("Error", "Please select a tenant to update and fill required fields");
           return;
       }
       try {
           int tenantId = Integer.parseInt(tenantIdStr);
           int residentId = Integer.parseInt(residentIdStr);
           Integer monthlyRent = monthlyRentStr.isEmpty() ? null : Integer.parseInt(monthlyRentStr);
           Tenant tenant = new Tenant();
           tenant.setTenantId(tenantId);
           tenant.setResidentId(residentId);
           tenant.setLeaseStartDate(leaseStartDate);
           tenant.setLeaseEndDate(leaseEndDate);
           tenant.setMonthlyRent(monthlyRent);
           tenantDAO.update(tenant);
           showAlert("Success", "Tenant updated successfully");
           loadTenantData();
           clearForm();
       } catch (NumberFormatException e) {
           showAlert("Error", "Tenant ID, Resident ID, and Monthly Rent must be valid numbers");
       } catch (SQLException e) {
           showAlert("Error", "Failed to update tenant: " + e.getMessage());
       }
   }
   private void deleteTenant() {
       Object[] fields = (Object[]) ((GridPane) tenantTable.getParent().getChildrenUnmodifiable().get(1)).getUserData();
       String tenantIdStr = ((TextField) fields[0]).getText();
       if (tenantIdStr.isEmpty()) {
           showAlert("Error", "Please select a tenant to delete");
           return;
       }
       try {
           int tenantId = Integer.parseInt(tenantIdStr);
           tenantDAO.delete(tenantId);
           showAlert("Success", "Tenant deleted successfully");
           loadTenantData();
           clearForm();
       } catch (NumberFormatException e) {
           showAlert("Error", "Tenant ID must be a number");
       } catch (SQLException e) {
           showAlert("Error", "Failed to delete tenant: " + e.getMessage());
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




