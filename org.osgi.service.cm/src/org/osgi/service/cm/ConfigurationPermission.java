/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.cm;

import java.lang.reflect.Constructor;
import java.security.*;

import org.osgi.framework.Bundle;

/**
 * Indicates a bundle's authority to set or get a Configuration.
 * 
 * This permission verifies if the caller has the authority to get the
 * configuration of a specific PID and to get set it. The target of this
 * permission is a filter expression. The filter gives access to the following
 * parameters:
 * <ul>
 * <li>signer - The signer of the bundle that receives the configuration</li>
 * <li>location - The location of the bundle that receives the configuration
 * </li>
 * <li>id - The id of the bundle that receives the configuration</li>
 * <li>name - The bundle symbolic name of the bundle that receives the
 * configuration</li>
 * <li>pid - The PID for a singleton configuration</li>
 * <li>factoryPid - The Factory PID for a factory configuration</li>
 * </ul>
 * The target bundle is not always known ahead of time. In that case, signer,
 * name and location will not be set. The target bundle can be found by the fact
 * that it has already registered a target service with the given PID, or the
 * setter has set the bundle location, which identifies the target bundle. The
 * filter can also check what PID (for singletons) or factory PID (for
 * factories) is used. <br/>The actions for this permission are:
 * <ul>
 * <li>SET - The right to update a configuration. Checked during the
 * <code>update</code> method. This implies READ</li>
 * <li>GET - The right to be updated. The Configuration Admin must verify that
 * the bundle that receives the configuration has permission with the
 * <code>hasPermission</code> method before it calls the right variation of
 * the <code>updated</code> method.</li>
 * <li>READ - The right to see a Configuration object. This action is needed
 * also when creation a new configuration.</li>
 * <li>REBIND - The right to change the binding of an existing bound configuration.</li>
 * <li>PLUGIN - The right to be called as a plugin</li>
 * </ul>
 * Every bundle must implicitly given the permission to set its own
 * configuration.
 * 
 * @version $Revision$
 * @since 1.2
 */

public final class ConfigurationPermission extends Permission {
	static final long			serialVersionUID	= 5716868734811965383L;
	/**
	 * The action string <code>get</code>.
	 */
	public final static String	GET					= "get";
	/**
	 * The action string <code>set</code>. This implies ### what
	 */
	public final static String	SET					= "set";

	/**
	 * The action string <code>read</code>.
	 */
	public final static String	READ				= "read";

	/**
	 * The action string <code>rebind</code>.
	 */
	public static final String	REBIND				= "rebind";

