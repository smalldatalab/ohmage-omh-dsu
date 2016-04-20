package org.openmhealth.dpu.util;

public class ManualJobTriggerInfo {

    private String userId;
    private String jobName;

    public ManualJobTriggerInfo() {
    }

    public ManualJobTriggerInfo(String userId, String jobName) {
        this.userId = userId;
        this.jobName = jobName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public String toString() {
        return "ManualJobTriggerInfo{" +
                "userId='" + userId + '\'' +
                ", jobName='" + jobName + '\'' +
                '}';
    }
}
