package dao;

import model.Gate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GateDAO {
    private static final String TABLE_NAME = "gate";

    // INSERT with validation
    public static boolean insertGate(Gate gate) throws SQLException {
        String query = "INSERT INTO " + TABLE_NAME + " (GateNumber, Description) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, gate.getGateNumber());
            stmt.setString(2, gate.getDescription());
            
            return stmt.executeUpdate() > 0;
        }
    }

    // UPDATE with validation
    public static boolean updateGate(Gate gate) throws SQLException {
        String query = "UPDATE " + TABLE_NAME + " SET Description=? WHERE GateNumber=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, gate.getDescription());
            stmt.setInt(2, gate.getGateNumber());
            
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE with existence check
    public static boolean deleteGate(int gateNumber) throws SQLException {
        String query = "DELETE FROM " + TABLE_NAME + " WHERE GateNumber=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, gateNumber);
            return stmt.executeUpdate() > 0;
        }
    }

    // SELECT ALL
    public static List<Gate> getAllGates() throws SQLException {
        List<Gate> gates = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                gates.add(new Gate(
                    rs.getInt("GateNumber"),
                    rs.getString("Description")
                ));
            }
        }
        return gates;
    }

    // SELECT by number
    public static Optional<Gate> getGateByNumber(int gateNumber) throws SQLException {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE GateNumber=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, gateNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? 
                    Optional.of(new Gate(rs.getInt("GateNumber"), rs.getString("Description"))) :
                    Optional.empty();
            }
        }
    }

    // SEARCH by description
    public static List<Gate> searchGatesByDescription(String keyword) throws SQLException {
        List<Gate> gates = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE Description LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    gates.add(new Gate(
                        rs.getInt("GateNumber"),
                        rs.getString("Description")
                    ));
                }
            }
        }
        return gates;
    }
    
    public static List<Integer> getAllGateNumbers() throws SQLException {
        List<Integer> gateNumbers = new ArrayList<>();
        String query = "SELECT GateNumber FROM gate";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                gateNumbers.add(rs.getInt("GateNumber"));
            }
        }
        return gateNumbers;
    }
}