	/*
	 * This class will load the ConfigurationPermission class in the package named by the
	 * org.osgi.vender.cm package. For each instance of this class, an
	 * instance of the vendor ConfigurationPermission class will be created and this 
	 * class will delegate method calls to the vendor ConfigurationPermission instance.
	 */
	private static final String packageProperty = "org.osgi.vendor.cm";
	private static final Constructor			initFilter;
	private static final Constructor			initBundle;
	static {
		Constructor[] constructors = (Constructor[]) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				String packageName = System.getProperty(packageProperty);
				if (packageName == null) {
					throw new NoClassDefFoundError(packageProperty+" property not set");
				}
				
				Class delegateClass;
				try {
					delegateClass = Class.forName(packageName+".ConfigurationPermission");
				}
				catch (ClassNotFoundException e) {
					throw new NoClassDefFoundError(e.toString());
				}

				Constructor[] result = new Constructor[2];
				try {
					result[0] = delegateClass.getConstructor(new Class[] {
							String.class, String.class});
					result[1] = delegateClass.getConstructor(new Class[] {
							Bundle.class, String.class, String.class, Integer.class});
				}
				catch (NoSuchMethodException e) {
					throw new NoSuchMethodError(e.toString());
				}
				
				return result;
			}
		});
	
		initFilter = constructors[0];		
		initBundle = constructors[1];
	}

	/**
	 * This is the delegate permission created by the constructor.
	 */
	private final Permission delegate;

	/**
	 * Create a new ConfigurationPermission.
	 * 
	 * This constructor must only be used to create a permission that is going
	 * to be checked. Configuration Admin service must use the alternate
	 * constructor.
	 * 
	 * <p>
	 * Examples:
	 * <pre>
	 *       (signer=*,o=ACME,c=US)   
	 *       (pid=com.acme.*)
	 *       (factoryPid=com.acme.*)
	 *       (&amp;(signer=*,o=ACME,c=US)(pid=com.acme.*))   
	 * </pre>
	 * 
	 * <p>
	 * There are the following actions: <code>get</code>,<code>set</code>,
	 * and <code>read</code>. The <code>set</code> action allows a bundle
	 * to create, list, get, update and delete configurations for the target.
	 * The <code>get</code> action allows a bundle to receive a configuration
	 * object. The target bundle is itself in that case. The <code>read</code>
	 * action permits listing the configurations. The <code>set</code> action
	 * implies <code>read</code>.
	 *
	 * @param filter A filter expression that can use signer, location, name, pid or factoryPid
	 * @param actions <code>get</code>, <code>set</code>, <code>read</code> (canonical order)
	 */

	public ConfigurationPermission(String filter, String actions) {
		super(filter);
		try {
			delegate = (Permission) initFilter
			.newInstance(new Object[] {filter, actions});
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e.toString());
		}

	}

	/**
	 * Create a new Configuration Permission. This constructor is the check
	 * version. It must be used by the Configuration Admin service to check the
	 * permission.
	 * 
	 * @param bundle The target bundle. This must be the bundle that will
	 *        receive the configuration.
	 * @param pid The PID in question (for factories, the PID is generated and
	 *        not checkeable).
	 * @param factoryPid The PID of the factory if this is a factory
	 *        configuration
	 * @param action The action (GET, SET, or READ [no combinations allowed])
	 */
	public ConfigurationPermission(Bundle bundle, String pid,
			String factoryPid, int action) {
		super(Long.toString(bundle.getBundleId()));
		try {
			delegate = (Permission) initBundle.newInstance(new Object[] {
					bundle, pid, factoryPid, new Integer(action)});
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e.toString());
		}

	}

	/**
	 * Determines if a <code>ConfigurationPermission</code> object "implies"
	 * the specified permission.
	 * 
	 * @param p The target permission to check.
	 * @return <code>true</code> if the specified permission is implied by
	 *         this object; <code>false</code> otherwise.
	 */

	public boolean implies(Permission p) {
    	if (!(p instanceof ConfigurationPermission)) {
    		return false;
    	}
    	
    	ConfigurationPermission pp = (ConfigurationPermission) p;
    	return delegate.implies(pp.delegate);
	}

    /**
     * Returns the canonical string representation of the <code>ConfigurationPermission</code> actions.
     *
     * <p>Always returns present <code>ConfigurationPermission</code> actions in the following order:
     * <code>GET</code>, <code>SET</code>, <code>READ</code>, <code>REBIND</code> 
     * @return Canonical string representation of the <code>ConfigurationPermission</code> actions.
     */
	public String getActions() {
		return delegate.getActions();
	}

    /**
     * Returns a new <code>PermissionCollection</code> object suitable for storing
     * <code>ConfigurationPermission</code>s.
     * 
     * @return A new <code>PermissionCollection</code> object.
     */
	
	public PermissionCollection newPermissionCollection() {
		return delegate.newPermissionCollection();
	}

    /**
     * Determines the equality of two <code>ConfigurationPermission</code> objects. <p>Two 
     * <code>ConfigurationPermission</code> objects are equal.
     *
     * @param obj The object being compared for equality with this object.
     * @return <code>true</code> if <code>obj</code> is equivalent to this 
     * <code>ConfigurationPermission</code>; <code>false</code> otherwise.
     */
    public boolean equals(Object obj)
    {
        if (obj == this) {
        	return true;
        }
        
        if (!(obj instanceof ConfigurationPermission))
        {
            return false;
        }
        
        ConfigurationPermission p = (ConfigurationPermission) obj;

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
}