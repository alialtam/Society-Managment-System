package model;
import java.time.LocalDate;
public class Resident {
   private int residentId;
   private String firstName;
   private String lastName;
   private String email;
   private int flatId;
   private LocalDate moveInDate;
   private String phone;
   public Resident(int residentId, String firstName, String lastName, String email,
                  int flatId, LocalDate moveInDate, String phone) {
       this.residentId = residentId;
       this.firstName = firstName;
       this.lastName = lastName;
       this.email = email;
       this.flatId = flatId;
       this.moveInDate = moveInDate;
       this.phone = phone;
   }
   // Getters
   public int getResidentId() { return residentId; }
   public String getFirstName() { return firstName; }
   public String getLastName() { return lastName; }
   public String getEmail() { return email; }
   public int getFlatId() { return flatId; }
   public LocalDate getMoveInDate() { return moveInDate; }
   public String getPhone() { return phone; }
   // Setters
   public void setResidentId(int residentId) { this.residentId = residentId; }
   public void setFirstName(String firstName) { this.firstName = firstName; }
   public void setLastName(String lastName) { this.lastName = lastName; }
   public void setEmail(String email) { this.email = email; }
   public void setFlatId(int flatId) { this.flatId = flatId; }
   public void setMoveInDate(LocalDate moveInDate) { this.moveInDate = moveInDate; }
   public void setPhone(String phone) { this.phone = phone; }
   // Helper methods
   public String getFullName() {
       return firstName + " " + lastName;
   }
// Added method to resolve the error
   public String getContactNumber() {
       return getPhone();
   }
}




