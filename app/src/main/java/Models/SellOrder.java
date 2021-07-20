package Models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Getter
@Setter
public class SellOrder {
    private int id;
    private int supplierId;
    private Date date;
    private List<SoldProduct> soldProducts = new ArrayList<>();

    public SellOrder(int id, int supplierId, Date date, List<SoldProduct> soldProducts) {
        this.id = id;
        this.supplierId = supplierId;
        this.date = date;
        this.soldProducts = soldProducts;
    }

    public SellOrder() {

    }

}
