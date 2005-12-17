/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import java.util.EventObject;

/**
 * StartLevel Event for the OSGi framework.
 *
 * Event which signifies that a start level change has been requested for the framework or for a bundle.
 *
 */
class StartLevelEvent extends EventObject {
	private static final long serialVersionUID = 3258125839085155891L;
	public final static int CHANGE_BUNDLE_SL = 0x00000000;
	public final static int CHANGE_FW_SL = 0x00000001;

	/**
	 * Event Type
	 */
	private transient int type;

	/**
	 * StartLevel - value depends on event type: 
	 *  CHANGE_BUNDLE_SL - value is the new bundle startlevel
	 *  CHANGE_FW_SL - value is the new framework startlevel
	 * 
	 */
	private transient int newSl;

	/**
	 * For a change in bundle startlevel, this is the bundle to be changed.
	 * For a change in framework startlevel, this is the bundle requesting the change.
	 */
	private transient AbstractBundle bundle;

	/**
	 * Creates a StartLevel event regarding the specified bundle.
	 *
	 * @param type The type of startlevel event (inc or dec)
	 * @param newSl the ultimate requested startlevel we are on our way to
	 * @param bundle The affected bundle, or system bundle if it is for the framework
	 */
	public StartLevelEvent(int type, int newSl, AbstractBundle bundle) {
		super(bundle);
		this.type = type;
		this.newSl = newSl;
		this.bundle = bundle;
	}

	public int getType() {
		return this.type;
	}

	public int getNewSL() {
		return this.newSl;
	}

	public AbstractBundle getBundle() {
		return this.bundle;
	}

}
