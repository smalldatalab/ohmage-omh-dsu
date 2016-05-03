package io.smalldata.ohmageomh.dsu.configuration;

import org.openmhealth.dsu.configuration.TestConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * A configuration for the ohmageomh extension of the authorisation server. This also serves as the application entry point when launching the
 * application using Spring Boot.
 *
 * @author Jared Sieling
 */
@Configuration
@ComponentScan(
        basePackages = "org.openmhealth, io.smalldata.ohmageomh",
        excludeFilters = {
                // this exclusion avoids duplicate auto-configurations, especially in integration tests
                @ComponentScan.Filter(value = EnableAutoConfiguration.class),
                // this exclusion avoids pulling in test-specific @Configuration in other integration tests
                @ComponentScan.Filter(value = TestConfiguration.class),
                // this exclusion avoids duplicate configurations specific to ohmageomh files
                @ComponentScan.Filter(type = FilterType.REGEX, pattern="org.openmhealth.dsu.configuration.SecurityConfiguration"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern="org.openmhealth.dsu.configuration.OAuth2AuthorizationServerConfiguration"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern="org.openmhealth.dsu.service.EndUserUserDetailsServiceImpl")
        })
@EnableAutoConfiguration
@EnableConfigurationProperties
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
