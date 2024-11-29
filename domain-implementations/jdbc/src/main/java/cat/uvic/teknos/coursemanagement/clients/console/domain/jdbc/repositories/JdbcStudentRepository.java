package cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.models.JdbcStudent;
import cat.uvic.teknos.coursemanagement.clients.console.models.Student;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.StudentRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class JdbcStudentRepository implements StudentRepository {
    private final Connection connection;
    private final JdbcGenreRepository genreRepository;

    public JdbcStudentRepository(Connection connection) {
        this.connection = connection;
        this.genreRepository = new JdbcGenreRepository(connection);
    }

    @Override
    public void save(Student model) {
        try {
            if (model.getId() <= 0) {
                insert(model);
            } else {
                update(model);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving student", e);
        }
    }

    private void insert(Student student) throws SQLException {
        String sql = "INSERT INTO student (FIRST_NAME, LAST_NAME, BORN_ON, GENRE) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            if (student.getBornOn() != null) {
                stmt.setDate(3, Date.valueOf(student.getBornOn()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            stmt.setInt(4, student.getGenre().getId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    student.setId(rs.getInt(1));
                }
            }
        }
    }

    private void update(Student student) throws SQLException {
        String sql = "UPDATE student SET FIRST_NAME=?, LAST_NAME=?, BORN_ON=?, GENRE=? WHERE ID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            if (student.getBornOn() != null) {
                stmt.setDate(3, Date.valueOf(student.getBornOn()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            stmt.setInt(4, student.getGenre().getId());
            stmt.setInt(5, student.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(Student model) {
        try {
            // First delete from student_course table
            String deleteRelationshipsSql = "DELETE FROM student_course WHERE STUDENT = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteRelationshipsSql)) {
                stmt.setInt(1, model.getId());
                stmt.executeUpdate();
            }

            // Then delete the student
            String deleteStudentSql = "DELETE FROM student WHERE ID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteStudentSql)) {
                stmt.setInt(1, model.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting student", e);
        }
    }

    @Override
    public Student get(Integer id) {
        try {
            String sql = "SELECT * FROM student WHERE ID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Student student = new JdbcStudent();
                        student.setId(rs.getInt("ID"));
                        student.setFirstName(rs.getString("FIRST_NAME"));
                        student.setLastName(rs.getString("LAST_NAME"));
                        Date bornOn = rs.getDate("BORN_ON");
                        if (bornOn != null) {
                            student.setBornOn(bornOn.toLocalDate());
                        }
                        student.setGenre(genreRepository.get(rs.getInt("GENRE")));
                        return student;
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting student", e);
        }
    }

    @Override
    public Set<Student> getAll() {
        try {
            Set<Student> students = new HashSet<>();
            String sql = "SELECT * FROM student";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Student student = new JdbcStudent();
                    student.setId(rs.getInt("ID"));
                    student.setFirstName(rs.getString("FIRST_NAME"));
                    student.setLastName(rs.getString("LAST_NAME"));
                    Date bornOn = rs.getDate("BORN_ON");
                    if (bornOn != null) {
                        student.setBornOn(bornOn.toLocalDate());
                    }
                    student.setGenre(genreRepository.get(rs.getInt("GENRE")));
                    students.add(student);
                }
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all students", e);
        }
    }
}