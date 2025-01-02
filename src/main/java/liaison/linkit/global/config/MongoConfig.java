package liaison.linkit.global.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = {
        "liaison.linkit.chat.domain.repository",
        "liaison.linkit.notification.domain.repository"
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
        String uri = String.format("mongodb+srv://%s:%s@%s/%s%s",
                username, password, host, database, uriOptions);
        logConnectionDetails(uri);
        return MongoClients.create(uri);
    }

    private void logConnectionDetails(String uri) {
        System.out.println("Connecting to MongoDB with URI: " + uri);
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Object> converters = new ArrayList<>();
        converters.add(new LocalDateTimeToStringConverter());
        converters.add(new StringToLocalDateTimeConverter());
        return new MongoCustomConversions(converters);
    }

    static class LocalDateTimeToStringConverter implements org.springframework.core.convert.converter.Converter<LocalDateTime, String> {
        @Override
        public String convert(LocalDateTime source) {
            return source.format(DateTimeFormatter.ISO_DATE_TIME); // ISO 8601 형식으로 변환
        }
    }

    static class StringToLocalDateTimeConverter implements org.springframework.core.convert.converter.Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(String source) {
            return LocalDateTime.parse(source, DateTimeFormatter.ISO_DATE_TIME);
        }
    }
}
