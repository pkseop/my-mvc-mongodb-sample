package my.spring.sample.mvc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordsRange {

    private double ltLat;

    private double ltLng;

    private double rbLat;

    private double rbLng;

    public CoordsRange(double ltLat, double ltLng, double rbLat, double rbLng) {
        this.ltLat = ltLat;
        this.ltLng = ltLng;
        this.rbLat = rbLat;
        this.rbLng = rbLng;
    }
}
