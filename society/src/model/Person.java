package model;
public class Person {
   private Integer residentId;
   private String firstName;
   private String lastName;
   private String email;
   // Default constructor
   public Person() {}
   // Parameterized constructor
   public Person(Integer residentId, String firstName, String lastName, String email) {
       this.residentId = residentId;
       this.firstName = firstName;
       this.lastName = lastName;
       this.email = email;
   }
   // Getters and Setters
   public Integer getResidentId() {
       return residentId;
   }
   public void setResidentId(Integer residentId) {
       this.residentId = residentId;
   }
   public String getFirstName() {
       return firstName;
   }
   public void setFirstName(String firstName) {
       this.firstName = firstName;
   }
   public String getLastName() {
       return lastName;
   }
   public void setLastName(String lastName) {
       this.lastName = lastName;
   }
   public String getEmail() {
       return email;
   }
   public void setEmail(String email) {
       this.email = email;
   }
}




