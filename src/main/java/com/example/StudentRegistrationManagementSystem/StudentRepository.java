package com.example.StudentRegistrationManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class StudentRepository {

    // FIX: no more catch-and-ignore. If the insert fails, the caller must know.
    // try-with-resources also closes the connection every time (the old code leaked it).
    public void save(Student student) throws SQLException {
        String sql = "INSERT INTO students (full_name, age, email) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, student.getFullName());
            ps.setInt(2, student.getAge());
            ps.setString(3, student.getEmail());
            ps.executeUpdate();
        }
    }

    public List<Student> findAllStudents() throws SQLException {
        // FIX: was "SELECT* FROM students by id" -> invalid SQL.
        String sql = "SELECT id, full_name, age, email FROM students ORDER BY id";
        List<Student> students = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));   // FIX: was new Student() -> empty objects
            }
        }
        return students;
    }

    public Optional<Student> findById(int id) throws SQLException {
        String sql = "SELECT id, full_name, age, email FROM students WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));   // FIX: was new Student()
                }
            }
        }
        return Optional.empty();
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        String fullName = rs.getString("full_name");
        String[] parts = fullName.split(" ", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";

        // FIX: this used a constructor with an empty body, so nothing was ever filled in.
        return new Student(rs.getInt("id"), rs.getInt("age"), firstName, lastName, rs.getString("email"));
    }
}