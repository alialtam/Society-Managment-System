package model;

public class Gate {
    private final int gateNumber;
    private String description;

    public Gate(int gateNumber, String description) {
        if (gateNumber <= 0) {
            throw new IllegalArgumentException("Gate number must be positive");
        }
        setDescription(description); // Validation happens here
        this.gateNumber = gateNumber;
    }

    // Getters
    public int getGateNumber() { return gateNumber; }
    public String getDescription() { return description; }

    // Validated setter
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (description.length() > 200) {
            throw new IllegalArgumentException("Description exceeds 200 characters");
        }
        this.description = description.trim();
    }

    @Override
    public String toString() {
        return String.format("Gate %d: %s", gateNumber, description);
    }
}