package model;
public class Flat {
   public enum FlatType {
       BHK1("1BHK"),
       BHK2("2BHK"),
       BHK3("3BHK"),
       PENTHOUSE("Penthouse");
       private final String dbValue;
       FlatType(String dbValue) {
           this.dbValue = dbValue;
       }
       public String getDbValue() {
           return dbValue;
       }
       public static FlatType fromString(String dbValue) {
           for (FlatType type : FlatType.values()) {
               if (type.dbValue.equalsIgnoreCase(dbValue)) {
                   return type;
               }
           }
           throw new IllegalArgumentException("Unknown flat type: " + dbValue);
       }
   }
   private int flatID;
   private String flatNumber;
   private int buildingID;
   private FlatType flatType;
   private String slotNumber;
   // Constructor for new flats (flatID to be generated)
   public Flat(String flatNumber, int buildingID, FlatType flatType, String slotNumber) {
       this(-1, flatNumber, buildingID, flatType, slotNumber);
   }
   // Constructor for existing flats
   public Flat(int flatID, String flatNumber, int buildingID, FlatType flatType, String slotNumber) {
       this.flatID = flatID;
       this.flatNumber = flatNumber;
       this.buildingID = buildingID;
       this.flatType = flatType;
       this.slotNumber = slotNumber;
   }
   // Getters and Setters
   public int getFlatID() { return flatID; }
   public void setFlatID(int flatID) { this.flatID = flatID; }
   public String getFlatNumber() { return flatNumber; }
   public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
   public int getBuildingID() { return buildingID; }
   public void setBuildingID(int buildingID) { this.buildingID = buildingID; }
   public FlatType getFlatType() { return flatType; }
   public void setFlatType(FlatType flatType) { this.flatType = flatType; }
   public String getSlotNumber() { return slotNumber; }
   public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }
   @Override
   public String toString() {
       return String.format("Flat{ID=%d, Number='%s', BuildingID=%d, Type=%s, Slot='%s'}",
               flatID, flatNumber, buildingID, flatType.getDbValue(), slotNumber);
   }
}


