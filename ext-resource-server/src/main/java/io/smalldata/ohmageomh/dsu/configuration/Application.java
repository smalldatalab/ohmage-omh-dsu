package io.smalldata.ohmageomh.dsu.configuration;

import org.openmhealth.dsu.configuration.TestConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * A configuration for the ohmageomh extension of the resource server. This also serves as the application entry point when launching the
 * application using Spring Boot.
 *
 * @author Jared Sieling
 */
@Configuration
@ComponentScan(
        basePackages = "org.openmhealth, io.smalldata.ohmageomh",
        excludeFilters = {
                @ComponentScan.Filter(value = EnableAutoConfiguration.class)
        })
@EnableAutoConfiguration
@EnableConfigurationProperties
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
