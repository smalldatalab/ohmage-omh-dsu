package org.openmhealth.dpu.reader;

import org.openmhealth.dpu.repository.EndUserRepository;
import org.openmhealth.dsu.domain.EndUser;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * (Description here)
 *
 * @author Jared Sieling.
 */
public class EndUserReader implements ItemReader<EndUser> {

    @Autowired
    private EndUserRepository endUserRepository;

    private List<EndUser> users = new ArrayList<>();
    private Iterator<EndUser> iterator;

    public EndUserReader() {
    }

    @PostConstruct
    void getUsers() {
        Optional<EndUser> user = endUserRepository.findOne("localguy");

        users.add(user.get());
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
