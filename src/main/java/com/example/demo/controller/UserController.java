package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserDTO;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * UserController - Kullanıcı Yönetimi Endpoint'leri
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Kullanıcı Yönetimi", description = "Kullanıcı profili ve yönetim")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    @Operation(summary = "Kullanıcı Getir", description = "ID ile kullanıcı bilgilerini al")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/username/{username}")
    @Operation(summary = "Username ile Getir", description = "Username ile kullanıcı bilgilerini al")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Email ile Getir", description = "Email ile kullanıcı bilgilerini al")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tüm Kullanıcılar", description = "Tüm kullanıcıları listele (Admin)")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsersPaginated(pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Kullanıcı Ara", description = "İsme göre kullanıcı ara")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @RequestParam String name,
            Pageable pageable) {
        Page<UserDTO> users = userService.searchUsers(name, pageable);
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Kullanıcı Güncelle", description = "Kullanıcı profilini güncelle")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Kullanıcı Sil", description = "Kullanıcıyı sil (Admin)")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Kullanıcı başarıyla silindi");
    }
    
    @PostMapping("/{id}/soft-delete")
    @Operation(summary = "Soft Delete", description = "Kullanıcıyı deaktif et")
    public ResponseEntity<String> softDeleteUser(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        userService.softDeleteUser(id, reason);
        return ResponseEntity.ok("Kullanıcı başarıyla deaktif edildi");
    }
    
    @PostMapping("/{id}/verify-email")
    @Operation(summary = "Email Doğrula", description = "Kullanıcının emailini doğrula")
    public ResponseEntity<String> verifyEmail(@PathVariable Long id) {
        userService.verifyEmail(id);
        return ResponseEntity.ok("Email başarıyla doğrulandı");
    }
    
    @PostMapping("/{id}/verify-phone")
    @Operation(summary = "Telefon Doğrula", description = "Kullanıcının telefonunu doğrula")
    public ResponseEntity<String> verifyPhone(@PathVariable Long id) {
        userService.verifyPhone(id);
        return ResponseEntity.ok("Telefon başarıyla doğrulandı");
    }
    
    @PostMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rol Ata", description = "Kullanıcıya rol ata (Admin)")
    public ResponseEntity<String> assignRole(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        userService.assignRole(userId, roleName);
        return ResponseEntity.ok("Rol başarıyla atandı");
    }
    
    @DeleteMapping("/{userId}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rol Kaldır", description = "Kullanıcıdan rol kaldır (Admin)")
    public ResponseEntity<String> removeRole(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        userService.removeRole(userId, roleName);
        return ResponseEntity.ok("Rol başarıyla kaldırıldı");
    }
}
