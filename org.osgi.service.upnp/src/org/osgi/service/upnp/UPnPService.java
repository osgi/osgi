/*
 * $Header$
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

package org.osgi.service.upnp;

/**
 * A representation of a UPnP Service.
 *
 * Each UPnP device contains zero or more services.
 * The UPnP description for a service defines actions, their arguments,
 * and event characteristics.
 **/

public interface UPnPService {

    /**
     * Property key for the optional service type uri.
     *
     * The service type property is used when registering UPnP Device
     * services and UPnP Event Listener services. The property
     * contains a <tt>String</tt> array (<tt>String[]</tt>) of service types. A UPnP Device service
     * can thus announce what types of services it contains. A
     * UPnP Event Listener service can announce for what type of UPnP services it
     * wants notifications.
     * The service version is encoded in the type string as specified
     * in the UPnP specification.
     * A <tt>null</tt> value is a wildcard, matching
     * <b>all</b> service types. Value is "UPnP.service.type".
     * @see UPnPService#getType()
     **/
    String TYPE      = "UPnP.service.type";

     /**
     * Property key for the optional service id.
     *
     * The service id property is used when registering UPnP Device
     * services or UPnP Event Listener services. The value of the property
     * contains a <tt>String</tt> array (<tt>String[]</tt>) of service ids. A UPnP Device service
     * can thus announce what service ids it contains. A UPnP Event Listener service
     * can announce for what UPnP service ids it wants notifications.
     * A service id does <b>not</b> have to be universally unique.
     * It must be unique only within a device.
     * A <tt>null</tt> value is a wildcard, matching
     * <b>all</b> services. The value is "UPnP.service.id".
     **/
    String ID        = "UPnP.service.id";


  /**
   * Returns the <tt>serviceId</tt> field in the
   * UPnP service description.
   *
   *
   * <p>For standard services defined by a UPnP Forum working committee, the
   * serviceId must contain the following components in the indicated order:
   * <ul>
   *   <li><tt>urn:upnp-org:serviceId:</tt></li>
   *   <li>service ID suffix</li></ul>
   * Example: <tt>urn:upnp-org:serviceId:serviceID</tt>.
   *
   * <p>Note that <tt>upnp-org</tt> is used
   * instead of <tt>schemas-upnp-org</tt> in this example because an XML
   * schema is not defined for each serviceId.</p>
   *
   * <p>For non-standard services specified by UPnP vendors, the serviceId
   * must contain the following components in the indicated order:
   * <ul>
   *   <li><tt>urn:</tt></li>
   *   <li>ICANN domain name owned by the vendor</li>
   *   <li><tt>:serviceId:</tt></li>
   *   <li>service ID suffix</li></ul>
   * Example: <tt>urn:domain-name:serviceId:serviceID</tt>.
   *
   * @return  The service ID suffix defined by a UPnP Forum working
   *          committee or specified by a UPnP vendor.
   *          Must be &lt;= 64 characters. Single URI.
   **/

  String getId();

  /**
   * Returns the <tt>serviceType</tt>
   * field in the UPnP service description.
   *
   * <p>For standard services defined by a UPnP Forum working committee,
   * the serviceType must contain the following components in the
   * indicated order:
   * <ul>
   *   <li><tt>urn:schemas-upnp-org:service:</tt></li>
   *   <li>service type suffix:</li>
   *   <li>integer service version</li></ul>
   * Example: <tt>urn:schemas-upnp-org:service:serviceType:v</tt>.
   *
   * <p>For non-standard services specified by UPnP vendors,
   * the <tt>serviceType</tt> must contain the following components in the
   * indicated order:
   * <ul>
   *    <li><tt>urn:</tt></li>
   *    <li>ICANN domain name owned by the vendor</li>
   *    <li><tt>:service:</tt></li>
   *    <li>service type suffix:</li>
   *    <li>integer service version</li></ul>
   * Example: <tt>urn:domain-name:service:serviceType:v</tt>.
   *
   * @return The service type suffix defined by a UPnP Forum working committee
   *         or specified by a UPnP vendor. Must be &lt;= 64 characters,
   *         not including the version suffix and separating colon.
   *         Single URI.
   **/

  String getType();

  /**
   * Returns the version suffix encoded in the <tt>serviceType</tt>
   * field in the UPnP service description.
   *
   * @return The integer service version defined by a UPnP Forum working committee
   *         or specified by a UPnP vendor.
   **/
    String getVersion();


  /**
   * Locates a specific action by name.
   *
   * Looks up an action by its name.
   *
   * @param name Name of action. Must not contain hyphen
   *             or hash characters. Should be &lt; 32 characters.
   *
   * @return The requested action or <tt>null</tt> if no action is found.
   **/
  UPnPAction getAction(String name);

  /**
   * Lists all actions provided by this service.
   *
   * @return Array of actions (<tt>UPnPAction[]</tt>)or <tt>null</tt> if no actions are defined
   *         for this service.
   **/
  UPnPAction[] getActions();

    /**
     * Lists all <tt>UPnPStateVariable</tt> objects provided by this service.
     *
     * @return Array of state variables or <tt>null</tt> if none are defined
     *         for this service.
     **/
    UPnPStateVariable[] getStateVariables();


    /**
     * Gets a <tt>UPnPStateVariable</tt> objects provided by this service by name
     *
     * @param name Name of the State Variable
     *
     * @return State variable or <tt>null</tt> if no such state variable exists
     *         for this service.
     **/

    UPnPStateVariable getStateVariable(String name);



}
