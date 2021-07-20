package Models.Dto;

import Models.SellOrder;
import Models.SoldProduct;
import Models.Supplier;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderDate {
    private List<SoldProduct> products = new ArrayList<>();
    private LocalDate date;
    private Supplier supplier;

    public OrderDate() {
    }
}
