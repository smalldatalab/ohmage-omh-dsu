package org.openmhealth.dpu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.openmhealth.dsu.domain.EndUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * (Description here)
 *
 * @author Jared Sieling.
 */
@Service
public class OmhShimService {

    private static final Logger log = LoggerFactory.getLogger(OmhShimService.class);

    private
    @Value("${omh.shim.fakedata}") Boolean useFakeData = false;

    private
    @Value("${omh.shim.url}") String shimUrl;

    @Autowired
    ObjectMapper mapper;

    public String getData(EndUser user, String shim, String endpoint, Boolean normalize, LocalDate startDate, LocalDate endDate) {
        if (!useFakeData) {
            HttpClient client = HttpClients.createDefault();
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(shimUrl)
                    .pathSegment("data", shim, endpoint)
                    .queryParam("username", user.getUsername())
                    .queryParam("normalize", normalize.toString())
                    .queryParam("dateStart", startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .queryParam("dateEnd", endDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

            URI shimDataUrl = uriBuilder.build().toUri();

            HttpGet shimRequest = new HttpGet(shimDataUrl);
            shimRequest.setHeader("Accept", "application/json");

            try {
                HttpResponse response = client.execute(shimRequest);
                String responseString = new BasicResponseHandler().handleResponse(response);

                return responseString;
            } catch (Exception ex) {
                return null;
            }

        } else {
            File file = new File("sample-data/omh-" + shim + "-" + endpoint + "-" + normalize.toString() + ".json");
            try {
                BufferedReader bR = new BufferedReader(  new FileReader(file));
                String responseString = bR.lines().collect(Collectors.joining(""));
                return responseString;
            } catch (Exception ex) {
                return null;
            }
        }
    }



}
