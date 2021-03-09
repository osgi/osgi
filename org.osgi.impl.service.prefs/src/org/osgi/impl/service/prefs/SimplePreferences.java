/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.prefs;

import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Simple "proof of concept" Preferences implementation. This implementation
 * achieves persistence by using a text-file-based offline backup facility,
 * storing the entire preference tree into a single file.
 * 
 * @author $Id$
 */
class SimplePreferences extends AbstractPreferences {
	private static final int	INIT_HASHTABLE_SIZE	= 3;
	private Hashtable<String,String>		prefs				= new Hashtable<>(
															INIT_HASHTABLE_SIZE);
	private Hashtable<String,Preferences>	kids				= new Hashtable<>(
															INIT_HASHTABLE_SIZE);

	protected SimplePreferences(SimplePreferences parent, String name) {
		super(parent, name);
		setModified();
	}

	@Override
	protected void putSpi(String key, String value) {
		setModified();
		prefs.put(key, value);
	}

	@Override
	protected String getSpi(String key) {
		return prefs.get(key);
	}

	@Override
	protected void removeSpi(String key) {
		setModified();
		prefs.remove(key);
	}

	@Override
	protected String[] keysSpi() {
		Enumeration<String> enumeration = prefs.keys();
		String[] result = new String[prefs.size()];
		for (int i = 0; enumeration.hasMoreElements(); i++) {
			result[i] = enumeration.nextElement();
		}
		return result;
		// Java 2 version:
		// return (String[]) prefs.keySet().toArray(new String[prefs.size()]);
	}

	@Override
	public String[] childrenNamesSpi() {
		Enumeration<String> enumeration = kids.keys();
		String[] result = new String[kids.size()];
		for (int i = 0; enumeration.hasMoreElements(); i++) {
			result[i] = enumeration.nextElement();
		}
		return result;
		// Java 2 version:
		// return (String[])
		//    kids.keySet().toArray(new String[kids.size()]);
	}

	@Override
	public void flush() throws BackingStoreException {
		synchronized (lock) {
			if (isRemoved()) {
				throw new IllegalStateException("Node has been removed.");
			}
			root.flush();
		}
	}

	@Override
	public void sync() throws BackingStoreException {
		flush();
	}

	@Override
	protected AbstractPreferences childSpi(String name) {
		AbstractPreferences result = (AbstractPreferences) kids.get(name);
		if (result == null) {
			result = new SimplePreferences(this, name);
			kids.put(name, result);
		}
		return result;
	}

	@Override
	protected void removeSpi() throws BackingStoreException{
		((SimplePreferences) parent()).kids.remove(name());
		setModified();
	}

	void setModified() {
		((SimplePreferences) root).setModified();
	}
}
