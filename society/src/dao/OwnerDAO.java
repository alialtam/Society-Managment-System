package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Owner;

public class OwnerDAO {
   private Connection connection;
   // Constructor to initialize with database connection
   public OwnerDAO(Connection connection) {
       this.connection = connection;
   }
   // Save a new owner
   public void save(Owner owner) throws SQLException {
       String sql = "INSERT INTO owner (ResidentID, OwnerStartDate, OwnershipPercentage) " +
                    "VALUES (?, ?, ?)";
       try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
           stmt.setInt(1, owner.getResidentId());
           stmt.setDate(2, Date.valueOf(owner.getOwnerStartDate()));
           stmt.setDouble(3, owner.getOwnershipPercentage());
           stmt.executeUpdate();
           // Retrieve the auto-generated OwnerID
           ResultSet generatedKeys = stmt.getGeneratedKeys();
           if (generatedKeys.next()) {
               owner.setOwnerId(generatedKeys.getInt(1));
           }
       }
   }
   // Update an existing owner
   public void update(Owner owner) throws SQLException {
       String sql = "UPDATE owner SET ResidentID = ?, OwnerStartDate = ?, OwnershipPercentage = ? " +
                    "WHERE OwnerID = ?";
       try (PreparedStatement stmt = connection.prepareStatement(sql)) {
           stmt.setInt(1, owner.getResidentId());
           stmt.setDate(2, Date.valueOf(owner.getOwnerStartDate()));
           stmt.setDouble(3, owner.getOwnershipPercentage());
           stmt.setInt(4, owner.getOwnerId());
           stmt.executeUpdate();
       }
   }
   // Delete an owner by OwnerID
   public void delete(Integer ownerId) throws SQLException {
       String sql = "DELETE FROM owner WHERE OwnerID = ?";
       try (PreparedStatement stmt = connection.prepareStatement(sql)) {
           stmt.setInt(1, ownerId);
           stmt.executeUpdate();
       }
   }
   // Find an owner by OwnerID
   public Owner findById(Integer ownerId) throws SQLException {
       String sql = "SELECT * FROM owner WHERE OwnerID = ?";
       try (PreparedStatement stmt = connection.prepareStatement(sql)) {
           stmt.setInt(1, ownerId);
           ResultSet rs = stmt.executeQuery();
           if (rs.next()) {
               return mapResultSetToOwner(rs);
           }
           return null;
       }
   }
   // Find all owners
   public List<Owner> findAll() throws SQLException {
       List<Owner> owners = new ArrayList<>();
       String sql = "SELECT * FROM owner";
       try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
           while (rs.next()) {
               owners.add(mapResultSetToOwner(rs));
           }
       }
       return owners;
   }
   // Helper method to map ResultSet to Owner object
   private Owner mapResultSetToOwner(ResultSet rs) throws SQLException {
       Owner owner = new Owner();
       owner.setOwnerId(rs.getInt("OwnerID"));
       owner.setResidentId(rs.getInt("ResidentID"));
       owner.setOwnerStartDate(rs.getDate("OwnerStartDate").toLocalDate());
       owner.setOwnershipPercentage(rs.getDouble("OwnershipPercentage"));
       // Note: firstName, lastName, email are not stored in owner table, so they remain null
       return owner;
   }
}




