/*
 * Copyright 2014 Open mHealth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openmhealth.dsu.controller;

import com.google.common.collect.Range;
import com.mongodb.gridfs.GridFSDBFile;
import org.openmhealth.dsu.domain.DataPoint;
import org.openmhealth.dsu.domain.DataPointMedia;
import org.openmhealth.dsu.domain.DataPointSearchCriteria;
import org.openmhealth.dsu.service.DataPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.openmhealth.dsu.configuration.OAuth2Properties.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * A controller that handles the calls that read and write data points.
 *
 * @author Emerson Farrugia
 */
@ApiController
public class DataPointController {

    private static final Logger log = LoggerFactory.getLogger(DataPointController.class);

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
    private MappingJackson2HttpMessageConverter converter;
    @Autowired
    private GridFsOperations gridFsOperations;
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
    // TODO confirm if HEAD handling needs anything additional
    // only allow clients with read scope to read data points
    @PreAuthorize("#oauth2.clientHasRole('" + CLIENT_ROLE + "') and #oauth2.hasScope('" + DATA_POINT_READ_SCOPE + "')")
    @RequestMapping(value = "/dataPoints", method = {HEAD, GET}, produces = APPLICATION_JSON_VALUE)
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
            Authentication authentication) {

        // TODO add validation or explicitly comment that this is handled using exception translators

        // determine the user associated with the access token to restrict the search accordingly
        String endUserId = getEndUserId(authentication);

        DataPointSearchCriteria searchCriteria =
                new DataPointSearchCriteria(endUserId, schemaNamespace, schemaName, schemaVersion);

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

    public String getEndUserId(Authentication authentication) {

        return authentication.getName();
    }

    /**
     * Reads a data point.
     *
     * @param id the identifier of the data point to read
     * @return a matching data point, if found
     */
    // TODO can identifiers be relative, e.g. to a namespace?
    // TODO confirm if HEAD handling needs anything additional
    // only allow clients with read scope to read a data point
    @PreAuthorize("#oauth2.clientHasRole('" + CLIENT_ROLE + "') and #oauth2.hasScope('" + DATA_POINT_READ_SCOPE + "')")
    // ensure that the returned data point belongs to the user associated with the access token
    @PostAuthorize("returnObject.body == null || returnObject.body.userId == principal.username")
    @RequestMapping(value = "/dataPoints/{id}", method = {HEAD, GET}, produces = APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<DataPoint> readDataPoint(@PathVariable String id) {

        Optional<DataPoint> dataPoint = dataPointService.findOne(id);

        if (!dataPoint.isPresent()) {
            return new ResponseEntity<>(NOT_FOUND);
        }

        // FIXME test @PostAuthorize
        return new ResponseEntity<>(dataPoint.get(), OK);
    }

    /**
     * Reads media data of a data point.
     *
     * @param id the identifier of the data point
     * @param mId the desired media id
     * @param authentication  user authentication
     * @return a matching data point, if found
     */
    // only allow clients with read scope to read a data point
    @PreAuthorize("#oauth2.clientHasRole('" + CLIENT_ROLE + "') and #oauth2.hasScope('" + DATA_POINT_READ_SCOPE + "')")
    @RequestMapping(value = "/dataPoints/{id}/media/{mId}", method = {HEAD, GET},
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE,MediaType.ALL_VALUE})
    public ResponseEntity<InputStreamResource> readDataPointMedia(@PathVariable String id, @PathVariable String mId, Authentication authentication) {
        Query query = new Query();

        query.addCriteria(where("metadata.data_point_id").is(id));
        query.addCriteria(where("metadata.user_id").is(getEndUserId(authentication)));
        query.addCriteria(where("metadata.media_id").is(mId));

        GridFSDBFile gridFsFile = gridFsOperations.findOne(query);

        if(gridFsFile != null){
            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentType(MediaType.parseMediaType(gridFsFile.getContentType()));
            respHeaders.setContentLength(gridFsFile.getLength());
            respHeaders.setContentDispositionFormData("attachment", mId);
            InputStreamResource inputStreamResource = new InputStreamResource(gridFsFile.getInputStream());
            return new ResponseEntity<>(inputStreamResource, respHeaders, OK);
        }
        return new ResponseEntity<>(NOT_FOUND);

    }



    /**
     * Internal write data point function
     * @param dataPoint  data point to write
     * @param mediaParts  media multi parts (if any)
     * @param authentication  user authentication
     * @return CONFLICT if data point exists, otherwise CREATED
     * @throws IOException
     */
    public ResponseEntity<?> writeDataPoint(DataPoint dataPoint,  Optional<List<MultipartFile>> mediaParts,
                                            Authentication authentication) throws IOException {
        // FIXME test validation
        if (dataPointService.exists(dataPoint.getId())) {
            return new ResponseEntity<>(CONFLICT);
        }
        // set the owner of the data point to be the user associated with the access token
        String endUserId = getEndUserId(authentication);
        dataPoint.setUserId(endUserId);



        // store the media files (if any)
        if(mediaParts.isPresent()) {
            for (MultipartFile mediaPart : mediaParts.get()) {
                DataPointMedia media = new DataPointMedia(dataPoint, mediaPart);
                dataPoint.getHeader().getMedia().add(media);
                gridFsOperations.store(media.getStream(), media.getId(), media.getContentType(), media);

            }
        }
        // save data points
        dataPointService.save(dataPoint);
        return new ResponseEntity<>(CREATED);
    }
    /**
     * Writes a data point without media data.
     *
     * @param dataPoint the data point to write
     */
    // only allow clients with write scope to write data points
    @PreAuthorize("#oauth2.clientHasRole('" + CLIENT_ROLE + "') and #oauth2.hasScope('" + DATA_POINT_WRITE_SCOPE + "')")
    @RequestMapping(value = "/dataPoints", method = POST, consumes = APPLICATION_JSON_VALUE )
    public ResponseEntity<?> writeDataPoint(@RequestBody @Valid DataPoint dataPoint, Authentication authentication) throws IOException {
        return writeDataPoint(dataPoint, Optional.<List<MultipartFile>>empty(), authentication);
    }


    /**
     * Writes a data point with media data as multi-parts.
     *
     * @param mediaParts the media multi-parts
     * @param dataPointStr  data point string in JSON format
     *
     */
    // only allow clients with write scope to write data points
    @PreAuthorize("#oauth2.clientHasRole('" + CLIENT_ROLE + "') and #oauth2.hasScope('" + DATA_POINT_WRITE_SCOPE + "')")
    @RequestMapping(value = "/dataPoints", method = POST, consumes = MULTIPART_FORM_DATA_VALUE,
                                                          headers = "content-type="+MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> writeDataPoint(@RequestParam("media") List<MultipartFile> mediaParts,
                                            @RequestParam("data")  String dataPointStr,
                                            Authentication authentication) throws IOException {
        DataPoint dataPoint = converter.getObjectMapper().readValue(dataPointStr, DataPoint.class);
        return  writeDataPoint(dataPoint, Optional.of(mediaParts), authentication);
    }

    /**
     * Deletes a data point.
     *
     * @param id the identifier of the data point to delete
     */
    // only allow clients with delete scope to delete data points
    @PreAuthorize(
            "#oauth2.clientHasRole('" + CLIENT_ROLE + "') and #oauth2.hasScope('" + DATA_POINT_DELETE_SCOPE + "')")
    @RequestMapping(value = "/dataPoints/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDataPoint(@PathVariable String id, Authentication authentication) {

        String endUserId = getEndUserId(authentication);

        // only delete the data point if it belongs to the user associated with the access token
        Long dataPointsDeleted = dataPointService.deleteByIdAndUserId(id, endUserId);

        return new ResponseEntity<>(dataPointsDeleted == 0 ? NOT_FOUND : OK);
    }
}
