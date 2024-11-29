package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models.JpaGenre;
import cat.uvic.teknos.coursemanagement.clients.console.models.Genre;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.GenreRepository;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.Set;

public class JpaGenreRepository implements GenreRepository {
    private final EntityManagerFactory entityManagerFactory;

    public JpaGenreRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Genre model) {
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
            throw new RuntimeException("Error saving genre", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void delete(Genre model) {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Genre genre = entityManager.find(JpaGenre.class, model.getId());
            if (genre != null) {
                entityManager.remove(genre);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new RuntimeException("Error deleting genre", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Genre get(Integer id) {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(JpaGenre.class, id);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Set<Genre> getAll() {
        var entityManager = entityManagerFactory.createEntityManager();
        try {
            return new HashSet<>(
                    entityManager.createQuery("SELECT g FROM JpaGenre g", JpaGenre.class)
                            .getResultList()
            );
        } finally {
            entityManager.close();
        }
    }
}