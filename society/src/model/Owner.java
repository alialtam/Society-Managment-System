package model;
import java.time.LocalDate;
public class Owner extends Person {
   private Integer ownerId;
   private LocalDate ownerStartDate;
   private Double ownershipPercentage;
   // Default constructor
   public Owner() {
       super();
   }
   // Parameterized constructor
   public Owner(Integer residentId, String firstName, String lastName, String email,
                Integer ownerId, LocalDate ownerStartDate, Double ownershipPercentage) {
       super(residentId, firstName, lastName, email);
       this.ownerId = ownerId;
       this.ownerStartDate = ownerStartDate;
       this.ownershipPercentage = ownershipPercentage;
   }
   // Getters and Setters
   public Integer getOwnerId() {
       return ownerId;
   }
   public void setOwnerId(Integer ownerId) {
       this.ownerId = ownerId;
   }
   public LocalDate getOwnerStartDate() {
       return ownerStartDate;
   }
   public void setOwnerStartDate(LocalDate ownerStartDate) {
       this.ownerStartDate = ownerStartDate;
   }
   public Double getOwnershipPercentage() {
       return ownershipPercentage;
   }
   public void setOwnershipPercentage(Double ownershipPercentage) {
       this.ownershipPercentage = ownershipPercentage;
   }
}




