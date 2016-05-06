package io.smalldata.ohmageomh.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Study.
 */
@Entity
@Table(name = "study")
@Document(indexName = "study")
public class Study implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @NotNull
    @Column(name = "remove_gps", nullable = false)
    private Boolean removeGps;

    @ManyToMany
    @JoinTable(name = "study_manager",
               joinColumns = @JoinColumn(name="studies_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="managers_id", referencedColumnName="ID"))
    private Set<User> managers = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "study_survey",
               joinColumns = @JoinColumn(name="studies_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="surveys_id", referencedColumnName="ID"))
    private Set<Survey> surveys = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "study_integration",
               joinColumns = @JoinColumn(name="studies_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="integrations_id", referencedColumnName="ID"))
    private Set<Integration> integrations = new HashSet<>();

    @ManyToMany(mappedBy = "studies")
    @JsonIgnore
    private Set<Participant> participants = new HashSet<>();

    @ManyToMany(mappedBy = "studies")
    @JsonIgnore
    private Set<Organization> organizations = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isRemoveGps() {
        return removeGps;
    }

    public void setRemoveGps(Boolean removeGps) {
        this.removeGps = removeGps;
    }

    public Set<User> getManagers() {
        return managers;
    }

    public void setManagers(Set<User> users) {
        this.managers = users;
    }

    public Set<Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(Set<Survey> surveys) {
        this.surveys = surveys;
    }

    public Set<Integration> getIntegrations() {
        return integrations;
    }

    public void setIntegrations(Set<Integration> integrations) {
        this.integrations = integrations;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    public Set<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Set<Organization> organizations) {
        this.organizations = organizations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Study study = (Study) o;
        if(study.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, study.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Study{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", removeGps='" + removeGps + "'" +
            '}';
    }
}
