package cat.uvic.teknos.coursemanagement.clients.console.services.controllers;

import cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models.JpaGenre;
import cat.uvic.teknos.coursemanagement.clients.console.models.ModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.RepositoryFactory;
import cat.uvic.teknos.coursemanagement.clients.console.services.exception.ResourceNotFoundException;
import cat.uvic.teknos.coursemanagement.clients.console.services.exception.ServerErrorException;
import cat.uvic.teknos.coursemanagement.clients.console.services.utils.Mappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenreController implements Controller {
    private final RepositoryFactory repositoryFactory;
    private final ModelFactory modelFactory;
    private final ObjectMapper mapper;

    public GenreController(RepositoryFactory repositoryFactory, ModelFactory modelFactory) {
        this.repositoryFactory = repositoryFactory;
        this.modelFactory = modelFactory;
        this.mapper = Mappers.get();
    }

    @Override
    public String get(int id) {
        try {
            var genre = repositoryFactory.getGenreRepository().get(id);
            if (genre == null) {
                throw new ResourceNotFoundException("Genre not found with id: " + id);
            }
            return mapper.writeValueAsString(genre);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Error serializing genre", e);
        }
    }

    @Override
    public String get() {
        try {
            var genres = repositoryFactory.getGenreRepository().getAll();
            return mapper.writeValueAsString(genres);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Error serializing genres", e);
        }
    }

    @Override
    public void post(String json) {
        try {
            var genre = mapper.readValue(json, JpaGenre.class);
            repositoryFactory.getGenreRepository().save(genre);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Error deserializing genre", e);
        }
    }

    @Override
    public void put(int id, String json) {
        try {
            var genre = mapper.readValue(json, JpaGenre.class);
            genre.setId(id);
            repositoryFactory.getGenreRepository().save(genre);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Error deserializing genre", e);
        }
    }

    @Override
    public void delete(int id) {
        var genre = repositoryFactory.getGenreRepository().get(id);
        if (genre == null) {
            throw new ResourceNotFoundException("Genre not found with id: " + id);
        }
        repositoryFactory.getGenreRepository().delete(genre);
    }
}
