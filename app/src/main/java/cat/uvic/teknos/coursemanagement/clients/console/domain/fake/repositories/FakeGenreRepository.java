package cat.uvic.teknos.coursemanagement.clients.console.domain.fake.repositories;

import cat.uvic.teknos.coursemanagement.clients.console.domain.fake.models.FakeGenre;
import cat.uvic.teknos.coursemanagement.clients.console.models.Genre;
import cat.uvic.teknos.coursemanagement.clients.console.repositories.GenreRepository;

import java.util.HashSet;
import java.util.Set;


public class FakeGenreRepository implements GenreRepository {

    @Override
    public void save(Genre model) {
        if (model.getId() <= 0) {
            model.setId(1);
        }
    }



    @Override
    public void delete(Genre model) {

    }

    @Override
    public Genre get(Integer id) {
        return new FakeGenre();
    }

    @Override
    public Set<Genre> getAll() {
        var genres = new HashSet<Genre>();

        var male = new FakeGenre();
        male .setId(1);
        male.setDescription("Male");
        genres.add(male);

        var female = new FakeGenre();
        female .setId(2);
        female.setDescription("Female");
        genres.add(male);

        return genres;
    }
}
