package cat.uvic.teknos.coursemanagement.clients.console.models;

public interface ModelFactory {
    Address createAddress();
    Course createCourse();
    Student courseStudent();
    Genre createGenre();
}
