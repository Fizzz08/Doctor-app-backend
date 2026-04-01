package com.example.DemoProject.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Controller
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/Home")
    public String homePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";  // fallback if someone accesses directly
        }

        model.addAttribute("username", userDetails.getUsername());
        return "Home";
    }


    @GetMapping("/demo")
    public ResponseEntity<ResponseMessage> sayHello() {
        ResponseMessage responseMessage = new ResponseMessage("Welcome to Home page");
        return ResponseEntity.ok(responseMessage); // Automatically serialized to JSON
    }

}
