package my.spring.sample.mvc.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataListResponse<T> {

    private List<T> data;
}
