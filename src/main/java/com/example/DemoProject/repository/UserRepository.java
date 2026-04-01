package com.example.DemoProject.repository;

import com.example.DemoProject.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    //All Crud Database Methods

    Optional<User> findByEmail(String email);

    User findByName(String name);

    boolean existsByEmail(String email);
}

