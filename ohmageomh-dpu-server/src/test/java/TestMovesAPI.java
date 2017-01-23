import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smalldata.ohmageomh.data.domain.DataPoint;
import io.smalldata.ohmageomh.dpu.config.Application;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmhealth.schema.domain.omh.DataPointHeader;
import org.openmhealth.schema.domain.omh.MobilityDailySummary;
import org.openmhealth.schema.domain.omh.StepCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

/**
 * To test the interface to the clojure Moves API.
 * Created by changun on 1/22/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes=Application.class,
        initializers=ConfigFileApplicationContextInitializer.class)
public class TestMovesAPI {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testDeserializeMobilitySummary() throws IOException {

        // a sample mobility daily summary. Note that the deserialization ignores many fields that are not being used
        // at the moment.
        JsonNode responseRoot = objectMapper.readTree("{\n" +
                "  \"body\": {\n" +
                "    \"active_time_in_seconds\": 64,\n" +
                "    \"walking_distance_in_km\": 0.032,\n" +
                "    \"date\": \"2015-12-08\",\n" +
                "    \"home\": {\n" +
                "      \"time_not_at_home\": {\n" +
                "        \"value\": 0,\n" +
                "        \"unit\": \"sec\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"walking_distance\": {\n" +
                "      \"value\": 0.032,\n" +
                "      \"unit\": \"km\"\n" +
                "    },\n" +
                "    \"zone\": \"-05:00\",\n" +
                "    \"leave_home_time\": null,\n" +
                "    \"max_gait_speed_in_meter_per_second\": null,\n" +
                "    \"return_home_time\": null,\n" +
                "    \"geodiameter_in_km\": 0,\n" +
                "    \"steps\": 54,\n" +
                "    \"coverage\": 0.3452007546383639,\n" +
                "    \"time_not_at_home_in_seconds\": 0,\n" +
                "    \"longest_trek\": {\n" +
                "      \"value\": 0.032,\n" +
                "      \"unit\": \"km\"\n" +
                "    },\n" +
                "    \"episodes\": [\n" +
                "      {\n" +
                "        \"raw-data\": {\n" +
                "          \"lastUpdate\": \"20151209T160552Z\",\n" +
                "          \"activities\": [\n" +
                "            {\n" +
                "              \"distance\": 1,\n" +
                "              \"activity\": \"walking\",\n" +
                "              \"manual\": false,\n" +
                "              \"duration\": 2,\n" +
                "              \"trackPoints\": [],\n" +
                "              \"steps\": 2,\n" +
                "              \"endTime\": \"20151209T001425-0500\",\n" +
                "              \"startTime\": \"20151209T001423-0500\",\n" +
                "              \"group\": \"walking\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"place\": {\n" +
                "            \"location\": {\n" +
                "              \"lon\": -73.32648,\n" +
                "              \"lat\": 40.713072\n" +
                "            },\n" +
                "            \"type\": \"unknown\",\n" +
                "            \"id\": 230388125\n" +
                "          },\n" +
                "          \"endTime\": \"20151209T095322-0500\",\n" +
                "          \"startTime\": \"20151208T154358-0500\",\n" +
                "          \"type\": \"place\"\n" +
                "        },\n" +
                "        \"cluster\": {\n" +
                "          \"longitude\": -73.32648,\n" +
                "          \"latitude\": 40.713072,\n" +
                "          \"first-time\": \"2015-12-08T15:43:58.000-05:00\",\n" +
                "          \"last-time\": \"2017-01-22T15:38:54.000-05:00\",\n" +
                "          \"total-time-in-minutes\": 198456,\n" +
                "          \"number-days\": 172,\n" +
                "          \"hourly-distribution\": [\n" +
                "            [\n" +
                "              0,\n" +
                "              151.67940250000007\n" +
                "            ],\n" +
                "            [\n" +
                "              1,\n" +
                "              151.54523555555562\n" +
                "            ],\n" +
                "            [\n" +
                "              2,\n" +
                "              151.7613469444445\n" +
                "            ],\n" +
                "            [\n" +
                "              3,\n" +
                "              150.99995805555562\n" +
                "            ],\n" +
                "            [\n" +
                "              4,\n" +
                "              150.2860694444445\n" +
                "            ],\n" +
                "            [\n" +
                "              5,\n" +
                "              149.51273638888898\n" +
                "            ],\n" +
                "            [\n" +
                "              6,\n" +
                "              148.7338483333334\n" +
                "            ],\n" +
                "            [\n" +
                "              7,\n" +
                "              145.99995944444456\n" +
                "            ],\n" +
                "            [\n" +
                "              8,\n" +
                "              142.19357250000013\n" +
                "            ],\n" +
                "            [\n" +
                "              9,\n" +
                "              131.14885388888908\n" +
                "            ],\n" +
                "            [\n" +
                "              10,\n" +
                "              122.04274472222241\n" +
                "            ],\n" +
                "            [\n" +
                "              11,\n" +
                "              107.80663972222236\n" +
                "            ],\n" +
                "            [\n" +
                "              12,\n" +
                "              104.62802472222235\n" +
                "            ],\n" +
                "            [\n" +
                "              13,\n" +
                "              117.0080236111113\n" +
                "            ],\n" +
                "            [\n" +
                "              14,\n" +
                "              115.76607805555577\n" +
                "            ],\n" +
                "            [\n" +
                "              15,\n" +
                "              119.06468750000018\n" +
                "            ],\n" +
                "            [\n" +
                "              16,\n" +
                "              133.89162777777798\n" +
                "            ],\n" +
                "            [\n" +
                "              17,\n" +
                "              139.89829500000016\n" +
                "            ],\n" +
                "            [\n" +
                "              18,\n" +
                "              135.0007958333335\n" +
                "            ],\n" +
                "            [\n" +
                "              19,\n" +
                "              136.73468305555576\n" +
                "            ],\n" +
                "            [\n" +
                "              20,\n" +
                "              144.9727366666668\n" +
                "            ],\n" +
                "            [\n" +
                "              21,\n" +
                "              150.64745694444454\n" +
                "            ],\n" +
                "            [\n" +
                "              22,\n" +
                "              155.05273500000004\n" +
                "            ],\n" +
                "            [\n" +
                "              23,\n" +
                "              152.79940222222228\n" +
                "            ]\n" +
                "          ]\n" +
                "        },\n" +
                "        \"home?\": true,\n" +
                "        \"end\": \"2015-12-08T23:59:59.999-05:00\",\n" +
                "        \"start\": \"2015-12-08T15:43:58.000-05:00\",\n" +
                "        \"inferred-state\": \"still\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"raw-data\": {\n" +
                "          \"distance\": 32,\n" +
                "          \"activity\": \"walking\",\n" +
                "          \"manual\": false,\n" +
                "          \"duration\": 64,\n" +
                "          \"trackPoints\": [],\n" +
                "          \"steps\": 54,\n" +
                "          \"endTime\": \"20151208T154521-0500\",\n" +
                "          \"startTime\": \"20151208T154417-0500\",\n" +
                "          \"group\": \"walking\"\n" +
                "        },\n" +
                "        \"distance\": 0.032,\n" +
                "        \"duration\": 64,\n" +
                "        \"end\": \"2015-12-08T15:45:21.000-05:00\",\n" +
                "        \"start\": \"2015-12-08T15:44:17.000-05:00\",\n" +
                "        \"inferred-state\": \"on_foot\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"step_count\": 54,\n" +
                "    \"active_time\": {\n" +
                "      \"value\": 64,\n" +
                "      \"unit\": \"sec\"\n" +
                "    },\n" +
                "    \"device\": \"moves-app\",\n" +
                "    \"geodiameter\": {\n" +
                "      \"value\": 0,\n" +
                "      \"unit\": \"km\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"header\": {\n" +
                "    \"acquisition_provenance\": {\n" +
                "      \"modality\": \"SENSED\",\n" +
                "      \"source_name\": \"moves-app\"\n" +
                "    },\n" +
                "    \"creation_date_time_epoch_milli\": 1449607521000,\n" +
                "    \"creation_date_time\": \"2015-12-08T15:45:21.000-05:00\",\n" +
                "    \"schema_id\": {\n" +
                "      \"version\": {\n" +
                "        \"minor\": 0,\n" +
                "        \"major\": 2\n" +
                "      },\n" +
                "      \"name\": \"mobility-daily-summary\",\n" +
                "      \"namespace\": \"cornell\"\n" +
                "    },\n" +
                "    \"user_id\": \"TEST_USER\",\n" +
                "    \"id\": \"cornell.mobility-daily-summary_v2.0_HSS_LC_111_moves-app_2015-12-08\"\n" +
                "  },\n" +
                "  \"_class\": \"org.openmhealth.dsu.domain.DataPoint\",\n" +
                "  \"_id\": \"cornell.mobility-daily-summary_v2.0_HSS_LC_111_moves-app_2015-12-08\"\n" +
                "}");

        DataPoint<MobilityDailySummary> dataPoint = new DataPoint<>(
                objectMapper.readValue(responseRoot.get("header").toString(), DataPointHeader.class),
                objectMapper.treeToValue(responseRoot.get("body"), MobilityDailySummary.class));


        // check the parsed data point
        DataPointHeader header = dataPoint.getHeader();
        MobilityDailySummary body = dataPoint.getBody();

        Assert.assertEquals(LocalDate.of(2015, 12, 8), header.getCreationDateTime().toLocalDate());
        Assert.assertEquals(2, header.getBodySchemaId().getVersion().getMajor());
        Assert.assertEquals(0, header.getBodySchemaId().getVersion().getMinor());
        Assert.assertEquals(64, body.getActiveTime().getValue().intValue());
        Assert.assertEquals(0.032, body.getWalkingDistance().getValue().floatValue(), 0.00000001);
        Assert.assertEquals(54, body.getStepCount().intValue());

    }
}
