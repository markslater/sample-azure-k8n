package azurus;

import java.net.URI;

public interface RequestStorage extends QuietCloseable {
    void store(URI requestURI, String protocol, String requestMethod, String clientAddress);
}
