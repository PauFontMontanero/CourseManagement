package cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.domain.jdbc.models.JdbcCourse;
import cat.uvic.teknos.coursemanagement.clients.console.models.Course;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.CourseRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class JdbcCourseRepository implements CourseRepository {
    private final Connection connection;

    public JdbcCourseRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Course model) {
        try {
            if (model.getId() <= 0) {
                String sql = "INSERT INTO course (NAME, YEAR) VALUES (?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, model.getName());
                    stmt.setInt(2, model.getYear());
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            model.setId(rs.getInt(1));
                        }
                    }
                }
            } else {
                String sql = "UPDATE course SET NAME=?, YEAR=? WHERE ID=?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, model.getName());
                    stmt.setInt(2, model.getYear());
                    stmt.setInt(3, model.getId());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving course", e);
        }
    }

    @Override
    public void delete(Course model) {
        try {
            String deleteRelationshipsSql = "DELETE FROM student_course WHERE COURSE = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteRelationshipsSql)) {
                stmt.setInt(1, model.getId());
                stmt.executeUpdate();
            }

            String deleteCourseSql = "DELETE FROM course WHERE ID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteCourseSql)) {
                stmt.setInt(1, model.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting course", e);
        }
    }

    @Override
    public Course get(Integer id) {
        try {
            String sql = "SELECT * FROM course WHERE ID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Course course = new JdbcCourse();
                        course.setId(rs.getInt("ID"));
                        course.setName(rs.getString("NAME"));
                        course.setYear(rs.getInt("YEAR"));
                        return course;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting course", e);
        }
    }

    @Override
    public Set<Course> getAll() {
        try {
            Set<Course> courses = new HashSet<>();
            String sql = "SELECT * FROM course";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Course course = new JdbcCourse();
                    course.setId(rs.getInt("ID"));
                    course.setName(rs.getString("NAME"));
                    course.setYear(rs.getInt("YEAR"));
                    courses.add(course);
                }
            }
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all courses", e);
        }
    }
}