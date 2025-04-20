package dao;

import model.SecurityPersonnel;
import util.AlertUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SecurityPersonnelDAO {
    private static final String TABLE = "securitypersonnel";

    // INSERT with auto-generated ID
    public static boolean insert(SecurityPersonnel sp) throws SQLException {
        if (!isContactUnique(sp.getContactNo(), 0)) {
            throw new SQLException("Contact number already exists");
        }

        String sql = "INSERT INTO " + TABLE + 
                   " (FirstName, LastName, ContactNO, ShiftTimings, GateNumber) " +
                   "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, sp.getFirstName());
            stmt.setString(2, sp.getLastName());
            stmt.setString(3, sp.getContactNo());
            stmt.setString(4, sp.getShiftTimings());
            stmt.setInt(5, sp.getGateNumber());

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        sp.setSecurityId(generatedKeys.getInt(1)); // Update the model with generated ID
                        return true;
                    }
                }
            }
            return false;
        }
    }

    // UPDATE remains the same
    public static boolean update(SecurityPersonnel sp) throws SQLException {
        if (!isContactUnique(sp.getContactNo(), sp.getSecurityId())) {
            throw new SQLException("Contact number already exists");
        }

        String sql = "UPDATE " + TABLE + " SET " +
                   "FirstName=?, LastName=?, ContactNO=?, ShiftTimings=?, GateNumber=? " +
                   "WHERE SecurityID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sp.getFirstName());
            stmt.setString(2, sp.getLastName());
            stmt.setString(3, sp.getContactNo());
            stmt.setString(4, sp.getShiftTimings());
            stmt.setInt(5, sp.getGateNumber());
            stmt.setInt(6, sp.getSecurityId());

            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE remains the same
    public static boolean delete(int securityId) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE SecurityID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, securityId);
            return stmt.executeUpdate() > 0;
        }
    }

    // GET ALL remains the same
    public static List<SecurityPersonnel> getAll() throws SQLException {
        List<SecurityPersonnel> personnel = new ArrayList<>();
        String sql = "SELECT s.*, g.Description as GateDescription FROM " + TABLE + " s " +
                     "LEFT JOIN gate g ON s.GateNumber = g.GateNumber";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                personnel.add(mapRowToPersonnel(rs));
            }
        }
        return personnel;
    }

    // GET BY ID remains the same
    public static Optional<SecurityPersonnel> getById(int securityId) throws SQLException {
        String sql = "SELECT * FROM " + TABLE + " WHERE SecurityID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, securityId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapRowToPersonnel(rs)) : Optional.empty();
            }
        }
    }

    // GET BY GATE remains the same
    public static List<SecurityPersonnel> getByGate(int gateNumber) throws SQLException {
        List<SecurityPersonnel> personnel = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " WHERE GateNumber=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gateNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    personnel.add(mapRowToPersonnel(rs));
                }
            }
        }
        return personnel;
    }

    // CHECK CONTACT UNIQUENESS remains the same
    private static boolean isContactUnique(String contactNo, int excludeId) throws SQLException {
        String sql = "SELECT 1 FROM " + TABLE + " WHERE ContactNO=? AND SecurityID!=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, contactNo);
            stmt.setInt(2, excludeId);
            return !stmt.executeQuery().next();
        }
    }

    // MAP RESULT SET remains the same
    private static SecurityPersonnel mapRowToPersonnel(ResultSet rs) throws SQLException {
        return new SecurityPersonnel(
            rs.getInt("SecurityID"),
            rs.getString("FirstName"),
            rs.getString("LastName"),
            rs.getString("ContactNO"),
            rs.getString("ShiftTimings"),
            rs.getInt("GateNumber")
        );
    }
}