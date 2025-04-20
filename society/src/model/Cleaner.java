package model;
public class Cleaner {
   private int cleanerID;
   private String firstName;
   private String lastName;
   private Integer buildingID; // Nullable Integer to match NULL in database
   private String contactNumber;
   public Cleaner(int cleanerID, String firstName, String lastName, Integer buildingID, String contactNumber) {
       this.cleanerID = cleanerID;
       this.firstName = firstName;
       this.lastName = lastName;
       this.buildingID = buildingID;
       this.contactNumber = contactNumber;
   }
   public Cleaner(String firstName, String lastName, Integer buildingID, String contactNumber) {
       this.firstName = firstName;
       this.lastName = lastName;
       this.buildingID = buildingID;
       this.contactNumber = contactNumber;
   }
   // Getters and Setters
   public int getCleanerID() { return cleanerID; }
   public void setCleanerID(int cleanerID) { this.cleanerID = cleanerID; }
   public String getFirstName() { return firstName; }
   public void setFirstName(String firstName) { this.firstName = firstName; }
   public String getLastName() { return lastName; }
   public void setLastName(String lastName) { this.lastName = lastName; }
   public Integer getBuildingID() { return buildingID; }
   public void setBuildingID(Integer buildingID) { this.buildingID = buildingID; }
   public String getContactNumber() { return contactNumber; }
   public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
   @Override
   public String toString() {
       return "Cleaner{" +
               "cleanerID=" + cleanerID +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", buildingID=" + buildingID +
               ", contactNumber='" + contactNumber + '\'' +
               '}';
   }
}


