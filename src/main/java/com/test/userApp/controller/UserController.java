package com.test.userApp.controller;

import com.test.userApp.entity.User;
import com.test.userApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/register")
    public User save(@RequestBody User user) {
        System.out.println("Request to save: " + user);
        return userService.saveUser(user);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/search")
    public Page<User> searchUser(@RequestParam("by") String searchBy, Pageable pageable) {
        System.out.println("Search user by: " + searchBy);
        return userService.searchUsers(searchBy, pageable);
    }

    @GetMapping("/permutation")
    public List<String> getPermutation(@RequestParam("string") String string) {
        return userService.getPermutations(string);
    }
}
