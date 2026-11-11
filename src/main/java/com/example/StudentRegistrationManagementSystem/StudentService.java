package com.example.StudentRegistrationManagementSystem;

import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void registerStudent(Student student) throws SQLException {
        // FIX: validate FIRST and THROW, so bad data never reaches the database.
        // (The old code saved before validating, and saved a second time at the end.)
        if (student.getFullName().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
        // Same rule as the table: CHECK (age > 18)
        if (student.getAge() <= 18) {
            throw new IllegalArgumentException("Age must be greater than 18.");
        }
        if (student.getEmail() == null || !student.getEmail().contains("@")) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }

        try {
            studentRepository.save(student);   // saved exactly once
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {   // PostgreSQL: unique violation
                throw new IllegalArgumentException("A student with this email already exists.");
            }
            throw e;
        }
    }

    public List<Student> findAllStudents() throws SQLException {
        return studentRepository.findAllStudents();
    }

    public Optional<Student> findById(int id) throws SQLException {
        return studentRepository.findById(id);
    }
}