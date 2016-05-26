package io.smalldata.ohmageomh.data.domain;

/**
 * Represents the date of the last stored data point for the user.
 *
 * @author Jared Sieling.
 */
public class LastDataPointDate {

    private String userId;
    private String date;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
