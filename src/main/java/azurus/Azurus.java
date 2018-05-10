package azurus;

import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.concurrent.Executors.newFixedThreadPool;

public final class Azurus {

    public static void main(final String... args) throws Exception {
        System.out.println("Hello, World!");
        try (
                final InputStream inputStream = Files.newInputStream(Paths.get("etc", "cosmos-credentials", "master-key.txt"));
                final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            final String masterKey = org.apache.commons.io.IOUtils.toString(inputStreamReader);
            System.out.println("Got a master key that was " + masterKey.length() + " characters long.");
            try (final RequestStorage requestStorage = new RequestStorageService("https://azurus.documents.azure.com:443/", masterKey).start()) {
                requestStorage.storeSomething();
                System.out.println("Stored something...");
            }
        }

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/", httpExchange -> {
            httpExchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
            httpExchange.sendResponseHeaders(200, 0);
            try (final OutputStream outputStream = httpExchange.getResponseBody();
                 final Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                writer.write("Hello, World!");
            }
        });
        httpServer.setExecutor(newFixedThreadPool(10));
        httpServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> httpServer.stop(10)));
    }

}
