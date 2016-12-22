package io.smalldata.ohmageomh.dpu.util;

import io.smalldata.ohmageomh.data.domain.EndUser;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * (Description here)
 *
 * @author Jared Sieling.
 */
public class ItemDTO {

    private Map<String, Object> extrasMap = new HashMap<>();
    private EndUser user;
    private LocalDate startDate;
    private LocalDate endDate;

    public ItemDTO() {}

    public ItemDTO(EndUser user) {
        this.user = user;
    }

    public EndUser getUser() {
        return user;
    }

    public void setUser(EndUser user) {
        this.user = user;
    }

    // Returns previous object associated with key, or null if empty.
    public Object setExtra(String key, Object extra) {
        return extrasMap.put(key, extra);
    }

    public Object getExtra(String key) {
        return extrasMap.get(key);
    }

    public void setStartDate(LocalDate date) {
        this.startDate = date;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setEndDate(LocalDate date) {
        this.endDate = date;
    }

    public LocalDate getEndDate() {
        return endDate;
    }





}
