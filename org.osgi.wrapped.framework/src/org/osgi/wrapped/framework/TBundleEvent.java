/*
 * Copyright (c) OSGi Alliance (2000, 2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.wrapped.framework;

import java.util.EventObject;

/**
 * An event from the Framework describing a bundle lifecycle change.
 * <p>
 * <code>BundleEvent</code> objects are delivered to
 * <code>SynchronousBundleListener</code>s and <code>BundleListener</code>s
 * when a change occurs in a bundle's lifecycle. A type code is used to identify
 * the event type for future extendability.
 * 
 * <p>
 * OSGi Alliance reserves the right to extend the set of types.
 * 
 * @Immutable
 * @see TBundleListener
 * @see TSynchronousBundleListener
 * @version $Revision$
 */

public class TBundleEvent extends EventObject {
	static final long		serialVersionUID	= 4080640865971756012L;
	/**
	 * Bundle that had a change occur in its lifecycle.
	 */
	private final TBundle	bundle;

	/**
	 * Type of bundle lifecycle change.
	 */
	private final int		type;

	/**
	 * The bundle has been installed.
	 * 
	 * @see TBundleContext#installBundle(String)
	 */
	public final static int	INSTALLED			= 0x00000001;

	/**
	 * The bundle has been started.
	 * <p>
	 * The bundle's
	 * {@link TBundleActivator#start(TBundleContext) BundleActivator start} method
	 * has been executed if the bundle has a bundle activator class.
	 * 
	 * @see TBundle#start()
	 */
	public final static int	STARTED				= 0x00000002;

	/**
	 * The bundle has been stopped.
	 * <p>
	 * The bundle's
	 * {@link TBundleActivator#stop(TBundleContext) BundleActivator stop} method
	 * has been executed if the bundle has a bundle activator class.
	 * 
	 * @see TBundle#stop()
	 */
	public final static int	STOPPED				= 0x00000004;

	/**
	 * The bundle has been updated.
	 * 
	 * @see TBundle#update()
	 */
	public final static int	UPDATED				= 0x00000008;

	/**
	 * The bundle has been uninstalled.
	 * 
	 * @see TBundle#uninstall
	 */
	public final static int	UNINSTALLED			= 0x00000010;

	/**
	 * The bundle has been resolved.
	 * 
	 * @see TBundle#RESOLVED
	 * @since 1.3
	 */
	public final static int	RESOLVED			= 0x00000020;

	/**
	 * The bundle has been unresolved.
	 * 
	 * @see TBundle#INSTALLED
	 * @since 1.3
	 */
	public final static int	UNRESOLVED			= 0x00000040;

	/**
	 * The bundle is about to be activated.
	 * <p>
	 * The bundle's
	 * {@link TBundleActivator#start(TBundleContext) BundleActivator start} method
	 * is about to be called if the bundle has a bundle activator class. This
	 * event is only delivered to {@link TSynchronousBundleListener}s. It is not
	 * delivered to <code>BundleListener</code>s.
	 * 
	 * @see TBundle#start()
	 * @since 1.3
	 */
	public final static int	STARTING			= 0x00000080;

	/**
	 * The bundle is about to deactivated.
	 * <p>
	 * The bundle's
	 * {@link TBundleActivator#stop(TBundleContext) BundleActivator stop} method
	 * is about to be called if the bundle has a bundle activator class. This
	 * event is only delivered to {@link TSynchronousBundleListener}s. It is not
	 * delivered to <code>BundleListener</code>s.
	 * 
	 * @see TBundle#stop()
	 * @since 1.3
	 */
	public final static int	STOPPING			= 0x00000100;

	/**
	 * The bundle will be lazily activated.
	 * <p>
	 * The bundle has a {@link TConstants#ACTIVATION_LAZY lazy activation policy}
	 * and is waiting to be activated. It is now in the
	 * {@link TBundle#STARTING STARTING} state and has a valid
	 * <code>BundleContext</code>. This event is only delivered to
	 * {@link TSynchronousBundleListener}s. It is not delivered to
	 * <code>BundleListener</code>s.
	 * 
	 * @since 1.4
	 */
	public final static int	LAZY_ACTIVATION		= 0x00000200;

	/**
	 * Creates a bundle event of the specified type.
	 * 
	 * @param type The event type.
	 * @param bundle The bundle which had a lifecycle change.
	 */

	public TBundleEvent(int type, TBundle bundle) {
		super(bundle);
		this.bundle = bundle;
		this.type = type;
	}

	/**
	 * Returns the bundle which had a lifecycle change. This bundle is the
	 * source of the event.
	 * 
	 * @return The bundle that had a change occur in its lifecycle.
	 */
	public TBundle getBundle() {
		return bundle;
	}

	/**
	 * Returns the type of lifecyle event. The type values are:
	 * <ul>
	 * <li>{@link #INSTALLED}
	 * <li>{@link #RESOLVED}
	 * <li>{@link #LAZY_ACTIVATION}
	 * <li>{@link #STARTING}
	 * <li>{@link #STARTED}
	 * <li>{@link #STOPPING}
	 * <li>{@link #STOPPED}
	 * <li>{@link #UPDATED}
	 * <li>{@link #UNRESOLVED}
	 * <li>{@link #UNINSTALLED}
	 * </ul>
	 * 
	 * @return The type of lifecycle event.
	 */

	public int getType() {
		return type;
	}
}
