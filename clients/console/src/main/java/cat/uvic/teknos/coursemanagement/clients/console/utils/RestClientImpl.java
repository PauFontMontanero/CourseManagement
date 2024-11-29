package cat.uvic.teknos.coursemanagement.clients.console.utils;

import cat.uvic.teknos.coursemanagement.clients.console.exceptions.RequestException;
import cat.uvic.teknos.coursemanagement.cryptoutils.CryptoUtils;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpResponse;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestClientImpl implements RestClient {
    private final String host;
    private final int port;
    private final RawHttp http;
    private final ObjectMapper mapper;
    private final Certificate serverCertificate;

    public RestClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
        this.http = new RawHttp();
        this.mapper = Mappers.get();

        try {
            // Load server certificate from keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream("clients/console/src/main/resources/client1.p12"), "Teknos01.".toCharArray());
            this.serverCertificate = keyStore.getCertificate("server");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load server certificate", e);
        }
    }

    @Override
    public <T> T get(String path, Class<T> responseType, Function<byte[], byte[]> responseTransformer) throws RequestException {
        try {
            SecretKey symmetricKey = CryptoUtils.createSecretKey();
            HeaderEntry[] encryptionHeaders = prepareEncryptedRequest(null, symmetricKey);

            var request = http.parseRequest(
                    "GET http://" + host + ":" + port + path + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            getHeadersString(encryptionHeaders) +
                            "\r\n"
            );

            try (var socket = new Socket(host, port)) {
                request.writeTo(socket.getOutputStream());
                var response = http.parseResponse(socket.getInputStream());
                String responseBody = getBody(response, bytes -> {
                    try {
                        return CryptoUtils.decrypt(new String(bytes), symmetricKey).getBytes();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                return mapper.readValue(responseBody, responseType);
            }
        } catch (IOException e) {
            throw new RequestException("Error during GET request", e);
        }
    }

    @Override
    public <T> T[] getAll(String path, Class<T[]> responseType, Function<byte[], byte[]> responseTransformer) throws RequestException {
        try {
            SecretKey symmetricKey = CryptoUtils.createSecretKey();
            HeaderEntry[] encryptionHeaders = prepareEncryptedRequest(null, symmetricKey);

            var request = http.parseRequest(
                    "GET http://" + host + ":" + port + path + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            getHeadersString(encryptionHeaders) +
                            "\r\n"
            );

            try (var socket = new Socket(host, port)) {
                request.writeTo(socket.getOutputStream());
                var response = http.parseResponse(socket.getInputStream());
                String responseBody = getBody(response, bytes -> {
                    try {
                        return CryptoUtils.decrypt(new String(bytes), symmetricKey).getBytes();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                return mapper.readValue(responseBody.getBytes(), responseType);
            }
        } catch (IOException e) {
            throw new RequestException("Error during GET ALL request", e);
        }
    }

    @Override
    public <T> T post(String path, String body, Function<byte[], byte[]> responseTransformer, HeaderEntry... headers) throws RequestException {
        try {
            SecretKey symmetricKey = CryptoUtils.createSecretKey();
            String encryptedBody = CryptoUtils.encrypt(body, symmetricKey);
            HeaderEntry[] encryptionHeaders = prepareEncryptedRequest(encryptedBody, symmetricKey);
            HeaderEntry[] allHeaders = combineHeaders(headers, encryptionHeaders);

            StringBuilder requestBuilder = new StringBuilder();
            requestBuilder.append("POST http://").append(host).append(":").append(port).append(path)
                    .append(" HTTP/1.1\r\n")
                    .append("Host: ").append(host).append("\r\n")
                    .append("Content-Type: application/json\r\n")
                    .append("Content-Length: ").append(encryptedBody.length()).append("\r\n")
                    .append(getHeadersString(allHeaders))
                    .append("\r\n")
                    .append(encryptedBody);

            var request = http.parseRequest(requestBuilder.toString());

            try (var socket = new Socket(host, port)) {
                request.writeTo(socket.getOutputStream());
                var response = http.parseResponse(socket.getInputStream());
                String responseBody = getBody(response, bytes -> {
                    try {
                        return CryptoUtils.decrypt(new String(bytes), symmetricKey).getBytes();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                return mapper.readValue(responseBody, (Class<T>) Object.class);
            }
        } catch (IOException e) {
            throw new RequestException("Error during POST request", e);
        }
    }

    @Override
    public <T> T put(String path, String body, Function<byte[], byte[]> responseTransformer, HeaderEntry... headers) throws RequestException {
        try {
            SecretKey symmetricKey = CryptoUtils.createSecretKey();
            String encryptedBody = CryptoUtils.encrypt(body, symmetricKey);
            HeaderEntry[] encryptionHeaders = prepareEncryptedRequest(encryptedBody, symmetricKey);
            HeaderEntry[] allHeaders = combineHeaders(headers, encryptionHeaders);

            StringBuilder requestBuilder = new StringBuilder();
            requestBuilder.append("PUT http://").append(host).append(":").append(port).append(path)
                    .append(" HTTP/1.1\r\n")
                    .append("Host: ").append(host).append("\r\n")
                    .append("Content-Type: application/json\r\n")
                    .append("Content-Length: ").append(encryptedBody.length()).append("\r\n")
                    .append(getHeadersString(allHeaders))
                    .append("\r\n")
                    .append(encryptedBody);

            var request = http.parseRequest(requestBuilder.toString());

            try (var socket = new Socket(host, port)) {
                request.writeTo(socket.getOutputStream());
                var response = http.parseResponse(socket.getInputStream());
                String responseBody = getBody(response, bytes -> {
                    try {
                        return CryptoUtils.decrypt(new String(bytes), symmetricKey).getBytes();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                return mapper.readValue(responseBody, (Class<T>) Object.class);
            }
        } catch (IOException e) {
            throw new RequestException("Error during PUT request", e);
        }
    }

    @Override
    public void delete(String path) throws RequestException {
        try {
            SecretKey symmetricKey = CryptoUtils.createSecretKey();
            HeaderEntry[] encryptionHeaders = prepareEncryptedRequest(null, symmetricKey);

            var request = http.parseRequest(
                    "DELETE http://" + host + ":" + port + path + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            getHeadersString(encryptionHeaders) +
                            "\r\n"
            );

            try (var socket = new Socket(host, port)) {
                request.writeTo(socket.getOutputStream());
                var response = http.parseResponse(socket.getInputStream());
                // We still need to read the body to ensure proper handling
                getBody(response, null);
            }
        } catch (IOException e) {
            throw new RequestException("Error during DELETE request", e);
        }
    }

    private HeaderEntry[] prepareEncryptedRequest(String body, SecretKey symmetricKey) throws RequestException {
        try {
            List<HeaderEntry> headers = new ArrayList<>();

            // Encrypt symmetric key with server's public key
            String encryptedKey = CryptoUtils.asymmetricEncrypt(
                    CryptoUtils.toBase64(symmetricKey.getEncoded()),
                    serverCertificate.getPublicKey()
            );
            headers.add(new HeaderEntry("X-Encrypted-Key", encryptedKey));

            // Add hash if body exists
            if (body != null) {
                String bodyHash = CryptoUtils.getHash(body);
                headers.add(new HeaderEntry("X-Body-Hash", bodyHash));
            }

            return headers.toArray(new HeaderEntry[0]);
        } catch (Exception e) {
            throw new RequestException("Failed to prepare encrypted request", e);
        }
    }

    private void verifyResponseHash(RawHttpResponse<?> response) throws RequestException {
        String expectedHash = response.getHeaders().getFirst("X-Response-Hash").orElse(null);
        if (expectedHash != null) {
            try {
                byte[] bodyBytes = getBody(response, null).getBytes(StandardCharsets.UTF_8);
                String actualHash = CryptoUtils.getHash(new String(bodyBytes, StandardCharsets.UTF_8));
                if (!expectedHash.equals(actualHash)) {
                    throw new RequestException("Invalid response hash");
                }
            } catch (IOException e) {
                throw new RequestException("Error verifying response hash", e);
            }
        }
    }

    private String getBody(RawHttpResponse<?> response, Function<byte[], byte[]> transformer) throws IOException {
        byte[] bodyBytes = response.getBody()
                .map(b -> {
                    try {
                        return b.asRawBytes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(new byte[0]);

        if (transformer != null) {
            bodyBytes = transformer.apply(bodyBytes);
        }

        return new String(bodyBytes, StandardCharsets.UTF_8);
    }

    private String getHeadersString(HeaderEntry[] headers) {
        StringBuilder sb = new StringBuilder();
        for (HeaderEntry header : headers) {
            if (header != null && header.getName() != null && header.getValue() != null) {
                sb.append(header.getName()).append(": ").append(header.getValue()).append("\r\n");
            }
        }
        return sb.toString();
    }

    private HeaderEntry[] combineHeaders(HeaderEntry[] original, HeaderEntry[] additional) {
        List<HeaderEntry> combined = new ArrayList<>();
        if (original != null) {
            combined.addAll(Arrays.asList(original));
        }
        if (additional != null) {
            combined.addAll(Arrays.asList(additional));
        }
        return combined.toArray(new HeaderEntry[0]);
    }
}