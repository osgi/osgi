/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.framework;

import java.util.Dictionary;

/**
 * Defines standard names for the OSGi environment property, service property,
 * and Manifest header attribute keys.
 *
 * <p>The values associated with these keys are of type <tt>java.lang.String</tt>,
 * unless otherwise indicated.
 *
 * @version $Revision$
 * @since 1.1
 * @see Bundle#getHeaders()
 * @see BundleContext#getProperty
 * @see BundleContext#registerService(String[],Object,Dictionary)
 */

public interface Constants
{
    /**
     * Location identifier of the OSGi <i>system bundle</i>, which is
     * defined to be &quot;System Bundle&quot;.
     */
    public static final String SYSTEM_BUNDLE_LOCATION = "System Bundle";

    /**
     * Manifest header (named &quot;Bundle-Category&quot;)
     * identifying the bundle's category.
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_CATEGORY = "Bundle-Category";

    /**
     * Manifest header (named &quot;Bundle-ClassPath&quot;)
     * identifying a list of nested JAR files, which are bundle resources used
     * to extend the bundle's classpath.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_CLASSPATH = "Bundle-ClassPath";

    /**
     * Manifest header (named &quot;Bundle-Copyright&quot;)
     * identifying the bundle's copyright information, which may be retrieved
     * from the <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_COPYRIGHT = "Bundle-Copyright";

    /**
     * Manifest header (named &quot;Bundle-Description&quot;)
     * containing a brief description of the bundle's functionality.
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_DESCRIPTION = "Bundle-Description";

    /**
     * Manifest header (named &quot;Bundle-Name&quot;)
     * identifying the bundle's name.
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_NAME = "Bundle-Name";

    /**
     * Manifest header (named &quot;Bundle-NativeCode&quot;)
     * identifying a number of hardware environments and the native language code
     * libraries that the bundle is carrying for each of these environments.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_NATIVECODE = "Bundle-NativeCode";

    /**
     * Manifest header (named &quot;Export-Package&quot;)
     * identifying the names (and optionally version numbers) of the packages
     * that the bundle offers to the Framework for export.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String EXPORT_PACKAGE = "Export-Package";

    /**
     * Manifest header (named &quot;Export-Service&quot;)
     * identifying the fully qualified class names of the services that the
     * bundle may register (used for informational purposes only).
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String EXPORT_SERVICE = "Export-Service";

    /**
     * Manifest header (named &quot;Import-Package&quot;)
     * identifying the names (and optionally, version numbers) of the packages
     * that the bundle is dependent on.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String IMPORT_PACKAGE = "Import-Package";

    /**
     * Manifest header (named &quot;DynamicImport-Package&quot;)
     * identifying the names of the packages
     * that the bundle may dynamically import during execution.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     * @since 1.2
     */
    public static final String DYNAMICIMPORT_PACKAGE = "DynamicImport-Package";

    /**
     * Manifest header (named &quot;Import-Service&quot;)
     * identifying the fully qualified class names of the services that the
     * bundle requires (used for informational purposes only).
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String IMPORT_SERVICE = "Import-Service";

    /**
     * Manifest header (named &quot;Bundle-Vendor&quot;)
     * identifying the bundle's vendor.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_VENDOR = "Bundle-Vendor";

    /**
     * Manifest header (named &quot;Bundle-Version&quot;)
     * identifying the bundle's version.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_VERSION = "Bundle-Version";

    /**
     * Manifest header (named &quot;Bundle-DocURL&quot;)
     * identifying the bundle's documentation URL, from which further
     * information about the bundle may be obtained.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_DOCURL = "Bundle-DocURL";

    /**
     * Manifest header (named &quot;Bundle-ContactAddress&quot;)
     * identifying the contact address where problems with the
     * bundle may be reported; for example, an email address.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_CONTACTADDRESS = "Bundle-ContactAddress";

    /**
     * Manifest header attribute (named &quot;Bundle-Activator&quot;)
     * identifying the bundle's activator class.
     *
     * <p>If present, this header specifies the name of the bundle resource
     * class that implements the <tt>BundleActivator</tt> interface and whose
     * <tt>start</tt> and <tt>stop</tt> methods are called by the Framework
     * when the bundle is started and stopped, respectively.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_ACTIVATOR = "Bundle-Activator";

    /**
     * Manifest header (named &quot;Bundle-UpdateLocation&quot;)
     * identifying the location from which a new bundle version is
     * obtained during a bundle update operation.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     */
    public static final String BUNDLE_UPDATELOCATION = "Bundle-UpdateLocation";

