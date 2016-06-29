package io.smalldata.ohmageomh.dpu.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smalldata.ohmageomh.data.domain.EndUser;
import io.smalldata.ohmageomh.data.service.DataPointService;
import io.smalldata.ohmageomh.dpu.service.OmhShimService;
import io.smalldata.ohmageomh.data.domain.DataPoint;
import org.openmhealth.schema.domain.omh.DataPointHeader;
import org.openmhealth.schema.domain.omh.StepCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component("syncFitbitProcessor")
public class SyncFitbitProcessor implements ItemProcessor<EndUser, List<DataPoint<StepCount>>> {

    private static final Logger log = LoggerFactory.getLogger(SyncFitbitProcessor.class);

    @Autowired
    DataPointService dataPointService;

    @Inject
    private OmhShimService omhShimService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<DataPoint<StepCount>> process(EndUser user) throws Exception {

        // Set date range for past calendar week, ending Sunday
        LocalDate now = LocalDate.now();
//        LocalDate now = LocalDate.parse('2016-02-03', DateTimeFormatter.ISO_LOCAL_DATE)
        LocalDate endDate = now.minusDays(now.getDayOfWeek().getValue());
        LocalDate startDate = endDate.minusDays(6);

        // Fetch data
        JsonNode responseRoot = omhShimService.getDataAsJsonNode(user, "fitbit", "steps", true, startDate, endDate);
        JsonNode responseBody = responseRoot.get("body");

        List<DataPoint<StepCount>> dataPoints = new ArrayList<DataPoint<StepCount>>();
        if(responseBody.isArray()) {
            for(JsonNode dataPointJson : responseBody) {
                DataPoint<StepCount> dataPoint = new DataPoint<>(
                        objectMapper.readValue(dataPointJson.get("header").toString(), DataPointHeader.class),
                        objectMapper.readValue(dataPointJson.get("body").toString(), StepCount.class));

                // TODO Check if the data points already exist, or just delete dataPoints in last 7 days

                // set the owner of the data point to be the user associated with the access token
                dataPointService.setUserId(dataPoint.getHeader(), user.getUsername());

                dataPointService.save(dataPoint);
                dataPoints.add(dataPoint);
            }
        }

        log.info("=== Syncing Fitbit data now for user: " + user.getUsername());
        return dataPoints;
    }
}