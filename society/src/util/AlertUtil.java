package util;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
public class AlertUtil {
   public static void showAlert(String title, String message) {
       Alert alert = new Alert(Alert.AlertType.INFORMATION);
       alert.setTitle(title);
       alert.setHeaderText(null);
       alert.setContentText(message);
       alert.showAndWait();
   }
  
   public static void showErrorAlert(String title, String message) {
       Alert alert = new Alert(Alert.AlertType.ERROR);
       alert.setTitle(title);
       alert.setHeaderText(null);
       alert.setContentText(message);
       alert.showAndWait();
   }
  
   public static boolean showConfirmation(String title, String message) {
       Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
       alert.setTitle(title);
       alert.setHeaderText(null);
       alert.setContentText(message);
      
       Optional<ButtonType> result = alert.showAndWait();
       return result.isPresent() && result.get() == ButtonType.OK;
   }
}




