package my.spring.sample.mvc.dto;

import lombok.Getter;
import lombok.Setter;
import my.spring.sample.mvc.model.PageInfo;

import java.util.List;

@Getter
@Setter
public class DataListWithPageResponse<T>  {

    private List<T> data;

    private PageInfo page;
}
