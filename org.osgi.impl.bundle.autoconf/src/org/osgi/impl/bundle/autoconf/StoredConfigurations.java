/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.impl.bundle.autoconf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StoredConfigurations {
	private final File dataFile;
	private final Set storedConfigurations;

	public StoredConfigurations(File f) throws StreamCorruptedException, IOException, ClassNotFoundException {
		dataFile = f;
		Set c;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(dataFile));
			c = (Set) ois.readObject();
		} catch (FileNotFoundException e) {
			c = new HashSet();
		}
		if (ois!=null) ois.close();
		storedConfigurations = c;
	}

	public void flush() throws FileNotFoundException, IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile));
		oos.writeObject(storedConfigurations);
	}
	
	public void remove(StoredConfiguration sc) {
		storedConfigurations.remove(sc);
	}
	
	public void remove(String pid) {
		for (Iterator iter = storedConfigurations.iterator(); iter.hasNext();) {
			StoredConfiguration e = (StoredConfiguration) iter.next();
			if (e.pid.equals(pid)) {
				storedConfigurations.remove(e);
				return; // pid is unique
			}
		}
	}

	public void add(StoredConfiguration s) {
		storedConfigurations.add(s);
	}
	
	public List getByPackageName(String packageName) {
		ArrayList r = new ArrayList();
		for (Iterator iter = storedConfigurations.iterator(); iter.hasNext();) {
			StoredConfiguration element = (StoredConfiguration) iter.next();
			if (packageName.equals(element.packageName)) r.add(element);
		}
		return r;
	}
	
	public List getByPackageAndResourceName(String packageName,String resourceName) {
		ArrayList r = new ArrayList();
		for (Iterator iter = storedConfigurations.iterator(); iter.hasNext();) {
			StoredConfiguration element = (StoredConfiguration) iter.next();
			if (packageName.equals(element.packageName)&&resourceName.equals(element.resourceName)) r.add(element);
		}
		return r;
	}


}
