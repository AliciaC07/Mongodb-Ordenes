package Models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SoldProduct {
    private Product product;
    private int quantity;
    private float price;
    private LocalDate dateGenerated;
    private Supplier supplier;

    public SoldProduct(Product product, int quantity, float price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }


    public SoldProduct() {

    }
}
