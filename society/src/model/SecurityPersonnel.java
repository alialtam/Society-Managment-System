package model;

public class SecurityPersonnel {
    private int securityId;  // PRIMARY KEY (matches your schema)
    private String firstName;     // VARCHAR(50)
    private String lastName;      // VARCHAR(50)
    private String contactNo;     // VARCHAR(15) UNIQUE
    private String shiftTimings;  // ENUM('Morning','Evening','Night') NULLABLE
    private int gateNumber;       // INT (foreign key to gate table)

    public SecurityPersonnel(int securityId, String firstName, String lastName, 
                           String contactNo, String shiftTimings, int gateNumber) {
        this.securityId = 0;
		setSecurityId(securityId);
        setFirstName(firstName);
        setLastName(lastName);
        setContactNo(contactNo);
        setShiftTimings(shiftTimings); // Allows null
        setGateNumber(gateNumber);
    }

   
    public void setSecurityId(int securityId) {
        this.securityId = securityId;
    }

    private void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (firstName.length() > 50) {
            throw new IllegalArgumentException("First name exceeds 50 characters");
        }
        this.firstName = firstName.trim();
    }

    private void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (lastName.length() > 50) {
            throw new IllegalArgumentException("Last name exceeds 50 characters");
        }
        this.lastName = lastName.trim();
    }

    private void setContactNo(String contactNo) {
        if (contactNo == null || contactNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact number cannot be empty");
        }
        if (contactNo.length() > 15) {
            throw new IllegalArgumentException("Contact number exceeds 15 characters");
        }
        this.contactNo = contactNo.trim();
    }

    private void setShiftTimings(String shiftTimings) {
        // Allows null (matches your DB schema where NULL is permitted)
        if (shiftTimings != null && !shiftTimings.matches("Morning|Evening|Night")) {
            throw new IllegalArgumentException("Shift must be Morning/Evening/Night");
        }
        this.shiftTimings = shiftTimings;
    }

    private void setGateNumber(int gateNumber) {
        if (gateNumber <= 0) throw new IllegalArgumentException("Gate number must be positive");
        this.gateNumber = gateNumber;
    }

    // GETTERS (no setters for securityId as it's final)
    public int getSecurityId() { return securityId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getContactNo() { return contactNo; }
    public String getShiftTimings() { return shiftTimings; }
    public int getGateNumber() { return gateNumber; }

    @Override
    public String toString() {
        return String.format("%d: %s %s (Gate: %d)", 
            securityId, firstName, lastName, gateNumber);
    }
}