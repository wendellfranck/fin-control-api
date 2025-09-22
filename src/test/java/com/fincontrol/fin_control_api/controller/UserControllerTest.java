package com.fincontrol.fin_control_api.controller;

import com.fincontrol.fin_control_api.model.User;
import com.fincontrol.fin_control_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user = new User(1L, "user1", "user1@email.com", "pass");
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"));
    }

    @Test
    void testGetAllUsersEmpty() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetUserByIdFound() throws Exception {
        User user = new User(1L, "user1", "user1@email.com", "pass");
        when(userService.getUserById(1)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        when(userService.getUserById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User(1L, "user1", "user1@email.com", "pass");
        when(userService.createUser(any(User.class))).thenReturn(user);

        String json = "{\"user_id\":1,\"username\":\"user1\",\"email\":\"user1@email.com\",\"password\":\"pass\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    void testUpdateUserPartialFound() throws Exception {
        User updatedUser = new User(1L, "userUpdated", "user1@email.com", "pass");
        when(userService.updateUserPartial(eq(1), any(User.class))).thenReturn(Optional.of(updatedUser));

        String json = "{\"username\":\"userUpdated\"}";

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("userUpdated"));
    }

    @Test
    void testUpdateUserPartialNotFound() throws Exception {
        when(userService.updateUserPartial(eq(99), any(User.class))).thenReturn(Optional.empty());

        String json = "{\"username\":\"userUpdated\"}";

        mockMvc.perform(put("/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUserFound() throws Exception {
        when(userService.deleteUser(1)).thenReturn(true);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário deletado com sucesso!"));
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        when(userService.deleteUser(99)).thenReturn(false);

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
    }
}
