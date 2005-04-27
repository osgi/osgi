/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import org.eclipse.osgi.framework.util.Headers;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * This class is used by the Bundle Class to localize manifest headers.
 */
public class ManifestLocalization {
	private AbstractBundle bundle = null;
	private Dictionary rawHeaders = null;
	private Dictionary defaultLocaleHeaders = null;
	private Hashtable cache = new Hashtable(5);

	public ManifestLocalization(AbstractBundle bundle, Dictionary rawHeaders) {
		this.bundle = bundle;
		this.rawHeaders = rawHeaders;
	}

	protected Dictionary getHeaders(String localeString) {
		if (localeString.length() == 0)
			return (rawHeaders);
		boolean isDefaultLocale = false;
		String defaultLocale = Locale.getDefault().toString();
		if (localeString.equals(defaultLocale)) {
			if (defaultLocaleHeaders != null)
				return (defaultLocaleHeaders);
			isDefaultLocale = true;
		}
		try {
			bundle.checkValid();
		} catch (IllegalStateException ex) {
			// defaultLocaleHeaders should have been initialized on uninstall
			if (defaultLocaleHeaders != null)
				return defaultLocaleHeaders;
			return (rawHeaders);
		}
		ResourceBundle localeProperties = getResourceBundle(localeString);
		if (localeProperties == null && !isDefaultLocale)
			// could not find the requested locale use the default locale
			localeProperties = getResourceBundle(defaultLocale);
		Enumeration e = this.rawHeaders.keys();
		Headers localeHeaders = new Headers(this.rawHeaders.size());
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String) this.rawHeaders.get(key);
			if (value.startsWith("%") && (value.length() > 1)) { //$NON-NLS-1$
				String propertiesKey = value.substring(1);
				try {
					value = localeProperties == null ? propertiesKey : (String) localeProperties.getObject(propertiesKey);
				} catch (MissingResourceException ex) {
					value = propertiesKey;
				}
			}
			localeHeaders.set(key, value);
		}
		if (isDefaultLocale) {
			defaultLocaleHeaders = localeHeaders;
		}
		return (localeHeaders);
	}

	private String[] buildNLVariants(String nl) {
		ArrayList result = new ArrayList();
		int lastSeparator;
		while ((lastSeparator = nl.lastIndexOf('_')) != -1) {
			result.add(nl);
			if (lastSeparator != -1) {
				nl = nl.substring(0, lastSeparator);
			}
		}
		result.add(nl);
		// always add the default locale string
		result.add(""); //$NON-NLS-1$
		return (String[]) result.toArray(new String[result.size()]);
	}

	/*
	 * This method find the appropiate Manifest Localization file inside the
	 * bundle. If not found, return null.
	 */
	protected ResourceBundle getResourceBundle(String localeString) {
		String propertiesLocation = (String) rawHeaders.get(Constants.BUNDLE_LOCALIZATION);
		if (propertiesLocation == null) {
			propertiesLocation = Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME;
		}

		BundleResourceBundle result = (BundleResourceBundle) cache.get(localeString);
		if (result != null)
			return (ResourceBundle) (result.isEmpty() ? null : result);
		String[] nlVarients = buildNLVariants(localeString);
		BundleResourceBundle parent = null;
		for (int i = nlVarients.length-1; i >= 0; i--) {
			BundleResourceBundle varientBundle = (BundleResourceBundle) cache.get(nlVarients[i]);
			URL varientURL = findResource(propertiesLocation + (nlVarients[i].equals("") ? nlVarients[i] : '_' + nlVarients[i]) + ".properties"); //$NON-NLS-1$ //$NON-NLS-2$
			if (varientURL != null) {
				InputStream resourceStream = null;
				try {
					resourceStream = varientURL.openStream();
					varientBundle = new LocalizationResourceBundle(resourceStream);
				} catch (IOException e) {
					// ignore and continue
				} finally {
					if (resourceStream != null) {
						try {
							resourceStream.close();
						} catch (IOException e3) {
							//Ignore exception
						}
					}
				}
			}

			if (varientBundle == null) {
				varientBundle = new EmptyResouceBundle();
			}
			if (parent != null)
				varientBundle.setParent((ResourceBundle)parent);
			cache.put(nlVarients[i], varientBundle);
			parent = varientBundle;
		}
		result = (BundleResourceBundle) cache.get(localeString);
		return (ResourceBundle) (result.isEmpty() ? null : result);
	}

	private URL findResource(String resource) {
		AbstractBundle searchBundle = bundle;
		if (bundle.isResolved()) {
			if (bundle.isFragment() && bundle.getHosts() != null) {
				//if the bundle is a fragment, look in the host first
				searchBundle = bundle.getHosts()[0].getBundleHost();
				if (searchBundle.getState() == Bundle.UNINSTALLED)
					searchBundle = bundle;
			}
			return findInResolved(resource, searchBundle);
		}
		return findInBundle(resource, searchBundle);
	}

	private URL findInResolved(String filePath, AbstractBundle bundleHost) {

		URL result = findInBundle(filePath, bundleHost);
		if (result != null)
			return result;
		return findInFragments(filePath, bundleHost);
	}

	private URL findInBundle(String filePath, AbstractBundle searchBundle) {
		return searchBundle.getEntry(filePath);
	}

	private URL findInFragments(String filePath, AbstractBundle searchBundle) {
		org.osgi.framework.Bundle[] fragments = searchBundle.getFragments();
		URL fileURL = null;
		for (int i = 0; fragments != null && i < fragments.length && fileURL == null; i++) {
			if (fragments[i].getState() != Bundle.UNINSTALLED)
				fileURL = fragments[i].getEntry(filePath);
		}
		return fileURL;
	}

	private abstract interface BundleResourceBundle{
		void setParent(ResourceBundle parent);
		boolean isEmpty();
	}
	private class LocalizationResourceBundle extends PropertyResourceBundle implements BundleResourceBundle{
		public LocalizationResourceBundle(InputStream in) throws IOException {
			super(in);
		}
		public void setParent(ResourceBundle parent) {
			super.setParent(parent);
		}
		public boolean isEmpty() {
			return false;
		}
	}

	private class EmptyResouceBundle extends ResourceBundle implements BundleResourceBundle{
		public Enumeration getKeys() {
			return null;
		}
		protected Object handleGetObject(String arg0) throws MissingResourceException {
			return null;
		}
		public void setParent(ResourceBundle parent) {
			super.setParent(parent);
		}
		public boolean isEmpty() {
			if (parent == null)
				return true;
			return ((BundleResourceBundle)parent).isEmpty();
		}
	}
}