    /**
     * Manifest header attribute (named &quot;specification-version&quot;)
     * identifying the version of a package specified in the
	 * Export-Package or Import-Package manifest header.
     *
     * <p>The attribute value is encoded in the Export-Package or
	 * Import-Package manifest header like:
     * <pre>
     * Import-Package: org.osgi.framework ; specification-version="1.1"
     * </pre>
     */
    public static final String PACKAGE_SPECIFICATION_VERSION =
    "specification-version";

    /**
     * Manifest header attribute (named &quot;processor&quot;) identifying the processor
	 * required to run native bundle code specified in the Bundle-NativeCode manifest header).
     *
	 * <p>The attribute value is encoded in the Bundle-NativeCode manifest header like:
     * <pre>
     * Bundle-NativeCode: http.so ; processor=x86 ...
     * </pre>
     */
    public static final String BUNDLE_NATIVECODE_PROCESSOR = "processor";

    /**
     * Manifest header attribute (named &quot;osname&quot;) identifying the
     * operating system required to run native bundle code specified in the Bundle-NativeCode
	 * manifest header).
	 * <p>The attribute value is encoded in the Bundle-NativeCode manifest header like:
     * <pre>
     * Bundle-NativeCode: http.so ; osname=Linux ...
     * </pre>
     */
    public static final String BUNDLE_NATIVECODE_OSNAME = "osname";

    /**
     * Manifest header attribute (named &quot;osversion&quot;) identifying the
     * operating system version required to run native bundle code specified in the Bundle-NativeCode
	 * manifest header).
	 * <p>The attribute value is encoded in the Bundle-NativeCode manifest header like:
     * <pre>
     * Bundle-NativeCode: http.so ; osversion="2.34" ...
     * </pre>
     */
    public static final String BUNDLE_NATIVECODE_OSVERSION = "osversion";

    /**
     * Manifest header attribute (named &quot;language&quot;) identifying the
     * language in which the native bundle code is written specified in the
	 * Bundle-NativeCode manifest header. See ISO 639 for possible values.
	 * <p>The attribute value is encoded in the Bundle-NativeCode manifest header like:
     * <pre>
     * Bundle-NativeCode: http.so ; language=nl_be ...
     * </pre>
     */
    public static final String BUNDLE_NATIVECODE_LANGUAGE = "language";

    /**
     * Manifest header (named &quot;Bundle-RequiredExecutionEnvironment&quot;)
     * identifying the required
     * execution environment for the bundle.  The service platform may run this
     * bundle if any of the execution environments named in this header matches
     * one of the execution environments it
     * implements.
     *
     * <p>The attribute value may be retrieved from the
     * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
     * @since 1.2
     */
    public static final String BUNDLE_REQUIREDEXECUTIONENVIRONMENT = "Bundle-RequiredExecutionEnvironment";


    /*
     * Framework environment properties.
     */

    /**
     * Framework environment property (named &quot;org.osgi.framework.version&quot;)
     * identifying the Framework version.
     *
     * <p>The value of this property may be retrieved by calling the
    * <tt>BundleContext.getProperty</tt> method.
     */
    public static final String FRAMEWORK_VERSION =
    "org.osgi.framework.version";

    /**
     * Framework environment property (named &quot;org.osgi.framework.vendor&quot;)
     * identifying the Framework implementation vendor.
     *
     * <p>The value of this property may be retrieved by calling the
     * <tt>BundleContext.getProperty</tt> method.
     */
    public static final String FRAMEWORK_VENDOR = "org.osgi.framework.vendor";

    /**
     * Framework environment property (named &quot;org.osgi.framework.language&quot;)
     * identifying the Framework implementation language (see ISO 639 for possible values).
     *
     * <p>The value of this property may be retrieved by calling the
     * <tt>BundleContext.getProperty</tt> method.
     */
    public static final String FRAMEWORK_LANGUAGE =
    "org.osgi.framework.language";

    /**
     * Framework environment property (named &quot;org.osgi.framework.os.name&quot;)
     * identifying the Framework host-computer's operating system.
     *
     * <p>The value of this property may be retrieved by calling the <tt>BundleContext.getProperty</tt> method.
     */
    public static final String FRAMEWORK_OS_NAME =
    "org.osgi.framework.os.name";

    /**
     * Framework environment property (named &quot;org.osgi.framework.os.version&quot;)
     * identifying the Framework host-computer's operating system version number.
     *
     * <p>The value of this property may be retrieved by calling the <tt>BundleContext.getProperty</tt> method.
     */
    public static final String FRAMEWORK_OS_VERSION =
    "org.osgi.framework.os.version";

