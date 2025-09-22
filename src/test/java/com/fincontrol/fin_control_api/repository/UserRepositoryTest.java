package com.fincontrol.fin_control_api.repository;

import com.fincontrol.fin_control_api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindById() {
        User user = new User(null, "user1", "user1@email.com", "pass1");
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findById(Math.toIntExact(saved.getId()));

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("user1");
    }

    @Test
    void testFindAll() {
        User user1 = new User(null, "user1", "user1@email.com", "pass1");
        User user2 = new User(null, "user2", "user2@email.com", "pass2");
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2).extracting(User::getUsername).contains("user1", "user2");
    }

    @Test
    void testUpdateUser() {
        User user = new User(null, "user1", "user1@email.com", "pass1");
        User saved = userRepository.save(user);

        saved.setUsername("updatedUser");
        User updatedSaved = userRepository.save(saved);

        Optional<User> updated = userRepository.findById(Math.toIntExact(updatedSaved.getId()));

        assertThat(updated).isPresent();
        assertThat(updated.get().getUsername()).isEqualTo("updatedUser");
    }

    @Test
    void testDeleteUser() {
        User user = new User(null, "user1", "user1@email.com", "pass1");
        User saved = userRepository.save(user);

        userRepository.deleteById(Math.toIntExact(saved.getId()));

        Optional<User> deleted = userRepository.findById(Math.toIntExact(saved.getId()));
        assertThat(deleted).isEmpty();
    }

    @Test
    void testDeleteAllUsers() {
        userRepository.save(new User(null, "user1", "user1@email.com", "pass1"));
        userRepository.save(new User(null, "user2", "user2@email.com", "pass2"));

        userRepository.deleteAll();

        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }
}
