package my.spring.sample.mvc.service.domain;

import my.spring.sample.mvc.collection.User;
import my.spring.sample.mvc.exception.BadRequestException;
import my.spring.sample.mvc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserDomainService implements UserDetailsService {

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

    public User findOneByUsername(String username) throws UsernameNotFoundException {
        Optional<User> op = userRepository.findOneByUsername(username);
        if(op.isEmpty())
            throw new UsernameNotFoundException("Can't find user [" + username + "]");
        return op.get();
    }

    public boolean isExistByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.findOneByUsername(username);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return org.springframework.security.core.userdetails.User.builder()
            .username(username)
            .password(user.getPassword())
            .authorities(authorities)
            .build();
    }
}
