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

package org.osgi.service.wireadmin;

import java.util.Dictionary;

/**
 * A connection between a Producer service and a Consumer service.
 *
 * <p>A <tt>Wire</tt> object connects a Producer service
 * to a Consumer service.
 * Both the Producer and Consumer services are identified
 * by their unique <tt>service.pid</tt> values.
 * The Producer and Consumer services may communicate with
 * each other via <tt>Wire</tt> objects that connect them.
 * The Producer service may send updated values to the
 * Consumer service by calling the {@link #update} method.
 * The Consumer service may request an updated value from the
 * Producer service by calling the {@link #poll} method.
 *
 * <p>A Producer service and a Consumer service may be
 * connected through multiple <tt>Wire</tt> objects.
 *
 * <p>Security Considerations. <tt>Wire</tt> objects are available to
 * Producer and Consumer services connected to a given
 * <tt>Wire</tt> object and to bundles which can access the <tt>WireAdmin</tt> service.
 * A bundle must have <tt>ServicePermission[GET,WireAdmin]</tt> to get the <tt>WireAdmin</tt> service to
 * access all <tt>Wire</tt> objects.
 * A bundle registering a Producer service or a Consumer service
 * must have the appropriate <tt>ServicePermission[REGISTER,Consumer|Producer]</tt> to register the service and
 * will be passed <tt>Wire</tt> objects when the service object's
 * <tt>consumersConnected</tt> or <tt>producersConnected</tt> method is called.
 *
 * <p>Scope. Each Wire object can have a scope set with the <tt>setScope</tt> method. This
 * method should be called by a Consumer service when it assumes a Producer service that is
 * composite (supports multiple information items). The names in the scope must be
 * verified by the <tt>Wire</tt> object before it is used in communication. The semantics of the
 * names depend on the Producer service and must not be interpreted by the Wire Admin service.
 *
 * @version $Revision$
 */
public interface Wire
{
    /**
	 * Return the state of this <tt>Wire</tt> object.
	 *
	 * <p>A connected <tt>Wire</tt> must always be disconnected before
	 * becoming invalid.
	 *
	 * @return <tt>false</tt> if this <tt>Wire</tt> object is invalid because it
	 * has been deleted via {@link WireAdmin#deleteWire};
	 * <tt>true</tt> otherwise.
	 */
    public boolean isValid();

    /**
	 * Return the connection state of this <tt>Wire</tt> object.
	 *
	 * <p>A <tt>Wire</tt> is connected after the Wire Admin service receives
	 * notification that the Producer service and
	 * the Consumer service for this <tt>Wire</tt> object are both registered.
	 * This method will return <tt>true</tt> prior to notifying the Producer
	 * and Consumer services via calls
	 * to their respective <tt>consumersConnected</tt> and <tt>producersConnected</tt>
	 * methods.
	 * <p>A <tt>WireAdminEvent</tt> of type {@link WireAdminEvent#WIRE_CONNECTED}
	 * must be broadcast by the Wire Admin service when
	 * the <tt>Wire</tt> becomes connected.
	 *
	 * <p>A <tt>Wire</tt> object
	 * is disconnected when either the Consumer or Producer
	 * service is unregistered or the <tt>Wire</tt> object is deleted.
	 * <p>A <tt>WireAdminEvent</tt> of type {@link WireAdminEvent#WIRE_DISCONNECTED}
	 * must be broadcast by the Wire Admin service when
	 * the <tt>Wire</tt> becomes disconnected.
	 *
	 * @return <tt>true</tt> if both the Producer and Consumer
	 * for this <tt>Wire</tt> object are connected to the <tt>Wire</tt> object;
	 * <tt>false</tt> otherwise.
	 */
    public boolean isConnected();

    /**
	 * Return the list of data types understood by the
	 * Consumer service connected to this <tt>Wire</tt> object. Note that
	 * subclasses of the classes in this list are acceptable data types as well.
	 *
	 * <p>The list is the value of the {@link WireConstants#WIREADMIN_CONSUMER_FLAVORS}
	 * service property of the
	 * Consumer service object connected to this object. If no such
	 * property was registered or the type of the property value is not
	 * <tt>Class[]</tt>, this method must return <tt>null</tt>.
	 *
	 * @return An array containing the list of classes understood by the
	 * Consumer service or <tt>null</tt> if
	 * the <tt>Wire</tt> is not connected,
	 * or the consumer did not register a {@link WireConstants#WIREADMIN_CONSUMER_FLAVORS} property
	 * or the value of the property is not of type <tt>Class[]</tt>.
	 */
    public Class[] getFlavors();

    /**
	 * Update the value.
	 *
	 * <p>This methods is called by the Producer service to
	 * notify the Consumer service connected to this <tt>Wire</tt> object
	 * of an updated value.
	 * <p>If the properties of this <tt>Wire</tt> object contain a
	 * {@link WireConstants#WIREADMIN_FILTER} property,
	 * then filtering is performed.
	 * If the Producer service connected to this <tt>Wire</tt>
	 * object was registered with the service
	 * property {@link WireConstants#WIREADMIN_PRODUCER_FILTERS}, the
	 * Producer service will perform the filtering according to the rules specified
	 * for the filter. Otherwise, this <tt>Wire</tt> object
	 * will perform the filtering of the value.
	 * <p>If no filtering is done, or the filter indicates the updated value should
	 * be delivered to the Consumer service, then
	 * this <tt>Wire</tt> object must call
	 * the {@link Consumer#updated} method with the updated value.
	 * If this <tt>Wire</tt> object is not connected, then the Consumer
	 * service must not be called and the value is ignored.<p>
	 * If the value is an <tt>Envelope</tt> object, and the scope name is not permitted, then the
	 * <tt>Wire</tt> object must ignore this call and not transfer the object to the Consumer
	 * service.
	 *
	 * <p>A <tt>WireAdminEvent</tt> of type {@link WireAdminEvent#WIRE_TRACE}
	 * must be broadcast by the Wire Admin service after
	 * the Consumer service has been successfully called.
	 *
	 * @param value The updated value. The value should be an instance of
	 * one of the types returned by {@link #getFlavors}.
	 * @see WireConstants#WIREADMIN_FILTER
	 */
    public void update(Object value);

