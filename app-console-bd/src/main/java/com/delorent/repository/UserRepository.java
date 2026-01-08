package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {
  private final JdbcTemplate jdbc;

  public UserRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<String> findAllUsernames() {
    return jdbc.query("SELECT * FROM Utilisateurs",
        (rs, rowNum) -> rs.getString("username"));
  }
}
