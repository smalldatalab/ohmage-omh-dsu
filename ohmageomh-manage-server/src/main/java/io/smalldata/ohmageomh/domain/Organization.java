package io.smalldata.ohmageomh.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Organization.
 */
@Entity
@Table(name = "organization")
@Document(indexName = "organization")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 255)
    @Column(name = "name", length = 255)
    private String name;

    @ManyToMany
    @JoinTable(name = "organization_study",
               joinColumns = @JoinColumn(name="organizations_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="studies_id", referencedColumnName="ID"))
    private Set<Study> studies = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "organization_owner",
               joinColumns = @JoinColumn(name="organizations_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="owners_id", referencedColumnName="ID"))
    private Set<User> owners = new HashSet<>();

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

    public Set<Study> getStudies() {
        return studies;
    }

    public void setStudies(Set<Study> studies) {
        this.studies = studies;
    }

    public Set<User> getOwners() {
        return owners;
    }

    public void setOwners(Set<User> users) {
        this.owners = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Organization organization = (Organization) o;
        if(organization.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, organization.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Organization{" +
            "id=" + id +
            ", name='" + name + "'" +
            '}';
    }
}
