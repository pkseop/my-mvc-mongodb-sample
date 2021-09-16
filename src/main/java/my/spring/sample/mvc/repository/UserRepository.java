package my.spring.sample.mvc.repository;

import my.spring.sample.mvc.collection.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByUsername(String username);

    Optional<User> findOneByUsername(String username);
}
