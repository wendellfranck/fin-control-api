package com.fincontrol.fin_control_api.controller;

import com.fincontrol.fin_control_api.model.User;
import com.fincontrol.fin_control_api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /users - lista todos os usuários
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /users/{id} - busca usuário pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /users - cria um novo usuário
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userService.createUser(user);
        return ResponseEntity.status(201).body(saved);
    }

    // PUT /users/{id} - atualiza parcialmente um usuário
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserPartial(@PathVariable int id, @RequestBody User user) {
        Optional<User> updated = userService.updateUserPartial(id, user);
        return updated.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /users/{id} - remove um usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.ok("Usuário deletado com sucesso!")
                : ResponseEntity.notFound().build();
    }
}
