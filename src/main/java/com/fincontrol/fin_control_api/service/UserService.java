package com.fincontrol.fin_control_api.service;

import com.fincontrol.fin_control_api.model.User;
import com.fincontrol.fin_control_api.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Busca todos os usuários.
     */
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    /**
     * Busca usuário pelo ID com cache.
     * Se o usuário já estiver no Redis, retorna de lá.
     * Se não estiver, busca no banco e adiciona ao cache.
     */
    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUserById(int id) {
        log.info("Buscando usuário no banco de dados. id={}", id);
        return repository.findById(id);
    }

    /**
     * Cria um novo usuário ou atualiza existente.
     * Também atualiza o cache no Redis.
     */
    @CachePut(value = "users", key = "#user.id")
    public User createUser(User user) {
        return repository.save(user);
    }

    /**
     * Atualiza parcialmente um usuário.
     */
    @CachePut(value = "users", key = "#id")
    public Optional<User> updateUserPartial(int id, User user) {
        return repository.findById(id).map(existingUser -> {
            if (user.getUsername() != null) existingUser.setUsername(user.getUsername());
            if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
            if (user.getPassword() != null) existingUser.setPassword(user.getPassword());
            return repository.save(existingUser);
        });
    }

    /**
     * Remove usuário por ID.
     * Também remove do cache no Redis.
     */
    @CacheEvict(value = "users", key = "#id")
    public boolean deleteUser(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Remove todos os usuários e limpa o cache.
     */
    @CacheEvict(value = "users", allEntries = true)
    public void deleteAllUsers() {
        repository.deleteAll();
    }
}
