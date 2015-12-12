package org.openmhealth.dsu.domain.ohmage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GPS Coordinates object (unlike Location as it does not have accuracy and time)
 * Created by changun on 12/12/15.
 */
public class Coordinates {
    final Number latitude, longitude;

    @JsonCreator
    public Coordinates(
            @JsonProperty(value = Location.JSON_KEY_LATITUDE, required = true) final Number latitude,
            @JsonProperty(value = Location.JSON_KEY_LONGITUDE, required = true) final Number longitude
    ) {
        this.latitude = latitude;
        this.longitude = longitude;

    }
}
