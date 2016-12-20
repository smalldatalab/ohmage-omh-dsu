package io.smalldata.ohmageomh.dpu.config;

import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.mongodb.MongoExecutionContextDao;
import org.springframework.batch.mongodb.MongoJobExecutionDao;
import org.springframework.batch.mongodb.MongoJobInstanceDao;
import org.springframework.batch.mongodb.MongoStepExecutionDao;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Jared Sieling.
 */

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

//    @Value("${step.thread.core.pool.size}")
//    private int corePoolSize;
//
//    @Value("${step.thread.max.pool.size}")
//    private int maxPoolSize;

    @Autowired
    private MongoExecutionContextDao executionContextDao;

    @Autowired
    private MongoJobExecutionDao jobExecutionDao;

    @Autowired
    private MongoJobInstanceDao jobInstanceDao;

    @Autowired
    private MongoStepExecutionDao stepExecutionDao;

    public static final String DOT_ESCAPE_STRING = "\\{dot\\}";
    public static final String DOT_STRING = "\\.";


    @Bean
    public SimpleJobOperator jobOperator() {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobLauncher(jobLauncher());
        jobOperator.setJobExplorer(jobExplorer());
        jobOperator.setJobRepository(jobRepository());
        jobOperator.setJobRegistry(jobRegistry());
        return jobOperator;
    }

    @Bean
    public JobExplorer jobExplorer() {
        return new SimpleJobExplorer(jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);
    }

    @Bean
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

    @Bean
    JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry());
        return postProcessor;
    }

    @Bean
    public SimpleJobLauncher jobLauncher() {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        return jobLauncher;
    }

    @Bean
    public JobRepository jobRepository() {
        return new SimpleJobRepository(jobInstanceDao, jobExecutionDao, stepExecutionDao, executionContextDao);
    }

//    @Bean
//    public TaskExecutor taskExecutor() {
//        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(corePoolSize);
//        taskExecutor.setMaxPoolSize(maxPoolSize);
//        return taskExecutor;
//    }

    @Bean
    public JobParametersIncrementer jobParametersIncrementer() {
        return new RunIdIncrementer();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

}
