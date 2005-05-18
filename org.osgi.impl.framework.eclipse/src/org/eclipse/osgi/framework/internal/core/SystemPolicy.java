/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.framework.internal.core;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;

public class SystemPolicy implements IBuddyPolicy {

	private static class ParentClassLoader extends ClassLoader {
		protected ParentClassLoader() {
			super(null);
		}
	}

	public static final byte BOOT = 0;
	public static final byte EXT = 1;
	public static final byte APP = 2;

	private static SystemPolicy[] instances = new SystemPolicy[3];

	private ClassLoader classLoader;

	public static SystemPolicy getInstance(final byte type) {
		if (instances[type] == null) {
			instances[type] = new SystemPolicy();
			instances[type].classLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					return createClassLoader(type);
				}
			});
		}
		return instances[type];
	}

	public SystemPolicy() {
		//Nothing to do
	}
	
	public SystemPolicy(ClassLoader parent) {
		classLoader = parent;
	}
	
	static ClassLoader createClassLoader(byte type) {
		switch (type) {
			case APP :
				if (ClassLoader.getSystemClassLoader() != null)
					return ClassLoader.getSystemClassLoader();
				return new ParentClassLoader();

			case BOOT :
				return new ParentClassLoader();

			case EXT :
				if (ClassLoader.getSystemClassLoader() != null)
					return ClassLoader.getSystemClassLoader().getParent();
				return new ParentClassLoader();
		}
		return null;
	}

	public Class loadClass(String name) {
		try {
			return classLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public URL loadResource(String name) {
		return classLoader.getResource(name);
	}

	public Enumeration loadResources(String name) {
		try {
			return classLoader.getResources(name);
		} catch (IOException e) {
			return null;
		}
	}

}
