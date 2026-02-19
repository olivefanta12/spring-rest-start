package com.metacoding.springv2.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// findbyId, findAll, save, deleteById, count (CRUD)
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select u from User u where u.username = :username")
    public Optional<User> findByUsername(@Param("username") String username);

}