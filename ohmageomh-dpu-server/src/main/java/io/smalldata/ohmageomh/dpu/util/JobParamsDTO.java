package io.smalldata.ohmageomh.dpu.util;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * A wrapper class for JobParameters that adds some helper methods for common application
 * specific fields.
 *
 * @author Jared Sieling.
 */
public class JobParamsDTO {

    private Map<String, JobParameter> rawParams;

    public static final String START_DATE_JOB_PARAM = "startDate";
    public static final String END_DATE_JOB_PARAM = "endDate";
    public static final String DATE_DURATION_DAYS_JOB_PARAM = "durationDays";

    public JobParamsDTO(Map<String, JobParameter> params) {
        this.rawParams = params;
    }

    public boolean hasDates() {
        if(rawParams.get(START_DATE_JOB_PARAM) != null || rawParams.get(END_DATE_JOB_PARAM) != null) {
            return true;
        } else {
            return false;
        }
    }

    public LocalDate getStartDate() {
        JobParameters params = new JobParameters(rawParams);
        if(rawParams.get(START_DATE_JOB_PARAM) != null) {
            return LocalDate.parse(params.getString(START_DATE_JOB_PARAM),
                    DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            LocalDate endDate = LocalDate.parse(params.getString(END_DATE_JOB_PARAM),
                    DateTimeFormatter.ISO_LOCAL_DATE);
            return endDate.minusDays(Long.parseLong(params.getString(DATE_DURATION_DAYS_JOB_PARAM)) - 1);
        }
    }

    public LocalDate getEndDate() {
        JobParameters params = new JobParameters(rawParams);
        if(rawParams.get(END_DATE_JOB_PARAM) != null) {
            return LocalDate.parse(params.getString(END_DATE_JOB_PARAM),
                    DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            LocalDate endDate = LocalDate.parse(params.getString(START_DATE_JOB_PARAM),
                    DateTimeFormatter.ISO_LOCAL_DATE);
            return endDate.plusDays(Long.parseLong(params.getString(DATE_DURATION_DAYS_JOB_PARAM)) - 1);
        }
    }
}
