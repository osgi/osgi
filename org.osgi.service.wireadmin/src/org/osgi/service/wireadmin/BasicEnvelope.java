/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2002, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.wireadmin;

/**
 * <code>BasicEnvelope</code> is an implementation of the {@link Envelope}
 * interface
 * 
 * @version $Revision$
 */
public class BasicEnvelope implements Envelope {
	Object	value;
	Object	identification;
	String	scope;

	/**
	 * Constructor.
	 * 
	 * @param value Content of this envelope, may be <code>null</code>.
	 * @param identification Identifying object for this <code>Envelope</code>
	 *        object, must not be <code>null</code>
	 * @param scope Scope name for this object, must not be <code>null</code>
	 * @see Envelope
	 */
	public BasicEnvelope(Object value, Object identification, String scope) {
		this.value = value;
		this.identification = identification;
		this.scope = scope;
	}

	/**
	 * @see org.osgi.service.wireadmin.Envelope#getValue()
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @see org.osgi.service.wireadmin.Envelope#getIdentification()
	 */
	public Object getIdentification() {
		return identification;
	}

	/**
	 * @see org.osgi.service.wireadmin.Envelope#getScope()
	 */
	public String getScope() {
		return scope;
	}
}
