package com.example.DemoProject.consumer.repository;

import com.example.DemoProject.Entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser_Email(String email);


    @Query("SELECT p FROM Profile p JOIN p.user u WHERE u.email = :email")
    Optional<Profile> findByUserEmail(@Param("email") String email);
}
