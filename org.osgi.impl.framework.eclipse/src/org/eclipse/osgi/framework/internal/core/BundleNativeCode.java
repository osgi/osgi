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
package org.eclipse.osgi.framework.internal.core;

import java.util.Enumeration;
import java.util.Vector;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.*;
import org.osgi.framework.Constants;

/**
 * This class represents a description of native code.
 * 
 * Native Code dependencies
 * 
 * The Bundle-NativeCode header allows a bundle to carry the native code it
 * needs, and make use of it when it is installed. The bundle must have
 * RuntimePermission in order to run native code in the Framework. The value of
 * the header must conform to the following syntax:
 * 
 * 
 * Bundle-NativeCode: nativecode-clause ( , nativecode-clause)*
 * nativecode-clause: nativepaths ( ; env-parameter )* nativepaths: nativepath ( ;
 * nativepath )* env-parameter: ( processordef | osnamedef | osversiondef |
 * languagedef ) processordef: <I>processor= </I>token osnamedef: <I>osname=
 * </I>token osversiondef: <I>osversion= </I>token languagedef: <I>language=
 * </I>token
 * 
 * For example:
 * 
 * Bundle-NativeCode: http.dll ; osname=Win95; processor=x86; language=en,
 * libhttp.so; osname=Solaris; processor=sparc
 * 
 * The Bundle-NativeCode header allows a bundle programmer to specify an
 * environment, and to declare what native code libraries it carries for that
 * specific environment. The environment is characterized by the following
 * properties:
 * 
 * <UL>
 * <LI><CODE>processor</CODE> The processor on which the hosting the
 * Framework is running. It is compared against <CODE>
 * org.osgi.framework.processor</CODE>.
 * <LI><CODE>osname</CODE> The operating system name. It is compared against
 * <CODE>org.osgi.framework.os.name</CODE>.
 * <LI><CODE>selection-filter</CODE> An optional filter that can be used to
 * match against system properties. If the filter does not match then the
 * native code clause will not be selected.
 * <LI><CODE>osversion</CODE> The version of the operating system. It is
 * compared against <CODE>org.osgi.framework.os.version</CODE>.
 * <LI><CODE>language</CODE> The language. It is compared against <CODE>
 * org.osgi.framework.language</CODE>.
 * </UL>
 * 
 * These properties should follow the conventions and values defined in the
 * "Open Software Description" specification.
 * 
 * The Framework uses the following algorithm to find the "best" matching
 * native code clause:
 * 
 * <ol>
 * <li>Pick the clauses with a matching processor and operating system with
 * the one the Framework runs on. If no clause matches both the required
 * processor and operating system, the bundle installation/activation fails. If
 * only one clause matches, it can be used, otherwise, remaining steps are
 * executed.</li>
 * <li>Pick the clauses that best match the operating system version. If they
 * match each other exactly, that clause is considered the best match. If there
 * is only one clause with an exact match, it can be used. If there are more
 * than one clause that matches the property, these clauses will be picked to
 * perform the next step. Operating system versions are taken to be backward
 * compatible. If there is no exact match in the clauses, clauses with
 * operating system versions lower than the value specified in
 * org.osgi.framework.osversion will be picked. If there is only one clause
 * which has a compatible operating system version, it can be used. Otherwise,
 * all clauses with compatible operating system versions will go through the
 * next step. If no clause has a matching or compatible operating system
 * version, pick the clause that does not have operating system version
 * specified. If that is not possible, the bundle installation fails.</li>
 * <li>Pick the clause that best matches the language. If more than one clause
 * remains at that point, then the Framework is free to pick amongst them
 * randomly. If no clauses have the exact match with the value of the property,
 * pick the clause that does not have language specified. If that is not
 * possible, the bundle installation fails.</li>
 * </ol>
 *  
 */
public class BundleNativeCode {
	/**
	 * The Native Code paths for the Native Code entry
	 */
	private Attribute nativepaths;
	/**
	 * The processor attribute for this Native Code entry
	 */
	private Attribute processor;
	/**
	 * The osname attribute for this Native Code entry
	 */
	private Attribute osname;
	/**
	 * The language attribute for this Native Code entry
	 */
	private Attribute language;
	/**
	 * The osversion attribute for this Native Code entry
	 */
	private Attribute osversion;
	/**
	 * The filter attribute for this Native Code entry
	 */
	private String filterString;
	/**
	 * The Framework for this BundleNativeCode
	 */
	private AbstractBundle bundle;

