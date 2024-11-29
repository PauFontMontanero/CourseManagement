package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models.JpaModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.models.ModelFactory;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.DbAssertions;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
class JpaCourseRepositoryTest {
    private static JpaRepositoryFactory repositoryFactory;
    private static ModelFactory modelFactory;
    private final Connection connection;

    public JpaCourseRepositoryTest(Connection connection) {
        this.connection = connection;
    }

    @BeforeAll
    static void setUp() {
        repositoryFactory = new JpaRepositoryFactory();
        modelFactory = new JpaModelFactory();
    }

    @BeforeEach
    void resetData() {
        // Reset course 1 back to "Singing I"
        var course1 = modelFactory.createCourse();
        course1.setId(1);
        course1.setName("Singing I");
        course1.setYear(2024);
        repositoryFactory.getCourseRepository().save(course1);

        // Reset course 2 back to "Singing II"
        var course2 = modelFactory.createCourse();
        course2.setId(2);
        course2.setName("Singing II");
        course2.setYear(2025);
        repositoryFactory.getCourseRepository().save(course2);
    }

    @AfterAll
    static void tearDown() {
        if (repositoryFactory != null) {
            repositoryFactory.close();
        }
    }

    @Test
    @DisplayName("Given a new course (id = 0), when save is called, then a new record is added to the COURSE table")
    void save() {
        var course = modelFactory.createCourse();
        course.setName("New Course");
        course.setYear(2024);

        var repository = repositoryFactory.getCourseRepository();
        repository.save(course);

        assertTrue(course.getId() > 0);

        DbAssertions.assertThat(connection)
                .table("COURSE")
                .where("ID", course.getId())
                .hasOneLine();
    }

    @Test
    @DisplayName("Given an existing course with modified fields, when save is called, then COURSE table is updated")
    void update() {
        var course = modelFactory.createCourse();
        course.setId(1);
        course.setName("Updated Course");
        course.setYear(2025);

        var repository = repositoryFactory.getCourseRepository();
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
        course.setId(2);  // Using course 2 which has student relationships

        var repository = repositoryFactory.getCourseRepository();
        repository.delete(course);

        // Verify that relationships are gone
        DbAssertions.assertThat(connection)
                .table("STUDENT_COURSE")
                .where("COURSE", course.getId())
                .doesNotExist();

        // Verify that course is gone
        DbAssertions.assertThat(connection)
                .table("COURSE")
                .where("ID", course.getId())
                .doesNotExist();
    }

    @Test
    @DisplayName("Given an existing course, when get is called, then the method returns an instance of Course")
    void get() {
        var repository = repositoryFactory.getCourseRepository();
        var course = repository.get(1);

        assertNotNull(course);
        assertEquals("Singing I", course.getName());
        assertEquals(2024, course.getYear());
    }

    @Test
    @DisplayName("Given existing courses, when getAll is called, then the method returns all the courses")
    void getAll() {
        var repository = repositoryFactory.getCourseRepository();
        var courses = repository.getAll();

        assertNotNull(courses);
        assertFalse(courses.isEmpty());

        DbAssertions.assertThat(connection)
                .table("COURSE")
                .hasLines(courses.size());
    }
}