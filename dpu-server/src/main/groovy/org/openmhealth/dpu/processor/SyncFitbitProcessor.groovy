package org.openmhealth.dpu.processor

import org.openmhealth.dpu.util.DataPoint
import org.openmhealth.dpu.util.EndUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@Component("syncFitbitProcessor")
public class SyncFitbitProcessor implements ItemProcessor<EndUser, List<DataPoint>> {

    private static final Logger log = LoggerFactory.getLogger(SyncFitbitProcessor.class);

    @Override
    public List<DataPoint> process(EndUser user) throws Exception {
        List<DataPoint> dataPoints = new ArrayList<DataPoint>();

        DataPoint result = new DataPoint();
        dataPoints.add(result);
        log.info("=== Syncing Fitbit data now for user: " + user.getUsername())
        return dataPoints;
    }
}