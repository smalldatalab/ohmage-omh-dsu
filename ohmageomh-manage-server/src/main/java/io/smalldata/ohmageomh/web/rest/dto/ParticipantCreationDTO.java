package io.smalldata.ohmageomh.web.rest.dto;

import io.smalldata.ohmageomh.data.domain.EndUserRegistrationData;
import io.smalldata.ohmageomh.domain.Participant;

/**
 * A DTO to store the information needed to create a participant within a study.
 *
 * @author Jared Sieling.
 */
public class ParticipantCreationDTO {
    private Long id;
    private String dsuId;
    private String label;
    private String password;
    private String email;

    public ParticipantCreationDTO() {

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Participant getParticipant() {
        Participant participant = new Participant();
        participant.setDsuId(this.dsuId);
        participant.setLabel(this.label);
        return participant;
    }

    public EndUserRegistrationData getEndUserRegistrationData() {
        EndUserRegistrationData data = new EndUserRegistrationData();
        data.setUsername(this.dsuId);
        data.setPassword(this.password);
        data.setEmailAddress(this.email);
        return data;
    }

}
