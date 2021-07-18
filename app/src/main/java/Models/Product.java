package Models;

import java.util.List;

public class Product {
    private String id;
    private String description;
    private List<Warehouse> warehouse;
    private String unit;

    public Product(String id, String description, List<Warehouse> warehouse) {
        this.id = id;
        this.description = description;
        this.warehouse = warehouse;
        this.unit = "single";
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

    public List<Warehouse> getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(List<Warehouse> warehouse) {
        this.warehouse = warehouse;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
