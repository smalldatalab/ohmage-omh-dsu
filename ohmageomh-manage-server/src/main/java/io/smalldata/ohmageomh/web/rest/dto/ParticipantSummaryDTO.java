package io.smalldata.ohmageomh.web.rest.dto;

import io.smalldata.ohmageomh.domain.Participant;

import java.util.HashMap;
import java.util.Map;

/**
 * A DTO representing a study participant, with data summary information.
 *
 * @author Jared Sieling.
 */
public class ParticipantSummaryDTO {

    private Long id;
    private String dsuId;
    private String label;
    private Map<String, String> latestDataPointDates;

    public ParticipantSummaryDTO(Participant participant) {
        this.id = participant.getId();
        this.dsuId = participant.getDsuId();
        this.label = participant.getLabel();
        this.latestDataPointDates = new HashMap<String, String>();
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

    public Map<String, String> getLatestDataPointDates() {
        return latestDataPointDates;
    }

    public void setLatestDataPointDates(Map<String, String> latestDataPointDates) {
        this.latestDataPointDates = latestDataPointDates;
    }

    public void addLatestDataPointDate(String dataTypeId, String date){
        this.latestDataPointDates.put(dataTypeId, date);
    }




}
