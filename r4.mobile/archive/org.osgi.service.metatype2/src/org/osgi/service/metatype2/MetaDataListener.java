/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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
package org.osgi.service.metatype2;


/**
 * MetaDataListeners are registered as OSGi Services. 
 * The {@link MetaDataService} is responsible for tracking these services and 
 * notifying them when a MetaType has been added, removed or modified.
 * <p>
 * A <code>MetaDataListener</code> can narrow the MetaTypes for which events 
 * will be received by including in its service registration properties a 
 * filter under the key {@link #METATYPE_FILTER}. The value of this property should be a 
 * <code>String</code> representing LDAP filtering expression. 
 * The properties, which may be used in the LDAP filter are 
 * {@link MetaDataService#METATYPE_CATEGORY} and 
 * {@link org.osgi.framework.Constants#SERVICE_PID} (for the MetaType ID).
 * <br>
 * The listener will be notified only for changes in MetaTypes which category 
 * and ID satisfy this filter.
 * <br>
 * If such property is omitted the listener will receive events 
 * for all MetaTypes.
 *  
 * @version $Revision$
 */
public interface MetaDataListener {
  
  /**
   * <code>MetaDataListeners</code> may specify a LDAP filter under this key 
   * in their service registration properties to limit the 
   * MetaTypes for which to receive events.
   * <br>
   * The value of this property must be a <code>String</code> representing a 
   * valid LDAP filter. 
   */
  public static final String METATYPE_FILTER = "org.osgi.metatype.filter";
  
  /**
   * Event type which signals that a new MetaType is available.
   */
  public static final int ADDED = 0;
  
  /**
   * Event type which signals that the corresponding MetaType is no move available.
   */
  public static final int REMOVED = 1;
  
  /**
   * Event type which signals that the corresponding MetaType was modified.
   */
  public static final int MODIFIED = 2;

  
  /**
   * Receive a MetaType event. 
   * 
   * @param category The category of the MetaType for which event is 
   * received or null if it has no category.
   * @param id The ID of the MetaType for which event is received.
   * @param eventType the event type. Possible values are {@link #ADDED},
   * {@link #REMOVED}, {@link #MODIFIED}.
   */
  public void metaDataChanged(String category, String id, int eventType);
}

