package liaison.linkit.global.config;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@RequiredArgsConstructor
@EnableMongoRepositories(
        basePackages = {
            "liaison.linkit.chat.domain.repository",
            "liaison.linkit.notification.domain.repository",
            "liaison.linkit.team.domain.repository.announcement"
        })
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private String port;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.password}")
    private String password;

    @Value("${spring.data.mongodb.uri-options:}")
    private String uriOptions;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        String uri =
                String.format(
                        "mongodb+srv://%s:%s@%s/%s%s",
                        username, password, host, database, uriOptions);
        logConnectionDetails(uri);
        return MongoClients.create(uri);
    }

    private void logConnectionDetails(String uri) {
        System.out.println("Connecting to MongoDB with URI: " + uri);
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        return new MongoCustomConversions(converters);
    }
}
