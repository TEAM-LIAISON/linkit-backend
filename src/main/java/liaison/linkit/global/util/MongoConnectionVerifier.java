package liaison.linkit.global.util;

import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoConnectionVerifier {

    private final MongoClient mongoClient;

    public void verifyConnection(String databaseName) {
        try {
            boolean dbExists =
                    mongoClient
                            .getDatabase(databaseName)
                            .listCollectionNames()
                            .iterator()
                            .hasNext();
            if (dbExists) {
                System.out.println("Successfully connected to the database: " + databaseName);
            } else {
                System.out.println("Database exists, but no collections found in: " + databaseName);
            }
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
        }
    }
}
