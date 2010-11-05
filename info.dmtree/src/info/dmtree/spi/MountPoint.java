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
	 * 
	 * @param topic
	 *            the topic of the event to send. Valid values are:
	 *            <ul>
	 *            <li><code>info/dmtree/DmtEvent/ADDED</code> if the change was
	 *            cause by a rename action
	 *            <p>
	 *            <li><code> info/dmtree/DmtEvent/DELETED</code> if the change
	 *            was cause by a copy action
	 *            <p>
	 *            <li><code> info/dmtree/DmtEvent/REPLACED</code> if the change
	 *            was cause by a copy action
	 *            </ul>
	 * @param relativeURIs
	 *            an array of affected node <code>URI</code>'s.
	 *            <p>
	 *            All <code>URI</code>'s specified here are relative to the
	 *            current <code>MountPoint</code>'s mountPath.
	 * @param properties
	 *            an optional parameter that can be provided to add properties
	 *            to the Event that is going to be send by the DMTAdmin
	 */
	void postEvent(String topic, String[] relativeURIs, Dictionary properties);

	/**
	 * Posts an event via the DmtAdmin about changes in the current plugins
	 * subtree.
	 * 
	 * @param topic
	 *            the topic of the event to send. Valid values are:
	 *            <ul>
	 *            <li><code>info/dmtree/DmtEvent/RENAMED</code> if the change
	 *            was cause by a rename action
	 *            <p>
	 *            <li><code> info/dmtree/DmtEvent/COPIED</code> if the change
	 *            was cause by a copy action
	 *            </ul>
	 * @param relativeURIs
	 *            an array of affected node <code>URI</code>'s.
	 *            <p>
	 *            All <code>URI</code>'s specified here are relative to the
	 *            current <code>MountPoint</code>'s mountPath.
	 * @param newRelativeURIs
	 *            an array of affected node <code>URI</code>'s.
	 *            <p>
	 *            This parameter can be null and will only be used if the topics
	 *            are either <code>info/dmtree/DmtEvent/RENAMED</code> or
	 *            <code>info/dmtree/DmtEvent/COPIED</code>
	 * @param properties
	 *            an optional parameter that can be provided to add properties
	 *            to the Event that is going to be send by the DMTAdmin
	 */
	void postEvent(String topic, String[] relativeURIs,
			String[] newRelativeURIs, Dictionary properties);
}
