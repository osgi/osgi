/*
 * $Header: /cvshome/repository/org/osgi/service/upnp/UPnPLocalStateVariable.java,
 * v 1.20 2003/12/30 
 *
 * Copyright (c) The Open Services Gateway Initiative (2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */


/**
 * To keep the current values getting from subscribed UPnPDevices. 
 * 
 * The actual values of the UPnPStateVaraible are passed as Java object type. 
 **/

package org.osgi.service.upnp;

public interface UPnPLocalStateVariable extends UPnPStateVariable {
	
   /**
   * This method will keep the current values of UPnPStateVariables of UPnPDevice  
   * whenever UPnPStateVariable's value is changed , this method must be called. 
   *
   * @return <tt>Object</tt> current value of UPnPStateVariable.
   *  if the current value is initialized with the default value defined UPnP service description.
   */
	public Object getCurrentValue();
}
