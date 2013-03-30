package cz.cecrdlem.blog.dao;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;

@Configuration
@EnableMongoRepositories
public class MongoConfig extends AbstractMongoConfiguration {

	@Override
	public Mongo mongo() throws Exception {
		return new Mongo();
	}

	@Override
	protected String getDatabaseName() {
		return "blog";
	}
}
