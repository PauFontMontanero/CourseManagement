package cat.uvic.teknos.coursemanagement.clients.console.domain.fake.models;

import cat.uvic.teknos.coursemanagement.clients.console.models.*;
import cat.uvic.teknos.coursemanagement.models.*;

public class FakeModelFactory implements ModelFactory {

    @Override
    public Address createAddress() {
        return new FakeAddress();
    }

    @Override
    public Course createCourse() {
        return new FakeCourse();
    }

    @Override
    public Student courseStudent() {
        return  new FakeStudent();
    }

    @Override
    public Genre createGenre() {
        return new FakeGenre();
    }
}
