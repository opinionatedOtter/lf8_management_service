package de.szut.lf8_project;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {WithAppContextContainerTest.Initializer.class})
public abstract class WithAppContextContainerTest {

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:13.3");

    protected static JdbcTemplate jdbcTemplate;


    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl().replace("jdbc:", "jdbc:tc:"),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());

            PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource();
            pgSimpleDataSource.setUrl(postgreSQLContainer.getJdbcUrl());
            pgSimpleDataSource.setPassword(postgreSQLContainer.getPassword());
            pgSimpleDataSource.setUser(postgreSQLContainer.getUsername());
            pgSimpleDataSource.setCurrentSchema("public");

            jdbcTemplate = new JdbcTemplate(pgSimpleDataSource);
        }
    }
}
