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
import java.util.Dictionary;
/**
 * Manage multiple service instances.
 *
 * Bundles registering this interface are giving the
 * Configuration Admin service the ability to create and configure a number of
 * instances of a service that the implementing bundle can
 * provide. For example, a bundle implementing a DHCP server could be
 * instantiated multiple times for different interfaces using a factory.
 *
 * <p>Each of these <i>service instances</i> is represented, in the persistent
 * storage of the Configuration Admin service, by a factory <tt>Configuration</tt> object that
 * has a PID. When such a <tt>Configuration</tt> is updated, the Configuration Admin service
 * calls the <tt>ManagedServiceFactory</tt> updated method with the new
 * properties. When <tt>updated</tt> is called with a new PID, the Managed Service Factory
 * should create a new factory instance based on these
 * configuration properties.  When called with a
 * PID that it has seen before, it should update that existing service
 * instance with the new configuration information.
 *
 * <p>In general it is expected that the implementation of this
 * interface will maintain a data structure that maps PIDs to the
 * factory instances that it has created. The semantics of a
 * factory instance are defined by the Managed Service Factory.
 * However, if the factory instance is registered as a service object with the
 * service registry, its PID should match the PID of the corresponding
 * <tt>Configuration</tt> object (but it should <b>not</b> be registered
 * as a Managed Service!).
 *
 * <p>An example that demonstrates the use of a factory. It will
 * create serial ports under command of the Configuration Admin service.
 * <pre>
 * class SerialPortFactory
 *   implements ManagedServiceFactory {
 *   ServiceRegistration registration;
 *   Hashtable ports;
 *   void start(BundleContext context) {
 *     Hashtable properties = new Hashtable();
 *     properties.put( Constants.SERVICE_PID,
 *       "com.acme.serialportfactory" );
 *     registration = context.registerService(
 *       ManagedServiceFactory.class.getName(),
 *       this,
 *       properties
 *     );
 *   }
 *   public void updated( String pid,
 *     Dictionary properties  ) {
 *     String portName = (String) properties.get("port");
 *     SerialPortService port =
 *       (SerialPort) ports.get( pid );
 *     if ( port == null ) {
 *       port = new SerialPortService();
 *       ports.put( pid, port );
 *       port.open();
 *     }
 *     if ( port.getPortName().equals(portName) )
 *       return;
 *     port.setPortName( portName );
 *   }
 *   public void deleted( String pid ) {
 *     SerialPortService port =
 *       (SerialPort) ports.get( pid );
 *     port.close();
 *     ports.remove( pid );
 *   }
 *   ...
 * }
 * </pre>
 *
 * @version $Revision$
*/
public interface ManagedServiceFactory {

    /**
     * Return a descriptive name of this factory.
     *
     * @return the name for the factory, which might be localized
    */
    String getName();

    /**
     * Create a new instance, or update the configuration of an
     * existing instance.
     *
     * If the PID of the <tt>Configuration</tt> object is new for the Managed Service Factory,
     * then create a new factory instance, using the configuration
     * <tt>properties</tt> provided. Else, update the service instance with the
     * provided <tt>properties</tt>.
     *
     * <p>If the factory instance is registered with the Framework, then
     * the configuration <tt>properties</tt> should be copied to its registry
     * properties. This is not mandatory and
     * security sensitive properties should obviously not be copied.
     *
     * <p>If this method throws any <tt>Exception</tt>, the
     * Configuration Admin service must catch it and should log it.
     *
     * <p>When the implementation of updated detects any kind of
     * error in the configuration properties, it should create a
     * new {@link ConfigurationException} which describes the problem.
     *
     * <p>The Configuration Admin service must call this method asynchronously. This implies that
     * implementors of the <tt>ManagedServiceFactory</tt> class can be assured that the
     * callback will not take place during registration when they
     * execute the registration in a synchronized method.
     *
     * @param pid The PID for this configuration.
     * @param properties A copy of the configuration properties.
     * This argument must not contain the
     * service.bundleLocation" property. The value of this property
     * may be obtained from the <tt>Configuration.getBundleLocation</tt> method.
     * @throws ConfigurationException when the configuration properties are invalid.
    */
    void updated( String pid, Dictionary properties  ) throws ConfigurationException;

    /**
     * Remove a factory instance.
     *
     * Remove the factory instance associated with the PID. If the instance was
     * registered with the service registry, it should be unregistered.
     * <p>If this method throws any <tt>Exception</tt>, the Configuration Admin
     * service must catch it and should log it.
     * <p> The Configuration Admin service must call this method asynchronously.
     *
     * @param pid the PID of the service to be removed
    */
    void deleted( String pid  );
}


