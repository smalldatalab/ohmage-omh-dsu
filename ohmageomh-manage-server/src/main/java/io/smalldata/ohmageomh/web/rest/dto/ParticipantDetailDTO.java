package io.smalldata.ohmageomh.web.rest.dto;

import io.smalldata.ohmageomh.domain.Participant;

import java.util.HashMap;
import java.util.Map;

/**
 * A DTO representing a study participant, with data summary information.
 *
 * @author Jared Sieling.
 */
public class ParticipantDetailDTO {

    private Long id;
    private String dsuId;
    private String label;
    private Map<String, String> latestDataPoints;

    public ParticipantDetailDTO(Participant participant) {
        this.id = participant.getId();
        this.dsuId = participant.getDsuId();
        this.label = participant.getLabel();
        this.latestDataPoints = new HashMap<String, String>();
        latestDataPoints.put("Step Count", "2016-01-04");
        latestDataPoints.put("Physical Activity", "2016-03-05");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDsuId() {
        return dsuId;
    }

    public void setDsuId(String dsuId) {
        this.dsuId = dsuId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, String> getLatestDataPoints() {
        return latestDataPoints;
    }

    public void setLatestDataPoints(Map<String, String> latestDataPoints) {
        this.latestDataPoints = latestDataPoints;
    }
}
