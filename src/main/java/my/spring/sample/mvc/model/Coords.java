package my.spring.sample.mvc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coords {

    private double lat;

    private double lng;

    public Coords(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
