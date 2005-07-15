/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.*;

/**
 * Indicates the caller's authority to perform specific privileged administrative 
 * operations on or to get sensitive information about a bundle.
 * <pre>
 * Action               Methods
 * class                Bundle.loadClass
 * execute              Bundle.start
 *                      Bundle.stop
 * extensionLifecycle   BundleContext.installBundle
 *                      Bundle.update
 *                      Bundle.uninstall
 * lifecycle            BundleContext.installBundle
 *                      Bundle.update
 *                      Bundle.uninstall
 *                      StartLevel.setBundleStartLevel
 * listener             BundleContext.addBundleListener for SynchronousBundleListener
 *                      BundleContext.removeBundleListener for SynchronousBundleListener
 * metadata             Bundle.getHeaders
 *                      Bundle.getLocation
 * permission           PermissionAdmin.setPermissions
 *                      PermissionAdmin.setDefaultPermissions
 * resolve              PackageAdmin.refreshPackages
 *                      PackageAdmin.resolveBundles
 * resource             Bundle.getResource
 *                      Bundle.getEntry
 *                      Bundle.getEntryPaths
 *                      Bundle resource/entry URL creation
 * startlevel           StartLevel.setStartLevel
 *                      StartLevel.setInitialBundleStartLevel 
 *</pre> 
 * <p>The special action "*" will represent all actions.
 * 
 * @version $Revision$
 */

public final class AdminPermission extends Permission {
	static final long	serialVersionUID	= 307051004521261705L;

	/**
	 * The action string <code>class</code> (Value is "class").
	 */
	public final static String	CLASS	= "class";
	/**
	 * The action string <code>execute</code> (Value is "execute").
	 */
	public final static String	EXECUTE	= "execute";
	/**
	 * The action string <code>extensionLifecycle</code> (Value is "extensionLifecycle").
	 */
	public final static String	EXTENSIONLIFECYCLE	= "extensionLifecycle";
	/**
	 * The action string <code>lifecycle</code> (Value is "lifecycle").
	 */
	public final static String	LIFECYCLE	= "lifecycle";
	/**
	 * The action string <code>listener</code> (Value is "listener").
	 */
	public final static String	LISTENER	= "listener";
	/**
	 * The action string <code>metadata</code> (Value is "metadata").
	 */
	public final static String	METADATA	= "metadata";
	/**
	 * The action string <code>permission</code> (Value is "permission").
	 */
	public final static String	PERMISSION	= "permission";
	/**
	 * The action string <code>resolve</code> (Value is "resolve").
	 */
	public final static String	RESOLVE	= "resolve";
	/**
	 * The action string <code>resource</code> (Value is "resource").
	 */
	public final static String	RESOURCE	= "resource";
	/**
	 * The action string <code>startlevel</code> (Value is "startlevel").
	 */
	public final static String	STARTLEVEL	= "startlevel";

	/*
	 * NOTE: A framework implementor may also choose to replace this
	 * class in their distribution with a class that directly interfaces
	 * with the framework implementation.
	 */

