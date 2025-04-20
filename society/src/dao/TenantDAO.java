package dao;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Tenant;
public class TenantDAO {
   private Connection connection;
   // Constructor to initialize with database connection
   public TenantDAO(Connection connection) {
       this.connection = connection;
   }
   // Save a new tenant
   public void save(Tenant tenant) throws SQLException {
       String sql = "INSERT INTO tenant (ResidentID, LeaseStartDate, LeaseEndDate, MonthlyRent) " +
                    "VALUES (?, ?, ?, ?)";
       try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
           stmt.setInt(1, tenant.getResidentId());
           stmt.setDate(2, Date.valueOf(tenant.getLeaseStartDate()));
           stmt.setDate(3, tenant.getLeaseEndDate() != null ? Date.valueOf(tenant.getLeaseEndDate()) : null);
           stmt.setObject(4, tenant.getMonthlyRent(), Types.INTEGER);
           stmt.executeUpdate();
           // Retrieve the auto-generated TenantID
           ResultSet generatedKeys = stmt.getGeneratedKeys();
           if (generatedKeys.next()) {
               tenant.setTenantId(generatedKeys.getInt(1));
           }
       }
   }
   // Update an existing tenant
   public void update(Tenant tenant) throws SQLException {
       String sql = "UPDATE tenant SET ResidentID = ?, LeaseStartDate = ?, LeaseEndDate = ?, MonthlyRent = ? " +
                    "WHERE TenantID = ?";
       try (PreparedStatement stmt = connection.prepareStatement(sql)) {
           stmt.setInt(1, tenant.getResidentId());
           stmt.setDate(2, Date.valueOf(tenant.getLeaseStartDate()));
           stmt.setDate(3, tenant.getLeaseEndDate() != null ? Date.valueOf(tenant.getLeaseEndDate()) : null);
           stmt.setObject(4, tenant.getMonthlyRent(), Types.INTEGER);
           stmt.setInt(5, tenant.getTenantId());
           stmt.executeUpdate();
       }
   }
   // Delete a tenant by TenantID
   public void delete(Integer tenantId) throws SQLException {
       String sql = "DELETE FROM tenant WHERE TenantID = ?";
       try (PreparedStatement stmt = connection.prepareStatement(sql)) {
           stmt.setInt(1, tenantId);
           stmt.executeUpdate();
       }
   }
   // Find a tenant by TenantID
   public Tenant findById(Integer tenantId) throws SQLException {
       String sql = "SELECT * FROM tenant WHERE TenantID = ?";
       try (PreparedStatement stmt = connection.prepareStatement(sql)) {
           stmt.setInt(1, tenantId);
           ResultSet rs = stmt.executeQuery();
           if (rs.next()) {
               return mapResultSetToTenant(rs);
           }
           return null;
       }
   }
   // Find all tenants
   public List<Tenant> findAll() throws SQLException {
       List<Tenant> tenants = new ArrayList<>();
       String sql = "SELECT * FROM tenant";
       try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
           while (rs.next()) {
               tenants.add(mapResultSetToTenant(rs));
           }
       }
       return tenants;
   }
   // Helper method to map ResultSet to Tenant object
   private Tenant mapResultSetToTenant(ResultSet rs) throws SQLException {
       Tenant tenant = new Tenant();
       tenant.setTenantId(rs.getInt("TenantID"));
       tenant.setResidentId(rs.getInt("ResidentID"));
       tenant.setLeaseStartDate(rs.getDate("LeaseStartDate").toLocalDate());
       Date leaseEndDate = rs.getDate("LeaseEndDate");
       tenant.setLeaseEndDate(leaseEndDate != null ? leaseEndDate.toLocalDate() : null);
       tenant.setMonthlyRent(rs.getInt("MonthlyRent"));
       if (rs.wasNull()) {
           tenant.setMonthlyRent(null);
       }
       // Note: firstName, lastName, email are not stored in tenant table, so they remain null
       return tenant;
   }
}




