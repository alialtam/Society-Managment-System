package test;

import dao.ResidentDAO;
import model.Resident;

import java.util.List;

public class ResidentDAOTest {

    private static ResidentDAO residentDAO;

    public static void main(String[] args) {
        residentDAO = new ResidentDAO();
        
        // Run all tests and output the results
        System.out.println("Starting ResidentDAO Test...\n");

        testGetResidentsOrderedByMoveInDate();
        testGetResidentsMovedBefore2024();
        testGetResidentsByFirstNameStartingWithA();

        System.out.println("\nTest execution completed.");
    }

    // Test getResidentsOrderedByMoveInDate method
    public static void testGetResidentsOrderedByMoveInDate() {
        System.out.println("Testing: Get Residents Ordered By MoveInDate...");
        List<Resident> residents = residentDAO.getResidentsOrderedByMoveInDate();
        
        if (residents != null && !residents.isEmpty()) {
            printResidentData(residents);
        } else {
            System.out.println("Result: FAILED - No residents found or list is empty.");
        }
    }

    // Test getResidentsMovedBefore2024 method
    public static void testGetResidentsMovedBefore2024() {
        System.out.println("\nTesting: Get Residents Moved Before 2024...");
        List<Resident> residents = residentDAO.getResidentsMovedBefore2024();
        
        if (residents != null && !residents.isEmpty()) {
            printResidentData(residents);
        } else {
            System.out.println("Result: FAILED - No residents found or list is empty.");
        }
    }

    // Test getResidentsByFirstNameStartingWithA method
    public static void testGetResidentsByFirstNameStartingWithA() {
        System.out.println("\nTesting: Get Residents By FirstName Starting With 'A'...");
        List<Resident> residents = residentDAO.getResidentsByFirstNameStartingWithA();
        
        if (residents != null && !residents.isEmpty()) {
            printResidentData(residents);
        } else {
            System.out.println("Result: FAILED - No residents found or list is empty.");
        }
    }

    // Utility method to print resident data in a MySQL-like table format
    public static void printResidentData(List<Resident> residents) {
        System.out.println("------------------------------------------------------------");
        System.out.printf("| %-5s | %-15s | %-20s | %-15s |\n", "ID", "First Name", "Move In Date", "Last Name");
        System.out.println("------------------------------------------------------------");

        for (Resident resident : residents) {
            System.out.printf("| %-5d | %-15s | %-20s | %-15s |\n",
                    resident.getResidentId(),
                    resident.getFirstName(),
                    resident.getMoveInDate(),
                    resident.getLastName());
        }

        System.out.println("------------------------------------------------------------");
    }
}
