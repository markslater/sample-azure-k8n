package azurus;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static java.util.concurrent.Executors.newFixedThreadPool;

public final class Azurus {

    public static void main(final String... args) throws IOException {
        System.out.println("Hello, World!");
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
