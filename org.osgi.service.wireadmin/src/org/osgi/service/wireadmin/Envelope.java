/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2002).
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
package org.osgi.service.wireadmin;

/**
 * Identifies a contained value.
 * 
 * An <tt>Envelope</tt> object combines a status value, an identification
 * object and a scope name. The <tt>Envelope</tt> object allows the use of
 * standard Java types when a Producer service can produce more than one kind of
 * object. The <tt>Envelope</tt> object allows the Consumer service to
 * recognize the kind of object that is received. For example, a door lock could
 * be represented by a <tt>Boolean</tt> object. If the <tt>Producer</tt>
 * service would send such a <tt>Boolean</tt> object, then the Consumer
 * service would not know what door the <tt>Boolean</tt> object represented.
 * The <tt>Envelope</tt> object contains an identification object so the
 * Consumer service can discriminate between different kinds of values. The
 * identification object may be a simple <tt>String</tt> object, but it can
 * also be a domain specific object that is mutually agreed by the Producer and
 * the Consumer service. This object can then contain relevant information that
 * makes the identification easier.
 * <p>
 * The scope name of the envelope is used for security. The Wire object must
 * verify that any <tt>Envelope</tt> object send through the <tt>update</tt>
 * method or coming from the <tt>poll</tt> method has a scope name that
 * matches the permissions of both the Producer service and the Consumer service
 * involved. The wireadmin package also contains a class <tt>BasicEnvelope</tt>
 * that implements the methods of this interface.
 * 
 * @see WirePermission
 * @see BasicEnvelope
 * 
 * @version $Revision$
 */
public interface Envelope {
	/**
	 * Return the value associated with this <tt>Envelope</tt> object.
	 * 
	 * @return the value of the status item, or <tt>null</tt> when no item is
	 *         associated with this object.
	 */
	public Object getValue();

	/**
	 * Return the identification of this <tt>Envelope</tt> object.
	 * 
	 * An identification may be of any Java type. The type must be mutually
	 * agreed between the Consumer and Producer services.
	 * 
	 * @return an object which identifies the status item in the address space
	 *         of the composite producer, must not be null.
	 */
	public Object getIdentification();

	/**
	 * Return the scope name of this <tt>Envelope</tt> object.
	 * 
	 * Scope names are used to restrict the communication between the Producer
	 * and Consumer services. Only <tt>Envelopes</tt> objects with a scope
	 * name that is permitted for the Producer and the Consumer services must be
	 * passed through a <tt>Wire</tt> object.
	 * 
	 * @return the security scope for the status item, must not be null.
	 */
	public String getScope();
}
