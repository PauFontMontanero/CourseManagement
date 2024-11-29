package cat.uvic.teknos.coursemanagement.clients.console.services.controllers;

import cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models.JpaStudent;
import cat.uvic.teknos.coursemanagement.clients.console.models.ModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.RepositoryFactory;
import cat.uvic.teknos.coursemanagement.clients.console.services.exception.ServerErrorException;

import cat.uvic.teknos.coursemanagement.clients.console.services.utils.Mappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class StudentController implements Controller {
    private final RepositoryFactory repositoryFactory;
    private final ModelFactory modelFactory;
    private final ObjectMapper mapper;

    public StudentController(RepositoryFactory repositoryFactory, ModelFactory modelFactory) {
        this.repositoryFactory = repositoryFactory;
        this.modelFactory = modelFactory;
        this.mapper = Mappers.get();
    }

    @Override
    public String get(int id) {
        try {
            var student = repositoryFactory.getStudentRepository().get(id);
            return mapper.writeValueAsString(student);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Error serializing student", e);
        }
    }

    @Override
    public String get() {
        try {
            var students = repositoryFactory.getStudentRepository().getAll();
            return mapper.writeValueAsString(students);
        } catch (JsonProcessingException e) {
            throw new ServerErrorException("Error serializing students", e);
        }
    }

    @Override
    public void post(String json) {
        try {
            System.out.println("Received JSON: " + json);
            JpaStudent student = mapper.readValue(json, JpaStudent.class);

            // Add these debug lines
            System.out.println("Deserialized student: " + student);
            System.out.println("Student genre: " + student.getGenre());

            try {
                repositoryFactory.getStudentRepository().save(student);
            } catch (Exception e) {
                e.printStackTrace(); // Add this to see the actual save error
                throw new ServerErrorException("Error saving student: " + e.getMessage(), e);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ServerErrorException("Error deserializing student: " + e.getMessage(), e);
        }
    }

    @Override
    public void put(int id, String json) {
        try {
            System.out.println("Received JSON for update: " + json); // Add this debug line
            JpaStudent updatedStudent = mapper.readValue(json, JpaStudent.class);
            updatedStudent.setId(id);
            repositoryFactory.getStudentRepository().save(updatedStudent);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Add this line
            throw new ServerErrorException("Error deserializing student: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        var student = repositoryFactory.getStudentRepository().get(id);
        if (student != null) {
            repositoryFactory.getStudentRepository().delete(student);
        }
    }
}
