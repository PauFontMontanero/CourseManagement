package cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.models.ModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.models.JdbcModelFactory;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.DbAssertions;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
class JdbcCourseRepositoryTest {
    private final ModelFactory modelFactory = new JdbcModelFactory();
    private final Connection connection;
    private final JdbcCourseRepository repository;

    public JdbcCourseRepositoryTest(Connection connection) {
        this.connection = connection;
        this.repository = new JdbcCourseRepository(connection);
    }

    @BeforeEach
    void resetData() {
        // Reset course 1 back to "Singing I"
        var course1 = modelFactory.createCourse();
        course1.setId(1);
        course1.setName("Singing I");
        course1.setYear(2024);
        repository.save(course1);

        // Reset course 2 back to "Singing II"
        var course2 = modelFactory.createCourse();
        course2.setId(2);
        course2.setName("Singing II");
        course2.setYear(2025);
        repository.save(course2);
    }

    @Test
    @DisplayName("Given a new course (id = 0), when save, then a new record is added to the COURSE table")
    void insert() {
        var course = modelFactory.createCourse();
        course.setName("New Course");
        course.setYear(2024);

        var repository = new JdbcCourseRepository(connection);
        repository.save(course);

        assertTrue(course.getId() > 0);

        DbAssertions.assertThat(connection)
                .table("COURSE")
                .where("ID", course.getId())
                .hasOneLine();
    }

    @Test
    @DisplayName("Given an existing course with modified fields, when save, then COURSE table is updated")
    void update() {
        var course = modelFactory.createCourse();
        course.setId(1);
        course.setName("Updated Course");
        course.setYear(2025);

        var repository = new JdbcCourseRepository(connection);
        repository.save(course);

        DbAssertions.assertThat(connection)
                .table("COURSE")
                .where("ID", course.getId())
                .column("NAME")
                .valueEqual("Updated Course");

        DbAssertions.assertThat(connection)
                .table("COURSE")
                .where("ID", course.getId())
                .column("YEAR")
                .valueEqual(2025);
    }

    @Test
    @DisplayName("Given an existing course, when delete is called, then COURSE table is updated")
    void delete() {
        var course = modelFactory.createCourse();
        course.setId(1);

        var repository = new JdbcCourseRepository(connection);

        // Delete the course (which should handle relationships)
        repository.delete(course);

        // Verify course is gone
        DbAssertions.assertThat(connection)
                .table("STUDENT_COURSE")
                .where("COURSE", course.getId())
                .doesNotExist();

        // Verify course is gone
        DbAssertions.assertThat(connection)
                .table("COURSE")
                .where("ID", course.getId())
                .doesNotExist();
    }

    @Test
    @DisplayName("Given an existing course, when get is called, then the method returns an instance of Course")
    void get() {
        var repository = new JdbcCourseRepository(connection);
        var course = repository.get(1);

        assertNotNull(course);
        assertEquals("Singing I", course.getName());
        assertEquals(2024, course.getYear());
    }

    @Test
    @DisplayName("Given existing courses, when getAll is called, then the method returns all the courses")
    void getAll() {
        var repository = new JdbcCourseRepository(connection);
        var courses = repository.getAll();

        assertNotNull(courses);

        DbAssertions.assertThat(connection)
                .table("COURSE")
                .hasLines(courses.size());
    }
}