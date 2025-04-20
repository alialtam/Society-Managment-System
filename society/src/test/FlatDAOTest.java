package test;


import dao.FlatDAO;
import model.Flat;
import model.Flat.FlatType;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class FlatDAOTest {
    public static void main(String[] args) {
        FlatDAO flatDAO = new FlatDAO();
        Scanner scanner = new Scanner(System.in);


        System.out.println("Enter Flat Type to filter (1BHK, 2BHK, 3BHK, Penthouse): ");
        String inputType = scanner.nextLine().trim();


        try {
            // Convert input string to FlatType enum
            FlatType flatType = FlatType.fromString(inputType);


            // Get flats by type
            List<Flat> flats = flatDAO.getFlatsByType(flatType);


            // Display results
            if (flats.isEmpty()) {
                System.out.println("No flats found for type: " + inputType);
            } else {
                System.out.println("Flats of type " + inputType + ":");
                System.out.println("--------------------------------------------------");
                for (Flat flat : flats) {
                    System.out.printf("FlatID: %d, FlatNumber: %s, BuildingID: %d, FlatType: %s, SlotNumber: %s%n",
                            flat.getFlatID(), flat.getFlatNumber(), flat.getBuildingID(),
                            flat.getFlatType().getDbValue(), flat.getSlotNumber());
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}




