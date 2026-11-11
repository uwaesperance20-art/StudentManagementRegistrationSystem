package com.example.StudentRegistrationManagementSystem;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("student", new Student());
        return "register";
    }
    @PostMapping("/register")
    public String registerStudent(
            @ModelAttribute("student") Student student,
            BindingResult result,
            Model model) {

        System.out.println("****** POST REQUEST RECEIVED ******");
        System.out.println("Name: " + student.getFullName());
        System.out.println("Age: " + student.getAge());
        System.out.println("Email: " + student.getEmail());

        try {
            studentService.registerStudent(student);

            System.out.println("STUDENT SAVED SUCCESSFULLY!");

            model.addAttribute("successMessage",
                    "Student registered successfully!");

            model.addAttribute("student", new Student());

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("errorMessage", e.getMessage());
        }

        return "register";
    }

    @GetMapping("/all")
    public String showAllStudents(Model model) throws SQLException {
        model.addAttribute("students", studentService.findAllStudents());
        return "students";
    }

    @GetMapping("/{id}")
    public String showStudentById(@PathVariable int id, Model model) throws SQLException {
        Optional<Student> student = studentService.findById(id);

        if (student.isPresent()) {
            model.addAttribute("students", List.of(student.get()));
        } else {
            model.addAttribute("students", List.of());
            model.addAttribute("message", "No student found with id " + id);
        }
        return "students";
    }
}