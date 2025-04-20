package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ParkingSlot;
public class ParkingSlotDAO {
   private Connection getConnection() throws SQLException {
       return DBConnection.getConnection(); // Assumes DBConnection.java has a static getConnection() method
   }
   // Create
   public void addParkingSlot(ParkingSlot slot) throws SQLException {
       String sql = "INSERT INTO parkingslot (SlotNumber, VehicleType) VALUES (?, ?)";
       try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
           stmt.setString(1, slot.getSlotNumber());
           stmt.setString(2, slot.getVehicleType());
           stmt.executeUpdate();
       }
   }
   // Read (All)
   public List<ParkingSlot> getAllParkingSlots() throws SQLException {
       List<ParkingSlot> slots = new ArrayList<>();
       String sql = "SELECT * FROM parkingslot";
       try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
           while (rs.next()) {
               slots.add(new ParkingSlot(rs.getString("SlotNumber"), rs.getString("VehicleType")));
           }
       }
       return slots;
   }
   // Read (By SlotNumber)
   public ParkingSlot getParkingSlotById(String slotNumber) throws SQLException {
       String sql = "SELECT * FROM parkingslot WHERE SlotNumber = ?";
       try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
           stmt.setString(1, slotNumber);
           try (ResultSet rs = stmt.executeQuery()) {
               if (rs.next()) {
                   return new ParkingSlot(rs.getString("SlotNumber"), rs.getString("VehicleType"));
               }
           }
       }
       return null;
   }
   // Update
   public void updateParkingSlot(ParkingSlot slot) throws SQLException {
       String sql = "UPDATE parkingslot SET VehicleType = ? WHERE SlotNumber = ?";
       try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
           stmt.setString(1, slot.getVehicleType());
           stmt.setString(2, slot.getSlotNumber());
           stmt.executeUpdate();
       }
   }
   // Delete
   public void deleteParkingSlot(String slotNumber) throws SQLException {
       String sql = "DELETE FROM parkingslot WHERE SlotNumber = ?";
       try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
           stmt.setString(1, slotNumber);
           stmt.executeUpdate();
       }
   }
}


