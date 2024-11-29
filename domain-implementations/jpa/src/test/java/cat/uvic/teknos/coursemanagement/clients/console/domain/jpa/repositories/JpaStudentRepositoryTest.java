package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models.JpaModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.models.ModelFactory;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.DbAssertions;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
class JpaStudentRepositoryTest {
    private static JpaRepositoryFactory repositoryFactory;
    private static ModelFactory modelFactory;
    private final Connection connection;

    public JpaStudentRepositoryTest(Connection connection) {
        this.connection = connection;
    }

    @BeforeAll
    static void setUp() {
        repositoryFactory = new JpaRepositoryFactory();
        modelFactory = new JpaModelFactory();
    }

    @BeforeEach
    void resetData() {
        // Reset student 1 back to original state
        var student1 = modelFactory.courseStudent();
        student1.setId(1);
        student1.setFirstName("Nick");
        student1.setLastName("Drake");
        student1.setGenre(repositoryFactory.getGenreRepository().get(1));
        repositoryFactory.getStudentRepository().save(student1);

        // Reset student 2 back to original state
        var student2 = modelFactory.courseStudent();
        student2.setId(2);
        student2.setFirstName("Laura");
        student2.setLastName("Pausini");
        student2.setGenre(repositoryFactory.getGenreRepository().get(1));
        repositoryFactory.getStudentRepository().save(student2);
    }

    @AfterAll
    static void tearDown() {
        if (repositoryFactory != null) {
            repositoryFactory.close();
        }
    }

    @Test
    @DisplayName("Given a new student (id = 0), when save is called, then a new record is added to the STUDENT table")
    void save() {
        var student = modelFactory.courseStudent();
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setBornOn(LocalDate.of(2000, 1, 1));
        student.setGenre(repositoryFactory.getGenreRepository().get(1));

        var repository = repositoryFactory.getStudentRepository();
        repository.save(student);

        assertTrue(student.getId() > 0);

        DbAssertions.assertThat(connection)
                .table("STUDENT")
                .where("ID", student.getId())
                .hasOneLine();
    }

    @Test
    @DisplayName("Given an existing student with modified fields, when save is called, then STUDENT table is updated")
    void update() {
        var student = modelFactory.courseStudent();
        student.setId(1);
        student.setFirstName("Updated");
        student.setLastName("Student");
        student.setGenre(repositoryFactory.getGenreRepository().get(1));

        var repository = repositoryFactory.getStudentRepository();
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
        student.setId(2);

        var repository = repositoryFactory.getStudentRepository();
        repository.delete(student);

        DbAssertions.assertThat(connection)
                .table("STUDENT")
                .where("ID", student.getId())
                .doesNotExist();
    }

    @Test
    @DisplayName("Given an existing student, when get is called, then the method returns an instance of Student")
    void get() {
        var repository = repositoryFactory.getStudentRepository();
        var student = repository.get(1);

        assertNotNull(student);
        assertEquals("Nick", student.getFirstName());
        assertEquals("Drake", student.getLastName());
        assertNotNull(student.getGenre());
    }

    @Test
    @DisplayName("Given existing students, when getAll is called, then the method returns all the students")
    void getAll() {
        var repository = repositoryFactory.getStudentRepository();
        var students = repository.getAll();

        assertNotNull(students);
        assertFalse(students.isEmpty());

        DbAssertions.assertThat(connection)
                .table("STUDENT")
                .hasLines(students.size());
    }
}