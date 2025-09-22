package com.fincontrol.fin_control_api.service;

import com.fincontrol.fin_control_api.model.User;
import com.fincontrol.fin_control_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        user1 = new User(1L, "user1", "user1@email.com", "pass1");
        user2 = new User(2L, "user2", "user2@email.com", "pass2");
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(2).contains(user1, user2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByIdFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));

        Optional<User> result = userService.getUserById(1);

        assertThat(result).isPresent().contains(user1);
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(99);

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findById(99);
    }

    @Test
    void testCreateUser() {
        when(userRepository.save(user1)).thenReturn(user1);

        User created = userService.createUser(user1);

        assertThat(created).isEqualTo(user1);
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void testUpdateUserPartial() {
        User update = new User();
        update.setUsername("updatedName");

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<User> updated = userService.updateUserPartial(1, update);

        assertThat(updated).isPresent();
        assertThat(updated.get().getUsername()).isEqualTo("updatedName");
        assertThat(updated.get().getEmail()).isEqualTo(user1.getEmail());
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserPartialNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        Optional<User> updated = userService.updateUserPartial(99, new User());

        assertThat(updated).isEmpty();
        verify(userRepository, times(1)).findById(99);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUserFound() {
        when(userRepository.existsById(1)).thenReturn(true);

        boolean result = userService.deleteUser(1);

        assertThat(result).isTrue();
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(99)).thenReturn(false);

        boolean result = userService.deleteUser(99);

        assertThat(result).isFalse();
        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteAllUsers() {
        userService.deleteAllUsers();

        verify(userRepository, times(1)).deleteAll();
    }
}
