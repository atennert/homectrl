package org.atennert.homectrl.registration;

import java.util.Set;

public interface IHostAddressBook
{
    /**
     * Returns the host information belonging to an deviceId.
     *
     * @param deviceId
     * @return
     */
    DataDescription getHostInformation( int deviceId );

    /**
     * Get the devices of a host, identified by the hosts address and the IDs of
     * the data fields / devices on the host.
     *
     * @param senderAddress
     *            address of the host node, that has send information
     * @param referenceIds
     *            data field IDs of devices on the host node
     * @return the homectrl device descriptions of all identified (registered)
     *         devices
     */
    Set<DataDescription> getHostDevices( String senderAddress, Set<String> referenceIds );
}
