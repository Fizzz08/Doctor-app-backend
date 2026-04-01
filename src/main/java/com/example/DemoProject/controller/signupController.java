//package com.example.DemoProject.controller;
//
//import com.example.DemoProject.services.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//
//@Controller
//@RequestMapping("/api")
//public class signupController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @GetMapping("/check-email")
//    public ResponseEntity<Boolean> checkEmail(@RequestParam("email") String email) {
//        boolean emailExists = userService.emailExists(email);  // Check if email exists
//        return ResponseEntity.ok(emailExists);
//    }
//
//}