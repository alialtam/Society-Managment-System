package dao;
import model.Flat;
import model.Flat.FlatType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class FlatDAO {
   private static final String INSERT_SQL =
       "INSERT INTO flat (FlatID, FlatNumber, BuildingID, FlatType, SlotNumber) VALUES (?, ?, ?, ?, ?)";
   private static final String SELECT_ALL_SQL = "SELECT * FROM flat";
   private static final String SELECT_BY_ID_SQL = "SELECT * FROM flat WHERE FlatID = ?";
   private static final String UPDATE_SQL =
       "UPDATE flat SET FlatNumber=?, BuildingID=?, FlatType=?, SlotNumber=? WHERE FlatID=?";
   private static final String DELETE_SQL = "DELETE FROM flat WHERE FlatID = ?";
   // Assume BuildingDAO and ParkingSlotDAO exist for foreign key validation
   private BuildingDAO buildingDAO = new BuildingDAO();
   private ParkingSlotDAO parkingSlotDAO = new ParkingSlotDAO();
   public boolean insertFlat(Flat flat) throws SQLException {
       // Validate FlatType
       if (flat.getFlatType() == null) {
           throw new IllegalArgumentException("FlatType cannot be null");
       }
       // Validate foreign keys
       if (buildingDAO.getBuildingById(flat.getBuildingID()) == null) {
           throw new IllegalArgumentException("Invalid BuildingID: " + flat.getBuildingID());
       }
       if (parkingSlotDAO.getParkingSlotById(flat.getSlotNumber()) == null) {
           throw new IllegalArgumentException("Invalid SlotNumber: " + flat.getSlotNumber());
       }
       try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
          
           setInsertParameters(stmt, flat);
          
           int affectedRows = stmt.executeUpdate();
           return affectedRows > 0;
       }
   }
   public List<Flat> getAllFlats() throws SQLException {
       List<Flat> flats = new ArrayList<>();
      
       try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
          
           while (rs.next()) {
               flats.add(mapResultSetToFlat(rs));
           }
       }
       return flats;
   }
   public Flat getFlatById(int flatID) throws SQLException {
       try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
          
           stmt.setInt(1, flatID);
           try (ResultSet rs = stmt.executeQuery()) {
               if (rs.next()) {
                   return mapResultSetToFlat(rs);
               }
           }
       }
       return null;
   }
   public boolean updateFlat(Flat flat) throws SQLException {
       // Validate FlatType
       if (flat.getFlatType() == null) {
           throw new IllegalArgumentException("FlatType cannot be null");
       }
       // Validate foreign keys
       if (buildingDAO.getBuildingById(flat.getBuildingID()) == null) {
           throw new IllegalArgumentException("Invalid BuildingID: " + flat.getBuildingID());
       }
       if (parkingSlotDAO.getParkingSlotById(flat.getSlotNumber()) == null) {
           throw new IllegalArgumentException("Invalid SlotNumber: " + flat.getSlotNumber());
       }
       try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
          
           setUpdateParameters(stmt, flat);
          
           return stmt.executeUpdate() > 0;
       }
   }
   public boolean deleteFlat(int flatID) throws SQLException {
       try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
          
           stmt.setInt(1, flatID);
           return stmt.executeUpdate() > 0;
       }
   } 
   public List<Flat> getFlatsByType(FlatType type) throws SQLException {
	    String sql = "SELECT * FROM flat WHERE FlatType = ?";
	    List<Flat> flats = new ArrayList<>();
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, type.getDbValue());
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                flats.add(mapResultSetToFlat(rs));
	            }
	        }
	    }
	    return flats;
	}
   
   private void setInsertParameters(PreparedStatement stmt, Flat flat) throws SQLException {
       stmt.setInt(1, flat.getFlatID());
       stmt.setString(2, flat.getFlatNumber());
       stmt.setInt(3, flat.getBuildingID());
       stmt.setString(4, flat.getFlatType().getDbValue());
       stmt.setString(5, flat.getSlotNumber());
   }
   private void setUpdateParameters(PreparedStatement stmt, Flat flat) throws SQLException {
       stmt.setString(1, flat.getFlatNumber());
       stmt.setInt(2, flat.getBuildingID());
       stmt.setString(3, flat.getFlatType().getDbValue());
       stmt.setString(4, flat.getSlotNumber());
       stmt.setInt(5, flat.getFlatID());
   }
   private Flat mapResultSetToFlat(ResultSet rs) throws SQLException {
       return new Flat(
           rs.getInt("FlatID"),
           rs.getString("FlatNumber"),
           rs.getInt("BuildingID"),
           FlatType.fromString(rs.getString("FlatType")),
           rs.getString("SlotNumber")
       );
   }
}
