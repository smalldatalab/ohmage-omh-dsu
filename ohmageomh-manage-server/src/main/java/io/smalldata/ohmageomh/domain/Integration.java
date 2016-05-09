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
 * A Integration.
 */
@Entity
@Table(name = "integration")
@Document(indexName = "integration")
public class Integration extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @ManyToMany
    @JoinTable(name = "integration_data_type",
               joinColumns = @JoinColumn(name="integrations_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="data_types_id", referencedColumnName="ID"))
    private Set<DataType> dataTypes = new HashSet<>();

    @ManyToMany(mappedBy = "integrations")
    @JsonIgnore
    private Set<Study> studies = new HashSet<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<DataType> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(Set<DataType> dataTypes) {
        this.dataTypes = dataTypes;
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
        Integration integration = (Integration) o;
        if(integration.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, integration.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Integration{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            '}';
    }
}
