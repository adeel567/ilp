package uk.ac.ed.inf;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Class for methods for communicating with a server.
 */
public class ServerIO {

    private static final int TIMEOUT_DURATION_IN_SECS = 10;
    private static final int HTTP_SUCCESS = 200;
    private static final HttpClient client = HttpClient.newHttpClient();

    /**
     * Performs a GET request on the server and returns the contents.
     * @param urlString full URL to connect to.
     * @return String of the body of the request.
     */
    public static String getRequest(String urlString) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .timeout(Duration.of(TIMEOUT_DURATION_IN_SECS,SECONDS)) //timeout in case server overloaded
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HTTP_SUCCESS) {
                System.err.printf("Fatal error due to error code: %s%n",
                        response.statusCode());
                System.exit(1);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Fatal error: could not connect to server.");
            System.exit(1);
        }
        return response.body();
    }

}
