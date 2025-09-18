package com.fincontrol.fin_control_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private DataSource dataSource;

    // GET /users - lista todos os usuários do banco
    @GetMapping("/users")
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT user_id, username, email, password_hash FROM users_schema.users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("user_id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String passwordHash = rs.getString("password_hash");

                users.add(new User(id, username, email, passwordHash));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    // POST /users - cria um novo usuário
    @PostMapping("/users")
    public String createUser(@RequestBody User user) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO users_schema.users (user_id, username, email, password_hash) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getUser_id());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword()); // Aqui o JSON ainda envia "password"

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return "Usuário criado com sucesso!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao criar usuário: " + e.getMessage();
        }
        return "Não foi possível criar o usuário";
    }

    // Classe interna User
    static class User {
        private int user_id;
        private String username;
        private String email;
        private String password; // aqui no JSON continua como "password"

        public User() {} // Necessário para desserialização do JSON

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
