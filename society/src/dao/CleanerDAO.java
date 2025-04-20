package dao;
import model.Cleaner;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class CleanerDAO {
  private static final String INSERT_SQL =
      "INSERT INTO cleaner (CleanerID, FirstName, LastName, BuildingID, ContactNumber) VALUES (?, ?, ?, ?, ?)";
  private static final String SELECT_ALL_SQL = "SELECT * FROM cleaner";
  private static final String SELECT_BY_ID_SQL = "SELECT * FROM cleaner WHERE CleanerID = ?";
  private static final String UPDATE_SQL =
      "UPDATE cleaner SET FirstName=?, LastName=?, BuildingID=?, ContactNumber=? WHERE CleanerID=?";
  private static final String DELETE_SQL = "DELETE FROM cleaner WHERE CleanerID = ?";
  // Assume BuildingDAO exists for foreign key validation
  private BuildingDAO buildingDAO = new BuildingDAO();
  public boolean insertCleaner(Cleaner cleaner) throws SQLException {
      // Validate ContactNumber uniqueness (basic check, assumes DAO handles constraints)
      if (getCleanerByContactNumber(cleaner.getContactNumber()) != null) {
          throw new SQLException("ContactNumber must be unique: " + cleaner.getContactNumber());
      }
      // Validate BuildingID if not null
      if (cleaner.getBuildingID() != null && buildingDAO.getBuildingById(cleaner.getBuildingID()) == null) {
          throw new IllegalArgumentException("Invalid BuildingID: " + cleaner.getBuildingID());
      }
      try (Connection conn = DBConnection.getConnection();
           PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
        
          setInsertParameters(stmt, cleaner);
        
          int affectedRows = stmt.executeUpdate();
          return affectedRows > 0;
      }
  }
  public List<Cleaner> getAllCleaners() throws SQLException {
      List<Cleaner> cleaners = new ArrayList<>();
    
      try (Connection conn = DBConnection.getConnection();
           Statement stmt = conn.createStatement();
           ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
        
          while (rs.next()) {
              cleaners.add(mapResultSetToCleaner(rs));
          }
      }
      return cleaners;
  }
  public Cleaner getCleanerById(int cleanerID) throws SQLException {
      try (Connection conn = DBConnection.getConnection();
           PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
        
          stmt.setInt(1, cleanerID);
          try (ResultSet rs = stmt.executeQuery()) {
              if (rs.next()) {
                  return mapResultSetToCleaner(rs);
              }
          }
      }
      return null;
  }
  public Cleaner getCleanerByContactNumber(String contactNumber) throws SQLException {
      String sql = "SELECT * FROM cleaner WHERE ContactNumber = ?";
      try (Connection conn = DBConnection.getConnection();
           PreparedStatement stmt = conn.prepareStatement(sql)) {
        
          stmt.setString(1, contactNumber);
          try (ResultSet rs = stmt.executeQuery()) {
              if (rs.next()) {
                  return mapResultSetToCleaner(rs);
              }
          }
      }
      return null;
  }
  public boolean updateCleaner(Cleaner cleaner) throws SQLException {
      // Validate ContactNumber uniqueness (excluding self)
      Cleaner existing = getCleanerByContactNumber(cleaner.getContactNumber());
      if (existing != null && existing.getCleanerID() != cleaner.getCleanerID()) {
          throw new SQLException("ContactNumber must be unique: " + cleaner.getContactNumber());
      }
      // Validate BuildingID if not null
      if (cleaner.getBuildingID() != null && buildingDAO.getBuildingById(cleaner.getBuildingID()) == null) {
          throw new IllegalArgumentException("Invalid BuildingID: " + cleaner.getBuildingID());
      }
      try (Connection conn = DBConnection.getConnection();
           PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
        
          setUpdateParameters(stmt, cleaner);
        
          return stmt.executeUpdate() > 0;
      }
  }
  public boolean deleteCleaner(int cleanerID) throws SQLException {
      try (Connection conn = DBConnection.getConnection();
           PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
        
          stmt.setInt(1, cleanerID);
          return stmt.executeUpdate() > 0;
      }
  }
 
//   /**
//    * Retrieves all cleaners grouped by BuildingID. Cleaners with NULL BuildingID are grouped under a null key.
//    * @return A Map where the key is the BuildingID (or null) and the value is a List of Cleaners for that building.
//    * @throws SQLException If a database error occurs.
//    */
//   public Map<Integer, List<Cleaner>> getCleanersGroupedByBuilding() throws SQLException {
//       String sql = "SELECT * FROM cleaner ORDER BY BuildingID";
//       Map<Integer, List<Cleaner>> cleanersByBuilding = new HashMap<>();
//
//       try (Connection conn = DBConnection.getConnection();
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery(sql)) {
//
//           while (rs.next()) {
//               Cleaner cleaner = mapResultSetToCleaner(rs);
//               Integer buildingID = cleaner.getBuildingID(); // May be null
//
//               // Get or create the list for this BuildingID
//               cleanersByBuilding.computeIfAbsent(buildingID, k -> new ArrayList<>()).add(cleaner);
//           }
//       }
//       return cleanersByBuilding;
//   }
 
  /**
   * Retrieves all cleaners assigned to a specific BuildingID.
   * @param buildingID The ID of the building to filter cleaners by.
   * @return A List of Cleaners assigned to the specified BuildingID.
   * @throws SQLException If a database error occurs.
   */
  public List<Cleaner> getCleanersByBuildingID(int buildingID) throws SQLException {
      String sql = "SELECT * FROM cleaner WHERE BuildingID = ?";
      List<Cleaner> cleaners = new ArrayList<>();
      try (Connection conn = DBConnection.getConnection();
           PreparedStatement stmt = conn.prepareStatement(sql)) {
          stmt.setInt(1, buildingID);
          try (ResultSet rs = stmt.executeQuery()) {
              while (rs.next()) {
                  cleaners.add(mapResultSetToCleaner(rs));
              }
          }
      }
      return cleaners;
  }
  private void setInsertParameters(PreparedStatement stmt, Cleaner cleaner) throws SQLException {
      stmt.setInt(1, cleaner.getCleanerID());
      stmt.setString(2, cleaner.getFirstName());
      stmt.setString(3, cleaner.getLastName());
      stmt.setObject(4, cleaner.getBuildingID()); // Handles NULL
      stmt.setString(5, cleaner.getContactNumber());
  }
  private void setUpdateParameters(PreparedStatement stmt, Cleaner cleaner) throws SQLException {
      stmt.setString(1, cleaner.getFirstName());
      stmt.setString(2, cleaner.getLastName());
      stmt.setObject(3, cleaner.getBuildingID()); // Handles NULL
      stmt.setString(4, cleaner.getContactNumber());
      stmt.setInt(5, cleaner.getCleanerID());
  }
  private Cleaner mapResultSetToCleaner(ResultSet rs) throws SQLException {
      return new Cleaner(
          rs.getInt("CleanerID"),
          rs.getString("FirstName"),
          rs.getString("LastName"),
          rs.getObject("BuildingID") != null ? rs.getInt("BuildingID") : null,
          rs.getString("ContactNumber")
      );
  }
}




