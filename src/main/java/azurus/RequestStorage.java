package azurus;

import com.microsoft.azure.documentdb.DocumentClientException;

public interface RequestStorage extends AutoCloseable {
    void storeSomething() throws DocumentClientException;
}
