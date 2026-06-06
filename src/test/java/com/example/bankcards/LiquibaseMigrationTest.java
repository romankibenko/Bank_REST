package com.example.bankcards;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class LiquibaseMigrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/migration/master.xml");
    }

    @Autowired
    private DataSource dataSource;

    @Test
    void tablesShouldExistAfterMigration() throws Exception {
        try (var connection = dataSource.getConnection();
             var rs = connection.getMetaData().getTables(null, null, "%", null)) {

            ResultSet result = rs;
            boolean hasUsersTable = false;
            boolean hasCardsTable = false;

            while (result.next()) {
                String tableName = result.getString("TABLE_NAME");
                if ("users".equalsIgnoreCase(tableName)) hasUsersTable = true;
                if ("cards".equalsIgnoreCase(tableName)) hasCardsTable = true;
            }

            assertThat(hasUsersTable).isTrue();
            assertThat(hasCardsTable).isTrue();
        }
    }
}
