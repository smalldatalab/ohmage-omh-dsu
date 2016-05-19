package io.smalldata.ohmageomh.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Range;
import io.smalldata.ohmageomh.data.domain.DataPoint;
import io.smalldata.ohmageomh.data.domain.DataPointSearchCriteria;
import io.smalldata.ohmageomh.data.service.DataPointService;
import io.smalldata.ohmageomh.domain.Participant;
import io.smalldata.ohmageomh.service.ParticipantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * REST controller for managing Data.
 */
@RestController
@RequestMapping("/api")
public class DataResource {

    private final Logger log = LoggerFactory.getLogger(DataResource.class);

    /*
     * These filtering parameters are temporary. They will likely change when a more generic filtering approach is
     * implemented.
     */
    public static final String CREATED_ON_OR_AFTER_PARAMETER = "created_on_or_after";
    public static final String CREATED_BEFORE_PARAMETER = "created_before";
    public static final String SCHEMA_NAMESPACE_PARAMETER = "schema_namespace";
    public static final String SCHEMA_NAME_PARAMETER = "schema_name";
    public static final String SCHEMA_VERSION_PARAMETER = "schema_version";

    public static final String RESULT_OFFSET_PARAMETER = "skip";
    public static final String RESULT_LIMIT_PARAMETER = "limit";
    public static final String DEFAULT_RESULT_LIMIT = "100";

    @Autowired
    private DataPointService dataPointService;
    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Reads data points.
     *
     * @param schemaNamespace the namespace of the schema the data points conform to
     * @param schemaName the name of the schema the data points conform to
     * @param schemaVersion the version of the schema the data points conform to
     * @param createdOnOrAfter the earliest creation timestamp of the data points to return, inclusive
     * @param createdBefore the latest creation timestamp of the data points to return, exclusive
     * @param offset the number of data points to skip
     * @param limit the number of data points to return
     * @return a list of matching data points
     */
    @RequestMapping(value = "/dataPoints", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    public
    @ResponseBody
    ResponseEntity<Iterable<DataPoint>> readDataPoints(
        @RequestParam(value = SCHEMA_NAMESPACE_PARAMETER) final String schemaNamespace,
        @RequestParam(value = SCHEMA_NAME_PARAMETER) final String schemaName,
        // TODO make this optional and update all associated code
        @RequestParam(value = SCHEMA_VERSION_PARAMETER) final String schemaVersion,
        // TODO replace with Optional<> in Spring MVC 4.1
        @RequestParam(value = CREATED_ON_OR_AFTER_PARAMETER, required = false)
        final OffsetDateTime createdOnOrAfter,
        @RequestParam(value = CREATED_BEFORE_PARAMETER, required = false) final OffsetDateTime createdBefore,
        @RequestParam(value = RESULT_OFFSET_PARAMETER, defaultValue = "0") final Integer offset,
        @RequestParam(value = RESULT_LIMIT_PARAMETER, defaultValue = DEFAULT_RESULT_LIMIT) final Integer limit,
        @RequestParam(value = "participant") final Long participantId,
        Authentication authentication) {

        // TODO add validation or explicitly comment that this is handled using exception translators

        Participant participant = participantService.findOne(participantId);

        DataPointSearchCriteria searchCriteria =
            new DataPointSearchCriteria(participant.getUsername(), schemaNamespace, schemaName, schemaVersion);

        if (createdOnOrAfter != null && createdBefore != null) {
            searchCriteria.setCreationTimestampRange(Range.closedOpen(createdOnOrAfter, createdBefore));
        }
        else if (createdOnOrAfter != null) {
            searchCriteria.setCreationTimestampRange(Range.atLeast(createdOnOrAfter));
        }
        else if (createdBefore != null) {
            searchCriteria.setCreationTimestampRange(Range.lessThan(createdBefore));
        }

        Iterable<DataPoint> dataPoints = dataPointService.findBySearchCriteria(searchCriteria, offset, limit);

        HttpHeaders headers = new HttpHeaders();

        // FIXME add pagination headers
        // headers.set("Next");
        // headers.set("Previous");

        return new ResponseEntity<>(dataPoints, headers, OK);
    }

//    /**
//     * GET  /dataPoints : get all the data.
//     *
//     * @return the ResponseEntity with status 200 (OK) and the list of dataPoints
//     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
//     */
//    @RequestMapping(value = "/dataPoints",
//        method = RequestMethod.GET,
//        produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    @ResponseBody
//    ResponseEntity<Iterable<DataPoint<StepCount>>> readDataPoints() {
//        // TODO Fix this to return actual data.
//        List<DataPoint<StepCount>> dataPoints = new ArrayList<DataPoint<StepCount>>();
//
//        try {
//            JsonNode responseRoot = objectMapper.readTree(fakeData());
//            JsonNode responseBody = responseRoot.get("body");
//
//            if(responseBody.isArray()) {
//                for(JsonNode dataPointJson : responseBody) {
//                    DataPoint<StepCount> dataPoint = new DataPoint<>(
//                        objectMapper.readValue(dataPointJson.get("header").toString(), DataPointHeader.class),
//                        objectMapper.readValue(dataPointJson.get("body").toString(), StepCount.class));
//
//                    dataPoints.add(dataPoint);
//                }
//            }
//        } catch(Exception ex) {
//
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//
//        return new ResponseEntity<>(dataPoints, headers, OK);
//    }

    private String fakeData() {
        return "{\"shim\":\"fitbit\",\"timeStamp\":1461079852,\"body\":[{\"header\":{\"id\":\"7e6a5772-354a-484c-8178-0af6d0506d45\",\"creation_date_time\":\"2016-04-19T15:30:52.755Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-11T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":8598}},{\"header\":{\"id\":\"9fa53f2d-4cdf-4ee7-8e14-bf5c74818356\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-12T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":5782}},{\"header\":{\"id\":\"4faba1f8-543b-4915-b99c-841f4a1b433c\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-13T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":5645}},{\"header\":{\"id\":\"c211b763-10bc-425f-b35d-85d1d89e49e2\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-14T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":8391}},{\"header\":{\"id\":\"821383f2-80ce-4c85-b815-2e7e3cc528ea\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-15T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":13932}},{\"header\":{\"id\":\"1a9fd6b5-4ba8-44d2-96d3-d08459f30b2e\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-16T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":8750}},{\"header\":{\"id\":\"9fe325e8-0122-4d0f-83d2-f424967962ba\",\"creation_date_time\":\"2016-04-19T15:30:52.758Z\",\"acquisition_provenance\":{\"source_name\":\"Fitbit Resource API\"},\"schema_id\":{\"namespace\":\"omh\",\"name\":\"step-count\",\"version\":\"1.0\"}},\"body\":{\"effective_time_frame\":{\"time_interval\":{\"start_date_time\":\"2016-04-17T00:00:00Z\",\"duration\":{\"unit\":\"d\",\"value\":1}}},\"step_count\":11821}}]}";
    }
}
