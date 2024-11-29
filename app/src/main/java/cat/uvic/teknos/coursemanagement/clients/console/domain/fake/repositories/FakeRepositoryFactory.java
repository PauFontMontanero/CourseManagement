package cat.uvic.teknos.coursemanagement.clients.console.domain.fake.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.repositories.CourseRepository;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.GenreRepository;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.RepositoryFactory;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.StudentRepository;

public class FakeRepositoryFactory implements RepositoryFactory {
    @Override
    public GenreRepository getGenreRepository() {
        return new FakeGenreRepository();
    }

    @Override
    public CourseRepository getCourseRepository() {
        return new FakeCourseRepository();
    }

    @Override
    public StudentRepository getStudentRepository() {
        return new FakeStudentRepository();
    }
}
