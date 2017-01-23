package org.openmhealth.schema.domain.omh;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.openmhealth.schema.serializer.SerializationConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Class to represent Mobility Daily Summary. Note that we didn't represent all the fields in the Mobility Daily Summary
 * returned by the Moves DPU. Those unrepresented fields will be added to the "additionalFields" collection and will also be serialized to the database when saved.
 * The following is a CSV mapping that should be used to present the data
 *   {
 "field": "body.date",
 "displayName": "Date"
 },
 {
 "field": "body.device",
 "displayName": "Source"
 },
 {
 "field": "body.active_time.value",
 "displayName": "Active Time (seconds)"
 },
 {
 "field": "body.walking_distance.value",
 "displayName": "Walking Distance (km)"
 },
 {
 "field": "body.step_count",
 "displayName": "Step Count"
 },
 {
 "field": "body.geodiameter.value",
 "displayName": "Geodiameter (km)"
 },
 {
 "field": "body.max_gait_speed_in_meter_per_second",
 "displayName": "Max Gait Speed (m/s)"
 },
 {
 "field": "body.home.leave_home_time",
 "displayName": "Left Home Time",
 "cellFilter": "date:'HH:mm:ss'"
 },
 {
 "field": "body.home.return_home_time",
 "displayName": "Return Home Time",
 "cellFilter": "date:'HH:mm:ss'"
 },
 {
 "field": "body.home.time_not_at_home.value",
 "displayName": "Time Not at Home (seconds)"
 },
 {
 "field": "body.coverage",
 "displayName": "Coverage"
 }
 ]
 * Created by changun on 1/23/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class MobilityDailySummary  extends Measure {
    public static final SchemaId SCHEMA_ID = new SchemaId(OMH_NAMESPACE, "mobility-daily-summary", "2.0");

    public HomeRelatedStatistics getHome() {
        return home;
    }

    public void setHome(HomeRelatedStatistics home) {
        this.home = home;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public float getCoverage() {
        return coverage;
    }

    public void setCoverage(float coverage) {
        this.coverage = coverage;
    }

    public float getMax_gait_speed_in_meter_per_second() {
        return max_gait_speed_in_meter_per_second;
    }

    public void setMax_gait_speed_in_meter_per_second(float max_gait_speed_in_meter_per_second) {
        this.max_gait_speed_in_meter_per_second = max_gait_speed_in_meter_per_second;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
    public static class HomeRelatedStatistics{
        @SerializationConstructor
        protected HomeRelatedStatistics() {
        }
        /*
         * Return/Leave Home Time are supposed to be ISO8601 encoded datetime. We use String to represent them
         * to avoid the timezone information from being lost during the (de)serialization.
         */
        private String returnHomeTime;
        private String leaveHomeTime;
        private DurationUnitValue timeNotAtHome;


        public String getReturnHomeTime() {
            return returnHomeTime;
        }

        public void setReturnHomeTime(String returnHomeTime) {
            this.returnHomeTime = returnHomeTime;
        }

        public String getLeaveHomeTime() {
            return leaveHomeTime;
        }

        public void setLeaveHomeTime(String leaveHomeTime) {
            this.leaveHomeTime = leaveHomeTime;
        }

        public DurationUnitValue getTimeNotAtHome() {
            return timeNotAtHome;
        }

        public void setTimeNotAtHome(DurationUnitValue timeNotAtHome) {
            this.timeNotAtHome = timeNotAtHome;
        }
    }
    private LocalDate date;
    private String device;
    private HomeRelatedStatistics home;
    private LengthUnitValue walkingDistance;
    private LengthUnitValue longestTrek;
    private DurationUnitValue activeTime;
    private LengthUnitValue geodiameter;
    private BigDecimal stepCount;
    private float coverage;

    // the speed is not yet modeled in OMH yet, so we use model it as raw floating point value here.
    private float max_gait_speed_in_meter_per_second;


    @SerializationConstructor
    protected MobilityDailySummary() {
    }



    @Override
    public SchemaId getSchemaId() {
        return SCHEMA_ID;
    }

    public LengthUnitValue getWalkingDistance() {
        return walkingDistance;
    }

    public void setWalkingDistance(LengthUnitValue walkingDistance) {
        this.walkingDistance = walkingDistance;
    }

    public LengthUnitValue getLongestTrek() {
        return longestTrek;
    }

    public void setLongestTrek(LengthUnitValue longestTrek) {
        this.longestTrek = longestTrek;
    }

    public DurationUnitValue getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(DurationUnitValue activeTime) {
        this.activeTime = activeTime;
    }

    public LengthUnitValue getGeodiameter() {
        return geodiameter;
    }

    public void setGeodiameter(LengthUnitValue geodiameter) {
        this.geodiameter = geodiameter;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getStepCount() {
        return stepCount;
    }

    public void setStepCount(BigDecimal stepCount) {
        this.stepCount = stepCount;
    }
}
