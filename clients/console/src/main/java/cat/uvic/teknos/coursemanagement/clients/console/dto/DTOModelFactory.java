package cat.uvic.teknos.coursemanagement.clients.console.dto;

public class DTOModelFactory {
    public static StudentDTO createStudentDTO() {
        return new StudentDTO();
    }

    public static CourseDTO createCourseDTO() {
        return new CourseDTO();
    }

    public static GenreDTO createGenreDTO() {
        return new GenreDTO();
    }
}