package Models;

public class Warehouse {
    private int idWarehouse;
    private int quantity;

    public Warehouse(int idWarehouse, int quantity) {
        this.idWarehouse = idWarehouse;
        this.quantity = quantity;
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