    /**
     * Framework environment property (named &quot;org.osgi.framework.processor&quot;)
     * identifying the Framework host-computer's processor name.
     * <p>The value of this property may be retrieved by calling the <tt>BundleContext.getProperty</tt> method.
     */
    public static final String FRAMEWORK_PROCESSOR =
    "org.osgi.framework.processor";

    /**
     * Framework environment property (named &quot;org.osgi.framework.executionenvironment&quot;)
     * identifying execution environments provided by the Framework.
     * <p>The value of this property may be retrieved by calling the <tt>BundleContext.getProperty</tt> method.
     * @since 1.2
     */
    public static final String FRAMEWORK_EXECUTIONENVIRONMENT =
    "org.osgi.framework.executionenvironment";


    /*
     * Service properties.
     */

    /**
     * Service property (named &quot;objectClass&quot;)
     * identifying all of the class names under which a service was registered in the Framework
     * (of type <tt>java.lang.String[]</tt>).
     *
     * <p>This property is set by the Framework when a service is registered.
     */
    public static final String OBJECTCLASS = "objectClass";

    /**
     * Service property (named &quot;service.id&quot;) identifying a service's
     * registration number (of type <tt>java.lang.Long</tt>).
     *
     * <p>The value of this property is assigned by the Framework when a
     * service is registered. The Framework assigns a unique value that
     * is larger than all previously assigned values since the Framework was
     * started.
     * These values are NOT persistent across restarts of the Framework.
     */
    public static final String SERVICE_ID = "service.id";

    /**
     * Service property (named &quot;service.pid&quot;) identifying a service's
     * persistent identifier.
     *
     * <p>This property may be supplied in the <tt>properties</tt>
     * <tt>Dictionary</tt> object passed to the <tt>BundleContext.registerService</tt> method.
     *
     * <p>A service's persistent identifier uniquely identifies the service
     * and persists across multiple Framework invocations.
     *
     * <p>By convention, every bundle has its own unique namespace,
     * starting with the bundle's identifier (see {@link Bundle#getBundleId})
     * and followed by a dot (.). A bundle may use this as the prefix of the
     * persistent identifiers for the services it registers.
     */
    public static final String SERVICE_PID = "service.pid";

    /**
     * Service property (named &quot;service.ranking&quot;)
     * identifying a service's ranking number (of type <tt>java.lang.Integer</tt>).
     *
     * <p>This property may be supplied in the <tt>properties
     * Dictionary</tt> object passed to the <tt>BundleContext.registerService</tt> method.
     *
     * <p>The service ranking is used by the Framework to determine the
     * <i>default</i> service to be returned from a call to the
     * {@link BundleContext#getServiceReference}method:
     * If more than one service implements the specified class, the <tt>ServiceReference</tt> object with
     * the highest ranking is returned.
     *
     * <p>The default ranking is zero (0). A service with a ranking of <tt>Integer.MAX_VALUE</tt>
     * is very likely to be returned as the default service, whereas a service with a ranking of
     * <tt>Integer.MIN_VALUE</tt> is very unlikely to be returned.
     *
     * <p>If the supplied property value is not of type <tt>java.lang.Integer</tt>,
     * it is deemed to have a ranking value of zero.
     */
     public static final String SERVICE_RANKING = "service.ranking";

    /**
     * Service property (named &quot;service.vendor&quot;) identifying a service's vendor.
     *
     * <p>This property may be supplied in the properties <tt>Dictionary</tt> object passed to
     * the <tt>BundleContext.registerService</tt> method.
     */
     public static final String SERVICE_VENDOR = "service.vendor";

    /**
     * Service property (named &quot;service.description&quot;)
     * identifying a service's description.
     *
     * <p>This property may be supplied in the properties <tt>Dictionary</tt>
     * object passed to the <tt>BundleContext.registerService</tt> method.
     */
	public static final String SERVICE_DESCRIPTION = "service.description";

	/**
	 * Manifest header (named &quot;Bundle-SymbolicName&quot;)
	 * identifying the bundle's symbolic name.
	 * <p>The attribute value may be retrieved from the
	 * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
	 * @since 1.3 
	 */
	public final static String BUNDLE_SYMBOLICNAME = "Bundle-SymbolicName";
	
	/**
	 * Manifest header attribute (named &quot;singleton&quot;)
	 * identifying whether a bundle is a singleton.
	 * The default value is <tt>false</tt>.
	 *
	 * <p>The attribute value is encoded in the Bundle-SymbolicName
	 * manifest header like:
	 * <pre>
	 * Bundle-SymbolicName: com.acme.module.test; singleton=true
	 * </pre>
	 * @since 1.3 
	 */
	public final static String SINGLETON_ATTRIBUTE = "singleton";
	
