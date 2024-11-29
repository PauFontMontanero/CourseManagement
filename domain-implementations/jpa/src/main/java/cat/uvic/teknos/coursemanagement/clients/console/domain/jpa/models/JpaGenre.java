package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models;

import cat.uvic.teknos.coursemanagement.clients.console.models.Genre;
import jakarta.persistence.*;

@Entity
@Table(name = "GENRE")
public class JpaGenre implements Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}