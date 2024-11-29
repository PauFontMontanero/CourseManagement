package cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.models.ModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.models.JdbcModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.models.Genre;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.DbAssertions;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
class JdbcStudentRepositoryTest {
    private final ModelFactory modelFactory = new JdbcModelFactory();
    private final Connection connection;
    private final JdbcStudentRepository repository;
    private final JdbcGenreRepository genreRepository;

    public JdbcStudentRepositoryTest(Connection connection) {
        this.connection = connection;
        this.repository = new JdbcStudentRepository(connection);
        this.genreRepository = new JdbcGenreRepository(connection);
    }

    @BeforeEach
    void resetData() {
        // Reset student 1 back to original state
        var student1 = modelFactory.courseStudent();
        student1.setId(1);
        student1.setFirstName("Nick");
        student1.setLastName("Drake");
        student1.setGenre(genreRepository.get(1));
        repository.save(student1);

        // Reset student 2 back to original state
        var student2 = modelFactory.courseStudent();
        student2.setId(2);
        student2.setFirstName("Laura");
        student2.setLastName("Pausini");
        student2.setGenre(genreRepository.get(1));
        repository.save(student2);
    }

    @Test
    @DisplayName("Given a new student (id = 0), when save, then a new record is added to the STUDENT table")
    void shouldInsertNewStudentTest() {
        var student = modelFactory.courseStudent();
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setBornOn(LocalDate.of(2000, 1, 1));

        // Get a genre for the student
        var genreRepository = new JdbcGenreRepository(connection);
        Genre genre = genreRepository.get(1);
        student.setGenre(genre);

        var repository = new JdbcStudentRepository(connection);
        repository.save(student);

        assertTrue(student.getId() > 0);

        DbAssertions.assertThat(connection)
                .table("STUDENT")
                .where("ID", student.getId())
                .hasOneLine();
    }

    @Test
    @DisplayName("Given an existing student with modified fields, when save, then STUDENT table is updated")
    void shouldUpdateAStudentTest() {
        var student = modelFactory.courseStudent();
        student.setId(1);
        student.setFirstName("Updated");
        student.setLastName("Student");

        // Get a genre for the student
        var genreRepository = new JdbcGenreRepository(connection);
        Genre genre = genreRepository.get(1);
        student.setGenre(genre);

        var repository = new JdbcStudentRepository(connection);
        repository.save(student);

        DbAssertions.assertThat(connection)
                .table("STUDENT")
                .where("ID", student.getId())
                .column("FIRST_NAME")
                .valueEqual("Updated");

        DbAssertions.assertThat(connection)
                .table("STUDENT")
                .where("ID", student.getId())
                .column("LAST_NAME")
                .valueEqual("Student");
    }

    @Test
    @DisplayName("Given an existing student, when delete is called, then STUDENT table is updated")
    void delete() {
        var student = modelFactory.courseStudent();
        student.setId(1);

        var repository = new JdbcStudentRepository(connection);
        repository.delete(student);

        // Verify student-course relationships are deleted
        DbAssertions.assertThat(connection)
                .table("STUDENT_COURSE")
                .where("STUDENT", student.getId())
                .doesNotExist();

        // Verify student is deleted
        DbAssertions.assertThat(connection)
                .table("STUDENT")
                .where("ID", student.getId())
                .doesNotExist();
    }
    @Test
    @DisplayName("Given an existing student, when get is called, then the method returns an instance of Student")
    void get() {
        var repository = new JdbcStudentRepository(connection);
        var student = repository.get(1);

        assertNotNull(student);
        assertEquals("Nick", student.getFirstName());
        assertEquals("Drake", student.getLastName());
        assertNotNull(student.getGenre());
        assertEquals("Male", student.getGenre().getDescription());
    }

    @Test
    @DisplayName("Given existing students, when getAll is called, then the method returns all the students")
    void getAll() {
        var repository = new JdbcStudentRepository(connection);
        var students = repository.getAll();

        assertNotNull(students);

        DbAssertions.assertThat(connection)
                .table("STUDENT")
                .hasLines(students.size());
    }
}