	/*
	 * This class will load the AdminPermission class in the package named by the
	 * org.osgi.vendor.framework package. For each instance of this class, an
	 * instance of the vendor AdminPermission class will be created and this 
	 * class will delegate method calls to the vendor AdminPermission instance.
	 */
	private static final String packageProperty = "org.osgi.vendor.framework";
	private static final Constructor initStringString;
	private static final Constructor initBundleString;
	static {
		Constructor[] constructors = (Constructor[]) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				String packageName = System.getProperty(packageProperty);
				if (packageName == null) {
					throw new NoClassDefFoundError(packageProperty+" property not set");
				}
				
				Class delegateClass;
				try {
					delegateClass = Class.forName(packageName+".AdminPermission");
				}
				catch (ClassNotFoundException e) {
					throw new NoClassDefFoundError(e.toString());
				}

				Constructor[] result = new Constructor[2];
				try {
					result[0] = delegateClass.getConstructor(new Class[] {
							String.class, String.class});
					result[1] = delegateClass.getConstructor(new Class[] {
							Bundle.class, String.class});
				}
				catch (NoSuchMethodException e) {
					throw new NoSuchMethodError(e.toString());
				}
				
				return result;
			}
		});
		
		initStringString = constructors[0];		
		initBundleString = constructors[1];
	}
	
	/*
	 * This is the delegate permission created by the constructor.
	 */
	private final Permission delegate;

	/**
	 * Creates a new <code>AdminPermission</code> object that matches 
	 * all bundles and has all actions.  Equivalent to 
	 * AdminPermission("*","*");
	 */
	public AdminPermission()
	{
		this("*", "*"); //$NON-NLS-1$
	}

	/**
	 * Creates a new <code>AdminPermission</code> object for use by the <code>Policy</code>
	 * object to instantiate new <code>Permission</code> objects.
	 * 
	 * Null arguments are equivalent to "*"
	 *
	 * @param filter an X.500 Distinguished Name suffix or "*" to match all bundles
	 * @param actions <code>class</code>, <code>execute</code>, <code>lifecycle</code>, 
	 * <code>listener</code>, <code>metadata</code>, <code>permission</code>, <code>resolve</code>, 
	 * <code>resource</code>, <code>startlevel</code>, or "*" to indicate all actions
	 */
	public AdminPermission(String filter, String actions)
	{
		//arguments will be null if called from a PermissionInfo defined with no args
		super(filter == null ? "*" : filter);
		try {
			try {
				delegate = (Permission) initStringString.newInstance(new Object[] {filter, actions});
			}
			catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
		catch (Error e) {
			throw e;
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Throwable e) {
			throw new RuntimeException(e.toString());
		}
	}

	/**
	 * Creates a new <code>AdminPermission</code> object to be used by the code
	 * that must check a <code>Permission</code> object.
	 * 
	 * @param bundle A bundle
	 * @param actions <code>class</code>, <code>execute</code>, <code>lifecycle</code>, 
	 * <code>listener</code>, <code>metadata</code>, <code>permission</code>, <code>resolve</code>, 
	 * <code>resource</code>, <code>startlevel</code>, or "*" to indicate all actions
	 */
	public AdminPermission(Bundle bundle, String actions) {
		super(Long.toString(bundle.getBundleId()));
		try {
			try {
				delegate = (Permission) initBundleString.newInstance(new Object[] {bundle, actions});
			}
			catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}
		catch (Error e) {
			throw e;
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Throwable e) {
			throw new RuntimeException(e.toString());
		}
	}

    /**
     * Determines the equality of two <code>AdminPermission</code> objects. <p>Two 
     * <code>AdminPermission</code> objects are equal.
     *
     * @param obj The object being compared for equality with this object.
     * @return <code>true</code> if <code>obj</code> is equivalent to this 
     * <code>AdminPermission</code>; <code>false</code> otherwise.
     */
    public boolean equals(Object obj)
    {
        if (obj == this) {
        	return true;
        }
        
        if (!(obj instanceof AdminPermission))
        {
            return false;
        }
        
        AdminPermission p = (AdminPermission) obj;

        return delegate.equals(p.delegate);
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return Hash code value for this object.
     */
	public int hashCode() {
		return delegate.hashCode();
	}

    /**
     * Returns the canonical string representation of the <code>AdminPermission</code> actions.
     *
     * <p>Always returns present <code>AdminPermission</code> actions in the following order:
     * <code>CLASS</code>, <code>EXECUTE</code>, <code>EXTENSIONLIFECYCLE</code>, <code>LIFECYCLE</code>, <code>LISTENER</code>, 
     * <code>METADATA</code>, <code>PERMISSION</code>, <code>RESOLVE</code>, <code>RESOURCE</code>, 
     * <code>STARTLEVEL</code>.
     * @return Canonical string representation of the <code>AdminPermission</code> actions.
     */
	public String getActions() {
		return delegate.getActions();
	}

    /**
     * Determines if the specified permission is implied by this object.
     * This method throws an exception if the specified permission was not
     * constructed with a bundle.
     * 
     * <p>This method returns <code>true</code> if
     * The specified permission is an AdminPermission AND
     * <ul>
     * 	<li>this object's filter is an X.500 Distinguished name suffix that 
     * matches the specified permission's bundle OR
     * 	<li>this object's filter is "*" OR
     * 	<li>this object's bundle is a equal to the specified permission's
     * bundle
     * </ul>
     * AND this object's actions include all of the specified permission's actions 	 
     *
     * Special case: if the specified permission was constructed with "*", then this method
     * returns <code>true</code> if this object's filter is "*" and this object's actions include
     * all of the specified permission's actions
     * 
     * @param p The permission to interrogate.
     *
     * @return <code>true</code> if the specified permission is implied by
     * this object; <code>false</code> otherwise.
     * @throws RuntimeException if specified permission was not constructed with
     * a bundle or "*"
     */
    public boolean implies(Permission p)
    {
    	if (!(p instanceof AdminPermission)) {
    		return false;
    	}
    	
        AdminPermission pp = (AdminPermission) p;
    	return delegate.implies(pp.delegate);
    }

    /**
     * Returns a new <code>PermissionCollection</code> object suitable for storing
     * <code>AdminPermission</code>s.
     * 
     * @return A new <code>PermissionCollection</code> object.
     */
	public PermissionCollection newPermissionCollection() {
		return delegate.newPermissionCollection();
	}
}
