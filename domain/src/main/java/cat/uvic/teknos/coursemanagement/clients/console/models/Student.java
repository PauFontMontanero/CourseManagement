package cat.uvic.teknos.coursemanagement.clients.console.models;

import java.time.LocalDate;
import java.util.Set;

public interface Student {
    int getId();
    void setId(int id);
    Genre getGenre();
    void setGenre(Genre genre);
    void setFirstName(String firstName);
    String getFirstName();
    String getLastName();
    void setLastName(String lastName);
    LocalDate getBornOn();
    void setBornOn(LocalDate bornOn);
    Set<Course> getCourses();
    void setCourses(Set<Course> courses);
}