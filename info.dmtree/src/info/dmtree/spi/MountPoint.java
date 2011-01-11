package info.dmtree.spi;

import java.util.Dictionary;

/**
 * This interface can be implemented to represent a single mount point.
 * <p>
 * It provides functionalites to get the absolute mounted uri and a shortcut
 * method to post events via the DmtAdmin.
 * 
 */
public interface MountPoint {

	/**
	 * Provides the absolute mount path of this <code>MountPoint</code>
	 * 
	 * @return the absolute mount path of this <code>MountPoint</code>
	 */
	String[] getMountPath();

	/**
	 * Posts an event via the DmtAdmin about changes in the current plugins
	 * subtree.
	 * <p>
	 * This method distributes Events asynchronously to the EventAdmin as well
	 * as to matching local DmtEventListeners (except
	 * SynchronousDmtEventListeners).
	 * 
	 * @param topic
	 *            the topic of the event to send. Valid values are:
	 *            <ul>
	 *            <li><code>info/dmtree/DmtEvent/ADDED</code> if the change was
	 *            caused by a rename action
	 *            <p>
	 *            <li><code> info/dmtree/DmtEvent/DELETED</code> if the change
	 *            was caused by a copy action
	 *            <p>
	 *            <li><code> info/dmtree/DmtEvent/REPLACED</code> if the change
	 *            was caused by a copy action
	 *            </ul>
	 * @param relativeURIs
	 *            an array of affected node <code>URI</code>'s. All
	 *            <code>URI</code>'s specified here are relative to the current
	 *            <code>MountPoint</code>'s mountPath. The value of this
	 *            parameter determines the value of the event property
	 *            <codes>EVENT_PROPERTY_NODES</codes>. An empty array or
	 *            <code>null</code> is permitted. In both cases the value of the
	 *            events <codes>EVENT_PROPERTY_NODES</codes> property will be
	 *            set to an empty array.
	 * 
	 * @param properties
	 *            an optional parameter that can be provided to add properties
	 *            to the Event that is going to be send by the DMTAdmin. If the
	 *            properties contain a key <codes>EVENT_PROPERTY_NODES</codes>,
	 *            then the value of this property is ignored and will be
	 *            overwritten by <code>relativeURIs</code>.
	 * @throws NullPointerException
	 *             if the topic is null
	 * @throws IllegalArgumentException
	 *             if the topic has not one of the defined values
	 * 
	 */
	void postEvent(String topic, String[] relativeURIs, Dictionary properties);

	/**
	 * Posts an event via the DmtAdmin about changes in the current plugins
	 * subtree.
	 * <p>
	 * This method distributes Events asynchronously to the EventAdmin as well
	 * as to matching local DmtEventListeners (except
	 * SynchronousDmtEventListeners).
	 * 
	 * @param topic
	 *            the topic of the event to send. Valid values are:
	 *            <ul>
	 *            <li><code>info/dmtree/DmtEvent/RENAMED</code> if the change
	 *            was caused by a rename action
	 *            <p>
	 *            <li><code> info/dmtree/DmtEvent/COPIED</code> if the change
	 *            was caused by a copy action
	 *            </ul>
	 * @param relativeURIs
	 *            an array of affected node <code>URI</code>'s.
	 *            <p>
	 *            All <code>URI</code>'s specified here are relative to the
	 *            current <code>MountPoint</code>'s mountPath. The value of this
	 *            parameter determines the value of the event property
	 *            <codes>EVENT_PROPERTY_NODES</codes>. An empty array or
	 *            <code>null</code> is permitted. In both cases the value of the
	 *            events <codes>EVENT_PROPERTY_NODES</codes> property will be
	 *            set to an empty array.
	 * @param newRelativeURIs
	 *            an array of affected node <code>URI</code>'s.The value of this
	 *            parameter determines the value of the event property
	 *            <codes>EVENT_PROPERTY_NEW_NODES</codes>. An empty array or
	 *            <code>null</code> is permitted. In both cases the value of the
	 *            events <codes>EVENT_PROPERTY_NEW_NODES</codes> property will
	 *            be set to an empty array.
	 * @param properties
	 *            an optional parameter that can be provided to add properties
	 *            to the Event that is going to be send by the DMTAdmin. If the
	 *            properties contain the keys
	 *            <codes>EVENT_PROPERTY_NODES</codes> or
	 *            <codes>EVENT_PROPERTY_NEW_NODES</codes>, then the values of
	 *            these properties are ignored and will be overwritten by
	 *            <code>relativeURIs</code> and <code>newRelativeURIs</code>.
	 * @throws NullPointerException
	 *             if the topic is null
	 * @throws IllegalArgumentException
	 *             if the topic has not one of the defined values
	 */
	void postEvent(String topic, String[] relativeURIs,
			String[] newRelativeURIs, Dictionary properties);

	/**
	 * Sends an event via the DmtAdmin to notify listeners synchronously about a
	 * potentially destructive operation.
	 * <p>
	 * This method distributes Events synchronously to the EventAdmin as well as
	 * to matching local SynchronousDmtEventListeners (not to DmtEventListeners)
	 * 
	 * @param topic
	 *            the topic of the event to send. Valid values are:
	 *            <ul>
	 *            <li><code>info/dmtree/DmtEvent/DESTRUCTIVE_OPERATION</code> if
	 *            the notification is about a destructive operation that causes
	 *            a potential change of the system state
	 *            <p>
	 *            </ul>
	 * @param relativeURIs
	 *            an array of affected node <code>URI</code>'s. All
	 *            <code>URI</code>'s specified here are relative to the current
	 *            <code>MountPoint</code>'s mountPath. The value of this
	 *            parameter determines the value of the event property
	 *            <codes>EVENT_PROPERTY_NODES</codes>. An empty array or
	 *            <code>null</code> is permitted. In both cases the value of the
	 *            events <codes>EVENT_PROPERTY_NODES</codes> property will be
	 *            set to an empty array.
	 * @param properties
	 *            an optional parameter that can be provided to add properties
	 *            to the Event that is going to be send by the DMTAdmin. If the
	 *            properties contain a key <codes>EVENT_PROPERTY_NODES</codes>,
	 *            then the value of this property is ignored and will be
	 *            overwritten by <code>relativeURIs</code>.
	 * @throws NullPointerException
	 *             if the topic is null
	 * @throws IllegalArgumentException
	 *             if the topic has not the defined value
	 */
	void sendEvent(String topic, String[] relativeURIs, Dictionary properties);

}
