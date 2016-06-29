package io.smalldata.ohmageomh.repository;

import io.smalldata.ohmageomh.domain.DataType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the DataType entity.
 */
public interface DataTypeRepository extends JpaRepository<DataType,Long> {

}
