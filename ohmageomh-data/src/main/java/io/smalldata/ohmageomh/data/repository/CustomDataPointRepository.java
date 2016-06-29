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

package io.smalldata.ohmageomh.data.repository;

import io.smalldata.ohmageomh.data.domain.DataPoint;
import io.smalldata.ohmageomh.data.domain.DataPointSearchCriteria;
import io.smalldata.ohmageomh.data.domain.LastDataPointDate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;


/**
 * A set of data point repository methods not automatically implemented by Spring Data repositories.
 *
 * @author Emerson Farrugia
 */
public interface CustomDataPointRepository {

    Iterable<DataPoint> findBySearchCriteria(DataPointSearchCriteria searchCriteria, @Nullable Integer offset,
                                             @Nullable Integer limit);

    List<LastDataPointDate> findLastDataPointDate(List<String> userIds, DataPointSearchCriteria criteria, String dateField);
}
