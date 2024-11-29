package cat.uvic.teknos.coursemanagement.clients.console.services;

import cat.uvic.teknos.coursemanagement.clients.console.services.controllers.Controller;
import cat.uvic.teknos.coursemanagement.clients.console.services.exception.ResourceNotFoundException;
import cat.uvic.teknos.coursemanagement.cryptoutils.CryptoUtils;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Map;

public class RequestRouterImpl implements RequestRouter {
    private static final RawHttp rawHttp = new RawHttp();
    private final Map<String, Controller> controllers;
    private final PrivateKey serverPrivateKey;

    public RequestRouterImpl(Map<String, Controller> controllers) {
        this.controllers = controllers;
        try {
            // Load server's private key from keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream("services/src/main/resources/server.p12"), "Teknos01.".toCharArray());
            this.serverPrivateKey = (PrivateKey) keyStore.getKey("server", "Teknos01.".toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load server private key", e);
        }
    }

    @Override
    public RawHttpResponse<?> execRequest(RawHttpRequest request) {
        var path = request.getUri().getPath();
        var method = request.getMethod();
        var pathParts = path.split("/");

        if (pathParts.length < 2) {
            throw new ResourceNotFoundException("Invalid request path");
        }

        var controllerName = pathParts[1];
        String responseJsonBody;

        try {
            // Get encrypted symmetric key from headers
            String encryptedKey = request.getHeaders().getFirst("X-Encrypted-Key")
                    .orElseThrow(() -> new SecurityException("Missing encrypted key"));

            // Decrypt symmetric key using server's private key
            String decryptedKeyBase64 = CryptoUtils.asymmetricDecrypt(encryptedKey, serverPrivateKey);
            SecretKey symmetricKey = CryptoUtils.decodeSecretKey(decryptedKeyBase64);

            // If request has a body, decrypt it
            String decryptedBody = null;
            if (request.getBody().isPresent() && (method.equals("POST") || method.equals("PUT"))) {
                String encryptedBody = request.getBody().get().asRawString(StandardCharsets.UTF_8);

                // Verify body hash
                String expectedHash = request.getHeaders().getFirst("X-Body-Hash")
                        .orElseThrow(() -> new SecurityException("Missing body hash"));
                String actualHash = CryptoUtils.getHash(encryptedBody);
                if (!expectedHash.equals(actualHash)) {
                    throw new SecurityException("Invalid body hash");
                }

                // Decrypt body
                decryptedBody = CryptoUtils.decrypt(encryptedBody, symmetricKey);
            }

            // Process request with decrypted body
            switch (controllerName) {
                case "students":
                    responseJsonBody = manageEndpoint(request, method, pathParts, "students", decryptedBody);
                    break;
                case "courses":
                    responseJsonBody = manageEndpoint(request, method, pathParts, "courses", decryptedBody);
                    break;
                case "genres":
                    responseJsonBody = manageEndpoint(request, method, pathParts, "genres", decryptedBody);
                    break;
                default:
                    throw new ResourceNotFoundException("Controller not found: " + controllerName);
            }

            // Encrypt response
            String encryptedResponse = CryptoUtils.encrypt(responseJsonBody, symmetricKey);
            String responseHash = CryptoUtils.getHash(encryptedResponse);

            return rawHttp.parseResponse(
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: application/json\r\n" +
                            "X-Response-Hash: " + responseHash + "\r\n" +
                            "Content-Length: " + encryptedResponse.length() + "\r\n" +
                            "\r\n" +
                            encryptedResponse
            );

        } catch (SecurityException e) {
            return rawHttp.parseResponse(
                    "HTTP/1.1 400 Bad Request\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + e.getMessage().length() + "\r\n" +
                            "\r\n" +
                            e.getMessage()
            );
        } catch (ResourceNotFoundException e) {
            return rawHttp.parseResponse(
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + e.getMessage().length() + "\r\n" +
                            "\r\n" +
                            e.getMessage()
            );
        } catch (Exception e) {
            return rawHttp.parseResponse(
                    "HTTP/1.1 500 Internal Server Error\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + e.getMessage().length() + "\r\n" +
                            "\r\n" +
                            e.getMessage()
            );
        }
    }

    private String manageEndpoint(RawHttpRequest request, String method, String[] pathParts, String controllerName, String decryptedBody) throws IOException {
        var controller = controllers.get(controllerName);
        String responseJsonBody;

        switch (method) {
            case "GET":
                if (pathParts.length > 2) {
                    responseJsonBody = controller.get(Integer.parseInt(pathParts[2]));
                } else {
                    responseJsonBody = controller.get();
                }
                break;

            case "POST":
                System.out.println("POST body: " + decryptedBody);
                controller.post(decryptedBody);
                responseJsonBody = "{}";
                break;

            case "PUT":
                System.out.println("PUT body: " + decryptedBody);
                controller.put(Integer.parseInt(pathParts[2]), decryptedBody);
                responseJsonBody = "{}";
                break;

            case "DELETE":
                if (pathParts.length != 3) {
                    throw new ResourceNotFoundException("ID required for DELETE request");
                }
                controller.delete(Integer.parseInt(pathParts[2]));
                responseJsonBody = "{}";
                break;

            default:
                throw new ResourceNotFoundException("Method not supported: " + method);
        }

        return responseJsonBody;
    }
}