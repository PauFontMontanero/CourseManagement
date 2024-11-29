package cat.uvic.teknos.coursemanagement.clients.console.domain.fake.models;

import cat.uvic.teknos.coursemanagement.clients.console.models.Course;
import cat.uvic.teknos.coursemanagement.clients.console.models.Student;

import java.util.HashSet;
import java.util.Set;

public class FakeCourse implements Course {
    private int id;
    private int year;
    private String name;
    private Set<Student>  students = new HashSet<>();

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
        return students;
    }

    @Override
    public void setStudents(Set<Student> students) {
        this.students = students;
    }
}
