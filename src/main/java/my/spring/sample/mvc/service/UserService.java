package my.spring.sample.mvc.service;

import my.spring.sample.mvc.collection.User;
import my.spring.sample.mvc.dto.UserCreateRequest;
import my.spring.sample.mvc.dto.UserUpdateRequest;
import my.spring.sample.mvc.exception.BadRequestException;
import my.spring.sample.mvc.service.domain.UserDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDomainService userDomainService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User create(UserCreateRequest body) throws BadRequestException {
        boolean exists = userDomainService.isExistByUsername(body.getUsername());
        if(exists) {
            throw new RuntimeException("Already exists username");
        }
        userDomainService.get(body.getUsername());

        User user = new User();
        user.setUsername(body.getUsername());
        user.setName(body.getName());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        return userDomainService.create(user);
    }

    public User get(String userId) throws BadRequestException {
        return userDomainService.get(userId);
    }

    public User update(String userId, UserUpdateRequest body) throws BadRequestException {
        User user = userDomainService.get(userId);
        if(body.getName() != null) {
            user.setName(body.getName());
        }
        return userDomainService.update(user);
    }

    public void delete(String userId) throws BadRequestException {
        User user = userDomainService.get(userId);
        userDomainService.delete(user);
    }

}
