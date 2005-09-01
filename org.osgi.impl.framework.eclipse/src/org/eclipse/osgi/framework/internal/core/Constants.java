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

/**
 * This interface contains the constants used by the eclipse
 * OSGi implementation.
 */

public class Constants implements org.osgi.framework.Constants {
	/** Default framework version */
	public static final String OSGI_FRAMEWORK_VERSION = "1.3"; //$NON-NLS-1$

	/** Framework vendor */
	public static final String OSGI_FRAMEWORK_VENDOR = "Eclipse"; //$NON-NLS-1$

	/** Bundle manifest name */
	public static final String OSGI_BUNDLE_MANIFEST = "META-INF/MANIFEST.MF"; //$NON-NLS-1$

	/** OSGi framework package name. */
	public static final String OSGI_FRAMEWORK_PACKAGE = "org.osgi.framework"; //$NON-NLS-1$

	/** Bundle resource URL protocol */
	public static final String OSGI_RESOURCE_URL_PROTOCOL = "bundleresource"; //$NON-NLS-1$

	/** Bundle entry URL protocol */
	public static final String OSGI_ENTRY_URL_PROTOCOL = "bundleentry"; //$NON-NLS-1$

	/** Processor aliases resource */
	public static final String OSGI_PROCESSOR_ALIASES = "processor.aliases"; //$NON-NLS-1$

	/** OS name aliases resource */
	public static final String OSGI_OSNAME_ALIASES = "osname.aliases"; //$NON-NLS-1$

	/** Default permissions for bundles with no permission set
	 * and there are no default permissions set.
	 */
	public static final String OSGI_DEFAULT_DEFAULT_PERMISSIONS = "default.permissions"; //$NON-NLS-1$

	/** Base implied permissions for all bundles */
	public static final String OSGI_BASE_IMPLIED_PERMISSIONS = "implied.permissions"; //$NON-NLS-1$

	/** Name of OSGi LogService */
	public static final String OSGI_LOGSERVICE_NAME = "org.osgi.service.log.LogService"; //$NON-NLS-1$

	/** Name of OSGi PackageAdmin */
	public static final String OSGI_PACKAGEADMIN_NAME = "org.osgi.service.packageadmin.PackageAdmin"; //$NON-NLS-1$

	/** Name of OSGi PermissionAdmin */
	public static final String OSGI_PERMISSIONADMIN_NAME = "org.osgi.service.permissionadmin.PermissionAdmin"; //$NON-NLS-1$

	/** Name of OSGi StartLevel */
	public static final String OSGI_STARTLEVEL_NAME = "org.osgi.service.startlevel.StartLevel"; //$NON-NLS-1$

	/** JVM java.vm.name property name */
	public static final String JVM_VM_NAME = "java.vm.name"; //$NON-NLS-1$

	/** JVM os.arch property name */
	public static final String JVM_OS_ARCH = "os.arch"; //$NON-NLS-1$

	/** JVM os.name property name */
	public static final String JVM_OS_NAME = "os.name"; //$NON-NLS-1$

	/** JVM os.version property name */
	public static final String JVM_OS_VERSION = "os.version"; //$NON-NLS-1$

	/** JVM user.language property name */
	public static final String JVM_USER_LANGUAGE = "user.language"; //$NON-NLS-1$

	/** JVM user.region property name */
	public static final String JVM_USER_REGION = "user.region"; //$NON-NLS-1$

	/** J2ME configuration property name */
	public static final String J2ME_MICROEDITION_CONFIGURATION = "microedition.configuration"; //$NON-NLS-1$

	/** J2ME profile property name */
	public static final String J2ME_MICROEDITION_PROFILES = "microedition.profiles"; //$NON-NLS-1$

	/** Persistent bundle status */
	public static final int BUNDLE_STARTED = 0x00000001;

	/** Property file locations and default names. */
	public static final String OSGI_PROPERTIES = "osgi.framework.properties"; //$NON-NLS-1$
	public static final String DEFAULT_OSGI_PROPERTIES = "osgi.properties"; //$NON-NLS-1$

	/** OSGI system package property */
	public static final String OSGI_FRAMEWORK_SYSTEM_PACKAGES = "org.osgi.framework.system.packages"; //$NON-NLS-1$
	public static final String OSGI_BOOTDELEGATION = "org.osgi.framework.bootdelegation"; //$NON-NLS-1$
	public static final String OSGI_SYSTEM_BUNDLE = "system.bundle"; //$NON-NLS-1$
	private static String INTERNAL_SYSTEM_BUNDLE = "org.eclipse.osgi"; //$NON-NLS-1$

	public static String getInternalSymbolicName() {
		return INTERNAL_SYSTEM_BUNDLE;
	}

	static void setInternalSymbolicName(String name) {
		INTERNAL_SYSTEM_BUNDLE = name;
	}

	/** OSGI implementation version properties key */
	public static final String OSGI_IMPL_VERSION_KEY = "osgi.framework.version"; //$NON-NLS-1$
	/** OSGi java profile; used to give a URL to a java profile */
	public static final String OSGI_JAVA_PROFILE = "osgi.java.profile"; //$NON-NLS-1$
	public static final String OSGI_JAVA_PROFILE_NAME = "osgi.java.profile.name"; //$NON-NLS-1$
	/** 
	 * OSGi java profile bootdelegation; used to indicate how the org.osgi.framework.bootdelegation
	 * property defined in the java profile should be processed, (ingnore, override, none). default is ignore
	 */
	public static final String OSGI_JAVA_PROFILE_BOOTDELEGATION = "osgi.java.profile.bootdelegation"; //$NON-NLS-1$
	/** indicates that the org.osgi.framework.bootdelegation in the java profile should be ingored */
	public static final String OSGI_BOOTDELEGATION_IGNORE = "ignore"; //$NON-NLS-1$
	/** indicates that the org.osgi.framework.bootdelegation in the java profile should override the system property */
	public static final String OSGI_BOOTDELEGATION_OVERRIDE = "override"; //$NON-NLS-1$
	/** indicates that the org.osgi.framework.bootdelegation in the java profile AND the system properties should be ignored */
	public static final String OSGI_BOOTDELEGATION_NONE = "none"; //$NON-NLS-1$
	/** OSGi strict delegation **/
	public static final String OSGI_RESOLVER_MODE = "osgi.resolverMode"; //$NON-NLS-1$
	public static final String STRICT_MODE = "strict"; //$NON-NLS-1$

