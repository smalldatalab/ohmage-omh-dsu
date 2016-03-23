package org.openmhealth.dpu.processor

import groovy.json.JsonSlurper
import org.openmhealth.dpu.service.OmhShimService
import org.openmhealth.dpu.util.DataPoint
import org.openmhealth.dpu.util.EndUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

import javax.inject.Inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component("syncFitbitProcessor")
public class SyncFitbitProcessor implements ItemProcessor<EndUser, List<DataPoint>> {

    private static final Logger log = LoggerFactory.getLogger(SyncFitbitProcessor.class);

    @Inject
    private OmhShimService omhShimService;

    @Override
    public List<DataPoint> process(EndUser user) throws Exception {

        // Set date range for past calendar week, ending Sunday
        LocalDate now = LocalDate.now()
//        LocalDate now = LocalDate.parse('2016-02-03', DateTimeFormatter.ISO_LOCAL_DATE)
        LocalDate endDate = now.minusDays(now.getDayOfWeek().getValue())
        LocalDate startDate = endDate.minusDays(6)

        // Fetch data
        String responseString = omhShimService.getData(user, "fitbit", "activity", false, startDate, endDate);

        JsonSlurper slurper = new JsonSlurper();
        def root = slurper.parseText(responseString);

        root.body?.each {
            def item = it.result.content.summary
        }


        List<DataPoint> dataPoints = new ArrayList<DataPoint>();

        DataPoint result = new DataPoint();
        dataPoints.add(result);
        log.info("=== Syncing Fitbit data now for user: " + user.getUsername())
        return dataPoints;
    }
}