package my.spring.sample.mvc.service.domain;

import my.spring.sample.mvc.collection.User;
import my.spring.sample.mvc.exception.BadRequestException;
import my.spring.sample.mvc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDomainService {

    @Autowired
    private UserRepository userRepository;

    private User insert(User user) {
        return userRepository.insert(user);
    }

    private User save(User user) {
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User create(User user) {
        user.created();
        return this.insert(user);
    }

    public User update(User user) {
        user.updated();
        return this.save(user);
    }

    public User get(String id) throws BadRequestException {
        Optional<User> op = userRepository.findById(id);
        if(op.isEmpty())
            throw new BadRequestException("Can't find user with id [" + id + "].");
        return op.get();
    }

    public boolean isExistByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
