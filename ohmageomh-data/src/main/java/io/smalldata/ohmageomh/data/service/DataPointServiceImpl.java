/*
 * Copyright 2014 Open mHealth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.smalldata.ohmageomh.data.service;

import io.smalldata.ohmageomh.data.domain.DataPointSearchCriteria;
import org.openmhealth.dsu.repository.DataPointRepository;
import org.openmhealth.schema.domain.omh.DataPoint;
import org.openmhealth.schema.domain.omh.DataPointHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


/**
 * @author Emerson Farrugia
 */
@Service
public class DataPointServiceImpl implements DataPointService {

    @Autowired
    private DataPointRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String id) {

        checkNotNull(id);
        checkArgument(!id.isEmpty());

        return repository.exists(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DataPoint> findOne(String id) {

        checkNotNull(id);
        checkArgument(!id.isEmpty());

        return repository.findOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<DataPoint> findBySearchCriteria(DataPointSearchCriteria searchCriteria, @Nullable Integer offset,
            @Nullable Integer limit) {

        checkNotNull(searchCriteria);
        checkArgument(offset == null || offset >= 0);
        checkArgument(limit == null || limit >= 0);

        return repository.findBySearchCriteria(searchCriteria, offset, limit);
    }

    @Override
    @Transactional
    public DataPoint save(DataPoint dataPoint) {

        checkNotNull(dataPoint);

        return repository.save(dataPoint);
    }

    @Override
    @Transactional
    public Iterable<DataPoint> save(Iterable<DataPoint> dataPoints) {

        checkNotNull(dataPoints);

        return repository.save(dataPoints);
    }

    @Override
    @Transactional
    public void delete(String id) {

        checkNotNull(id);
        checkArgument(!id.isEmpty());

        repository.delete(id);
    }

    @Override
    @Transactional
    public Long deleteByIdAndUserId(String id, String userId) {

        checkNotNull(id);
        checkArgument(!id.isEmpty());
        checkNotNull(userId);
        checkArgument(!userId.isEmpty());

        return repository.deleteByIdAndHeaderUserId(id, userId);
    }

    @Override
    public void setUserId(DataPointHeader header, String endUserId) {
        // this is currently implemented using reflection, until we see other use cases where mutability would be useful
        try {
            Field userIdField = header.getClass().getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(header, endUserId);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("A user identifier property can't be changed in the data point header.", e);
        }
    }
}
