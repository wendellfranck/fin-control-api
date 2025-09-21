package com.fincontrol.fin_control_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private DataSource dataSource;

    // GET /users - lista todos os usuários
    @GetMapping
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, email, password_hash FROM users_schema.users";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // GET /users/{id} - busca usuário pelo ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        String sql = "SELECT user_id, username, email, password_hash FROM users_schema.users WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // ou lançar exception customizada
    }

    // POST /users - cria um novo usuário
    @PostMapping
    public String createUser(@RequestBody User user) {
        String sql = "INSERT INTO users_schema.users (user_id, username, email, password_hash) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getUser_id());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());

            int rows = stmt.executeUpdate();
            return rows > 0 ? "Usuário criado com sucesso!" : "Não foi possível criar o usuário.";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao criar usuário: " + e.getMessage();
        }
    }

    // PUT /users/{id} - atualiza parcialmente um usuário
    @PutMapping("/{id}") // PATCH é mais semântico para updates parciais
    public String updateUserPartial(@PathVariable int id, @RequestBody User user) {
        // Lista para campos e valores
        List<String> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        if (user.getUsername() != null) {
            fields.add("username = ?");
            values.add(user.getUsername());
        }
        if (user.getEmail() != null) {
            fields.add("email = ?");
            values.add(user.getEmail());
        }
        if (user.getPassword() != null) {
            fields.add("password_hash = ?");
            values.add(user.getPassword());
        }

        if (fields.isEmpty()) {
            return "Nenhum campo para atualizar.";
        }

        String sql = "UPDATE users_schema.users SET " + String.join(", ", fields) + " WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Preencher os valores dinamicamente
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            stmt.setInt(values.size() + 1, id);

            int rows = stmt.executeUpdate();
            return rows > 0 ? "Usuário atualizado com sucesso!" : "Usuário não encontrado.";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao atualizar usuário: " + e.getMessage();
        }
    }


    // DELETE /users/{id} - remove um usuário
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        String sql = "DELETE FROM users_schema.users WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0 ? "Usuário deletado com sucesso!" : "Usuário não encontrado.";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao deletar usuário: " + e.getMessage();
        }
    }

    // Classe interna User
    static class User {
        private int user_id;
        private String username;
        private String email;
        private String password;

        public User() {}

        public User(int user_id, String username, String email, String password) {
            this.user_id = user_id;
            this.username = username;
            this.email = email;
            this.password = password;
        }

        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
