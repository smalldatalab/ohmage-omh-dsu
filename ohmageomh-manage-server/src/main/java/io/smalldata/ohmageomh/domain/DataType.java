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
 * A DataType.
 */
@Entity
@Table(name = "data_type")
@Document(indexName = "datatype")
public class DataType implements Serializable {

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

    @Size(max = 255)
    @Column(name = "schema_namespace", length = 255)
    private String schemaNamespace;

    @Size(max = 255)
    @Column(name = "schema_name", length = 255)
    private String schemaName;

    @Size(max = 255)
    @Column(name = "schema_version", length = 255)
    private String schemaVersion;

    @Column(name = "csv_mapper")
    private String csvMapper;

    @ManyToMany(mappedBy = "dataTypes")
    @JsonIgnore
    private Set<Integration> integrations = new HashSet<>();

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

    public String getSchemaNamespace() {
        return schemaNamespace;
    }

    public void setSchemaNamespace(String schemaNamespace) {
        this.schemaNamespace = schemaNamespace;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getCsvMapper() {
        return csvMapper;
    }

    public void setCsvMapper(String csvMapper) {
        this.csvMapper = csvMapper;
    }

    public Set<Integration> getIntegrations() {
        return integrations;
    }

    public void setIntegrations(Set<Integration> integrations) {
        this.integrations = integrations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataType dataType = (DataType) o;
        if(dataType.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, dataType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DataType{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", schemaNamespace='" + schemaNamespace + "'" +
            ", schemaName='" + schemaName + "'" +
            ", schemaVersion='" + schemaVersion + "'" +
            ", csvMapper='" + csvMapper + "'" +
            '}';
    }
}
