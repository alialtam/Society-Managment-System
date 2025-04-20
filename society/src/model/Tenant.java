package model;
import java.time.LocalDate;
public class Tenant extends Person {
   private Integer tenantId;
   private LocalDate leaseStartDate;
   private LocalDate leaseEndDate;
   private Integer monthlyRent;
   // Default constructor
   public Tenant() {
       super();
   }
   // Parameterized constructor
   public Tenant(Integer residentId, String firstName, String lastName, String email,
                 Integer tenantId, LocalDate leaseStartDate, LocalDate leaseEndDate, Integer monthlyRent) {
       super(residentId, firstName, lastName, email);
       this.tenantId = tenantId;
       this.leaseStartDate = leaseStartDate;
       this.leaseEndDate = leaseEndDate;
       this.monthlyRent = monthlyRent;
   }
   // Getters and Setters
   public Integer getTenantId() {
       return tenantId;
   }
   public void setTenantId(Integer tenantId) {
       this.tenantId = tenantId;
   }
   public LocalDate getLeaseStartDate() {
       return leaseStartDate;
   }
   public void setLeaseStartDate(LocalDate leaseStartDate) {
       this.leaseStartDate = leaseStartDate;
   }
   public LocalDate getLeaseEndDate() {
       return leaseEndDate;
   }
   public void setLeaseEndDate(LocalDate leaseEndDate) {
       this.leaseEndDate = leaseEndDate;
   }
   public Integer getMonthlyRent() {
       return monthlyRent;
   }
   public void setMonthlyRent(Integer monthlyRent) {
       this.monthlyRent = monthlyRent;
   }
}




