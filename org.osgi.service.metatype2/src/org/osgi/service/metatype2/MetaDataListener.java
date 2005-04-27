/*
* $Header$
* 
* Copyright (c) OSGi Alliance (2004). All Rights Reserved.
* 
* Implementation of certain elements of the OSGi Specification may be subject
* to third party intellectual property rights, including without limitation,
* patent rights (such a third party may or may not be a member of the OSGi
* Alliance). The OSGi Alliance is not responsible and shall not be held
* responsible in any manner for identifying or failing to identify any or all
* such third party intellectual property rights.
* 
* This document and the information contained herein are provided on an "AS IS"
* basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
* HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
* MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
* OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
* USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
* EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
* CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
* ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
* 
* All Company, brand and product names may be trademarks that are the sole
* property of their respective owners. All rights reserved.
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

