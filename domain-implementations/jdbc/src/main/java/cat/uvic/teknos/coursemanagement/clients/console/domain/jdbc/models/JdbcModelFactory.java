package cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.models;

import cat.uvic.teknos.coursemanagement.clients.console.models.*;

public class JdbcModelFactory implements ModelFactory {
    @Override
    public Address createAddress() {
        return new JdbcAddress();
    }

    @Override
    public Course createCourse() {
        return new JdbcCourse();
    }

    @Override
    public Student courseStudent() {
        return new JdbcStudent();
    }

    @Override
    public Genre createGenre() {
        return new JdbcGenre();
    }
}