    /**
	 * Poll for an updated value.
	 *
	 * <p>This methods is normally called by the Consumer service to
	 * request an updated value from the Producer service
	 * connected to this <tt>Wire</tt> object.
	 * This <tt>Wire</tt> object will call
	 * the {@link Producer#polled} method to obtain an updated value.
	 * If this <tt>Wire</tt> object is not connected, then the Producer
	 * service must not be called.<p>
	 *
	 * If this <tt>Wire</tt> object has a scope, then this method
	 * must return an array of <tt>Envelope</tt> objects. The objects returned must
	 * match the scope of this object. The <tt>Wire</tt> object must remove
	 * all <tt>Envelope</tt> objects with a scope name that is not in the <tt>Wire</tt> object's scope.
	 * Thus, the list of objects returned
	 * must only contain <tt>Envelope</tt> objects with a permitted scope name. If the
	 * array becomes empty, <tt>null</tt> must be returned.
	 *
	 * <p>A <tt>WireAdminEvent</tt> of type {@link WireAdminEvent#WIRE_TRACE}
	 * must be broadcast by the Wire Admin service after
	 * the Producer service has been successfully called.
	 *
	 * @return A value whose type should be one of the types
	 * returned by {@link #getFlavors}, <tt>Envelope[]</tt>, or <tt>null</tt> if
	 * the <tt>Wire</tt> object is not connected,
	 * the Producer service threw an exception, or
	 * the Producer service returned a value which is not an instance of
	 * one of the types returned by {@link #getFlavors}.
	 */
    public Object poll();
	
	/**
	 * Return the last value sent through this <tt>Wire</tt> object.
	 *
	 * <p>The returned value is the most recent, valid value passed to the
	 * {@link #update} method or returned by the {@link #poll} method
	 * of this object. If filtering is performed by this <tt>Wire</tt> object,
	 * this methods returns the last value provided by the Producer service. This
	 * value may be an <tt>Envelope[]</tt> when the Producer service
	 * uses scoping. If the return value is an Envelope object (or array), it
	 * must be verified that the Consumer service has the proper WirePermission to see it.
	 *
	 * @return The last value passed though this <tt>Wire</tt> object
	 * or <tt>null</tt> if no valid values have been passed or the Consumer service has no permission.
	 */
    public Object getLastValue();

    /**
	 * Return the wire properties for this <tt>Wire</tt> object.
	 *
	 * @return The properties for this <tt>Wire</tt> object.
	 * The returned <tt>Dictionary</tt> must be read only.
	 */
    public Dictionary getProperties();
	
	
	/**
	 * Return the calculated scope of this <tt>Wire</tt> object.
	 *
	 * The purpose of the <tt>Wire</tt> object's scope is to allow a Producer
	 * and/or Consumer service to produce/consume different types
	 * over a single <tt>Wire</tt> object (this was deemed necessary for efficiency
	 * reasons). Both the Consumer service and the
	 * Producer service must set an array of scope names (their scope) with
	 * the service registration property <tt>WIREADMIN_PRODUCER_SCOPE</tt>, or <tt>WIREADMIN_CONSUMER_SCOPE</tt> when they can
	 * produce multiple types. If a Producer service can produce different types, it should set this property
	 * to the array of scope names it can produce, the Consumer service
	 * must set the array of scope names it can consume. The scope of a <tt>Wire</tt>
	 * object is defined as the intersection of permitted scope names of the
	 * Producer service and Consumer service.
	 * <p>If neither the Consumer, or the Producer service registers scope names with its
	 * service registration, then the <tt>Wire</tt> object's scope must be <tt>null</tt>.
	 * <p>The <tt>Wire</tt> object's scope must not change when a Producer or Consumer services
	 * modifies its scope.
	 * <p>A scope name is permitted for a Producer service when the registering bundle has
	 * <tt>WirePermission[PRODUCE]</tt>, and for a Consumer service when
	 * the registering bundle has <tt>WirePermission[CONSUME]</tt>.<p>
	 * If either Consumer service or Producer service has not set a <tt>WIREADMIN_*_SCOPE</tt> property, then
	 * the returned value must be <tt>null</tt>.<p>
	 * If the scope is set, the <tt>Wire</tt> object must enforce the scope names when <tt>Envelope</tt> objects are
	 * used as a parameter to update or returned from the <tt>poll</tt> method. The <tt>Wire</tt> object must then
	 * remove all <tt>Envelope</tt> objects with a scope name that is not permitted.
	 *
	 * @return A list of permitted scope names or null if the Produce or Consumer service has set no scope names.
	 */	
	public String [] getScope();
	
	
	/**
	 * Return true if the given name is in this <tt>Wire</tt> object's scope.
	 *
	 * @param name The scope name
	 * @return true if the name is listed in the permitted scope names
	 */
	public boolean hasScope( String name );
}

