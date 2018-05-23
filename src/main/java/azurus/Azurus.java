package azurus;

import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.concurrent.Executors.newFixedThreadPool;

public final class Azurus {

    public static void main(final String... args) throws Exception {
        System.out.println("Hello, World!");
        final String masterKey = lookupMasterKey(); // TODO... this ought to happen on demand, to pick up changed secrets.
        final RequestStorage requestStorage = new RequestStorageService("https://request-storage-service/", masterKey).start();

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/", httpExchange -> {
            final URI requestURI = httpExchange.getRequestURI();
            final String protocol = httpExchange.getProtocol();
            final String requestMethod = httpExchange.getRequestMethod();
            final String clientAddress = httpExchange.getRemoteAddress().getAddress().getHostAddress();
            httpExchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
            httpExchange.sendResponseHeaders(200, 0);
            try (final OutputStream outputStream = httpExchange.getResponseBody();
                 final Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                writer.write("Hello, World!");
                requestStorage.store(requestURI, protocol, requestMethod, clientAddress); // TODO hostname?
            }
        });
        httpServer.setExecutor(newFixedThreadPool(10));
        httpServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            httpServer.stop(10);
            requestStorage.close();
        }));
    }

    private static String lookupMasterKey() throws Exception {
        try (
                final InputStream inputStream = Files.newInputStream(Paths.get("etc", "cosmos-credentials", "master-key.txt"));
                final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return IOUtils.toString(inputStreamReader);
        }
    }

}
