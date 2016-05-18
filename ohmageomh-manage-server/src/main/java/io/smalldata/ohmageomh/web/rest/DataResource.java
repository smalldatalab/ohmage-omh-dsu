package io.smalldata.ohmageomh.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmhealth.schema.domain.omh.DataPoint;
import org.openmhealth.schema.domain.omh.DataPointHeader;
import org.openmhealth.schema.domain.omh.StepCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

/**
 * REST controller for managing Data.
 */
@RestController
@RequestMapping("/api")
public class DataResource {

    private final Logger log = LoggerFactory.getLogger(DataResource.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * GET  /dataPoints : get all the data.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of dataPoints
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/dataPoints",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    ResponseEntity<Iterable<DataPoint<StepCount>>> readDataPoints() {
        // TODO Fix this to return actual data.
        List<DataPoint<StepCount>> dataPoints = new ArrayList<DataPoint<StepCount>>();

        try {
            JsonNode responseRoot = objectMapper.readTree(fakeData());
            JsonNode responseBody = responseRoot.get("body");

            if(responseBody.isArray()) {
                for(JsonNode dataPointJson : responseBody) {
                    DataPoint<StepCount> dataPoint = new DataPoint<>(
                        objectMapper.readValue(dataPointJson.get("header").toString(), DataPointHeader.class),
                        objectMapper.readValue(dataPointJson.get("body").toString(), StepCount.class));

                    dataPoints.add(dataPoint);
                }
            }
        } catch(Exception ex) {

        }

        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(dataPoints, headers, OK);
    }

    private String fakeData() {
        return "{\"shim\":\"fitbit\",\"timeStamp\":1461079852,\"body\":[{\"header\":{\"id\":\"7e6a5772-354a-484c-8178-0af6d0506d45\",\"creation_date_time\":\"2016-04-19T15:30:52.755Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-11T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":8598}},{\"header\":{\"id\":\"9fa53f2d-4cdf-4ee7-8e14-bf5c74818356\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-12T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":5782}},{\"header\":{\"id\":\"4faba1f8-543b-4915-b99c-841f4a1b433c\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-13T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":5645}},{\"header\":{\"id\":\"c211b763-10bc-425f-b35d-85d1d89e49e2\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-14T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":8391}},{\"header\":{\"id\":\"821383f2-80ce-4c85-b815-2e7e3cc528ea\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-15T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":13932}},{\"header\":{\"id\":\"1a9fd6b5-4ba8-44d2-96d3-d08459f30b2e\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-16T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":8750}},{\"header\":{\"id\":\"9fe325e8-0122-4d0f-83d2-f424967962ba\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-17T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":11821}}]}";
    }
}
