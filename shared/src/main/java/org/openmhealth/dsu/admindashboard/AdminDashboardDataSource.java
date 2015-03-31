package org.openmhealth.dsu.admindashboard;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * The configuration for accessing the database used by the admin dashboard.
 * (usually a Postgres db)
 * Only enable it when the admin dashboard is in use.
 * Created by changun on 2/26/15.
 */
@Configuration
public class AdminDashboardDataSource {
    @Bean(name="admindashboardDataSource")
    @ConfigurationProperties(prefix="admindashboard.datasource")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
}
