package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models.JpaStudent;
import cat.uvic.teknos.coursemanagement.clients.console.models.Student;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.StudentRepository;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.Set;

public class JpaStudentRepository implements StudentRepository {
    private final EntityManagerFactory entityManagerFactory;

    public JpaStudentRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Student model) {
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
            throw new RuntimeException("Error saving student", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void delete(Student model) {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Student student = entityManager.find(JpaStudent.class, model.getId());
            if (student != null) {
                entityManager.remove(student);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new RuntimeException("Error deleting student", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Student get(Integer id) {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            var student = entityManager.createQuery(
                            "SELECT DISTINCT s FROM JpaStudent s " +
                                    "LEFT JOIN FETCH s.courses " +
                                    "WHERE s.id = :id", JpaStudent.class)
                    .setParameter("id", id)
                    .getResultList();

            return student.isEmpty() ? null : student.get(0);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Set<Student> getAll() {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            return new HashSet<>(
                    entityManager.createQuery(
                                    "SELECT DISTINCT s FROM JpaStudent s " +
                                            "LEFT JOIN FETCH s.courses", JpaStudent.class)
                            .getResultList()
            );
        } finally {
            entityManager.close();
        }
    }
}