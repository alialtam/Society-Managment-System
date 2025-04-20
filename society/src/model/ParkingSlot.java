package model;
public class ParkingSlot {
   private String slotNumber;
   private String vehicleType;
   // Constructor
   public ParkingSlot(String slotNumber, String vehicleType) {
       this.slotNumber = slotNumber;
       this.vehicleType = vehicleType;
   }
   // Getters and Setters
   public String getSlotNumber() {
       return slotNumber;
   }
   public void setSlotNumber(String slotNumber) {
       this.slotNumber = slotNumber;
   }
   public String getVehicleType() {
       return vehicleType;
   }
   public void setVehicleType(String vehicleType) {
       this.vehicleType = vehicleType;
   }
}


