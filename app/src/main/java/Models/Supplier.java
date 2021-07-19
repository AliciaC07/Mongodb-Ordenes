package Models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Supplier {
    private int id;
    private String productId;
    private int deliveryDate;
    private float price;

    public Supplier(int id, String productId, int deliveryDate, float price) {
        this.id = id;
        this.productId = productId;
        this.deliveryDate = deliveryDate;
        this.price = price;
    }

    public Supplier() {
    }
}
