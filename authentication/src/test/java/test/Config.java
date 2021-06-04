package test;

import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * This is a configuration class that enables the use of an embedded
 * in-memory database for persistence layer tests and integration tests.
 */
@Configuration
@EntityScan(basePackages = "nl.tudelft.sem.authentication.entities")
@EnableJpaRepositories(basePackages = "nl.tudelft.sem.authentication.repositories")
@PropertySource("classpath:test.properties")
@EnableTransactionManagement
public class Config {
    @Autowired
    private transient Environment env;

    /**
     * This method configures a {@link DataSource} bean that is used
     * for connecting to the embedded test database.
     *
     * @return The {@link DataSource} bean object.
     */
    @Bean
    public DataSource dataSourceTest() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(
                Objects.requireNonNull(env.getProperty("jdbc.driverClassName"))
        );
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.pass"));

        return dataSource;
    }
}