package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models.JpaModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.models.ModelFactory;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.DbAssertions;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import java.sql.Connection;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
class JpaGenreRepositoryTest {
    private static JpaRepositoryFactory repositoryFactory;
    private static ModelFactory modelFactory;
    private final Connection connection;

    public JpaGenreRepositoryTest(Connection connection) {
        this.connection = connection;
    }

    @BeforeAll
    static void setUp() {
        repositoryFactory = new JpaRepositoryFactory();
        modelFactory = new JpaModelFactory();
    }

    @BeforeEach
    void resetData() {
        // Reset genre 1 back to "Male"
        var genre1 = modelFactory.createGenre();
        genre1.setId(1);
        genre1.setDescription("Male");
        repositoryFactory.getGenreRepository().save(genre1);

        // Reset genre 2 back to "Female"
        var genre2 = modelFactory.createGenre();
        genre2.setId(2);
        genre2.setDescription("Female");
        repositoryFactory.getGenreRepository().save(genre2);
    }

    @AfterAll
    static void tearDown() {
        if (repositoryFactory != null) {
            repositoryFactory.close();
        }
    }

    @Test
    @DisplayName("Given a new genre (id = 0), when save is called, then a new record is added to the GENRE table")
    void save() {
        var genre = modelFactory.createGenre();
        genre.setDescription("Undefined");

        var repository = repositoryFactory.getGenreRepository();

        repository.save(genre);

        assertTrue(genre.getId() > 0);

        DbAssertions.assertThat(connection)
                .table("GENRE")
                .where("ID", genre.getId())
                .hasOneLine();
    }

    @Test
    @DisplayName("Given an existing genre with modified fields, when save is called, then GENRE table is updated")
    void update() {
        var genre = modelFactory.createGenre();
        genre.setId(1);
        genre.setDescription("Super Male");

        var repository = repositoryFactory.getGenreRepository();
        repository.save(genre);

        DbAssertions.assertThat(connection)
                .table("GENRE")
                .where("ID", genre.getId())
                .column("DESCRIPTION")
                .valueEqual("Super Male");
    }

    @Test
    @DisplayName("Given an existing genre, when delete is called, then GENRE table is updated")
    void delete() {
        var genre = modelFactory.createGenre();
        genre.setId(2);

        var repository = repositoryFactory.getGenreRepository();
        repository.delete(genre);

        DbAssertions.assertThat(connection)
                .table("GENRE")
                .where("ID", genre.getId())
                .doesNotExist();
    }

    @Test
    @DisplayName("Given an existing genre, when get is called, then the method return an instance of Genre")
    void get() {
        var repository = repositoryFactory.getGenreRepository();
        var genre = repository.get(1);

        assertNotNull(genre);
        assertEquals("Male", genre.getDescription());
    }

    @Test
    @DisplayName("Given existing genres, when getAll is called, then the method return all the genres")
    void getAll() {
        var repository = repositoryFactory.getGenreRepository();
        var genres = repository.getAll();

        assertNotNull(genres);
        assertFalse(genres.isEmpty());

        DbAssertions.assertThat(connection)
                .table("GENRE")
                .hasLines(genres.size());
    }
}