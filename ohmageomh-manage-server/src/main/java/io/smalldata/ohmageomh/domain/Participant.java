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

    @Size(max = 255)
    @Column(name = "first_name", length = 255)
    private String firstName;

    @Size(max = 255)
    @Column(name = "last_name", length = 255)
    private String lastName;

    @Size(max = 255)
    @Column(name = "gmail", length = 255)
    private String gmail;

    @NotNull
    @Size(max = 255)
    @Column(name = "username", length = 255, nullable = false)
    private String username;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
            ", firstName='" + firstName + "'" +
            ", lastName='" + lastName + "'" +
            ", gmail='" + gmail + "'" +
            ", username='" + username + "'" +
            '}';
    }
}
