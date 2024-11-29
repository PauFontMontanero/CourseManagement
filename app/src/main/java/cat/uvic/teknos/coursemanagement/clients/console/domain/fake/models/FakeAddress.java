package cat.uvic.teknos.coursemanagement.clients.console.domain.fake.models;

import cat.uvic.teknos.coursemanagement.clients.console.models.Address;

public class FakeAddress implements Address {
    private int id;
    private String zip;
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
