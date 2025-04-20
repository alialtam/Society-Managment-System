package model;

public class Building {
    private int buildingId;
    private String name;
    private int totalFlats;

    public Building(int buildingId, String name, int totalFlats) {
        setBuildingId(buildingId);
        setName(name);
        setTotalFlats(totalFlats);
    }

    // Getters
    public int getBuildingId() { return buildingId; }
    public String getName() { return name; }
    public int getTotalFlats() { return totalFlats; }

    // Setters with validation
    public void setBuildingId(int buildingId) {
        if (buildingId < 0) {
            throw new IllegalArgumentException("Building ID must be non-negative");
        }
        this.buildingId = buildingId;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Building name cannot be empty");
        }
        this.name = name.trim();
    }

    public void setTotalFlats(int totalFlats) {
        if (totalFlats <= 0) {
            throw new IllegalArgumentException("Total flats must be positive");
        }
        this.totalFlats = totalFlats;
    }

    @Override
    public String toString() {
        return name + " (ID: " + buildingId + ")";
    }
}