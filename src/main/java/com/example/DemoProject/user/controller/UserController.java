package com.example.DemoProject.user.controller;

import com.example.DemoProject.Security.JwtUtil;
import com.example.DemoProject.exeption.ResourceNotFoundException;
import com.example.DemoProject.Entity.User;
import com.example.DemoProject.user.repository.UserRepository;
import com.example.DemoProject.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    //create user Rest API
    @PostMapping
    public User createUser(@RequestBody User user){
        return userRepository.save(user);
    }

    //Get user by id Rest API
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist with id " + id));
        return ResponseEntity.ok(user);
    }

    //Delete all the registered users
    @DeleteMapping
    public boolean deleteAllUser(){
        if(!getAllUser().isEmpty()) {
            userRepository.deleteAll();
            System.out.println("Data deleted successfully");
            return true;
        }
        else {
            System.out.println("No Data exist");
            return false;
        }
    }

    //Checks the email in the database
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }


    //getting the name from the token
    @GetMapping("/name")
    @ResponseBody
    public String getUserData(@RequestHeader("Authorization") String authorizationHeader) {
        // Extract the token from the Authorization header (format: "Bearer <token>")
        String token = authorizationHeader.substring(7);  // Remove "Bearer " prefix
        return jwtUtil.extractName(token);
    }

}
