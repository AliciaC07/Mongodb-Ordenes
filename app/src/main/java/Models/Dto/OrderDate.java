package Models.Dto;

import Models.SellOrder;
import Models.SoldProduct;
import Models.Supplier;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class OrderDate {
    private Set<SoldProduct> products = new HashSet<>();
    private LocalDate date;
    private Supplier supplier;

    public OrderDate() {
    }
}
