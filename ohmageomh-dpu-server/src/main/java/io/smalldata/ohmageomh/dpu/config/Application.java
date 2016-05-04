package io.smalldata.ohmageomh.dpu.config;

import io.smalldata.ohmageomh.dpu.service.BootstrapService;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author Jared Sieling.
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

    @Inject
    private BootstrapService bootstrapService;

    @PostConstruct
    public void initApplication() {
//        bootstrapService.run();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
