package util;

public class InputValidator {
    public static boolean validateStringInput(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            AlertUtil.showErrorAlert("Validation Error", fieldName + " cannot be empty");
            return false;
        }
        return true;
    }
    
    public static boolean validateNumericInput(String input, String fieldName) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("Validation Error", fieldName + " must be a valid number");
            return false;
        }
    }
}