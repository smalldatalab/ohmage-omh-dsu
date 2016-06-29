package io.smalldata.ohmageomh.data.domain;

import org.openmhealth.schema.domain.omh.DataPointHeader;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * We need this class only to accommodate the annotation @Document, since we have multiple datasources.
 *
 * @author Jared Sieling.
 */
@Document
public class DataPoint<T> extends org.openmhealth.schema.domain.omh.DataPoint<T> {
    public DataPoint(DataPointHeader header, T body) {
        super(header, body);
    }
}
