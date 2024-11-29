package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.exceptions.RepositoryException;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.CourseRepository;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.GenreRepository;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.RepositoryFactory;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.StudentRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.util.Properties;

public class JpaRepositoryFactory implements RepositoryFactory, AutoCloseable {
    private final EntityManagerFactory entityManagerFactory;

    public JpaRepositoryFactory() {
        try {
            var properties = new Properties();
            properties.load(JpaRepositoryFactory.class.getResourceAsStream("/jpa.properties"));

            entityManagerFactory = Persistence.createEntityManagerFactory("course-management", properties);
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public GenreRepository getGenreRepository() {
        return new JpaGenreRepository(entityManagerFactory);
    }

    @Override
    public CourseRepository getCourseRepository() {
        return new JpaCourseRepository(entityManagerFactory);
    }

    @Override
    public StudentRepository getStudentRepository() {
        return new JpaStudentRepository(entityManagerFactory);
    }

    @Override
    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}