	public static final String OSGI_FRAMEWORKBEGINNINGSTARTLEVEL = "osgi.framework.beginningstartlevel"; //$NON-NLS-1$

	/** Properties defaults */
	public static final String DEFAULT_STARTLEVEL = "1"; //$NON-NLS-1$

	/** Eclipse-SystemBundle header */
	public static final String ECLIPSE_SYSTEMBUNDLE = "Eclipse-SystemBundle"; //$NON-NLS-1$
	public static final String ECLIPSE_PLATFORMFILTER = "Eclipse-PlatformFilter"; //$NON-NLS-1$

	/**
	 * Manifest Export-Package directive indicating that the exported package should only 
	 * be made available when the resolver is not in strict mode.
	 */
	public static final String INTERNAL_DIRECTIVE = "x-internal"; //$NON-NLS-1$

	/**
	 * Manifest Export-Package directive indicating that the exported package should only 
	 * be made available to friends of the exporting bundle.
	 */
	public static final String FRIENDS_DIRECTIVE = "x-friends"; //$NON-NLS-1$

	/**
	 * Manifest header (named &quot;Provide-Package&quot;)
	 * identifying the packages name
	 * provided to other bundles which require the bundle.
	 *
	 * <p>
	 * NOTE: this is only used for backwards compatibility, bundles manifest using
	 * syntax version 2 will not recognize this header.
	 *
	 * <p>The attribute value may be retrieved from the
	 * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
	 * @deprecated
	 */
	public final static String PROVIDE_PACKAGE = "Provide-Package"; //$NON-NLS-1$

	/**
	 * Manifest header attribute (named &quot;reprovide&quot;)
	 * for Require-Bundle
	 * identifying that any packages that are provided
	 * by the required bundle must be reprovided by the requiring bundle.
	 * The default value is <tt>false</tt>.
	 * <p>
	 * The attribute value is encoded in the Require-Bundle manifest 
	 * header like:
	 * <pre>
	 * Require-Bundle: com.acme.module.test; reprovide="true"
	 * </pre>
	 * <p>
	 * NOTE: this is only used for backwards compatibility, bundles manifest using
	 * syntax version 2 will not recognize this attribute.
	 * @deprecated
	 */
	public final static String REPROVIDE_ATTRIBUTE = "reprovide"; //$NON-NLS-1$

	/**
	 * Manifest header attribute (named &quot;optional&quot;)
	 * for Require-Bundle
	 * identifying that a required bundle is optional and that
	 * the requiring bundle can be resolved if there is no 
	 * suitable required bundle.
	 * The default value is <tt>false</tt>.
	 *
	 * <p>The attribute value is encoded in the Require-Bundle manifest 
	 * header like:
	 * <pre>
	 * Require-Bundle: com.acme.module.test; optional="true"
	 * </pre>
	 * <p>
	 * NOTE: this is only used for backwards compatibility, bundles manifest using
	 * syntax version 2 will not recognize this attribute.
	 * @since 1.3 <b>EXPERIMENTAL</b>
	 * @deprecated
	 */
	public final static String OPTIONAL_ATTRIBUTE = "optional"; //$NON-NLS-1$

	/**
	 * Manifest header attribute (named &quot;require-packages&quot;)
	 * for Require-Bundle
	 * specifying the subset of packages that are accessible from
	 * the required bundle.  If the require-packages parameter
	 * is not specified then all packages provided by the required bundle
	 * are accessible.  The value of this parameter must be a quoted
	 * string.  The syntax of the quoted string value is the same as
	 * that of the Provide-Package manifest header value.
	 *
	 * <p> The attribute value is encoded in the Require-Bundle
	 * manifest header like:
	 * <pre>
	 * Require-Bundle: org.osgi.test;
	 *  require-packages="org.osgi.test.pkg1,org.osgi.test.pkg2"
	 * </pre>
	 * <p>
	 * NOTE: this is only used for backwards compatibility, bundles manifest using
	 * syntax version 2 will not recognize this attribute.
	 * @deprecated
	 */
	public final static String REQUIRE_PACKAGES_ATTRIBUTE = "require-packages"; //$NON-NLS-1$

	 /**
	 * The key used to designate the buddy loader associated with a given bundle.
	 */
	 public final static String BUDDY_LOADER = "Eclipse-BuddyPolicy"; //$NON-NLS-1$

	 public final static String REGISTERED_POLICY = "Eclipse-RegisterBuddy";  //$NON-NLS-1$

	/**
	 * Manifest header (named &quot;Reexport-Package&quot;) identifying the
	 * names of the packages that the bundle offers to the Framework for
	 * reexport.
	 * 
	 * <p>
	 * The attribute value may be retrieved from the <code>Dictionary</code>
	 * object returned by the <code>Bundle.getHeaders</code> method.
	 * 
	 * @deprecated
	 */
	 public final static String REEXPORT_PACKAGE = "Reexport-Package"; //$NON-NLS-1$
}
