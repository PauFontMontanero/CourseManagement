package cat.uvic.teknos.coursemanagement.clients.console.services.controllers;

public interface Controller {
    String get(int id);
    String get();
    void post(String json);
    void put(int id, String json);
    void delete(int id);
}
