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

package org.eclipse.osgi.framework.pkcs7verify;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.util.*;

/**
 * Class to manage the different KeyStores we should check for certificates of
 * Signed JAR
 */
public class KeyStores {
	/**
	 * java.policy files properties of the java.security file
	 */
	private static final String JAVA_POLICY_URL = "policy.url."; //$NON-NLS-1$
	/**
	 * Default keystore type in java.security file
	 */
	private static final String DEFAULT_KEYSTORE_TYPE = "keystore.type"; //$NON-NLS-1$
	/**
	 * List of KeyStores
	 */
	private List /* of KeystoreHandle */listOfKeyStores;

	/**
	 * KeyStores constructor comment.
	 */
	public KeyStores() {
		super();
		initializeDefaultKeyStores();
	}

	private void processKeyStore(URL url, String type) {
		if (type == null) {
			type = KeyStore.getDefaultType();
		}
		try {
			KeyStore ks = KeyStore.getInstance(type);
			ks.load(url.openStream(), null);
			listOfKeyStores.add(ks);
		} catch (Exception e) {
			log(e);
		}
	}

	/**
	 * populate the list of Keystores should be done with Dialog with
	 * Cancel/Skip button if the connection to the URL is down...
	 */
	private void initializeDefaultKeyStores() {
		listOfKeyStores = new ArrayList(5);
		// get JRE cacerts
		try {
			URL url = new URL("file", null, 0, System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			processKeyStore(url, Security.getProperty(DEFAULT_KEYSTORE_TYPE));
		} catch (MalformedURLException e) {
			// should not happen, hardcoded...
		}
		// get java.home .keystore
		try {
			URL url = new URL("file", null, 0, System.getProperty("user.home") + File.separator + ".keystore"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			processKeyStore(url, Security.getProperty(DEFAULT_KEYSTORE_TYPE));
		} catch (MalformedURLException e) {
			// should not happen, hardcoded...
		}
		// get KeyStores from policy files...
		int index = 1;
		String java_policy = Security.getProperty(JAVA_POLICY_URL + index);
		while (java_policy != null) {
			// retrieve keystore url from java.policy
			// also retrieve keystore type
			processKeystoreFromLocation(java_policy);
			index++;
			java_policy = Security.getProperty(JAVA_POLICY_URL + index);
		}
	}

	/**
	 * retrieve the keystore from java.policy file
	 */
	private void processKeystoreFromLocation(String location) {
		InputStream in = null;
		char[] buff = new char[4096];
		int indexOf$ = location.indexOf("${"); //$NON-NLS-1$
		int indexOfCurly = location.indexOf('}', indexOf$);
		if (indexOf$ != -1 && indexOfCurly != -1) {
			String prop = System.getProperty(location.substring(indexOf$ + 2, indexOfCurly));
			String location2 = location.substring(0, indexOf$);
			location2 += prop;
			location2 += location.substring(indexOfCurly + 1);
			location = location2;
		}
		try {
			URL url = new URL(location);
			//System.out.println("getKeystoreFromLocation: location is: "
			// +location);
			in = url.openStream();
			Reader reader = new InputStreamReader(in);
			int result = reader.read(buff);
			StringBuffer contentBuff = new StringBuffer();
			while (result != -1) {
				contentBuff.append(buff, 0, result);
				result = reader.read(buff);
			}
			if (contentBuff.length() > 0) {
				String content = new String(contentBuff);
				int indexOfKeystore = content.indexOf("keystore"); //$NON-NLS-1$
				if (indexOfKeystore != -1) {
					int indexOfSemiColumn = content.indexOf(';', indexOfKeystore);
					processKeystoreFromString(content.substring(indexOfKeystore, indexOfSemiColumn), url);
					return;
				}
			}
		} catch (MalformedURLException e) {
			log(e);
		} catch (IOException e) {
			log(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	/**
	 * retrieve the keystore from java.policy file
	 */
	private void processKeystoreFromString(String content, URL rootURL) {
		String keyStoreType = Security.getProperty(DEFAULT_KEYSTORE_TYPE);
		int indexOfSpace = content.indexOf(' ');
		if (indexOfSpace == -1)
			return;
		int secondSpace = content.lastIndexOf(',');
		if (secondSpace == -1) {
			secondSpace = content.length();
		} else {
			keyStoreType = content.substring(secondSpace + 1, content.length()).trim();
		}
		URL url = null;
		try {
			url = new URL(rootURL, content.substring(indexOfSpace, secondSpace));
		} catch (MalformedURLException e1) {
			log(e1);
		}
		if (url != null)
			processKeyStore(url, null);
	}

	private void log(Exception e) {
		e.printStackTrace();
	}

	public boolean isTrusted(Certificate cert) {
		Iterator it = listOfKeyStores.iterator();
		while (it.hasNext()) {
			KeyStore ks = (KeyStore) it.next();
			try {
				if (ks.getCertificateAlias(cert) != null) {
					return true;
				}
			} catch (KeyStoreException e) {
				log(e);
			}
		}
		return false;
	}
}