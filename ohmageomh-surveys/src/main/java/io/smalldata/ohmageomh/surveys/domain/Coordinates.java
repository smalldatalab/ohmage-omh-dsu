package io.smalldata.ohmageomh.surveys.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GPS Coordinates object (unlike Location as it does not have accuracy and time)
 * Created by changun on 12/12/15.
 */
public class Coordinates {
    @JsonProperty(Location.JSON_KEY_LATITUDE)  private final double latitude;
    @JsonProperty(Location.JSON_KEY_LONGITUDE) private final double longitude;

    @JsonCreator
    public Coordinates(
            @JsonProperty(value = Location.JSON_KEY_LATITUDE, required = true) final double latitude,
            @JsonProperty(value = Location.JSON_KEY_LONGITUDE, required = true) final double longitude
    ) {
        this.latitude = latitude;
        this.longitude = longitude;

    }
}
