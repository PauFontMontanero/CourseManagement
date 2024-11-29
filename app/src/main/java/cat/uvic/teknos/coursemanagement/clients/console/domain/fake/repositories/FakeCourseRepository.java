package cat.uvic.teknos.coursemanagement.clients.console.domain.fake.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.models.Course;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.CourseRepository;

import java.util.Set;

public class FakeCourseRepository implements CourseRepository {
    @Override
    public void save(Course model) {

    }

    @Override
    public void delete(Course model) {

    }

    @Override
    public Course get(Integer id) {
        return null;
    }

    @Override
    public Set<Course> getAll() {
        return Set.of();
    }
}
