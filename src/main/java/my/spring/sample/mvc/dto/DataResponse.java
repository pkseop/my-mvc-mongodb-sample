package my.spring.sample.mvc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataResponse<T> {

    private T data;
}
