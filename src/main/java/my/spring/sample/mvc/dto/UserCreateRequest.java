package my.spring.sample.mvc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

    private String username;

    private String name;

    private String password;
}
