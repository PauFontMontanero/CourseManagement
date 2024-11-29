package cat.uvic.teknos.coursemanagement.clients.console.services;

import cat.uvic.teknos.coursemanagement.clients.console.services.exception.ServerException;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    public final int PORT = 8080;
    private final RequestRouter requestRouter;
    private volatile boolean SHUTDOWN_SERVER;
    private final ExecutorService executorService;
    private ServerSocket serverSocket;
    private static final String PROPERTIES_FILE = "/server.properties";
    private final Thread shutdownMonitor;

    public Server(RequestRouter requestRouter) {
        this.requestRouter = requestRouter;
        this.executorService = Executors.newFixedThreadPool(2); // Adjust pool size as needed
        this.shutdownMonitor = new Thread(this::monitorShutdown);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            shutdownMonitor.start();
            System.out.println("Server started on port " + PORT);

            while (!SHUTDOWN_SERVER) {
                try {
                    var clientSocket = serverSocket.accept();
                    executorService.submit(() -> {
                        System.out.println("Handling request on thread: " + Thread.currentThread().getName());
                        try {
                            var rawHttp = new RawHttp(RawHttpOptions.newBuilder()
                                    .doNotInsertHostHeaderIfMissing()
                                    .build());
                            var request = rawHttp.parseRequest(clientSocket.getInputStream());
                            System.out.println("Processing request on thread: " + Thread.currentThread().getName());
                            var response = requestRouter.execRequest(request);
                            response.writeTo(clientSocket.getOutputStream());
                            System.out.println("Completed request on thread: " + Thread.currentThread().getName());
                        } catch (IOException e) {
                            System.err.println("Error processing request: " + e.getMessage());
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                System.err.println("Error closing client socket: " + e.getMessage());
                            }
                        }
                    });
                } catch (IOException e) {
                    if (!SHUTDOWN_SERVER) {
                        System.err.println("Error accepting connection: " + e.getMessage());
                    }
                }
            }
            System.out.println("Exited server loop");
        } catch (IOException e) {
            throw new ServerException(e);
        } finally {
            shutdown();
        }
    }

    private void monitorShutdown() {
        while (!SHUTDOWN_SERVER) {
            try {
                Thread.sleep(5000); // Check every 5 seconds
                Properties properties = new Properties();

                // Load from resources folder
                try (InputStream is = getClass().getResourceAsStream(PROPERTIES_FILE)) {
                    if (is != null) {
                        properties.load(is);
                        if (Boolean.parseBoolean(properties.getProperty("shutdown", "false"))) {
                            System.out.println("Shutdown signal received from properties file");
                            SHUTDOWN_SERVER = true;
                        }
                    } else {
                        System.err.println("server.properties not found in resources");
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading properties file: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void shutdown() {
        System.out.println("Initiating server shutdown...");

        // Close the server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }

        // Shutdown executor service gracefully
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        // Interrupt shutdown monitor thread
        shutdownMonitor.interrupt();

        System.out.println("Server shutdown complete");
    }
}