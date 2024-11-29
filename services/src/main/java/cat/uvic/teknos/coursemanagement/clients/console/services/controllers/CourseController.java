package cat.uvic.teknos.coursemanagement.clients.console.services.controllers;

import cat.uvic.teknos.coursemanagement.clients.console.models.Course;
import cat.uvic.teknos.coursemanagement.clients.console.models.ModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.RepositoryFactory;
import cat.uvic.teknos.coursemanagement.clients.console.services.exception.ResourceNotFoundException;
import cat.uvic.teknos.coursemanagement.clients.console.services.exception.ServerErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CourseController implements Controller {
    private final RepositoryFactory repositoryFactory;
    private final ModelFactory modelFactory;
    private final ObjectMapper mapper;

    public CourseController(RepositoryFactory repositoryFactory, ModelFactory modelFactory) {
        this.repositoryFactory = repositoryFactory;
        this.modelFactory = modelFactory;
        this.mapper = new ObjectMapper();
    }

    @Override
    public String get(int id) {
        try {
            var course = repositoryFactory.getCourseRepository().get(id);
            if (course == null) {
                throw new ResourceNotFoundException("Course not found with id: " + id);
            }
            return mapper.writeValueAsString(course);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Error serializing course", e);
        }
    }

    @Override
    public String get() {
        try {
            var courses = repositoryFactory.getCourseRepository().getAll();
            return mapper.writeValueAsString(courses);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Error serializing courses", e);
        }
    }

    @Override
    public void post(String json) {
        try {
            Course course = modelFactory.createCourse();
            mapper.readerForUpdating(course).readValue(json);

            if (course.getId() != 0) {
                throw new ServerErrorException("New course should not have an ID");
            }

            repositoryFactory.getCourseRepository().save(course);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Error deserializing course", e);
        }
    }

    @Override
    public void put(int id, String json) {
        try {
            var repository = repositoryFactory.getCourseRepository();
            var existingCourse = repository.get(id);

            if (existingCourse == null) {
                throw new ResourceNotFoundException("Course not found with id: " + id);
            }

            mapper.readerForUpdating(existingCourse).readValue(json);
            repository.save(existingCourse);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Error deserializing course", e);
        }
    }

    @Override
    public void delete(int id) {
        var repository = repositoryFactory.getCourseRepository();
        var course = repository.get(id);

        if (course == null) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }

        repository.delete(course);
    }
}