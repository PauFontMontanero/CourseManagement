package cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.models.JdbcGenre;
import cat.uvic.teknos.coursemanagement.clients.console.models.Genre;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.GenreRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class JdbcGenreRepository implements GenreRepository {
    private final Connection connection;

    public JdbcGenreRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Genre model) {
        try {
            if (model.getId() <= 0) {
                String sql = "INSERT INTO genre (DESCRIPTION) VALUES (?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, model.getDescription());
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            model.setId(rs.getInt(1));
                        }
                    }
                }
            } else {
                String sql = "UPDATE genre SET DESCRIPTION = ? WHERE ID = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, model.getDescription());
                    stmt.setInt(2, model.getId());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving genre", e);
        }
    }

    @Override
    public void delete(Genre model) {
        try {
            String updateStudentsSql = "UPDATE student SET GENRE = NULL WHERE GENRE = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateStudentsSql)) {
                stmt.setInt(1, model.getId());
                stmt.executeUpdate();
            }

            String deleteGenreSql = "DELETE FROM genre WHERE ID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteGenreSql)) {
                stmt.setInt(1, model.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting genre", e);
        }
    }

    @Override
    public Genre get(Integer id) {
        try {
            String sql = "SELECT * FROM genre WHERE ID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Genre genre = new JdbcGenre();
                        genre.setId(rs.getInt("ID"));
                        genre.setDescription(rs.getString("DESCRIPTION"));
                        return genre;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting genre", e);
        }
    }

    @Override
    public Set<Genre> getAll() {
        try {
            Set<Genre> genres = new HashSet<>();
            String sql = "SELECT * FROM genre";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Genre genre = new JdbcGenre();
                    genre.setId(rs.getInt("ID"));
                    genre.setDescription(rs.getString("DESCRIPTION"));
                    genres.add(genre);
                }
            }
            return genres;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all genres", e);
        }
    }
}