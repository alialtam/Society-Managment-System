package application.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

public class MainController {
    @FXML private ComboBox<String> tableSelector;
    @FXML private TableView<?> dataTable;

    @FXML
    private void handleRefresh() {
        String selectedTable = tableSelector.getValue();
        System.out.println("Refreshing data for table: " + selectedTable);
        // Add your refresh logic here
    }

    @FXML
    private void initialize() {
        // Initialize the ComboBox selection
        tableSelector.getSelectionModel().selectFirst();
    }
}