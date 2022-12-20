package com.nike.ncp.common.configuration;

import com.mongodb.MongoClientSettings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoPropertiesClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile("!local")
public class MongoConfiguration {

	private final MongoProperties mongoProperties;

	public MongoConfiguration(MongoProperties mongoProperties) {
		super();
		this.mongoProperties = mongoProperties;
	}

	@Bean

	public MongoClientSettings mongoClientSettings() {
		return MongoClientSettings
					.builder()
					.applyToSslSettings(builder -> builder.enabled(true).invalidHostNameAllowed(true))
				    .build();
	}


//	@Bean
//	public MongoClient mongoClient(){
//		return MongoClients.create(mongoClientSettings());
//	}

	@Bean
	public MongoPropertiesClientSettingsBuilderCustomizer mongoPropertiesCustomizer(MongoProperties properties, Environment environment) {
		return new MongoPropertiesClientSettingsBuilderCustomizer(properties, environment);
	}

}
