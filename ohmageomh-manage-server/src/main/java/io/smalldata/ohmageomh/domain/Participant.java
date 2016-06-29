package io.smalldata.ohmageomh.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Participant.
 */
@Entity
@Table(name = "participant")
@Document(indexName = "participant")
public class Participant extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "dsu_id", length = 255, nullable = false)
    private String dsuId;

    @Size(max = 255)
    @Column(name = "label", length = 255)
    private String label;

    @ManyToMany
    @JoinTable(name = "participant_study",
               joinColumns = @JoinColumn(name="participants_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="studies_id", referencedColumnName="ID"))
    private Set<Study> studies = new HashSet<>();

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

    public Set<Study> getStudies() {
        return studies;
    }

    public void setStudies(Set<Study> studies) {
        this.studies = studies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Participant participant = (Participant) o;
        if(participant.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, participant.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Participant{" +
            "id=" + id +
            ", dsuId='" + dsuId + "'" +
            ", label='" + label + "'" +
            '}';
    }
}
