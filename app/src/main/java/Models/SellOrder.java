package Models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
public class SellOrder {
    private int id;
    private int supplierId;
    private Date date;
    private Set<SoldProduct> soldProducts = new HashSet<>();
    private float totalAmount;

    public SellOrder(int id, int supplierId, Date date, Set<SoldProduct> soldProducts,float totalAmount) {
        this.id = id;
        this.supplierId = supplierId;
        this.date = date;
        this.soldProducts = soldProducts;
        this.totalAmount = totalAmount;
    }

    public SellOrder() {

    }

}
