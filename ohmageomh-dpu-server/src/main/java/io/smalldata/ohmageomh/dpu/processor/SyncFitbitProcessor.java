package io.smalldata.ohmageomh.dpu.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smalldata.ohmageomh.data.domain.EndUser;
import io.smalldata.ohmageomh.data.service.DataPointService;
import io.smalldata.ohmageomh.dpu.service.OmhShimService;
import io.smalldata.ohmageomh.data.domain.DataPoint;
import io.smalldata.ohmageomh.dpu.util.ItemDTO;
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
import java.util.Map;

@Component
public class SyncFitbitProcessor implements ItemProcessor<ItemDTO, List<DataPoint<StepCount>>> {

    private static final Logger log = LoggerFactory.getLogger(SyncFitbitProcessor.class);

    @Autowired
    DataPointService dataPointService;

    @Inject
    private OmhShimService omhShimService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<DataPoint<StepCount>> process(ItemDTO item) throws Exception {

        // Create the StepCount data points.
        List<DataPoint<StepCount>> dataPoints = new ArrayList<DataPoint<StepCount>>();
        JsonNode stepsNode = (JsonNode) item.getExtra("stepsNode");
        if(stepsNode.isArray()) {
            for(JsonNode dataPointJson : stepsNode) {
                DataPoint<StepCount> dataPoint = new DataPoint<>(
                        objectMapper.readValue(dataPointJson.get("header").toString(), DataPointHeader.class),
                        objectMapper.readValue(dataPointJson.get("body").toString(), StepCount.class));

                // TODO Check if the data points already exist, or just delete dataPoints in last 7 days

                // set the owner of the data point to be the user associated with the access token
                dataPointService.setUserId(dataPoint.getHeader(), item.getUser().getUsername());

                dataPointService.save(dataPoint);
                dataPoints.add(dataPoint);
            }
        }

        log.info("=== Syncing Fitbit data now for user: " + item.getUser().getUsername());
        return dataPoints;
    }
}