package Models;

import java.util.Date;
import java.util.List;

public class SellOrder {
    private int id;
    private int supplierId;
    private Date date;
    private List<SoldProduct> soldProducts;

    public SellOrder(int id, int supplierId, Date date, List<SoldProduct> soldProducts) {
        this.id = id;
        this.supplierId = supplierId;
        this.date = date;
        this.soldProducts = soldProducts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<SoldProduct> getSoldProducts() {
        return soldProducts;
    }

    public void setSoldProducts(List<SoldProduct> soldProducts) {
        this.soldProducts = soldProducts;
    }
}
