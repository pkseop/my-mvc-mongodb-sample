package my.spring.sample.mvc.controller;

import my.spring.sample.mvc.collection.User;
import my.spring.sample.mvc.dto.UserCreateRequest;
import my.spring.sample.mvc.dto.UserUpdateRequest;
import my.spring.sample.mvc.exception.BadRequestException;
import my.spring.sample.mvc.service.UserService;
import my.spring.sample.mvc.utils.ResponseUtils;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UserCreateRequest body) throws BadRequestException {
        User user = userService.create(body);
        return ResponseUtils.ok(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> get(String userId) throws BadRequestException {
        User user = userService.get(userId);
        return ResponseUtils.ok(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> update(@PathVariable String userId,
                                    @RequestBody UserUpdateRequest body) throws BadRequestException {
        User user = userService.update(userId, body);
        return ResponseUtils.ok(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(String userId) throws BadRequestException {
        userService.delete(userId);
        return ResponseUtils.ok("Success");
    }

}
