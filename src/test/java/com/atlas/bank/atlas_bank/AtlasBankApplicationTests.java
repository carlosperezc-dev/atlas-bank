package com.atlas.bank.atlas_bank;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
@SpringBootTest(properties= {
		"ATLAS_DB_USER=atlas",
		"ATLAS_DB_PASSWORD=atlas",
		"spring.kafka.listener.auto-startup=false"
})
class AtlasBankApplicationTests {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer POSTGRES =
			new PostgreSQLContainer("postgres:17-alpine")
					.withDatabaseName("atlasbank")
					.withUsername("atlas")
					.withPassword("atlas");

	@Test
	void contextLoads() {
	}

}
