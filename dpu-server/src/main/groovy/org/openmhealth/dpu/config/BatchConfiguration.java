package org.openmhealth.dpu.config;

import org.openmhealth.dpu.processor.BlankProcessor;
import org.openmhealth.dpu.reader.EndUserReader;
import org.openmhealth.dpu.util.DpuResult;
import org.openmhealth.dpu.util.EndUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * (Description here)
 *
 * @author Jared Sieling.
 */
@Configuration
@EnableBatchProcessing
@Import(DataSourceConfiguration.class)
public class BatchConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);
    private static final String OVERRIDDEN_BY_EXPRESSION = null;

    @Bean(name = "endUserReader")
    @StepScope
    public ItemReader<EndUser> endUserReader() {
        return new EndUserReader();
    }

    @Bean(name = "sampleProcessor")
    public ItemProcessor<EndUser, DpuResult> sampleProcessor() {
        return new BlankProcessor();
    }

    @Bean(name = "dpuResultWriter")
    public MongoItemWriter<DpuResult> dpuResultWriter(MongoTemplate mongoTemplate) {
        MongoItemWriter<DpuResult> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        return writer;
    }

    @Bean(name = "sampleJob")
    public Job sampleJob(JobBuilderFactory jobs, StepBuilderFactory stepBuilderFactory,
                                      JobExecutionListener listener, MongoItemWriter<DpuResult> writer,
                                      @Qualifier("blankProcessor") ItemProcessor processor) {

        Step s1 = stepBuilderFactory.get("sampleStep1")
                .<EndUser, DpuResult>chunk(10)
                .reader(endUserReader())
                .processor(processor)
//                .writer(writer)
                .build();

        return jobs.get("sampleJob")
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
