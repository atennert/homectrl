package de.atennert.homectrl.util;

import de.atennert.homectrl.registration.DataDescription;
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
