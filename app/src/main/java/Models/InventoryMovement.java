package Models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class InventoryMovement {
    private int id;
    private int warehouse;
    private String type;
    private String idProduct;
    private int quantity;
    private Date date;

    public InventoryMovement(int id, int warehouse, String type, String idProduct, int quantity, Date date) {
        this.id = id;
        this.warehouse = warehouse;
        this.type = type;
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.date = date;
    }

    public InventoryMovement() {
    }
}