	/**
	 * Manifest header (named &quot;Bundle-Localization&quot;)
	 * identifying the base name of the bundle's localization file.
	 * <p>The attribute value may be retrieved from the
	 * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
	 * @since 1.3 
	 */
	public final static String BUNDLE_LOCALIZATION = "Bundle-Localization";
	
	/**
	 * Default value for the Bundle-Localization manifest header.
	 *
	 * @see #BUNDLE_LOCALIZATION
	 * @since 1.3 
	 */
	public final static String BUNDLE_LOCALIZATION_DEFAULT_BASENAME = "META-INF/bundle";

	/**
	 * Manifest header (named &quot;Provide-Package&quot;)
	 * identifying the packages name
	 * provided to other bundles which require the bundle.
	 *
	 * <p>The attribute value may be retrieved from the
	 * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
	 * @since 1.3 
	 */
	public final static String PROVIDE_PACKAGE = "Provide-Package";

	/**
	 * Manifest header (named &quot;Require-Bundle&quot;)
	 * identifying the symbolic names of other bundles
	 * required by the bundle.
	 *
	 * <p>The attribute value may be retrieved from the
	 * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
	 * @since 1.3 
	 */
	public final static String REQUIRE_BUNDLE = "Require-Bundle";

	/**
	 * Manifest header attribute (named &quot;bundle-version&quot;)
	 * identifying a range of versions for a bundle specified in the
	 * Require-Bundle or Fragment-Host manifest headers.
	 * The default value is <tt>0.0.0</tt>.
	 *
	 * <p>The attribute value is encoded in the Require-Bundle manifest
	 * header like:
	 * <pre>
	 * Require-Bundle: com.acme.module.test; bundle-version="1.1"
	 * Require-Bundle: com.acme.module.test; bundle-version="[1.0,2.0)"
	 * </pre>
	 * <p>
	 * The bundle-version attribute value uses a mathematical interval
	 * notation to specify a range of bundle versions.  A bundle-version
	 * attribute value specified as a single version means a version range that includes any
	 * bundle version greater than or equal to the specified version.
	 * @since 1.3 
	 */
	public static final String BUNDLE_VERSION_ATTRIBUTE = "bundle-version";

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
	 * @since 1.3 
	 */
	public final static String REPROVIDE_ATTRIBUTE = "reprovide";

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
	 * @since 1.3 
	 */
	public final static String OPTIONAL_ATTRIBUTE = "optional";

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
	 * @since 1.3 
	 */
	public final static String REQUIRE_PACKAGES_ATTRIBUTE = "require-packages";

	/**
	 * Manifest header (named &quot;Fragment-Host&quot;)
	 * identifying the symbolic name
	 * of another bundle for which that the bundle is a fragment.
	 *
	 * <p>The attribute value may be retrieved from the
	 * <tt>Dictionary</tt> object returned by the <tt>Bundle.getHeaders</tt> method.
	 * @since 1.3 
	 */
	public final static String FRAGMENT_HOST = "Fragment-Host";

	/**
	 * Manifest header attribute (named &quot;multiple-hosts&quot;)
	 * identifying if the fragment should attach to each bundle
	 * selected by the Fragment-Host manifest header.
	 * The default value is <tt>false</tt>.
	 *
	 * <p>The attribute value is encoded in the Fragment-Host
	 * manifest header like:
	 * <pre>
	 * Fragment-Host: com.acme.module.test; multiple-hosts="false"
	 * </pre>
	 * @since 1.3 
	 */
	public final static String MULTIPLE_HOSTS_ATTRIBUTE = "multiple-hosts";

	/**
	 * Manifest header attribute (named &quot;selection-filter&quot;) is used for
	 * selection by filtering based upon system properties.
	 *
	 * <p>The attribute value is encoded in
	 * manifest headers like:
	 * <pre>
	 * Bundle-NativeCode: libgtk.so; selection-filter="(ws=gtk)"; ...
	 * Bundle-ClassPath: base.jar, gtk.jar; selection-filter="(ws=gtk)", ...
	 * </pre>
	 * @since 1.3 
	 */
	public final static String SELECTION_FILTER_ATTRIBUTE = "selection-filter";

	/**
     * Framework environment property (named &quot;org.osgi.framework.hidepackages&quot;)
     * identifying packages to be made inaccessible in the bundle's parent classloader. 
     * 
     * The value of this property must conform to the syntax of the 
     * Provide-Package manifest header value.
     * 
     * <p>The value of this property may be retrieved by calling the
     * <tt>BundleContext.getProperty</tt> method.
	 * @since 1.3 
     */
    public static final String FRAMEWORK_HIDEPACKAGES = "org.osgi.framework.hidepackages";
}
