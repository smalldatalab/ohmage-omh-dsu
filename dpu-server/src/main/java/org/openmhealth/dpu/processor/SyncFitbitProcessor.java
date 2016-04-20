package org.openmhealth.dpu.processor;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.openmhealth.dpu.repository.DataPointRepository;
import org.openmhealth.dpu.service.OmhShimService;
import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.schema.domain.omh.DataPoint;
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
    private DataPointRepository repository;

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
        String responseString = omhShimService.getData(user, "fitbit", "steps", true, startDate, endDate);

        JsonNode responseRoot = objectMapper.readTree(responseString);
        JsonNode responseBody = responseRoot.get("body");

        List<DataPoint<StepCount>> dataPoints = new ArrayList<DataPoint<StepCount>>();
        if(responseBody.isArray()) {
            for(JsonNode dataPointJson : responseBody) {
                DataPoint<StepCount> dataPoint = new DataPoint<>(
                        objectMapper.readValue(dataPointJson.get("header").toString(), DataPointHeader.class),
                        objectMapper.readValue(dataPointJson.get("body").toString(), StepCount.class));
                repository.save(dataPoint);
                dataPoints.add(dataPoint);
            }
        }

        log.info("=== Syncing Fitbit data now for user: " + user.getUsername());
        return dataPoints;
    }
}