package io.smalldata.ohmageomh.dpu.config;

import io.smalldata.ohmageomh.dpu.reader.EndUserReader;
import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.schema.domain.omh.DataPoint;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Jared Sieling.
 */

@Configuration
@EnableBatchProcessing
@Import(BatchDataSourceConfiguration.class)
public class BatchConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);
    private static final String OVERRIDDEN_BY_EXPRESSION = null;

    @Bean
    BatchConfigurer configurer(@Qualifier("batchDataSource") DataSource dataSource){
        return new DefaultBatchConfigurer(dataSource);
    }

    @Bean(name = "endUserReader")
    @StepScope
    public ItemReader<EndUser> endUserReader() {
        return new EndUserReader();
    }
//
//    @Bean(name = "dpuResultWriter")
//    public MongoItemWriter<DataPoint> dpuResultWriter(MongoTemplate mongoTemplate) {
//        MongoItemWriter<DataPoint> writer = new MongoItemWriter<>();
//        writer.setTemplate(mongoTemplate);
//        return writer;
//    }

    @Bean(name = "syncFitbitJob")
    public Job syncFitbitJob(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory,
                         JobExecutionListener listener,
                         @Qualifier("syncFitbitProcessor") ItemProcessor<EndUser, List<DataPoint>> processor) {

        Step s1 = stepBuilderFactory.get("syncFitbitStep1")
                .<EndUser, List<DataPoint>>chunk(10)
                .reader(endUserReader())
                .processor(processor)
//                .writer(writer)
                .build();

        return jobs.get("syncFitbitJob")
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
}
