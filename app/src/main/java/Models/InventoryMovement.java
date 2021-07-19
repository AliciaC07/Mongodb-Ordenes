package Models;

import java.util.Date;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(int warehouse) {
        this.warehouse = warehouse;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
