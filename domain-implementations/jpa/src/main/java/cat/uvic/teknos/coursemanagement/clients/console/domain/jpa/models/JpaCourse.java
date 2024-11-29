package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models;

import cat.uvic.teknos.coursemanagement.clients.console.models.Course;
import cat.uvic.teknos.coursemanagement.clients.console.models.Student;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "COURSE")
public class JpaCourse implements Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "YEAR")
    private int year;

    @Column(name = "NAME")
    private String name;

    @JsonBackReference
    @ManyToMany(mappedBy = "courses")
    private Set<JpaStudent> students = new HashSet<>();

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public Set<Student> getStudents() {
        return new HashSet<>(students);
    }

    @Override
    public void setStudents(Set<Student> students) {
        this.students = students.stream()
                .map(student -> (JpaStudent) student)
                .collect(Collectors.toSet());
    }
}