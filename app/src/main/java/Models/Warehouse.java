package Models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Warehouse {
    private String id;
    private int quantity;

    public Warehouse(String id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public Warehouse() {
    }
}
