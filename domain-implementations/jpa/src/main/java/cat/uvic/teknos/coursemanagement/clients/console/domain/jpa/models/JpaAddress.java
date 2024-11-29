package cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models;

import cat.uvic.teknos.coursemanagement.clients.console.models.Address;
import jakarta.persistence.*;

@Entity
@Table(name = "ADDRESS")
public class JpaAddress implements Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "ZIP")
    private String zip;

    @Column(name = "STREET")
    private String street;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getZip() {
        return zip;
    }

    @Override
    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public String getStreet() {
        return street;
    }

    @Override
    public void setStreet(String street) {
        this.street = street;
    }
}