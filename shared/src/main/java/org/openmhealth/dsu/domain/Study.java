package org.openmhealth.dsu.domain;

/**
 * A barebone model for a Study.
 * Created by Cheng-Kang Hsieh on 3/25/15.
 */
public class Study {
    Long id;
    String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Study(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    @Override
    public String toString() {
        return "Study{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
