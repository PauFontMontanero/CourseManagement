package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models;

import cat.uvic.teknos.coursemanagement.clients.console.models.*;

public class JpaModelFactory implements ModelFactory {
    @Override
    public Address createAddress() {
        return new JpaAddress();
    }

    @Override
    public Course createCourse() {
        return new JpaCourse();
    }

    @Override
    public Student courseStudent() {
        return new JpaStudent();
    }

    @Override
    public Genre createGenre() {
        return new JpaGenre();
    }
}