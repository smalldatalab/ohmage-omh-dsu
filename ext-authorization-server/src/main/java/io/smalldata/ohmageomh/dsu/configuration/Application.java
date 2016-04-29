/*
 * Copyright 2014 Open mHealth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.smalldata.ohmageomh.dsu.configuration;

import org.openmhealth.dsu.configuration.TestConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.openmhealth.dsu.configuration.SecurityConfiguration;
import org.springframework.context.annotation.FilterType;


/**
 * A configuration for the ohmageomh extension of the authorisation server. This also serves as the application entry point when launching the
 * application using Spring Boot.
 *
 * @author Jared Sieling
 */
@Configuration
@ComponentScan(
        basePackages = "org.openmhealth",
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
