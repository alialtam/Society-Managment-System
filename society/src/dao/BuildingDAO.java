package dao;

import model.Building;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuildingDAO {
    
	public boolean addBuilding(Building building) {
	    // First get the next available ID
	    int newId = 1;
	    try (Connection conn = DBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery("SELECT MAX(BuildingID) FROM building")) {
	        if (rs.next()) {
	            newId = rs.getInt(1) + 1;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }

	    // Then insert with the new ID
	    String query = "INSERT INTO building (BuildingID, BuildingName, TotalFlats) VALUES (?, ?, ?)";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        stmt.setInt(1, newId);
	        stmt.setString(2, building.getName());
	        stmt.setInt(3, building.getTotalFlats());
	        
	        boolean success = stmt.executeUpdate() > 0;
	        if (success) {
	            building.setBuildingId(newId); // Update the building object with the new ID
	        }
	        return success;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
    public boolean updateBuilding(Building building) {
        String query = "UPDATE building SET BuildingName = ?, TotalFlats = ? WHERE BuildingID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, building.getName());
            stmt.setInt(2, building.getTotalFlats());
            stmt.setInt(3, building.getBuildingId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBuilding(int buildingId) {
        String query = "DELETE FROM building WHERE BuildingID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, buildingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Building> getAllBuildings() {
        List<Building> buildings = new ArrayList<>();
        String query = "SELECT * FROM building";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                buildings.add(new Building(
                    rs.getInt("BuildingID"),
                    rs.getString("BuildingName"),
                    rs.getInt("TotalFlats")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buildings;
    }

    public Building getBuildingById(int buildingId) {
        String query = "SELECT * FROM building WHERE BuildingID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, buildingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Building(
                        rs.getInt("BuildingID"),
                        rs.getString("BuildingName"),
                        rs.getInt("TotalFlats")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}