/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2001, 2002).
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

package org.osgi.service.cm;
import java.util.*;

/**
 * A service that can receive configuration data from a
 * Configuration Admin service.
 *
 * <p>A Managed Service is a service that needs
 * configuration data. Such an object should be registered
 * with the Framework registry with the <tt>service.pid</tt> property set to
 * some unique identitifier called a PID.
 *
 * <p>If the Configuration Admin service has a <tt>Configuration</tt> object corresponding
 * to this PID, it will callback the <tt>updated()</tt> method of the <tt>ManagedService</tt> object,
 * passing the properties of that <tt>Configuration</tt> object.
 *
 * <p>If it has no such <tt>Configuration</tt> object, then it calls
 * back with a <tt>null</tt> properties argument.  Registering a
 * Managed Service will always result in a callback to the <tt>updated()</tt> method
 * provided the Configuration Admin service is, or becomes active.  This
 * callback must always be done asynchronously.
 *
 * <p>Else, every time that either of the <tt>updated()</tt> methods is called
 * on that <tt>Configuration</tt> object, the <tt>ManagedService.updated()</tt> method with the
 * new properties is called. If the <tt>delete()</tt> method is
 * called on that <tt>Configuration</tt> object, <tt>ManagedService.updated()</tt> is called with
 * a <tt>null</tt> for the properties parameter.  All these callbacks must be done asynchronously.
 *
 * <p>The following example shows the code of a serial port that
 * will create a port depending on configuration information.
 * <pre>
 * class SerialPort implements ManagedService {
 *
 *   ServiceRegistration registration;
 *   Hashtable configuration;
 *   CommPortIdentifier id;
 *
 *   synchronized void open(CommPortIdentifier id,
 *   BundleContext context) {
 *     this.id = id;
 *     registration = context.registerService(
 *       ManagedService.class.getName(),
 *       this,
 *       null // Properties will come from CM in updated
 *     );
 *   }
 *
 *   Hashtable getDefaults() {
 *     Hashtable defaults = new Hashtable();
 *     defaults.put( "port", id.getName() );
 *     defaults.put( "product", "unknown" );
 *     defaults.put( "baud", "9600" );
 *     defaults.put( Constants.SERVICE_PID,
 *       "com.acme.serialport." + id.getName() );
 *     return defaults;
 *   }
 *
 *   public synchronized void updated(
 *     Dictionary configuration  ) {
 *     if ( configuration == <tt>null</tt> )
 *       registration.setProperties( getDefaults() );
 *     else {
 *       setSpeed( configuration.get("baud") );
 *       registration.setProperties( configuration );
 *     }
 *   }
 *   ...
 * }
 * </pre>
 * <p>As a convention, it is recommended that when a Managed Service
 * is updated, it should copy all the properties it does not
 * recognize into the service registration properties. This will
 * allow the Configuration Admin service to set properties on services which
 * can then be used by other applications.
 *
 * @version $Revision$
*/

public interface ManagedService {
    /**
     * Update the configuration for a Managed Service.
     *
     * <p>When the implementation of <tt>updated(Dictionary)</tt> detects any kind of
     * error in the configuration properties, it should create a
     * new <tt>ConfigurationException</tt> which describes the problem.  This
     * can allow a management system to provide useful information to
     * a human administrator.
     *
     * <p>If this method throws any other <tt>Exception</tt>, the
     * Configuration Admin service must catch it and should log it.
     * <p> The Configuration Admin service must call this method asynchronously which initiated the callback. This
     * implies that implementors of Managed Service can be assured
     * that the callback will not take place during registration
     * when they execute the registration in a synchronized method.
     *
     * @param properties A copy of the Configuration properties, or <tt>null</tt>.
     * This argument must not contain the
     * "service.bundleLocation" property. The value of this property
     * may be obtained from the <tt>Configuration.getBundleLocation</tt> method.
     * @throws ConfigurationException when the update fails
    */
    void updated( Dictionary properties  ) throws ConfigurationException;
}


