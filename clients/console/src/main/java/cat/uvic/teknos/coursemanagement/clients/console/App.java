package cat.uvic.teknos.coursemanagement.clients.console;

import cat.uvic.teknos.coursemanagement.clients.console.dto.CourseDTO;
import cat.uvic.teknos.coursemanagement.clients.console.dto.StudentDTO;
import cat.uvic.teknos.coursemanagement.clients.console.dto.GenreDTO;
import cat.uvic.teknos.coursemanagement.clients.console.exceptions.ConsoleClientException;
import cat.uvic.teknos.coursemanagement.clients.console.exceptions.RequestException;
import cat.uvic.teknos.coursemanagement.clients.console.utils.RestClient;
import cat.uvic.teknos.coursemanagement.clients.console.utils.RestClientImpl;
import cat.uvic.teknos.coursemanagement.clients.console.utils.Mappers;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class App {
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static final PrintStream out = new PrintStream(System.out);
    private static final RestClient restClient = new RestClientImpl("localhost", 8080);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        showBanner();

        var command = "";
        do {
            showMainMenu();
            command = readLine("Enter your choice: ");

            try {
                switch (command) {
                    case "1" -> manageStudents();
                    case "2" -> manageCourses();
                    case "3" -> manageGenres();
                    case "exit" -> out.println("Goodbye!");
                    default -> out.println("Invalid option. Please try again.");
                }
            } catch (ConsoleClientException e) {
                out.println("Error: " + e.getMessage());
            }

        } while (!command.equals("exit"));
    }

    private static void manageStudents() {
        var command = "";
        do {
            showStudentMenu();
            command = readLine("Enter your choice: ");

            try {
                switch (command) {
                    case "1" -> listStudents();
                    case "2" -> getStudent();
                    case "3" -> createStudent();
                    case "4" -> updateStudent();
                    case "5" -> deleteStudent();
                    case "back" -> {}
                    default -> out.println("Invalid option. Please try again.");
                }
            } catch (RequestException e) {
                out.println("Error: " + e.getMessage());
            }
        } while (!command.equals("back"));
    }

    private static void manageCourses() {
        var command = "";
        do {
            showCourseMenu();
            command = readLine("Enter your choice: ");

            try {
                switch (command) {
                    case "1" -> listCourses();
                    case "2" -> getCourse();
                    case "3" -> createCourse();
                    case "4" -> updateCourse();
                    case "5" -> deleteCourse();
                    case "back" -> {}
                    default -> out.println("Invalid option. Please try again.");
                }
            } catch (RequestException e) {
                out.println("Error: " + e.getMessage());
            }
        } while (!command.equals("back"));
    }

    private static void manageGenres() {
        var command = "";
        do {
            showGenreMenu();
            command = readLine("Enter your choice: ");

            try {
                switch (command) {
                    case "1" -> listGenres();
                    case "2" -> getGenre();
                    case "3" -> createGenre();
                    case "4" -> updateGenre();
                    case "5" -> deleteGenre();
                    case "back" -> {}
                    default -> out.println("Invalid option. Please try again.");
                }
            } catch (RequestException e) {
                out.println("Error: " + e.getMessage());
            }
        } while (!command.equals("back"));
    }

    // Implementation of student operations
    private static void listStudents() throws RequestException {
        StudentDTO[] students = restClient.getAll("/students", StudentDTO[].class, null);
        if (students.length == 0) {
            out.println("No students found.");
            return;
        }

        Arrays.stream(students).forEach(student ->
                out.printf("ID: %d, Name: %s %s, Born: %s, Genre: %s%n",
                        student.getId(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getBornOn(),
                        student.getGenre().getDescription()
                )
        );
    }

    private static void getStudent() throws RequestException {
        String id = readLine("Enter student ID: ");
        StudentDTO student = restClient.get("/students/" + id, StudentDTO.class, null);

        out.println("\nStudent Details:");
        out.printf("ID: %d%n", student.getId());
        out.printf("Name: %s %s%n", student.getFirstName(), student.getLastName());
        out.printf("Born: %s%n", student.getBornOn());
        out.printf("Genre: %s%n", student.getGenre().getDescription());
        out.println("\nCourses:");
        student.getCourses().forEach(course ->
                out.printf("- %s (Year: %d)%n", course.getName(), course.getYear())
        );
    }

    private static void createStudent() throws RequestException {
        try {
            StudentDTO student = new StudentDTO();
            student.setFirstName(readLine("Enter first name: "));
            student.setLastName(readLine("Enter last name: "));
            student.setBornOn(LocalDate.parse(readLine("Enter birth date (YYYY-MM-DD): "), dateFormatter));

            // Get genre ID and set it
            String genreId = readLine("Enter genre ID: ");
            GenreDTO genre = restClient.get("/genres/" + genreId, GenreDTO.class, null);
            student.setGenre(genre);

            String body = Mappers.get().writeValueAsString(student);
            restClient.post("/students", body, null);
            out.println("Student created successfully!");
        } catch (JsonProcessingException e) {
            throw new ConsoleClientException("Error creating student", e);
        }
    }

    private static void updateStudent() throws RequestException {
        try {
            String id = readLine("Enter student ID to update: ");
            StudentDTO student = restClient.get("/students/" + id, StudentDTO.class, null);

            out.println("Leave blank to keep current value");
            String firstName = readLine("Enter new first name [" + student.getFirstName() + "]: ");
            String lastName = readLine("Enter new last name [" + student.getLastName() + "]: ");
            String birthDate = readLine("Enter new birth date (YYYY-MM-DD) [" + student.getBornOn() + "]: ");
            String genreId = readLine("Enter new genre ID [" + student.getGenre().getId() + "]: ");

            if (!firstName.isEmpty()) student.setFirstName(firstName);
            if (!lastName.isEmpty()) student.setLastName(lastName);
            if (!birthDate.isEmpty()) student.setBornOn(LocalDate.parse(birthDate, dateFormatter));
            if (!genreId.isEmpty()) {
                GenreDTO genre = restClient.get("/genres/" + genreId, GenreDTO.class, null);
                student.setGenre(genre);
            }

            String body = Mappers.get().writeValueAsString(student);
            restClient.put("/students/" + id, body, null);
            out.println("Student updated successfully!");
        } catch (JsonProcessingException e) {
            throw new ConsoleClientException("Error updating student", e);
        }
    }

    private static void deleteStudent() throws RequestException {
        String id = readLine("Enter student ID to delete: ");
        restClient.delete("/students/" + id);
        out.println("Student deleted successfully!");
    }

    // Implementation of course operations
    private static void listCourses() throws RequestException {
        CourseDTO[] courses = restClient.getAll("/courses", CourseDTO[].class, null);
        if (courses.length == 0) {
            out.println("No courses found.");
            return;
        }

        Arrays.stream(courses).forEach(course ->
                out.printf("ID: %d, Name: %s, Year: %d%n",
                        course.getId(),
                        course.getName(),
                        course.getYear()
                )
        );
    }

    private static void getCourse() throws RequestException {
        String id = readLine("Enter course ID: ");
        CourseDTO course = restClient.get("/courses/" + id, CourseDTO.class, null);

        out.println("\nCourse Details:");
        out.printf("ID: %d%n", course.getId());
        out.printf("Name: %s%n", course.getName());
        out.printf("Year: %d%n", course.getYear());
        out.println("\nEnrolled Students:");
        course.getStudents().forEach(student ->
                out.printf("- %s %s%n", student.getFirstName(), student.getLastName())
        );
    }

    private static void createCourse() throws RequestException {
        try {
            CourseDTO course = new CourseDTO();
            course.setName(readLine("Enter course name: "));
            course.setYear(Integer.parseInt(readLine("Enter course year: ")));

            String body = Mappers.get().writeValueAsString(course);
            restClient.post("/courses", body, null);
            out.println("Course created successfully!");
        } catch (JsonProcessingException e) {
            throw new ConsoleClientException("Error creating course", e);
        } catch (NumberFormatException e) {
            throw new ConsoleClientException("Invalid year format", e);
        }
    }

    private static void updateCourse() throws RequestException {
        try {
            String id = readLine("Enter course ID to update: ");
            CourseDTO course = restClient.get("/courses/" + id, CourseDTO.class, null);

            out.println("Leave blank to keep current value");
            String name = readLine("Enter new name [" + course.getName() + "]: ");
            String year = readLine("Enter new year [" + course.getYear() + "]: ");

            if (!name.isEmpty()) course.setName(name);
            if (!year.isEmpty()) course.setYear(Integer.parseInt(year));

            String body = Mappers.get().writeValueAsString(course);
            restClient.put("/courses/" + id, body, null);
            out.println("Course updated successfully!");
        } catch (JsonProcessingException e) {
            throw new ConsoleClientException("Error updating course", e);
        } catch (NumberFormatException e) {
            throw new ConsoleClientException("Invalid year format", e);
        }
    }

    private static void deleteCourse() throws RequestException {
        String id = readLine("Enter course ID to delete: ");
        restClient.delete("/courses/" + id);
        out.println("Course deleted successfully!");
    }

    private static void listGenres() throws RequestException {
        GenreDTO[] genres = restClient.getAll("/genres", GenreDTO[].class, null);
        if (genres.length == 0) {
            out.println("No genres found.");
            return;
        }

        Arrays.stream(genres).forEach(genre ->
                out.printf("ID: %d, Description: %s%n",
                        genre.getId(),
                        genre.getDescription()
                )
        );
    }

    private static void getGenre() throws RequestException {
        String id = readLine("Enter genre ID: ");
        GenreDTO genre = restClient.get("/genres/" + id, GenreDTO.class, null);

        out.println("\nGenre Details:");
        out.printf("ID: %d%n", genre.getId());
        out.printf("Description: %s%n", genre.getDescription());
    }

    private static void createGenre() throws RequestException {
        try {
            GenreDTO genre = new GenreDTO();
            genre.setDescription(readLine("Enter genre description: "));

            String body = Mappers.get().writeValueAsString(genre);
            restClient.post("/genres", body, null);
            out.println("Genre created successfully!");
        } catch (JsonProcessingException e) {
            throw new ConsoleClientException("Error creating genre", e);
        }
    }

    private static void updateGenre() throws RequestException {
        try {
            String id = readLine("Enter genre ID to update: ");
            GenreDTO genre = restClient.get("/genres/" + id, GenreDTO.class, null);

            out.println("Leave blank to keep current value");
            String description = readLine("Enter new description [" + genre.getDescription() + "]: ");

            if (!description.isEmpty()) genre.setDescription(description);

            String body = Mappers.get().writeValueAsString(genre);
            restClient.put("/genres/" + id, body, null);
            out.println("Genre updated successfully!");
        } catch (JsonProcessingException e) {
            throw new ConsoleClientException("Error updating genre", e);
        }
    }

    private static void deleteGenre() throws RequestException {
        String id = readLine("Enter genre ID to delete: ");
        restClient.delete("/genres/" + id);
        out.println("Genre deleted successfully!");
    }

    // Menu display methods
    private static void showMainMenu() {
        out.println("\nMain Menu:");
        out.println("1. Manage Students");
        out.println("2. Manage Courses");
        out.println("3. Manage Genres");
        out.println("Type 'exit' to quit");
        out.println();
    }

    private static void showStudentMenu() {
        out.println("\nStudent Management:");
        out.println("1. List all students");
        out.println("2. Get student details");
        out.println("3. Create new student");
        out.println("4. Update student");
        out.println("5. Delete student");
        out.println("Type 'back' to return to main menu");
        out.println();
    }

    private static void showCourseMenu() {
        out.println("\nCourse Management:");
        out.println("1. List all courses");
        out.println("2. Get course details");
        out.println("3. Create new course");
        out.println("4. Update course");
        out.println("5. Delete course");
        out.println("Type 'back' to return to main menu");
        out.println();
    }

    private static void showGenreMenu() {
        out.println("\nGenre Management:");
        out.println("1. List all genres");
        out.println("2. Get genre details");
        out.println("3. Create new genre");
        out.println("4. Update genre");
        out.println("5. Delete genre");
        out.println("Type 'back' to return to main menu");
        out.println();
    }

    private static void showBanner() {
        out.println("===============================");
        out.println("Course Management System");
        out.println("===============================");
    }

    private static String readLine(String prompt) {
        out.print(prompt);
        try {
            return in.readLine().trim();
        } catch (IOException e) {
            throw new ConsoleClientException("Error reading input", e);
        }
    }
    private static void clearScreen() {
        out.print("\033[H\033[2J");
        out.flush();
    }
}