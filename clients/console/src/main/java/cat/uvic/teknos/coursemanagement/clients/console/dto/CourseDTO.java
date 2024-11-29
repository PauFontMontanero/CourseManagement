package cat.uvic.teknos.coursemanagement.clients.console.dto;

import java.util.HashSet;
import java.util.Set;

public class CourseDTO {
    private int id;
    private String name;
    private int year;
    private Set<StudentDTO> students = new HashSet<>();

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Set<StudentDTO> getStudents() {
        return students;
    }

    public void setStudents(Set<StudentDTO> students) {
        this.students = students != null ? students : new HashSet<>();
    }
}