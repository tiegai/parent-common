package com.nike.ncp.common.configuration;

import com.mongodb.MongoClientSettings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoPropertiesClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MongoConfiguration {

    private final MongoProperties mongoProperties;

    public MongoConfiguration(MongoProperties mongoProperties) {
        super();
        this.mongoProperties = mongoProperties;
    }

    @Bean
    @ConditionalOnProperty(value = "db.ssl.enabled", havingValue = "false", matchIfMissing = true)
    public MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder().build();
    }

    @Bean
    @ConditionalOnProperty(value = "db.ssl.enabled", havingValue = "true")
    public MongoClientSettings sslMongoClientSettings() {
        return MongoClientSettings
                .builder()
                .applyToSslSettings(builder -> builder.enabled(true).invalidHostNameAllowed(true))
                .build();
    }


    @Bean
    public MongoPropertiesClientSettingsBuilderCustomizer mongoPropertiesCustomizer(MongoProperties properties, Environment environment) {
        return new MongoPropertiesClientSettingsBuilderCustomizer(properties, environment);
    }

}
