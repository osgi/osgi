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
import org.osgi.framework.BundleException;

/**
 * The System Bundle of a Framework instance.
 * 
 * The <i>main</i> class of a framework implementation must implement this
 * interface. The instantiator of the framework implementation class then has a
 * system bundle object and can then use the methods of this interface to manage
 * and control the created framework instance.
 * 
 * <p>
 * The <i>main</i> class of a framework implementation must provide a public
 * constructor that takes a single {@link Properties} argument. The argument
 * provides this system bundle with configuration settings and must be used to
 * set the framework properties. The configuration properties can optionally be
 * backed by other Properties such as the System Properties The framework
 * instance must use these properties as the only source by using
 * {@link Properties#getProperty getProperty}, rather than <code>get</code>, so
 * that the Properties linked behavior is used. If the configuration argument is
 * null, this system bundle must use some useful default configuration
 * appropriate for the current VM. For example, the system packages for the
 * current execution environment should be properly exported.
 * 
 * <p>
 * A newly constructed system bundle must be in the {@link Bundle#INSTALLED
 * INSTALLED} state.
 * 
 * @ThreadSafe
 * @version $Revision$
 */
public interface SystemBundle extends Bundle {
	/**
	 * Specifies the the type of security manager the system bundle must use. If
	 * not specified then the system bundle will not set the VM security
	 * manager. The following types are defined:
	 * <ul>
	 * <li> osgi - Enables a security manager that supports all security aspects
	 * of the OSGi R4 specification (including postponed conditions).</li>
	 * </ul>
	 */
	String	SECURITY			= "org.osgi.framework.security";

	/**
	 * A valid file path in the file system to a directory that exists. The
	 * system bundle is free to use this directory as it sees fit. This area can
	 * not be shared with anything else. If this property is not set, the system
	 * bundle should use a persistent storage area in the current directory with
	 * a framework implementation specific name.
	 */
	String	STORAGE				= "org.osgi.framework.storage";

	/**
	 * A comma separated list of additional library file extensions that must be
	 * searched for when a bundle's class loader is searching for native
	 * libraries. If not set then only the library name returned by
	 * <code>System.mapLibraryName(String)</code> will be used to search. This
	 * is needed for certain operating systems which allow more than one
	 * extension for a library. For example AIX allows library extensions of
	 * <code>.a</code> and <code>.so</code>, but System.mapLibraryName(String)
	 * will only return names with the <code>.a</code> extension.
	 */
	String	LIBRARY_EXTENSIONS	= "org.osgi.framework.library.extensions";

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
	String	EXECPERMISSION		= "org.osgi.framework.command.execpermission";

	/**
	 * Points to a directory with certificates. ###??? Keystore? Certificate
	 * format?
	 * 
	 * TODO Need to complete this description
	 */
	String	ROOT_CERTIFICATES	= "org.osgi.framework.root.certificates";

	/**
	 * Specifies the current windowing system. The system bundle should provide
	 * a reasonable default if this is not set.
	 */
	String	WINDOWSYSTEM		= "org.osgi.framework.windowsystem";

	/**
	 * Specifies the beginning start level of the system bundle (See StartLevel
	 * service specification for more information).
	 */
	String	STARTLEVEL			= "org.osgi.framework.startlevel";

	/**
	 * Initialize this system bundle. After calling this method, this system
	 * bundle must be in the {@link Bundle#STARTING STARTING} state and must
	 * have a valid bundle context. This system bundle will not actually be
	 * started until <code>start</code> is called.
	 * 
	 * @throws BundleException If this system bundle could not be initialized.
	 */
	void init() throws BundleException;

	/**
	 * Wait until this system bundle is completely finished stopping. The
	 * <code>stop</code> method on a system bundle performs an asynchronous stop
	 * of the system bundle. This method can be used to wait until the
	 * asynchronous stop of this system bundle has completed. This method will
	 * only wait if called when this system bundle is in the
	 * {@link Bundle#STOPPING STOPPING} state.
	 * 
	 * This method must only be called after calling <code>stop</code> and will
	 * return when this system bundle is fully stopped and has cleaned up all
	 * its resources.
	 * 
	 * @param timeout Maximum number of milliseconds to wait until this system
	 *        bundle has finished stopping. A value of zero will wait
	 *        indefinitely.
	 * @throws InterruptedException If another thread interrupted the current
	 *         thread before or while the current thread was waiting for this
	 *         system bundle to finish stopping. The <i>interrupted status</i>
	 *         of the current thread is cleared when this exception is thrown.
	 * @throws IllegalStateException If this method is called when this system
	 *         bundle is in the {@link Bundle#STARTING STARTING} or
	 *         {@link Bundle#ACTIVE ACTIVE} states.
	 * @throws IllegalArgumentException If the value of timeout is negative.
	 */
	void waitForStop(long timeout) throws InterruptedException;
}
