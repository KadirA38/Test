package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Permission - Sistem İzinleri (Yetkileri)
 */
@Entity
@Table(name = "Permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name; // CREATE_EVENT, EDIT_EVENT, DELETE_EVENT vb.
    
    @Column(length = 200)
    private String description;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}
