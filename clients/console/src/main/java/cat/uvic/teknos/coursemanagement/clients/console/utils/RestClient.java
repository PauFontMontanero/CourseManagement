package cat.uvic.teknos.coursemanagement.clients.console.utils;

import cat.uvic.teknos.coursemanagement.clients.console.exceptions.RequestException;
import java.util.function.Function;

public interface RestClient {
    <T> T get(String path, Class<T> responseType, Function<byte[], byte[]> responseTransformer) throws RequestException;
    <T> T[] getAll(String path, Class<T[]> responseType, Function<byte[], byte[]> responseTransformer) throws RequestException;
    <T> T post(String path, String body, Function<byte[], byte[]> responseTransformer, HeaderEntry... headers) throws RequestException;
    <T> T put(String path, String body, Function<byte[], byte[]> responseTransformer, HeaderEntry... headers) throws RequestException;
    void delete(String path) throws RequestException;

    class HeaderEntry {
        private final String name;
        private final String value;

        public HeaderEntry(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}