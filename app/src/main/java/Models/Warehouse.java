package Models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Warehouse {
    private int idWarehouse;
    private int quantity;

    public Warehouse(int idWarehouse, int quantity) {
        this.idWarehouse = idWarehouse;
        this.quantity = quantity;
    }

    public Warehouse() {

    }
    public int getIdWarehouse() {
        return idWarehouse;
    }

    public void setIdWarehouse(int idWarehouse) {
        this.idWarehouse = idWarehouse;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
