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
    private String lastDataPointDate;

    public ParticipantSummaryDTO(Participant participant) {
        this.id = participant.getId();
        this.dsuId = participant.getDsuId();
        this.label = participant.getLabel();
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

    public String getLastDataPointDate() {
        return lastDataPointDate;
    }

    public void setLastDataPointDate(String lastDataPointDate) {
        this.lastDataPointDate = lastDataPointDate;
    }

}
