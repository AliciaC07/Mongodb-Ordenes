package Models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoldProduct {
    private Product product;
    private int quantity;
    private float price;

    public SoldProduct(Product product, int quantity, float price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }


    public SoldProduct() {

    }
}
