package my.spring.sample.mvc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageModel {
    private String original;

    private String thumbnail;

    private Integer width;

    private Integer height;

    public ImageModel() {}

    public ImageModel(String image) {
        original = image;
    }
}
