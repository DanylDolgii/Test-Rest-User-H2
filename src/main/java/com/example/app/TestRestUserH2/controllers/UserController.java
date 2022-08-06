package com.example.app.TestRestUserH2.controllers;

import com.example.app.TestRestUserH2.entity.User;
import com.example.app.TestRestUserH2.exceptions.UserException;
import com.example.app.TestRestUserH2.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) throws UserException {
        return service.saveUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") int id) {
        return service.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") int id,
                                                   @RequestBody User user) {
        return service.getUserById(id)
                .map(savedUser -> {

                    savedUser.setName(user.getName());
                    savedUser.setPhone(user.getPhone());
                    savedUser.setEmail(user.getEmail());

                    User updatedUser = service.updateUser(savedUser);
                    return new ResponseEntity<>(updatedUser, HttpStatus.OK);

                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int userId) {
        service.deleteUser(userId);
        return new ResponseEntity<>("Deleted.", HttpStatus.OK);
    }

}
