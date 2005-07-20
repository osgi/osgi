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

import org.osgi.framework.BundleException;

/**
 * Framework service which allows bundle programmers to inspect the bundles and
 * packages known to the Framework.  The PlatformAdmin service also allows bundles
 * with sufficient privileges to update the state of the framework by committing a new
 * configuration of bundles and packages.
 *
 * If present, there will only be a single instance of this service
 * registered with the Framework.
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.1
 */
public interface PlatformAdmin {

	/** 
	 * Returns a mutable state representing the current system.
	 * <p>
	 * This is a convenience method, fully equivalent to 
	 * <code>getState(true)</code>.
	 * </p> 
	 * @return a state representing the current framework.
	 */
	public State getState();

	/** 
	 * Returns a state representing the current system. If there is need to make
	 * changes to the returned state, a mutable state must be requested. 
	 * Otherwise, an immutable state should be requested. In this case, invoking 
	 * any of the operations that could cause the state to be changed will throw 
	 * an <code>java.lang.UnsupportedOperationException</code>.
	 * <p>
	 * If a mutable state is requested, the resulting state will <strong>not</strong> 
	 * be resolved. 
	 * </p> 
	 * @param mutable whether the returned state should mutable
	 * @return a state representing the current framework.
	 */
	public State getState(boolean mutable);

	/**
	 * Returns a state helper object. State helpers provide convenience methods 
	 * for manipulating states. 
	 * <p>
	 * A possible implementation for this
	 * method would provide the same single StateHelper instance to all clients.
	 * </p>
	 * 
	 * @return a state helper
	 * @see StateHelper
	 */
	public StateHelper getStateHelper();

	/**
	 * Commit the differences between the current state and the given state.
	 * The given state must return true from State.isResolved() or an exception 
	 * is thrown.  The resolved state is committed verbatim, as-is.  
	 * 
	 * @param state the future state of the framework
	 * @throws BundleException if the id of the given state does not match that of the
	 * 	current state or if the given state is not resolved.
	 */
	public void commit(State state) throws BundleException;
	
	/**
	 * Returns a resolver supplied by the system.  The returned resolver 
	 * will not be associated with any state.
	 * @return a system resolver
	 */
	public Resolver getResolver();

	/**
	 * Returns a factory that knows how to create state objects, such as bundle 
	 * descriptions and the different types of version constraints.
	 * @return a state object factory
	 */
	public StateObjectFactory getFactory();
}
