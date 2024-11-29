package cat.uvic.teknos.coursemanagement.clients.console.services.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.module.SimpleModule;
import cat.uvic.teknos.coursemanagement.clients.console.models.Genre;
import cat.uvic.teknos.coursemanagement.clients.console.domain.jpa.models.JpaGenre;

public class Mappers {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(Genre.class, JpaGenre.class);
        mapper.registerModule(module);
    }

    public static ObjectMapper get() {
        return mapper;
    }
}