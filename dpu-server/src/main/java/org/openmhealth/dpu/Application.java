package org.openmhealth.dpu;

import org.openmhealth.dpu.service.BootstrapService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * (Description here)
 *
 * @author Jared Sieling.
 */
@SpringBootApplication
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
