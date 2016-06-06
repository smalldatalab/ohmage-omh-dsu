package io.smalldata.ohmageomh.dsu.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * The configuration for accessing the database used for ohmage-omh objects. Not for the data points.
 *
 */
@Configuration
public class OhmageomhDataSource {
    @Bean(name="manageDataSource")
    @ConfigurationProperties(prefix="ohmageomh.datasource")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
}
