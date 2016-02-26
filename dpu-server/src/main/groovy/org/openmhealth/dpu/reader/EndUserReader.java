package org.openmhealth.dpu.reader;

import org.openmhealth.dpu.util.EndUser;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * (Description here)
 *
 * @author Jared Sieling.
 */
public class EndUserReader implements ItemReader<EndUser> {

    private List<EndUser> users = new ArrayList<>();
    private Iterator<EndUser> iterator;

    public EndUserReader() {
    }

    @PostConstruct
    void getUsers() {
        // Fake a user for now
        EndUser user = new EndUser();
        users.add(user);
        iterator = users.iterator();
    }

    @Override
    public EndUser read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }
}
