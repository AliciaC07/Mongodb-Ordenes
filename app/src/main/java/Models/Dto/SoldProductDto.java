package Models.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoldProductDto {
   private String  productId;
   private Integer quantity;
   private String unit;
   private Float price;

}
