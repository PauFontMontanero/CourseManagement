package cat.uvic.teknos.coursemanagement.services.controllers;

import cat.uvic.teknos.coursemanagement.models.ModelFactory;
import cat.uvic.teknos.coursemanagement.models.Student;
import cat.uvic.teknos.coursemanagement.repositories.RepositoryFactory;
import cat.uvic.teknos.coursemanagement.repositories.StudentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StudentController implements Controller<Integer, Student>{
    private final RepositoryFactory repositoryFactory;
    private final ModelFactory modelFactory;

    public StudentController(RepositoryFactory repositoryFactory, ModelFactory modelFactory) {
        this.repositoryFactory = repositoryFactory;
        this.modelFactory = modelFactory;
    }

    @Override
    public String get(Integer integer) {
        // retrieve (get) student from db
        // serialize student in json format

        return ""; // json
    }

    @Override
    public String get() {
        var students = repositoryFactory.getStudentRepository().getAll();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(students);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void post(Student value) {
        repositoryFactory.getStudentRepository().save(value);
    }

    @Override
    public void put(Integer key, Student value) {

    }

    @Override
    public void delete(Integer key) {

    }
}
