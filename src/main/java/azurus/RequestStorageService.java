package azurus;

import argo.format.CompactJsonFormatter;
import argo.format.JsonFormatter;
import com.microsoft.azure.documentdb.*;
import net.sourceforge.sorb.Service;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

import static argo.jdom.JsonNodeFactories.*;

final class RequestStorageService implements Service<RequestStorage> {
    private static final String DATABASE_ID = "azurus";
    private static final String COLLECTION_ID = "requests";
    private static final JsonFormatter JSON_FORMATTER = new CompactJsonFormatter();
    private final String host;
    private final String masterKey;

    RequestStorageService(String host, String masterKey) {
        this.host = host;
        this.masterKey = masterKey;
    }

    private static Database getTodoDatabase(final DocumentClient documentClient) throws DocumentClientException {
        final List<Database> databaseList = documentClient.queryDatabases("SELECT * FROM root r WHERE r.id='" + DATABASE_ID + "'", null).getQueryIterable().toList();

        if (databaseList.isEmpty()) {
            final Database databaseDefinition = new Database();
            databaseDefinition.setId(DATABASE_ID);
            return documentClient.createDatabase(databaseDefinition, null).getResource();
        } else if (databaseList.size() == 1) {
            return databaseList.get(0);
        } else {
            throw new RuntimeException("Who know's what's going on here?  The crappy thing has " + databaseList.size() + " databases with the \"unique\" id " + DATABASE_ID);
        }
    }

    private static DocumentCollection getTodoCollection(final DocumentClient documentClient) throws DocumentClientException {
        final List<DocumentCollection> collectionList = documentClient.queryCollections(
                getTodoDatabase(documentClient).getSelfLink(),
                "SELECT * FROM root r WHERE r.id='" + COLLECTION_ID + "'",
                null
        ).getQueryIterable().toList();

        if (collectionList.isEmpty()) {
            final DocumentCollection collectionDefinition = new DocumentCollection();
            collectionDefinition.setId(COLLECTION_ID);
            return documentClient.createCollection(getTodoDatabase(documentClient).getSelfLink(), collectionDefinition, null).getResource();
        } else if (collectionList.size() == 1) {
            return collectionList.get(0);
        } else {
            throw new RuntimeException("Who know's what's going on here?  The crappy thing has " + collectionList.size() + " collections with the \"unique\" id " + COLLECTION_ID);
        }
    }

    @Override
    public RequestStorage start() {
        final DocumentClient documentClient = new DocumentClient(host, masterKey, ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
        try {
            final DocumentCollection todoCollection = getTodoCollection(documentClient);
            return new RequestStorage() {
                @Override
                public void storeSomething() throws DocumentClientException {
                    final Document document = new Document(JSON_FORMATTER.format(object(
                            field(RandomStringUtils.random(10), string(RandomStringUtils.random(10))),
                            field(RandomStringUtils.random(10), string(RandomStringUtils.random(10))),
                            field("_ts", string(RandomStringUtils.random(10))) // TODO Cosmos will silently overwrite this.  Thanks, Microsoft!
                    )));
                    documentClient.createDocument(todoCollection.getSelfLink(), document, null, false);
                }

                @Override
                public void close() throws Exception {
                    documentClient.close();
                }
            };
        } catch (DocumentClientException e) {
            documentClient.close();
            throw new RuntimeException("Couldn't start", e);
        }
    }
}
