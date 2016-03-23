package org.openmhealth.dpu.web.rest;

import org.openmhealth.dpu.util.ManualJobTriggerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class JobResource {

    private final Logger log = LoggerFactory.getLogger(JobResource.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobLocator jobLocator;

     @RequestMapping(value = "/jobs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> executeJob(@Valid @RequestBody ManualJobTriggerInfo manualJobTriggerInfo)
             throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException,
             JobInstanceAlreadyCompleteException {
         log.info("REST request to trigger job execution: ", manualJobTriggerInfo);

         JobParametersBuilder jpBuilder = new JobParametersBuilder();
         jpBuilder.addString("initTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
         jpBuilder.addString("jobName", manualJobTriggerInfo.getJobName());
//         jpBuilder.addString("userId", manualJobTriggerInfo.getUserId());
//         jpBuilder.addString("callbackUrl", manualJobTriggerInfo.getCallbackUrl());

         try {
             jobLauncher.run(jobLocator.getJob(manualJobTriggerInfo.getJobName()), jpBuilder.toJobParameters());
             return new ResponseEntity<String>(HttpStatus.OK);
//             return ResponseEntity.ok().body(null);
         }  catch (NoSuchJobException e) {
             log.error("Could not execute on-demand job: " + manualJobTriggerInfo.getJobName(), e);
         }

         return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }



}
