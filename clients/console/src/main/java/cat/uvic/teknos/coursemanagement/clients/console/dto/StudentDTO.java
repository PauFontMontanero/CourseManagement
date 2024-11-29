package cat.uvic.teknos.coursemanagement.clients.console.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class StudentDTO {
    private int id;
    private String firstName;
    private String lastName;
    private LocalDate bornOn;
    private GenreDTO genre;
    private Set<CourseDTO> courses = new HashSet<>();

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBornOn() {
        return bornOn;
    }

    public void setBornOn(LocalDate bornOn) {
        this.bornOn = bornOn;
    }

    public GenreDTO getGenre() {
        return genre;
    }

    public void setGenre(GenreDTO genre) {
        this.genre = genre;
    }

    public Set<CourseDTO> getCourses() {
        return courses;
    }

    public void setCourses(Set<CourseDTO> courses) {
        this.courses = courses != null ? courses : new HashSet<>();
    }
}