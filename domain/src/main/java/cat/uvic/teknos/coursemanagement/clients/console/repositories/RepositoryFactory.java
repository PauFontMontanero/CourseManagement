package cat.uvic.teknos.coursemanagement.clients.console.repositories;

public interface RepositoryFactory {
    GenreRepository getGenreRepository();
    CourseRepository  getCourseRepository();
    StudentRepository getStudentRepository();
}
