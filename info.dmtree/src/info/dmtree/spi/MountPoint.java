/*
 * Copyright (c) OSGi Alliance (2010, 2011). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.dmtree.spi;

import java.util.Dictionary;

/**
 * This interface can be implemented to represent a single mount point.
 * <p>
 * It provides function to get the absolute mounted uri and a shortcut method to
 * post events via the DmtAdmin.
 * 
 * @remark Use map instead of dictionary
 * @version $Id$
 * @since 2.0
 */
public interface MountPoint {

	/**
	 * Provides the absolute mount path of this {@code MountPoint}
	 * 
	 * @return the absolute mount path of this {@code MountPoint}
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
	 *            <li>{@code info/dmtree/DmtEvent/ADDED} if the change was
	 *            caused by a rename action
	 *            <p>
	 *            <li>{@code  info/dmtree/DmtEvent/DELETED} if the change
	 *            was caused by a copy action
	 *            <p>
	 *            <li>{@code  info/dmtree/DmtEvent/REPLACED} if the change
	 *            was caused by a copy action
	 *            </ul>
	 * @param relativeURIs
	 *            an array of affected node {@code URI}'s. All
	 *            {@code URI}'s specified here are relative to the current
	 *            {@code MountPoint}'s mountPath. The value of this
	 *            parameter determines the value of the event property
	 *            {@code EVENT_PROPERTY_NODES}. An empty array or
	 *            {@code null} is permitted. In both cases the value of the
	 *            events {@code EVENT_PROPERTY_NODES} property will be
	 *            set to an empty array.
	 * 
	 * @param properties
	 *            an optional parameter that can be provided to add properties
	 *            to the Event that is going to be send by the DMTAdmin. If the
	 *            properties contain a key {@code EVENT_PROPERTY_NODES},
	 *            then the value of this property is ignored and will be
	 *            overwritten by {@code relativeURIs}.
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
	 *            <li>{@code info/dmtree/DmtEvent/RENAMED} if the change
	 *            was caused by a rename action
	 *            <p>
	 *            <li>{@code  info/dmtree/DmtEvent/COPIED} if the change
	 *            was caused by a copy action
	 *            </ul>
	 * @param relativeURIs
	 *            an array of affected node {@code URI}'s.
	 *            <p>
	 *            All {@code URI}'s specified here are relative to the
	 *            current {@code MountPoint}'s mountPath. The value of this
	 *            parameter determines the value of the event property
	 *            {@code EVENT_PROPERTY_NODES}. An empty array or
	 *            {@code null} is permitted. In both cases the value of the
	 *            events {@code EVENT_PROPERTY_NODES} property will be
	 *            set to an empty array.
	 * @param newRelativeURIs
	 *            an array of affected node {@code URI}'s.The value of this
	 *            parameter determines the value of the event property
	 *            {@code EVENT_PROPERTY_NEW_NODES}. An empty array or
	 *            {@code null} is permitted. In both cases the value of the
	 *            events {@code EVENT_PROPERTY_NEW_NODES} property will
	 *            be set to an empty array.
	 * @param properties
	 *            an optional parameter that can be provided to add properties
	 *            to the Event that is going to be send by the DMTAdmin. If the
	 *            properties contain the keys
	 *            {@code EVENT_PROPERTY_NODES} or
	 *            {@code EVENT_PROPERTY_NEW_NODES}, then the values of
	 *            these properties are ignored and will be overwritten by
	 *            {@code relativeURIs} and {@code newRelativeURIs}.
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
	 *            <li>{@code info/dmtree/DmtEvent/DESTRUCTIVE_OPERATION} if
	 *            the notification is about a destructive operation that causes
	 *            a potential change of the system state
	 *            <p>
	 *            </ul>
	 * @param relativeURIs
	 *            an array of affected node {@code URI}'s. All
	 *            {@code URI}'s specified here are relative to the current
	 *            {@code MountPoint}'s mountPath. The value of this
	 *            parameter determines the value of the event property
	 *            {@code EVENT_PROPERTY_NODES}. An empty array or
	 *            {@code null} is permitted. In both cases the value of the
	 *            events {@code EVENT_PROPERTY_NODES} property will be
	 *            set to an empty array.
	 * @param properties
	 *            an optional parameter that can be provided to add properties
	 *            to the Event that is going to be send by the DMTAdmin. If the
	 *            properties contain a key {@code EVENT_PROPERTY_NODES},
	 *            then the value of this property is ignored and will be
	 *            overwritten by {@code relativeURIs}.
	 * @throws NullPointerException
	 *             if the topic is null
	 * @throws IllegalArgumentException
	 *             if the topic has not the defined value
	 */
	void sendEvent(String topic, String[] relativeURIs, Dictionary properties);

}
