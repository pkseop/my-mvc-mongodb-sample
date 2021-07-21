package my.spring.sample.mvc.service;

import my.spring.sample.mvc.collection.User;
import my.spring.sample.mvc.dto.UserCreateRequest;
import my.spring.sample.mvc.dto.UserUpdateRequest;
import my.spring.sample.mvc.service.domain.UserDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDomainService userDomainService;

    public User create(UserCreateRequest body) {
        boolean exists = userDomainService.isExistByUsername(body.getUsername());
        if(exists) {
            throw new RuntimeException("Already exists username");
        }
        userDomainService.get(body.getUsername());

        User user = new User();
        user.setUsername(body.getUsername());
        user.setName(body.getName());
        //TODO security 기능 추가되면 해시해서 넣도록.
//        user.setPassword();
        return userDomainService.create(user);
    }

    public User get(String userId) {
        return userDomainService.get(userId);
    }

    public User update(String userId, UserUpdateRequest body) {
        User user = userDomainService.get(userId);
        if(body.getName() != null) {
            user.setName(body.getName());
        }
        if(body.getPassword() != null) {
            //TODO security 기능 추가되면 해시해서 넣도록.
//            user.setPassword(body.getPassword());
        }
        return userDomainService.update(user);
    }

    public void delete(String userId) {
        User user = userDomainService.get(userId);
        userDomainService.delete(user);
    }

}
