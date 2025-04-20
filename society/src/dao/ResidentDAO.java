package dao;
import model.Resident;
import util.AlertUtil;
import util.InputValidator;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ResidentDAO {
   // ✅ Add a new resident with transaction handling and validation
   public boolean addResident(Resident resident) {
       if (!validateResident(resident)) return false;
      
       Connection conn = null;
       try {
           conn = DBConnection.getConnection();
           conn.setAutoCommit(false);
          
           int newId = getNextResidentId(conn);
          
           String sql = "INSERT INTO resident (ResidentID, FirstName, LastName, EmailAddress, FlatID, MoveInDate, ContactNumber) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
           try (PreparedStatement stmt = conn.prepareStatement(sql)) {
               stmt.setInt(1, newId);
               stmt.setString(2, resident.getFirstName());
               stmt.setString(3, resident.getLastName());
               stmt.setString(4, resident.getEmail());
               stmt.setObject(5, resident.getFlatId() > 0 ? resident.getFlatId() : null, Types.INTEGER);
               stmt.setDate(6, Date.valueOf(resident.getMoveInDate()));
               stmt.setString(7, resident.getPhone());
              
               stmt.executeUpdate();
               conn.commit();
               resident.setResidentId(newId);
               return true;
           }
       } catch (SQLException e) {
           rollback(conn);
           AlertUtil.showErrorAlert("Database Error", "Failed to add resident: " + e.getMessage());
           return false;
       } finally {
           closeConnection(conn);
       }
   }
   // ✅ Update resident with full validation
   public boolean updateResident(Resident resident) {
       if (!validateResident(resident) || resident.getResidentId() <= 0) {
           AlertUtil.showErrorAlert("Validation Error", "Invalid resident data");
           return false;
       }
       Connection conn = null;
       try {
           conn = DBConnection.getConnection();
           conn.setAutoCommit(false);
          
           String sql = "UPDATE resident SET FirstName=?, LastName=?, EmailAddress=?, FlatID=?, MoveInDate=?, ContactNumber=? " +
                        "WHERE ResidentID=?";
           try (PreparedStatement stmt = conn.prepareStatement(sql)) {
               stmt.setString(1, resident.getFirstName());
               stmt.setString(2, resident.getLastName());
               stmt.setString(3, resident.getEmail());
               stmt.setObject(4, resident.getFlatId() > 0 ? resident.getFlatId() : null, Types.INTEGER);
               stmt.setDate(5, Date.valueOf(resident.getMoveInDate()));
               stmt.setString(6, resident.getPhone());
               stmt.setInt(7, resident.getResidentId());
              
               int affectedRows = stmt.executeUpdate();
               conn.commit();
               return affectedRows > 0;
           }
       } catch (SQLException e) {
           rollback(conn);
           AlertUtil.showErrorAlert("Database Error", "Failed to update resident: " + e.getMessage());
           return false;
       } finally {
           closeConnection(conn);
       }
   }
   // ✅ Delete resident with checks
   public boolean deleteResident(int residentId) {
       if (residentId <= 0) {
           AlertUtil.showErrorAlert("Validation Error", "Invalid resident ID");
           return false;
       }
       Connection conn = null;
       try {
           conn = DBConnection.getConnection();
           conn.setAutoCommit(false);
          
           if (hasTenantRecords(conn, residentId)) {
               AlertUtil.showErrorAlert("Constraint Error", "Cannot delete resident with tenant records");
               return false;
           }
          
           try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM resident WHERE ResidentID=?")) {
               stmt.setInt(1, residentId);
               int affectedRows = stmt.executeUpdate();
               conn.commit();
               return affectedRows > 0;
           }
       } catch (SQLException e) {
           rollback(conn);
           AlertUtil.showErrorAlert("Database Error", "Failed to delete resident: " + e.getMessage());
           return false;
       } finally {
           closeConnection(conn);
       }
   }
   // ✅ Get all residents (read-only, no transaction needed)
   public List<Resident> getAllResidents() {
       List<Resident> residents = new ArrayList<>();
       String sql = "SELECT * FROM resident";
      
       try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
          
           while (rs.next()) {
               residents.add(extractResidentFromResultSet(rs));
           }
       } catch (SQLException e) {
           AlertUtil.showErrorAlert("Database Error", "Failed to load residents: " + e.getMessage());
       }
       return residents;
   }
   // ✅ Get single resident
   public Resident getResidentById(int residentId) {
       String sql = "SELECT * FROM resident WHERE ResidentID=?";
      
       try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
          
           stmt.setInt(1, residentId);
           try (ResultSet rs = stmt.executeQuery()) {
               return rs.next() ? extractResidentFromResultSet(rs) : null;
           }
       } catch (SQLException e) {
           AlertUtil.showErrorAlert("Database Error", "Failed to load resident: " + e.getMessage());
           return null;
       }
   }
   // ✅ Check if flat exists
   public boolean flatExists(int flatId) {
       if (flatId <= 0) return false;
      
       String sql = "SELECT 1 FROM flat WHERE FlatID=? LIMIT 1";
       try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
          
           stmt.setInt(1, flatId);
           return stmt.executeQuery().next();
       } catch (SQLException e) {
           AlertUtil.showErrorAlert("Database Error", "Flat check failed: " + e.getMessage());
           return false;
       }
   }
   // ✅ Phone number uniqueness check
   public boolean isPhoneNumberUnique(String phone, int residentId) {
       if (phone == null || phone.trim().isEmpty()) return true;
      
       String sql = "SELECT ResidentID FROM resident WHERE ContactNumber=?";
       try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
          
           stmt.setString(1, phone.trim());
           ResultSet rs = stmt.executeQuery();
          
           if (!rs.next()) return true;
           return rs.getInt(1) == residentId;
       } catch (SQLException e) {
           AlertUtil.showErrorAlert("Database Error", "Phone validation failed: " + e.getMessage());
           return false;
       }
   }
   
   
	// Get all residents ordered by MoveInDate
	public List<Resident> getResidentsOrderedByMoveInDate() {
	    List<Resident> residents = new ArrayList<>();
	    String sql = "SELECT * FROM resident ORDER BY MoveInDate";
	
	    try (Connection conn = DBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	
	        while (rs.next()) {
	            residents.add(extractResidentFromResultSet(rs));
	        }
	    } catch (SQLException e) {
	        AlertUtil.showErrorAlert("Database Error", "Failed to load residents: " + e.getMessage());
	    }
	    return residents;
	}
	
	// Get residents who moved in before '2024-01-01'
	public List<Resident> getResidentsMovedBefore2024() {
	    List<Resident> residents = new ArrayList<>();
	    String sql = "SELECT * FROM resident WHERE MoveInDate < '2024-01-01'";

	    try (Connection conn = DBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            residents.add(extractResidentFromResultSet(rs));
	        }
	    } catch (SQLException e) {
	        AlertUtil.showErrorAlert("Database Error", "Failed to load residents: " + e.getMessage());
	    }
	    return residents;
	}
	
	
	// Get residents whose first name starts with 'A'
	public List<Resident> getResidentsByFirstNameStartingWithA() {
	    List<Resident> residents = new ArrayList<>();
	    String sql = "SELECT * FROM resident WHERE FirstName LIKE 'A%'";

	    try (Connection conn = DBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            residents.add(extractResidentFromResultSet(rs));
	        }
	    } catch (SQLException e) {
	        AlertUtil.showErrorAlert("Database Error", "Failed to load residents: " + e.getMessage());
	    }
	    return residents;
	}


   // ===== PRIVATE HELPER METHODS =====
  
   private boolean validateResident(Resident resident) {
       return InputValidator.validateStringInput(resident.getFirstName(), "First Name") &&
              InputValidator.validateStringInput(resident.getLastName(), "Last Name") &&
              InputValidator.validateStringInput(resident.getEmail(), "Email") &&
              InputValidator.validateStringInput(resident.getPhone(), "Phone") &&
              resident.getMoveInDate() != null;
   }
  
   private int getNextResidentId(Connection conn) throws SQLException {
       try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(ResidentID) FROM resident")) {
           return rs.next() ? rs.getInt(1) + 1 : 1;
       }
   }
  
   private Resident extractResidentFromResultSet(ResultSet rs) throws SQLException {
       return new Resident(
           rs.getInt("ResidentID"),
           rs.getString("FirstName"),
           rs.getString("LastName"),
           rs.getString("EmailAddress"),
           rs.getInt("FlatID"),
           rs.getDate("MoveInDate").toLocalDate(),
           rs.getString("ContactNumber")
       );
   }
  
   private boolean hasTenantRecords(Connection conn, int residentId) throws SQLException {
       try (PreparedStatement stmt = conn.prepareStatement(
               "SELECT 1 FROM tenant WHERE ResidentID=? LIMIT 1")) {
           stmt.setInt(1, residentId);
           return stmt.executeQuery().next();
       }
   }
  
   private void rollback(Connection conn) {
       if (conn != null) {
           try {
               conn.rollback();
           } catch (SQLException ex) {
               AlertUtil.showErrorAlert("Database Error", "Rollback failed: " + ex.getMessage());
           }
       }
   }
   public List<Resident> getResidentsNotTenants() throws SQLException {
       String sql = "SELECT * FROM resident WHERE ResidentID NOT IN (SELECT ResidentID FROM tenant)";
       List<Resident> residents = new ArrayList<>();
      
       try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
          
           while (rs.next()) {
               Resident resident = new Resident(0, sql, sql, sql, 0, null, sql);
               resident.setResidentId(rs.getInt("ResidentID"));
               resident.setFirstName(rs.getString("FirstName"));
               resident.setLastName(rs.getString("LastName"));
               residents.add(resident);
           }
       }
       return residents;
   }
  
   private void closeConnection(Connection conn) {
       if (conn != null) {
           try {
               conn.setAutoCommit(true);
               conn.close();
           } catch (SQLException e) {
               AlertUtil.showErrorAlert("Database Error", "Connection close failed: " + e.getMessage());
           }
       }
   }
}




