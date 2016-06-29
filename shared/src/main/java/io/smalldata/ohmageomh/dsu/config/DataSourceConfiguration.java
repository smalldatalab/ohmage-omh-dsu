package io.smalldata.ohmageomh.dsu.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * The configuration for accessing the database used for ohmage-omh objects. Not for the data points.
 *
 */
@Configuration
public class DataSourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="manageDataSource")
    @ConfigurationProperties(prefix="ohmageomh.datasource")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
}
