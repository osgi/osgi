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
 * An <code>Envelope</code> object combines a status value, an identification
 * object and a scope name. The <code>Envelope</code> object allows the use of
 * standard Java types when a Producer service can produce more than one kind of
 * object. The <code>Envelope</code> object allows the Consumer service to
 * recognize the kind of object that is received. For example, a door lock could
 * be represented by a <code>Boolean</code> object. If the <code>Producer</code>
 * service would send such a <code>Boolean</code> object, then the Consumer
 * service would not know what door the <code>Boolean</code> object represented.
 * The <code>Envelope</code> object contains an identification object so the
 * Consumer service can discriminate between different kinds of values. The
 * identification object may be a simple <code>String</code> object, but it can
 * also be a domain specific object that is mutually agreed by the Producer and
 * the Consumer service. This object can then contain relevant information that
 * makes the identification easier.
 * <p>
 * The scope name of the envelope is used for security. The Wire object must
 * verify that any <code>Envelope</code> object send through the <code>update</code>
 * method or coming from the <code>poll</code> method has a scope name that
 * matches the permissions of both the Producer service and the Consumer service
 * involved. The wireadmin package also contains a class <code>BasicEnvelope</code>
 * that implements the methods of this interface.
 * 
 * @see WirePermission
 * @see BasicEnvelope
 * 
 * @version $Revision$
 */
public interface Envelope {
	/**
	 * Return the value associated with this <code>Envelope</code> object.
	 * 
	 * @return the value of the status item, or <code>null</code> when no item is
	 *         associated with this object.
	 */
	public Object getValue();

	/**
	 * Return the identification of this <code>Envelope</code> object.
	 * 
	 * An identification may be of any Java type. The type must be mutually
	 * agreed between the Consumer and Producer services.
	 * 
	 * @return an object which identifies the status item in the address space
	 *         of the composite producer, must not be null.
	 */
	public Object getIdentification();

	/**
	 * Return the scope name of this <code>Envelope</code> object.
	 * 
	 * Scope names are used to restrict the communication between the Producer
	 * and Consumer services. Only <code>Envelopes</code> objects with a scope
	 * name that is permitted for the Producer and the Consumer services must be
	 * passed through a <code>Wire</code> object.
	 * 
	 * @return the security scope for the status item, must not be null.
	 */
	public String getScope();
}
