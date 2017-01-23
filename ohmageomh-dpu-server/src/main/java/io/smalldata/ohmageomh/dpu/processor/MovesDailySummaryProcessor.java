package io.smalldata.ohmageomh.dpu.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smalldata.ohmageomh.data.domain.DataPoint;
import io.smalldata.ohmageomh.data.domain.EndUser;
import io.smalldata.ohmageomh.data.service.DataPointService;
import io.smalldata.ohmageomh.dpu.service.OmhShimService;
import org.openmhealth.schema.domain.omh.DataPointHeader;
import org.openmhealth.schema.domain.omh.MobilityDailySummary;
import org.openmhealth.schema.domain.omh.StepCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import mobility_dpu.api;
import org.springframework.stereotype.Component;

/**
 * Mobility Daily Summary Processor. Invoke the Clojure-based Moves API in mobility-dpu package. Deserialize and return
 * an array of mobility summary data points returned by the API.
 *
 * Created by changun on 2016/6/21.
 */
@Component("syncMovesProcessor")
public class MovesDailySummaryProcessor implements ItemProcessor<EndUser, List<DataPoint<MobilityDailySummary>>> {
    private static final Logger log = LoggerFactory.getLogger(SyncFitbitProcessor.class);


    @Autowired
    DataPointService dataPointService;

    @Inject
    private OmhShimService omhShimService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<DataPoint<MobilityDailySummary>> process(EndUser user) throws Exception {
        JsonNode responseBody = objectMapper.readTree(mobility_dpu.api.movesDatapoints(user.toString()));


        List<DataPoint<MobilityDailySummary>> dataPoints = new ArrayList<DataPoint<MobilityDailySummary>>();
        if(responseBody.isArray()) {
            for(JsonNode dataPointJson : responseBody) {
                DataPoint<MobilityDailySummary> dataPoint = new DataPoint<>(
                        objectMapper.treeToValue(dataPointJson.get("header"), DataPointHeader.class),
                        objectMapper.treeToValue(dataPointJson.get("body"), MobilityDailySummary.class));

                // TODO Check if the data points already exist, or just delete dataPoints in last 7 days

                // set the owner of the data point to be the user associated with the access token
                dataPointService.setUserId(dataPoint.getHeader(), user.getUsername());

                dataPointService.save(dataPoint);
                dataPoints.add(dataPoint);
            }
        }

        log.info("=== Syncing Moves data now for user: " + user.getUsername());
        return dataPoints;
    }
}
