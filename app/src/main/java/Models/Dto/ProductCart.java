package Models.Dto;

import Models.Product;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductCart {

    private Product product;
    private int quantity;
    private int idWarehouse;

    public ProductCart() {
    }
}
