package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    List<User> findByUserRole(UserRole userRole);
    
    List<User> findByIsActive(Boolean isActive);
    
    List<User> findByIsEmailVerified(Boolean isEmailVerified);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.isEmailVerified = true")
    List<User> findAllVerifiedUsers();
    
    Page<User> findByUserRole(UserRole userRole, Pageable pageable);
    
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    Page<User> findAllActive(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> searchByName(@Param("name") String name, Pageable pageable);
}