	/**
	 * The AliasMapper used to alias OS Names.
	 */
	private static AliasMapper aliasMapper = Framework.aliasMapper;

	/**
	 * Constructor for BundleNativeCode. It reads bundle native code data from
	 * the manifest file.
	 *  
	 */
	protected BundleNativeCode(ManifestElement element, AbstractBundle bundle) {
		this.bundle = bundle;
		String[] nativePaths = element.getValueComponents();
		for (int i = 0; i < nativePaths.length; i++) {
			addPath(nativePaths[i]);
		}
		setAttribute(element, Constants.BUNDLE_NATIVECODE_OSNAME);
		setAttribute(element, Constants.BUNDLE_NATIVECODE_PROCESSOR);
		setAttribute(element, Constants.BUNDLE_NATIVECODE_OSVERSION);
		setAttribute(element, Constants.BUNDLE_NATIVECODE_LANGUAGE);
		setAttribute(element, Constants.SELECTION_FILTER_ATTRIBUTE);
	}

	private void setAttribute(ManifestElement element, String attribute) {
		String[] attrValues = element.getAttributes(attribute);
		if (attrValues != null) {
			for (int i = 0; i < attrValues.length; i++) {
				addAttribute(attribute, attrValues[i]);
			}
		}
	}

	/**
	 * Returns the native code paths.
	 * 
	 * @return Vector of String code paths.
	 */
	public String[] getPaths() {
		if (nativepaths == null) {
			return null;
		}
		String[] paths = new String[nativepaths.size()];
		nativepaths.toArray(paths);
		return (paths);
	}

	/**
	 * addPath is used to add a new element to the list of native files.
	 * 
	 * @param nativepath
	 *            new native file
	 */
	protected void addPath(String nativepath) {
		if (nativepaths == null) {
			nativepaths = new Attribute();
		}
		nativepaths.addElement(nativepath);
	}

	/**
	 * addAttribute is used to add the specification-version string to the
	 * package description. It is the only key supported at this time.
	 * 
	 * @param key
	 *            attribute key name
	 * @param value
	 *            attribute value name
	 */
	protected synchronized void addAttribute(String key, String value) {
		if (key.equals(Constants.BUNDLE_NATIVECODE_PROCESSOR)) {
			if (processor == null) {
				processor = new Attribute();
			}
			processor.addElement(aliasMapper.aliasProcessor(value));
			return;
		}
		if (key.equals(Constants.BUNDLE_NATIVECODE_OSNAME)) {
			if (osname == null) {
				osname = new Attribute();
			}
			osname.addElement(aliasMapper.aliasOSName(value));
			return;
		}
		if (key.equals(Constants.BUNDLE_NATIVECODE_OSVERSION)) {
			if (osversion == null) {
				osversion = new Attribute();
			}
			osversion.addElement(Version.parseVersion(value));
			return;
		}
		if (key.equals(Constants.SELECTION_FILTER_ATTRIBUTE)) {
			if (filterString == null) {
				filterString = value;
			}
			return;
		}
		if (key.equals(Constants.BUNDLE_NATIVECODE_LANGUAGE)) {
			if (language == null) {
				language = new Attribute();
			}
			language.addElement(value.toLowerCase());
			return;
		}
	}

