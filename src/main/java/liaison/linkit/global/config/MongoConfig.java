package liaison.linkit.global.config;

import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
@RequiredArgsConstructor
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.username}")
    private String userName;

    @Value("${spring.data.mongodb.password}")
    private String password;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Override
    protected String getDatabaseName() {
        return database;
    }
    
}
