/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.cm;

import java.util.*;

/**
 * A service that can receive configuration data from a Configuration Admin
 * service.
 * 
 * <p>
 * A Managed Service is a service that needs configuration data. Such an object
 * should be registered with the Framework registry with the
 * <code>service.pid</code> property set to some unique identitifier called a PID.
 * 
 * <p>
 * If the Configuration Admin service has a <code>Configuration</code> object
 * corresponding to this PID, it will callback the <code>updated()</code> method
 * of the <code>ManagedService</code> object, passing the properties of that
 * <code>Configuration</code> object.
 * 
 * <p>
 * If it has no such <code>Configuration</code> object, then it calls back with a
 * <code>null</code> properties argument. Registering a Managed Service will
 * always result in a callback to the <code>updated()</code> method provided the
 * Configuration Admin service is, or becomes active. This callback must always
 * be done asynchronously.
 * 
 * <p>
 * Else, every time that either of the <code>updated()</code> methods is called on
 * that <code>Configuration</code> object, the <code>ManagedService.updated()</code>
 * method with the new properties is called. If the <code>delete()</code> method
 * is called on that <code>Configuration</code> object,
 * <code>ManagedService.updated()</code> is called with a <code>null</code> for the
 * properties parameter. All these callbacks must be done asynchronously.
 * 
 * <p>
 * The following example shows the code of a serial port that will create a port
 * depending on configuration information.
 * 
 * <pre>
 * 
 *  class SerialPort implements ManagedService {
 * 
 *    ServiceRegistration registration;
 *    Hashtable configuration;
 *    CommPortIdentifier id;
 * 
 *    synchronized void open(CommPortIdentifier id,
 *    BundleContext context) {
 *      this.id = id;
 *      registration = context.registerService(
 *        ManagedService.class.getName(),
 *        this,
 *        null // Properties will come from CM in updated
 *      );
 *    }
 * 
 *    Hashtable getDefaults() {
 *      Hashtable defaults = new Hashtable();
 *      defaults.put( &quot;port&quot;, id.getName() );
 *      defaults.put( &quot;product&quot;, &quot;unknown&quot; );
 *      defaults.put( &quot;baud&quot;, &quot;9600&quot; );
 *      defaults.put( Constants.SERVICE_PID,
 *        &quot;com.acme.serialport.&quot; + id.getName() );
 *      return defaults;
 *    }
 * 
 *    public synchronized void updated(
 *      Dictionary configuration  ) {
 *      if ( configuration == 
 * <code>
 * null
 * </code>
 *  )
 *        registration.setProperties( getDefaults() );
 *      else {
 *        setSpeed( configuration.get(&quot;baud&quot;) );
 *        registration.setProperties( configuration );
 *      }
 *    }
 *    ...
 *  }
 *  
 * </pre>
 * 
 * <p>
 * As a convention, it is recommended that when a Managed Service is updated, it
 * should copy all the properties it does not recognize into the service
 * registration properties. This will allow the Configuration Admin service to
 * set properties on services which can then be used by other applications.
 * 
 * @version $Revision$
 */
public interface ManagedService {
	/**
	 * Update the configuration for a Managed Service.
	 * 
	 * <p>
	 * When the implementation of <code>updated(Dictionary)</code> detects any
	 * kind of error in the configuration properties, it should create a new
	 * <code>ConfigurationException</code> which describes the problem. This can
	 * allow a management system to provide useful information to a human
	 * administrator.
	 * 
	 * <p>
	 * If this method throws any other <code>Exception</code>, the Configuration
	 * Admin service must catch it and should log it.
	 * <p>
	 * The Configuration Admin service must call this method asynchronously
	 * which initiated the callback. This implies that implementors of Managed
	 * Service can be assured that the callback will not take place during
	 * registration when they execute the registration in a synchronized method.
	 * 
	 * @param properties A copy of the Configuration properties, or
	 *        <code>null</code>. This argument must not contain the
	 *        "service.bundleLocation" property. The value of this property may
	 *        be obtained from the <code>Configuration.getBundleLocation</code>
	 *        method.
	 * @throws ConfigurationException when the update fails
	 */
	void updated(Dictionary properties) throws ConfigurationException;
}
