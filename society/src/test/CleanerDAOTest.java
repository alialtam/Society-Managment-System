package test;


import dao.CleanerDAO;
import model.Cleaner;
import java.sql.SQLException;
import java.util.List;


public class CleanerDAOTest {
    public static void main(String[] args) {
        CleanerDAO cleanerDAO = new CleanerDAO();
        int buildingID = 1; // Hardcoded to BuildingID 1


        try {
            // Get cleaners for BuildingID 1
            List<Cleaner> cleaners = cleanerDAO.getCleanersByBuildingID(buildingID);


            // Display results
            if (cleaners.isEmpty()) {
                System.out.printf("No cleaners found for BuildingID: %d%n", buildingID);
            } else {
                System.out.printf("Cleaners for BuildingID: %d%n", buildingID);
                System.out.println("=======================================");


                // Display cleaners
                for (Cleaner cleaner : cleaners) {
                    System.out.printf("CleanerID: %d, Name: %s %s, Contact: %s%n",
                            cleaner.getCleanerID(),
                            cleaner.getFirstName(),
                            cleaner.getLastName(),
                            cleaner.getContactNumber());
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}




