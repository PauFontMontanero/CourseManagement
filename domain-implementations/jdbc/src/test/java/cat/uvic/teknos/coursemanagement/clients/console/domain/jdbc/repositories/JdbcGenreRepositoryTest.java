package cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.models.ModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.models.JdbcModelFactory;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.DbAssertions;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
class JdbcGenreRepositoryTest {
    private final ModelFactory modelFactory = new JdbcModelFactory();
    private final Connection connection;
    private final JdbcGenreRepository repository;

    public JdbcGenreRepositoryTest(Connection connection) {
        this.connection = connection;
        this.repository = new JdbcGenreRepository(connection);
    }

    @BeforeEach
    void resetData() {
        // Reset genre 1 back to "Male"
        var genre1 = modelFactory.createGenre();
        genre1.setId(1);
        genre1.setDescription("Male");
        repository.save(genre1);

        // Reset genre 2 back to "Female"
        var genre2 = modelFactory.createGenre();
        genre2.setId(2);
        genre2.setDescription("Female");
        repository.save(genre2);
    }
    @Test
    @DisplayName("Given a new genre (id = 0), when save, then a new record is added to the GENRE table")
    void shouldInsertNewGenreTest() {
        var genre = modelFactory.createGenre();
        genre.setDescription("Undefined");

        var repository = new JdbcGenreRepository(connection);

        // Test
        repository.save(genre);

        assertTrue(genre.getId() > 0);

        DbAssertions.assertThat(connection)
                .table("GENRE")
                .where("ID", genre.getId())
                .hasOneLine();
    }

    @Test
    @DisplayName("Given an existing genre with modified fields, when save, then GENRE table is updated")
    void shouldUpdateAGenreTest() {
        var genre = modelFactory.createGenre();
        genre.setId(1);
        genre.setDescription("Super Male");

        var repository = new JdbcGenreRepository(connection);
        repository.save(genre);

        //TODO: test database table updated
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
        genre.setId(1);

        var repository = new JdbcGenreRepository(connection);
        repository.delete(genre);

        // Verify genre is deleted
        DbAssertions.assertThat(connection)
                .table("GENRE")
                .where("ID", genre.getId())
                .doesNotExist();

        // Verify that no students reference this genre
        DbAssertions.assertThat(connection)
                .table("STUDENT")
                .where("GENRE", genre.getId())
                .doesNotExist();
    }

    @Test
    @DisplayName("Given an existing genre, when get is called, then the method return an instance of Genre")
    void get() {
        var repository = new JdbcGenreRepository(connection);
        var genre = repository.get(1);

        assertNotNull(genre);
        assertEquals("Male", genre.getDescription());  // Add specific assertion
    }

    @Test
    @DisplayName("Given existing genres, when getAll is called, then the method return all the genres")
    void getAll() {
        var repository = new JdbcGenreRepository(connection);
        var genres = repository.getAll();

        assertNotNull(genres);

        DbAssertions.assertThat(connection)
                .table("GENRE")
                .hasLines(genres.size());
    }
}