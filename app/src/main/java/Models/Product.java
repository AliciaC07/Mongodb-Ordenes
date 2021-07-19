package Models;

import java.util.List;

public class Product {
    private String id;
    private String description;
    private List<Warehouse> availability;
    private String unit;

    public Product(String id, String description, List<Warehouse> availability) {
        this.id = id;
        this.description = description;
        this.availability = availability;
        this.unit = "single";
    }

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Warehouse> getAvailability() {
        return availability;
    }

    public void setAvailability(List<Warehouse> availability) {
        this.availability = availability;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
