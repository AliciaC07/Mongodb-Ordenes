package Models.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailableDays {

    private int availableDays;
    private String id;
    private int totalAvailable;

    public AvailableDays() {
    }
}
