package com.nike.ncp.common.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Getter
@Configuration
public class MongoConfiguration {

    @Value("${mongo.connection.string}")
    private String mongoConnectString;
    @Value("${mongo.db.name}")
    private String mongoDbName;

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getMongoDbName());
    }

    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(getMongoConnectString());
        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build());
    }
}
