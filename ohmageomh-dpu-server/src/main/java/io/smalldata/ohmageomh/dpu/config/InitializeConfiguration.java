package io.smalldata.ohmageomh.dpu.config;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author Jared Sieling.
 */
@Configuration
public class InitializeConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private JobLauncher jobLauncher;

    private static final Logger log = LoggerFactory.getLogger(InitializeConfiguration.class);

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();

            // TODO: Schedule all the jobs
//            List<ChannelScheduledJob> schedules = channelScheduledJobRepository.findAll();
//            for (ChannelScheduledJob schedule : schedules) {
//                log.info("Scheduling: " + schedule.toString());
//
//                JobKey jobKey = new JobKey(schedule.getId(), "mygroup");
//                JobDataMap dataMap = new JobDataMap();
//                dataMap.put("jobName", schedule.getJobName());
//                dataMap.put("jobLocator", jobRegistry);
//                dataMap.put("jobLauncher", jobLauncher);
//                dataMap.put("channelId", schedule.getChannel().getId());
//                JobDetail mJobDetail = JobBuilder.newJob(JobLauncherDetails.class)
//                        .withIdentity(jobKey)
//                        .usingJobData(dataMap)
//                        .storeDurably(true)
//                        .build();
//
//                Trigger trigger = TriggerBuilder
//                        .newTrigger()
//                        .withIdentity(schedule.getId(), "mygroup")
//                        .forJob(mJobDetail)
//                        .withSchedule(
//                                CronScheduleBuilder.cronSchedule(schedule.getCronExpression()))
//                        .build();
//
//                scheduler.scheduleJob(mJobDetail, trigger);
//            }
        } catch (Exception ex) {
            log.info("Could not schedule jobs.");
        }
    }
}
