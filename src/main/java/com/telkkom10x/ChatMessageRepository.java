package com.telkkom10x;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ChatMessageRepository {

    private final JdbcTemplate jdbcTemplate;

    public ChatMessageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(ChatMessage message) {
        String sql = "INSERT INTO chat_messages (sender, content, chat_group, timestamp) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, message.getSender(), message.getContent(), message.getGroup(), message.getTimestamp());
    }

    public List<ChatMessage> findByGroup(String group) {
        String sql = "SELECT * FROM chat_messages WHERE chat_group = ? ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, new Object[]{group}, this::mapRowToChatMessage);
    }

    private ChatMessage mapRowToChatMessage(ResultSet rs, int rowNum) throws SQLException {
        return new ChatMessage(
                rs.getLong("id"),
                rs.getString("sender"),
                rs.getString("content"),
                rs.getString("chat_group"),
                rs.getTimestamp("timestamp").toLocalDateTime()
        );
    }
}