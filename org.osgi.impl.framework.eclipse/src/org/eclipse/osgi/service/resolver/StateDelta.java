/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.service.resolver;

public interface StateDelta {
	/**
	 * Returns an array of all the bundle deltas in this delta regardless of type.
	 * @return an array of bundle deltas
	 */
	public BundleDelta[] getChanges();

	/**
	 * Returns an array of all the members
	 * of this delta which match the given flags.  If an exact match is requested 
	 * then only delta members whose type exactly matches the given mask are
	 * included.  Otherwise, all bundle deltas whose type's bit-wise and with the
	 * mask is non-zero are included. 
	 * 
	 * @param mask
	 * @param exact
	 * @return an array of bundle deltas matching the given match criteria.
	 */
	public BundleDelta[] getChanges(int mask, boolean exact);

	/**
	 * Returns the state whose changes are represented by this delta.
	 * @return the state
	 */
	public State getState();
}
