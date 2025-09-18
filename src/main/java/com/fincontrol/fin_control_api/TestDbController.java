package com.fincontrol.fin_control_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
public class TestDbController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/test-db")
    public String testDb() {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT NOW()");
            if (rs.next()) {
                return "Banco conectado! Hora atual: " + rs.getString(1);
            }
        } catch (SQLException e) {
            return "Erro ao conectar ao banco: " + e.getMessage();
        }
        return "Não conseguiu ler o banco";
    }
}
