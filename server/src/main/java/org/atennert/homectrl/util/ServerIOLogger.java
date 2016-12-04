/*******************************************************************************
 * Copyright 2016 Andreas Tennert
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.atennert.homectrl.util;

import org.atennert.homectrl.registration.DataDescription;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is used to log incoming and outgoing data to a database.
 */
public class ServerIOLogger implements EventObserver
{
    private static final String INSERT_HOST_STATEMENT =
            "INSERT INTO hosts (host_name, device_ref) " +
                    "VALUES (?, ?)";

    private static final String INSERT_EVENT_STATEMENT =
            "INSERT INTO events (host, value) " +
                    "VALUES ( (SELECT host_id FROM hosts " +
                    "WHERE host_name = ? and device_ref = ?), ?)";

    private JdbcTemplate sqliteAccess;
    private Set<DataDescription> dataDescriptions;

    @Required
    public void setDataSource( DataSource dataSource )
    {
        this.sqliteAccess = new JdbcTemplate( dataSource );
    }

    @Required
    public void setDataDescriptions( Set<DataDescription> dataDescriptions )
    {
        this.dataDescriptions = dataDescriptions;
    }

    public void init()
    {
        List<Object[]> hostData = dataDescriptions.stream()
                .map( d -> new Object[]{d.hostName, d.referenceId} )
                .collect( Collectors.toList() );

        sqliteAccess.batchUpdate( INSERT_HOST_STATEMENT, hostData );
    }

    @Override
    public void notify( DataDescription description, Object value )
    {
        sqliteAccess.update( INSERT_EVENT_STATEMENT, description.hostName, description.referenceId, value );
    }
}
