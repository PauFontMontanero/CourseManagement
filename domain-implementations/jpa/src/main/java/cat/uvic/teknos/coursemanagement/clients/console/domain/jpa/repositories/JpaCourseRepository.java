package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models.JpaCourse;
import cat.uvic.teknos.coursemanagement.clients.console.models.Course;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.CourseRepository;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.Set;

public class JpaCourseRepository implements CourseRepository {
    private final EntityManagerFactory entityManagerFactory;

    public JpaCourseRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Course model) {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            if (model.getId() <= 0) {
                entityManager.persist(model);
            } else {
                entityManager.merge(model);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new RuntimeException("Error saving course", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void delete(Course model) {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();

            // First delete the relationships from student_course
            entityManager.createNativeQuery("DELETE FROM student_course WHERE course = ?")
                    .setParameter(1, model.getId())
                    .executeUpdate();

            // Then delete the course
            Course course = entityManager.find(JpaCourse.class, model.getId());
            if (course != null) {
                entityManager.remove(course);
            }

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new RuntimeException("Error deleting course", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Course get(Integer id) {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(JpaCourse.class, id);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Set<Course> getAll() {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            return new HashSet<>(
                    entityManager.createQuery("SELECT c FROM JpaCourse c", JpaCourse.class)
                            .getResultList()
            );
        } finally {
            entityManager.close();
        }
    }
}