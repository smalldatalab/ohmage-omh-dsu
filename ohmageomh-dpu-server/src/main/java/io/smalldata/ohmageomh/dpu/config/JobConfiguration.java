package io.smalldata.ohmageomh.dpu.config;

import io.smalldata.ohmageomh.data.domain.DataPoint;
import io.smalldata.ohmageomh.data.domain.EndUser;
import io.smalldata.ohmageomh.dpu.processor.SyncFitbitProcessor;
import io.smalldata.ohmageomh.dpu.reader.EndUserReader;
import io.smalldata.ohmageomh.dpu.util.ItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

/**
 * @author Jared Sieling.
 */

@Configuration
public class JobConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JobConfiguration.class);
    private static final String OVERRIDDEN_BY_EXPRESSION = null;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;


    /*
     * ===== CONFIGURE STEPS =====
     */

    @Bean
    public Step syncFitbitStep(
            @Qualifier("fitbitUserReader") ItemReader<ItemDTO> reader,
            @Qualifier("syncFitbitProcessor") ItemProcessor<ItemDTO, List<DataPoint>> processor,
            @Qualifier("dataPointsWriter") ItemWriter<List<DataPoint>> writer) {
        return stepBuilderFactory.get("syncFitbitStep")
                .<ItemDTO, List<DataPoint>>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    /*
     *  ===== CONFIGURE JOBS =====
     */

    @Bean
    public Job syncFitbitJob(JobExecutionListener listener,
            @Qualifier("syncFitbitStep") Step s1) {
        return jobBuilderFactory.get("syncFitbitJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }


    /*
     * ==== CONFIGURE READERS AND WRITERS
     *
     *  If they aren't here, reader and writer classes are in their respective packages. They MUST be annotated
     *  with @Component, and the naming convention for bean "Qualifier" is lower-camelcase of the class name.  If
     *  you want the object to instantiate each time the Step is run, add the @StepScope annotation.
     */

    @Bean
    @StepScope
    public ItemReader<EndUser> endUserReader() {
        return new EndUserReader();
    }

    @Bean
    public MongoItemWriter<List<DataPoint>> dataPointsWriter(MongoTemplate mongoTemplate) {
        MongoItemWriter<List<DataPoint>> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        return writer;
    }

}