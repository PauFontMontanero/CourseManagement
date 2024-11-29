package cat.uvic.teknos.coursemanagement.clients.console.backoffice;

import cat.uvic.teknos.coursemanagement.clients.console.models.ModelFactory;
import cat.uvic.teknos.coursemanagement.clients.console.models.Student;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.RepositoryFactory;
import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;


import java.io.BufferedReader;
import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static cat.uvic.teknos.coursemanagement.clients.console.backoffice.IOUtils.*;

public class StudentManager {
    private final BufferedReader in;
    private final PrintStream out;
    private final RepositoryFactory repositoryFacory;
    private final ModelFactory modelFactory;

    public StudentManager(BufferedReader in, PrintStream out, RepositoryFactory repositoryFactory, ModelFactory modelFactory) {
        this.in = in;
        this.out = out;

        this.repositoryFacory = repositoryFactory;
        this.modelFactory = modelFactory;
    }

    public void start() {
        out.println("Students:");
        out.println("Type:");
        out.println("1 to insert a new student");
        out.println("2 to modify a student data");
        out.println("3 to show a student details");
        out.println("4 to show all the students");

        var command = "";
        do {
            command = readLine(in);

            switch (command) {
                case "1" -> insert();
                case "2" -> update();
                case "3" -> get();
                case "4" -> getAll();
            }

        } while (!command.equals("exit"));

        out.println("Bye!");
    }

    private void get() {
    }

    private void insert() {

        out.println("Student inserted successfully");
    }

    private void update() {

    }

    private void getAll() {
        var students = repositoryFacory.getStudentRepository().getAll();
        out.println(AsciiTable.getTable(students, Arrays.asList(
                new Column().header("Id").with( s -> Integer.toString(s.getId())),
                new Column().header("First Name").with(Student::getFirstName),
                new Column().header("Last Name").with(Student::getLastName),
                new Column().header("Born on").with(s -> s.getBornOn().format(DateTimeFormatter.ISO_DATE))
                )));
    }
}
