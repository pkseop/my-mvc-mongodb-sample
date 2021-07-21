package my.spring.sample.mvc.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserCreateRequest {

    @NotBlank
    @Email
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String password;
}
