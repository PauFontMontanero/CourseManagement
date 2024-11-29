package cat.uvic.teknos.coursemanagement.clients.console.domain.fake.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.domain.fake.models.FakeStudent;
import cat.uvic.teknos.coursemanagement.clients.console.models.Student;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.StudentRepository;

import java.time.LocalDate;
import java.util.Set;

public class FakeStudentRepository implements StudentRepository {
    @Override
    public void save(Student model) {

    }

    @Override
    public void delete(Student model) {

    }

    @Override
    public Student get(Integer id) {
        return null;
    }

    @Override
    public Set<Student> getAll() {
        return Set.of(
                new FakeStudent() {{ setId(1); setFirstName("Laura"); setLastName("Palmer"); setBornOn(LocalDate.of(1970,1,1)); }},
                new FakeStudent() {{ setId(2); setFirstName("Genghis"); setLastName("Khan"); setBornOn(LocalDate.of(1970,1,1)); }},
                new FakeStudent() {{ setId(3); setFirstName("Carlo"); setLastName("Ancelotti"); setBornOn(LocalDate.of(1970,1,1)); }}
        );
    }
}
