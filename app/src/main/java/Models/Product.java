package Models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
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

    public Product() {
    }


}