	/**
	 * Override toString. Return a String representation of this object
	 * 
	 * @return String representation of the object
	 */
	public String toString() {
		int size = nativepaths.size();
		StringBuffer sb = new StringBuffer(50 * size);
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				sb.append(';');
			}
			sb.append(nativepaths.elementAt(i).toString());
		}
		if (processor != null) {
			size = processor.size();
			for (int i = 0; i < size; i++) {
				sb.append(';');
				sb.append(Constants.BUNDLE_NATIVECODE_PROCESSOR);
				sb.append('=');
				sb.append(processor.elementAt(i).toString());
			}
		}
		if (osname != null) {
			size = osname.size();
			for (int i = 0; i < size; i++) {
				sb.append(';');
				sb.append(Constants.BUNDLE_NATIVECODE_OSNAME);
				sb.append('=');
				sb.append(osname.elementAt(i).toString());
			}
		}
		if (osversion != null) {
			size = osversion.size();
			for (int i = 0; i < size; i++) {
				sb.append(';');
				sb.append(Constants.BUNDLE_NATIVECODE_OSVERSION);
				sb.append('=');
				sb.append(osversion.elementAt(i).toString());
			}
		}
		if (language != null) {
			size = language.size();
			for (int i = 0; i < size; i++) {
				sb.append(';');
				sb.append(Constants.BUNDLE_NATIVECODE_LANGUAGE);
				sb.append('=');
				sb.append(language.elementAt(i).toString());
			}
		}
		return (sb.toString());
	}

	/**
	 * Return the match value for the given processor and os name. A higher
	 * value indicates a better match.
	 * 
	 * @param processor
	 *            processor name to match against.
	 * @param osname
	 *            os name to match against.
	 * @return match value
	 */
	public int matchProcessorOSNameFilter(String processor, String osname) {
		if ((this.processor == null) || (this.osname == null)) {
			return (0);
		}
		String otherProcessor = aliasMapper.aliasProcessor(processor);
		String otherOSName = (String) aliasMapper.aliasOSName(osname);
		if (this.processor.equals(otherProcessor) && this.osname.equals(otherOSName) && matchFilter()) {
			return (1);
		}
		return (0);
	}

	/**
	 * Return the higest matching value for the given os version that is less
	 * than or equal to the given os version.
	 * 
	 * @param version
	 *            os version to match against.
	 * @return version or null if no match.
	 */
	public Version matchOSVersion(Version version) {
		if (this.osversion == null) {
			return Version.emptyVersion;
		}
		Version result = null;
		int size = this.osversion.size();
		for (int i = 0; i < size; i++) {
			Version ver = (Version) this.osversion.elementAt(i);
			int compare = ver.compareTo(version);
			if (compare == 0) /* versions are equal; best possible match */{
				return ver;
			}
			if (compare < 0) /* requested version < current OS version */{
				if ((result == null) || (ver.compareTo(result) > 0)) {
					result = ver; /*
					 * remember the highest version less than
					 * osversion
					 */
				}
			}
		}
		return result;
	}

	/**
	 * Return the match value for the given language. A higher value indicates
	 * a better match.
	 * 
	 * @param language
	 *            language name to match against.
	 * @return match value
	 */
	public int matchLanguage(String language) {
		if (this.language == null) {
			return (1);
		}
		if (this.language.equals(language.toLowerCase())) {
			return (2);
		}
		return (0);
	}

	public boolean matchFilter() {
		if (filterString == null) {
			return true;
		}
		FilterImpl filter;
		try {
			filter = new FilterImpl(filterString);
		} catch (InvalidSyntaxException e) {
			BundleException be = new BundleException(Msg.BUNDLE_NATIVECODE_INVALID_FILTER, e); //$NON-NLS-1$
			bundle.framework.publishFrameworkEvent(FrameworkEvent.ERROR, bundle, be);
			return false;
		}
		return filter.match(System.getProperties());
	}

	/**
	 * Extension of Vector for attributes.
	 */
	static class Attribute extends Vector {
		private static final long serialVersionUID = 3257005440914174512L;

		/**
		 * Attribute constructor.
		 *  
		 */
		Attribute() {
			super(10, 10);
		}

		/**
		 * Perform an "OR" operation on equals.
		 * 
		 * @param obj
		 *            Object to test against.
		 * @return true if at least one attribute is equal; false otherwise.
		 */
		public synchronized boolean equals(Object obj) {
			for (int i = 0; i < elementCount; i++) {
				Object data = elementData[i];
				if (data instanceof String) {
					if (elementData[i].equals(obj)) {
						return (true);
					}
				} else {
					Enumeration e = ((Vector) data).elements();
					while (e.hasMoreElements()) {
						if (((String) e.nextElement()).equals(obj)) {
							return true;
						}
					}
				}
			}
			return (false);
		}

		/**
		 * Add the object if it is not already in the vector.
		 * 
		 * @param obj
		 *            Object to add to the vector.
		 */
		public synchronized void addElement(Object obj) {
			if (!contains(obj)) {
				super.addElement(obj);
			}
		}
	}
}
