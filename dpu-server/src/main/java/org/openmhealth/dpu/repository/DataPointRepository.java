package org.openmhealth.dpu.repository;

import org.openmhealth.schema.domain.omh.DataPoint;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * @author Jared Sieling.
 */
public interface DataPointRepository extends Repository<DataPoint, String> {
    boolean exists(String id);

    Optional<DataPoint> findOne(String id);

    DataPoint save(DataPoint dataPoint);

    Iterable<DataPoint> save(Iterable<DataPoint> dataPoints);

    void delete(String id);
}
