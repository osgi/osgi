/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
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
package org.osgi.impl.service.cu.admin;

import java.util.Dictionary;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.Filter;

/**
 * This class is used to hold all the data needed for given listener - 
 * {@link org.osgi.service.cu.HierarchyListener}, 
 * {@link org.osgi.service.cu.ControlUnitAdminListener} or
 * {@link org.osgi.service.cu.StateVariableListener}.
 * 
 * @version $Revision$
 */
class ListenerData {
  
  protected final Object listener;
  protected final ServiceReference reference;
  protected final Filter filter;
  
  /**
   * Constructor.
   * 
   * @param listener listener
   * @param reference service reference of the listener
   * @param filter filter of the listener
   */
  ListenerData(Object listener, ServiceReference reference, Filter filter) {
    this.listener = listener;
    this.reference = reference;
    this.filter = filter;
  }
  
  /**
   * Check if the given table matches the filter of the listener.
   * 
   * @param table table to be checked
   * @return true if the table matches the filter, false - otherwise
   */
  boolean isMatching(Dictionary table) {
    return filter == null || filter.match(table);
  }

}