package io.smalldata.ohmageomh.dpu.reader;

import org.openmhealth.dsu.domain.EndUser;
import org.springframework.batch.item.ItemReader;

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
        user.setUsername("localguy");
        users.add(user);
        iterator = users.iterator();
    }

    @Override
    public EndUser read() throws Exception {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            return null;
        }
    }
}
