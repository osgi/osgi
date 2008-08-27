/*
 * $Date$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.framework.launch;

import java.util.Properties;

import org.osgi.framework.Bundle;

/**
 * This interface should be implemented by framework implementations when their
 * main object is created. It allows a configurator to set the properties and
 * launch the framework.
 * 
 * <p>
 * Implementations must provide a public constructor that takes a single
 * configuration {@link Properties} object argument. The configuration
 * Properties object provides the framework with configuration settings and must
 * be used for the framework properties. The configuration properties can
 * optionally be backed by other Properties such as the System properties The
 * framework must use this properties as the only source by using getProperty
 * (not get) so that the configurator can use the Properties linked behavior. If
 * the properties object is null, useful defaults should be used to make the
 * framework run appropriately in the current VM. I.e. the system packages for
 * the current execution environment should be properly exported. Any persistent
 * area should be defined in the current directory with a framework
 * implementation specific name.
 * 
 * 
 * </p>
 * 
 * TODO The javadoc in this class need a good scrub before release.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface SystemBundle extends Bundle {
	/**
	 * Specifies the the type of security manager the framework must use. If not
	 * specified then the framework will not set the VM security manager. The
	 * following types are defined:
	 * <ul>
	 * <li>
	 * osgi - Enables a security manager that supports all security aspects of
	 * the OSGi R4.0 specification (including postponed conditions).</li>
	 * </ul>
	 */
	String SECURITY = "org.osgi.framework.security";

	/**
	 * A valid file path in the file system to a directory that exists. The
	 * framework is free to use this directory as it sees fit. This area can not
	 * be shared with anything else. If this property is not set, the framework
	 * should use a file area from the parent bundle. If it is not embedded, it
	 * must use a reasonable platform default.
	 */
	String STORAGE = "org.osgi.framework.storage";

	/**
	 * A comma separated list of additional library file extensions that must be
	 * searched for when a bundle's {@link ClassLoader#findLibrary(String)}. If
	 * not set then only the library name returned by
	 * System.mapLibraryName(String) will be used to search. This is needed for
	 * certain operating systems which allow more than one extension for a
	 * library. For example AIX allows library extensions of .a and .so, but
	 * System.mapLibraryName(String) will only return names with the .a
	 * extension.
	 */
	String LIBRARIES = "org.osgi.framework.library.extensions";

	/**
	 * Specifies an optional OS specific command to set file permissions on
	 * extracted native code. On some operating systems it is required that
	 * native libraries be set to executable. This optional property allows you
	 * to specify the command. For example, on a UNIX style OS you could have
	 * the following value:
	 * 
	 * <pre>
	 * org.osgi.framework.command.execpermission = &quot;chmod +rx [fullpath]&quot;
	 * </pre>
	 * 
	 * The [fullpath] is used to substitute the actual file path by the
	 * framework.
	 */
	String EXECPERMISSION = "org.osgi.framework.command.execpermission";

	/**
	 * Points to a directory with certificates. ###??? Keystore? Certificate
	 * format?
	 */
	String ROOT_CERTIFICATES = "org.osgi.framework.root.certificates";

	/**
	 * Set by the configurator but the framework should provide a reasonable
	 * default.
	 */
	String WINDOWSYSTEM = "org.osgi.framework.windowsystem";

	/**
	 * Specifies the beginning start level of the framework (See StartLevel
	 * service specification for more information).
	 */
	String STARTLEVEL = "org.osgi.framework.startlevel";

	/**
	 * Configure this framework with the given properties. These properties can
	 * contain framework specific properties or of the general kind defined in
	 * the specification or in this interface.
	 * 
	 * 
	 */
	void init();

	/**
	 * Wait until the framework is completely finished.
	 * 
	 * This method will return if the framework is stopped and has cleaned up
	 * all the framework resources.
	 * 
	 * @param timeout
	 *            Maximum number of milliseconds to wait until the framework is
	 *            finished. Specifying a zero will wait indefinitely.
	 * @throws InterruptedException
	 *             When the wait was interrupted
	 * 
	 */

	void waitForStop(long timeout) throws InterruptedException;
}
