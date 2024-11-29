package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models;

import cat.uvic.teknos.coursemanagement.clients.console.models.Course;
import cat.uvic.teknos.coursemanagement.clients.console.models.Genre;
import cat.uvic.teknos.coursemanagement.clients.console.models.Student;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "STUDENT")
public class JpaStudent implements Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @ManyToOne
    @JoinColumn(name = "GENRE")
    private JpaGenre genre;

    @OneToOne
    @JoinColumn(name = "ADDRESS")
    private JpaAddress address;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "BORN_ON")
    private LocalDate bornOn;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "STUDENT_COURSE",
            joinColumns = @JoinColumn(name = "STUDENT"),
            inverseJoinColumns = @JoinColumn(name = "COURSE"))
    private Set<JpaCourse> courses = new HashSet<>();

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Genre getGenre() {
        return genre;
    }

    @Override
    public void setGenre(Genre genre) {
        this.genre = (JpaGenre) genre;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public LocalDate getBornOn() {
        return bornOn;
    }

    @Override
    public void setBornOn(LocalDate bornOn) {
        this.bornOn = bornOn;
    }

    @Override
    public Set<Course> getCourses() {
        return new HashSet<>(courses);
    }

    @Override
    public void setCourses(Set<Course> courses) {
        this.courses = courses.stream()
                .map(course -> (JpaCourse) course)
                .collect(Collectors.toSet());
    }
}