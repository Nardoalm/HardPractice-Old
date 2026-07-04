package live.pvphard.practice.core.database;

import com.mongodb.client.MongoDatabase;
import live.pvphard.practice.bootstrap.lifecycle.Service;

/**
 * @author Remi
 * @project hard-practice
 * @date 2/07/2025
 */
public interface MongoService extends Service {
    /**
     * Gets the active MongoDatabase instance for other services to use.
     *
     * @return The MongoDatabase instance.
     * @throws IllegalStateException if the service has not been initialized yet.
     */
    MongoDatabase getMongoDatabase();
}