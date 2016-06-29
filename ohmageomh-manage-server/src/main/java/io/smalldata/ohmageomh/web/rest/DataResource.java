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
            new DataPointSearchCriteria(participant.getDsuId(), schemaNamespace, schemaName, schemaVersion);

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

        // TODO Move the appending of this information into service, when possible
        for(DataPoint dataPoint : dataPoints) {
            dataPoint.getHeader().setAdditionalProperty("participant_id", participant.getId());
            dataPoint.getHeader().setAdditionalProperty("participant_label", participant.getLabel());
        }

        HttpHeaders headers = new HttpHeaders();

        // FIXME add pagination headers
        // headers.set("Next");
        // headers.set("Previous");

        return new ResponseEntity<>(dataPoints, headers, OK);
    }

}
