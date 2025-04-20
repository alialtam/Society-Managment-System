package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection first
            try {
                dao.DBConnection.getConnection().close();
            } catch (Exception e) {
                showErrorAlert("Database Error", "Failed to connect to database: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
            
            // Initialize UI
            UIManager uiManager = new UIManager();
            Scene scene = new Scene(uiManager.createMainLayout(), 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            
            primaryStage.setTitle("Residential Society Management System");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
            
        } catch (Exception e) {
            showErrorAlert("Application Error", "Failed to initialize application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}