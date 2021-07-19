package Models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
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
