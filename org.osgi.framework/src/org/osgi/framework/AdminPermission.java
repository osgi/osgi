/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.framework;

import java.io.InputStream;
import java.security.*;
import java.util.*;

/**
 * Indicates the caller's authority to perform specific privileged administrative 
 * operations on or to get sensitive information about a bundle.
 * 
 * <ul>
 *   <li>The <code>{@link AdminPermission#METADATA}</code> action allows calls to
 *   	<ul>
 *         <li>{@link Bundle#getHeaders()}
 *         <li>{@link Bundle#getHeaders(String)}
 *         <li>{@link Bundle#getLocation()}
 *         </ul>
 *   <li>The <code>{@link AdminPermission#RESOURCE}</code> action allows calls to
 *   	<ul>
 *   		<li>{@link Bundle#getResource(String)}
 *   		<li>{@link Bundle#getEntry(String)}
 *   		<li>{@link Bundle#getEntryPaths(String)}
 *   		<li>Bundle resource/entry URL creation
 *   	</ul>
 *   <li>The <code>{@link AdminPermission#METADATA}</code> action allows calls to
 *   	<ul>
 *   		<li>{@link Bundle#loadClass(String)}
 *   	</ul>
 *   <li>The <code>{@link AdminPermission#LIFECYCLE}</code> action allows calls to
 *   	<ul>
 *   		<li>{@link BundleContext#installBundle(String)}
 *   		<li>{@link BundleContext#installBundle(String, InputStream)}
 *   		<li>{@link Bundle#update()}
 *   		<li>{@link Bundle#update(InputStream)}
 *   		<li>{@link Bundle#uninstall()}
 *   	</ul>
 *   <li>The <code>{@link AdminPermission#EXECUTE}</code> action allows calls to
 *   	<ul>
 *   		<li>{@link Bundle#start()}
 *   		<li>{@link Bundle#stop()}
 *   		<li>{@link org.osgi.service.startlevel.StartLevel#setBundleStartLevel(Bundle, int)}
 *   	</ul>
 *   <li>The <code>{@link AdminPermission#LISTENER}</code> action allows calls to
 *   	<ul>
 *   		<li>{@link BundleContext#addBundleListener(BundleListener)} for 
 *   				<code>SynchronousBundleListener</code>
 *   		<li>{@link BundleContext#removeBundleListener(BundleListener)} for 
 *   				<code>SynchronousBundleListener</code>
 *   	</ul>
 *   <li>The <code>{@link AdminPermission#PERMISSION}</code> action allows calls to
 *   	<ul>
 *   		<li>{@link org.osgi.service.permissionadmin.PermissionAdmin#setPermissions(String, PermissionInfo[])}
 *   		<li>{@link org.osgi.service.permissionadmin.PermissionAdmin#setDefaultPermissions(PermissionInfo[])}
 *   	</ul>
 *   <li>The <code>{@link AdminPermission#RESOLVE}</code> action allows calls to
 *   	<ul>
 *   		<li>{@link org.osgi.service.packageadmin.PackageAdmin#refreshPackages(Bundle[])}</code>
 *   		<li>{@link org.osgi.service.packageadmin.PackageAdmin#resolveBundles(Bundle[])}</code>
 *   	</ul>
 *   <li>The <code>{@link AdminPermission#STARTLEVEL}</code> action allows calls to
 *   	<ul>
 *   		<li>{@link org.osgi.service.startlevel.StartLevel#setStartLevel(int)}
 *   		<li>{@link org.osgi.service.startlevel.StartLevel#setInitialBundleStartLevel(int)}
 *   	</ul>
 * </ul>
 * 
 * The special action "*" will represent all actions.
 * 
 * @version $Revision$
 */

public final class AdminPermission extends Permission
{
	static final long	serialVersionUID	= 207051004521261705L;
	/**
	 * The bundle governed by this AdminPermission - only used if 
	 * wildcard is false and filter == null
	 */
	protected Bundle bundle;
	
	/**
	 * Indicates that this AdminPermission refers to all bundles
	 */
	protected boolean wildcard;
	
	/**
	 * An x.500 distinguished name used to match a bundle's signature - only used if
	 * wildcard is false and bundle = null
	 */
	protected String filter;
	
    /**
     * The action string <code>class</code> (Value is "class").
     */
    public final static String CLASS = "class"; //$NON-NLS-1$

    /**
     * The action string <code>execute</code> (Value is "execute").
     */
    public final static String EXECUTE = "execute"; //$NON-NLS-1$
    
    /**
     * The action string <code>lifecycle</code> (Value is "lifecycle").
     */
    public final static String LIFECYCLE = "lifecycle"; //$NON-NLS-1$
    
    /**
     * The action string <code>listener</code> (Value is "listener").
     */
    public final static String LISTENER = "listener"; //$NON-NLS-1$
    
    /**
     * The action string <code>metadata</code> (Value is "metadata").
     */
    public final static String METADATA = "metadata"; //$NON-NLS-1$

    /**
     * The action string <code>permission</code> (Value is "permission").
     */
    public final static String PERMISSION = "permission"; //$NON-NLS-1$
	
    /**
     * The action string <code>resolve</code> (Value is "resolve").
     */
    public final static String RESOLVE = "resolve"; //$NON-NLS-1$

    /**
     * The action string <code>resource</code> (Value is "resource").
     */
    public final static String RESOURCE = "resource"; //$NON-NLS-1$
    
    /**
     * The action string <code>startlevel</code> (Value is "startlevel").
     */
    public final static String STARTLEVEL = "startlevel"; //$NON-NLS-1$

    /**
     * The action string <code>extensionLifecycle</code> (Value is "extensionLifecycle").
     */
    public final static String EXTENSIONLIFECYCLE = "extensionLifecycle"; //$NON-NLS-1$

    private final static int ACTION_CLASS				= 0x00000001;
    private final static int ACTION_EXECUTE				= 0x00000002;
    private final static int ACTION_LIFECYCLE			= 0x00000004;
    private final static int ACTION_LISTENER			= 0x00000008;
    private final static int ACTION_METADATA			= 0x00000010;
    private final static int ACTION_PERMISSION			= 0x00000020;
    private final static int ACTION_RESOLVE				= 0x00000040;
    private final static int ACTION_RESOURCE			= 0x00000080;
    private final static int ACTION_STARTLEVEL			= 0x00000100;
	private final static int ACTION_EXTENSIONLIFECYCLE	= 0x00000200;
    private final static int ACTION_ALL = 
		ACTION_CLASS 				|
		ACTION_EXECUTE 				|
		ACTION_LIFECYCLE 			|
		ACTION_LISTENER 			|
    	ACTION_METADATA 			|
		ACTION_PERMISSION			|
		ACTION_RESOLVE 				|
		ACTION_RESOURCE 			|
		ACTION_STARTLEVEL			|
		ACTION_EXTENSIONLIFECYCLE;
    private final static int ACTION_NONE = 0;

    /**
     * The actions mask.
     */
	protected transient int action_mask = ACTION_NONE;

    /**
     * The actions in canonical form.
     *
     * @serial
     */
    private String actions = null;
    
    /**
     * If this AdminPermission was constructed with a bundle, this dictionary holds
     * the properties of that bundle, used to match a filter in implies.
     * This is not initialized until necessary, and then cached in this object.
     */
    protected Dictionary bundleProperties;
    
    /**
     * If this AdminPermission was constructed with a filter, this dictionary holds
     * a Filter matching object used to evaluate the filter in implies.
     * This is not initialized until necessary, and then cached in this object
     */
    protected Filter filterImpl;
    
	/**
     * Creates a new <code>AdminPermission</code> object that matches 
     * all bundles and has all actions.  Equivalent to 
     * AdminPermission("*","*");
     */
    public AdminPermission()
    {
    	this("*",AdminPermission.ACTION_ALL); //$NON-NLS-1$
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
    	//arguments will be null if called from a PermissionInfo defined with
    	//no args
    	this(
    			(filter == null ? "*" : filter), //$NON-NLS-1$
				getMask((actions == null ? "*" : actions)) //$NON-NLS-1$
				);
    }

    /**
     * Creates a new <code>AdminPermission</code> object for use by the <code>Policy</code>
     * object to instantiate new <code>Permission</code> objects.
     * 
     * @param bundle A bundle
     * @param actions <code>class</code>, <code>execute</code>, <code>lifecycle</code>, 
     * <code>listener</code>, <code>metadata</code>, <code>permission</code>, <code>resolve</code>, 
     * <code>resource</code>, <code>startlevel</code>, or "*" to indicate all actions
     */
    public AdminPermission(Bundle bundle, String actions) {
    	super(bundle.toString());
    	this.bundle = bundle;
    	this.wildcard = false;
    	this.filter = null;
    	this.action_mask = getMask(actions);
    }
 
    /**
     * Package private constructor used by AdminPermissionCollection.
     *
     * @param filter name filter
     * @param action_mask mask
     */
    AdminPermission(String filter, int action_mask) {
    	super(filter);
    	
    	//name must be either * or a filter
    	if (filter.equals("*")) { //$NON-NLS-1$
    		this.wildcard = true;
    		this.filter = null;
    	} else {
			this.wildcard = false;
			this.filter = filter;
    	}
    	this.bundle = null;
    	this.action_mask = action_mask;
    }
    
//    /**
//     * TODO Canonicalize X.500 Distinguished Name Suffix
//     */
//    public static String canonicalize(String dnSuffix) {
//    	/*
//    	 * Escapes: 
//    	 *   o   a space or "#" character occurring at the beginning of the string
//    	 *   o   a space character occurring at the end of the string
//    	 *   o   one of the characters ",", "+", """, "\", "<", ">" or ";"
//    	 *   
//    	 * Implementations MAY escape other characters.
//    	 *   
//    	 * If a character to be escaped is one of the list shown above, then it
//    	 * is prefixed by a backslash ('\' ASCII 92).
//    	 * Otherwise the character to be escaped is replaced by a backslash and
//    	 * two hex digits, which form a single byte in the code of the
//    	 * character.
//    	 */
//
//    	//first break up in to tokens, dealing with escapes
//    	char [] chars = dnSuffix.toCharArray();
//    	int startIndex = 0;
//    	int endIndex = 1;
//    	List tokens = new ArrayList();
//    	while (endIndex < chars.length) {
//    		//find keys
//    		while (
//    				endIndex < chars.length && (
//    					chars[endIndex] != ',' || //find comma
//						chars[endIndex-1] == '\\' //avoid escaped commas
//						)
//					){
//    			endIndex++;
//    		}
//    		tokens.add(new String(chars,startIndex,endIndex-startIndex));
//    		
//    		endIndex++;
//    		startIndex = endIndex;
//    	}
//    	System.out.println("tokens: " + tokens.toString());
//    	/*
//    	 * Examples: 
//    	 * 
//    	 * CN=Steve Kille,O=Isode Limited,C=GB
//    	 * 
//    	 * multivalued:
//    	 * OU=Sales+CN=J. Smith,O=Widget Inc.,C=US
//    	 * 
//    	 * escaped comma:
//    	 * CN=L. Eagle,O=Sue\, Grabbit and Runn,C=GB
//    	 */
//    	
//    //	StringTokenizer tokenizer = new StreamTokenizer(dnSuffix);
//    	
//    	return null;
//    }
//    

    /**
     * Parse action string into action mask.
     *
     * @param actions Action string.
     * @return action mask.
     */
    private static int getMask(String actions) {
    
    	boolean seencomma = false;

    	int mask = ACTION_NONE;

    	if (actions == null) {
    		return mask;
    	}

    	char[] a = actions.toCharArray();

    	int i = a.length - 1;
    	if (i < 0)
    		return mask;

    	while (i != -1) {
    		char c;

    		// skip whitespace
    		while ((i!=-1) && ((c = a[i]) == ' ' ||
    				c == '\r' ||
					c == '\n' ||
					c == '\f' ||
					c == '\t'))
    			i--;

    		// check for the known strings
    		int matchlen;

    		if (i >= 4 && 
					(a[i-4] == 'c' || a[i-4] == 'C') &&
					(a[i-3] == 'l' || a[i-3] == 'L') &&
					(a[i-2] == 'a' || a[i-2] == 'A') &&
					(a[i-1] == 's' || a[i-1] == 'S') &&
					  (a[i] == 's' ||   a[i] == 'S'))
			{
				matchlen = 5;
				mask |= ACTION_CLASS;
	
    		} else if (i >= 6 && 
					(a[i-6] == 'e' || a[i-6] == 'E') &&
					(a[i-5] == 'x' || a[i-5] == 'X') &&
					(a[i-4] == 'e' || a[i-4] == 'E') &&
					(a[i-3] == 'c' || a[i-3] == 'C') &&
					(a[i-2] == 'u' || a[i-2] == 'U') &&
					(a[i-1] == 't' || a[i-1] == 'T') &&
					  (a[i] == 'e' ||   a[i] == 'E'))
			{
				matchlen = 7;
				mask |= ACTION_EXECUTE;
				
			} else if (i >= 17 && 
					(a[i-17] == 'e' || a[i-17] == 'E') &&
					(a[i-16] == 'x' || a[i-16] == 'X') &&
					(a[i-15] == 't' || a[i-15] == 'T') &&
					(a[i-14] == 'e' || a[i-14] == 'E') &&
					(a[i-13] == 'n' || a[i-13] == 'N') &&
					(a[i-12] == 's' || a[i-12] == 'S') &&
					(a[i-11] == 'i' || a[i-11] == 'I') &&
					(a[i-10] == 'o' || a[i-10] == 'O') &&
					(a[i-9] == 'n' || a[i-9] == 'N') &&
					(a[i-8] == 'l' || a[i-8] == 'L') &&
					(a[i-7] == 'i' || a[i-7] == 'I') &&
					(a[i-6] == 'f' || a[i-6] == 'F') &&
					(a[i-5] == 'e' || a[i-5] == 'E') &&
					(a[i-4] == 'c' || a[i-4] == 'C') &&
					(a[i-3] == 'y' || a[i-3] == 'Y') &&
					(a[i-2] == 'c' || a[i-2] == 'C') &&
					(a[i-1] == 'l' || a[i-1] == 'L') &&
					  (a[i] == 'e' ||   a[i] == 'E'))
    		{
    			matchlen = 18;
    			mask |= ACTION_EXTENSIONLIFECYCLE;

    		} else if (i >= 8 && 
					(a[i-8] == 'l' || a[i-8] == 'L') &&
					(a[i-7] == 'i' || a[i-7] == 'I') &&
					(a[i-6] == 'f' || a[i-6] == 'F') &&
					(a[i-5] == 'e' || a[i-5] == 'E') &&
					(a[i-4] == 'c' || a[i-4] == 'C') &&
					(a[i-3] == 'y' || a[i-3] == 'Y') &&
					(a[i-2] == 'c' || a[i-2] == 'C') &&
					(a[i-1] == 'l' || a[i-1] == 'L') &&
					  (a[i] == 'e' ||   a[i] == 'E'))
			{
				matchlen = 9;
				mask |= ACTION_LIFECYCLE;
				
			} else if (i >= 7 && 
					(a[i-7] == 'l' || a[i-7] == 'L') &&
					(a[i-6] == 'i' || a[i-6] == 'I') &&
					(a[i-5] == 's' || a[i-5] == 'S') &&
					(a[i-4] == 't' || a[i-4] == 'T') &&
					(a[i-3] == 'e' || a[i-3] == 'E') &&
					(a[i-2] == 'n' || a[i-2] == 'N') &&
					(a[i-1] == 'e' || a[i-1] == 'E') &&
					  (a[i] == 'r' ||   a[i] == 'R'))
			{
				matchlen = 8;
				mask |= ACTION_LISTENER;
			
			} else if (i >= 7 && 
    				(a[i-7] == 'm' || a[i-7] == 'M') &&
    	            (a[i-6] == 'e' || a[i-6] == 'E') &&
    	            (a[i-5] == 't' || a[i-5] == 'T') &&
    	            (a[i-4] == 'a' || a[i-4] == 'A') &&
    	            (a[i-3] == 'd' || a[i-3] == 'D') &&
    	            (a[i-2] == 'a' || a[i-2] == 'A') &&
					(a[i-1] == 't' || a[i-1] == 'T') &&
					  (a[i] == 'a' ||   a[i] == 'A'))
    		{
    			matchlen = 8;
    			mask |= ACTION_METADATA;

    		} else if (i >= 9 && 
					(a[i-9] == 'p' || a[i-9] == 'P') &&
					(a[i-8] == 'e' || a[i-8] == 'E') &&
					(a[i-7] == 'r' || a[i-7] == 'R') &&
					(a[i-6] == 'm' || a[i-6] == 'M') &&
					(a[i-5] == 'i' || a[i-5] == 'I') &&
					(a[i-4] == 's' || a[i-4] == 'S') &&
					(a[i-3] == 's' || a[i-3] == 'S') &&
					(a[i-2] == 'i' || a[i-2] == 'I') &&
					(a[i-1] == 'o' || a[i-1] == 'O') &&
					  (a[i] == 'n' ||   a[i] == 'N'))
    		{
    			matchlen = 10;
    			mask |= ACTION_PERMISSION;
			
    		} else if (i >= 6 && 
					(a[i-6] == 'r' || a[i-6] == 'R') &&
					(a[i-5] == 'e' || a[i-5] == 'E') &&
					(a[i-4] == 's' || a[i-4] == 'S') &&
					(a[i-3] == 'o' || a[i-3] == 'O') &&
					(a[i-2] == 'l' || a[i-2] == 'L') &&
					(a[i-1] == 'v' || a[i-1] == 'V') &&
					  (a[i] == 'e' ||   a[i] == 'E'))
    		{
    			matchlen = 7;
    			mask |= ACTION_RESOLVE;
    			
    		} else if (i >= 7 && 
    					(a[i-7] == 'r' || a[i-7] == 'R') &&
						(a[i-6] == 'e' || a[i-6] == 'E') &&
						(a[i-5] == 's' || a[i-5] == 'S') &&
						(a[i-4] == 'o' || a[i-4] == 'O') &&
						(a[i-3] == 'u' || a[i-3] == 'U') &&
						(a[i-2] == 'r' || a[i-2] == 'R') &&
						(a[i-1] == 'c' || a[i-1] == 'C') &&
						  (a[i] == 'e' ||   a[i] == 'E'))
			{
    			matchlen = 8;
    			mask |= ACTION_RESOURCE;

    		} else if (i >= 9 && 
					(a[i-9] == 's' || a[i-9] == 'S') &&
					(a[i-8] == 't' || a[i-8] == 'T') &&
					(a[i-7] == 'a' || a[i-7] == 'A') &&
					(a[i-6] == 'r' || a[i-6] == 'R') &&
					(a[i-5] == 't' || a[i-5] == 'T') &&
					(a[i-4] == 'l' || a[i-4] == 'L') &&
					(a[i-3] == 'e' || a[i-3] == 'E') &&
					(a[i-2] == 'v' || a[i-2] == 'V') &&
					(a[i-1] == 'e' || a[i-1] == 'E') &&
					  (a[i] == 'l' ||   a[i] == 'L'))
    		{
    			matchlen = 10;
    			mask |= ACTION_STARTLEVEL;

    		} else if (i >= 0 && 
					(a[i] == '*'))
    		{
    			matchlen = 1;
    			mask |= ACTION_ALL;

			} else {
				// parse error
				throw new IllegalArgumentException(
						"invalid permission: " + actions);
        }

        // make sure we didn't just match the tail of a word
        // like "ackbarfstartlevel".  Also, skip to the comma.
        seencomma = false;
        while (i >= matchlen && !seencomma) {
        	switch(a[i-matchlen]) {
        		case ',':
        			seencomma = true;
        			/*FALLTHROUGH*/
        		case ' ': case '\r': case '\n':
        		case '\f': case '\t':
        			break;
        		default:
        			throw new IllegalArgumentException(
        					"invalid permission: " + actions);
        	}
        	i--;
        }

        // point i at the location of the comma minus one (or -1).
        i -= matchlen;
    }

    if (seencomma) {
        throw new IllegalArgumentException("invalid permission: " +
                        actions);
    }

    return mask;
    }
     
    /**
     * Called by <code><@link AdminPermission#implies(Permission)></code> on an AdminPermission
     * which was constructed with a Bundle.  This method loads a dictionary with the
     * filter-matchable properties of this bundle.  The dictionary is cached so this lookup
     * only happens once.
     * 
     * This method should only be called on an AdminPermission which was constructed with a 
     * bundle
     * 
     * @return a dictionary of properties for this bundle
     */
    private Dictionary getProperties() {
    	if (bundleProperties == null) {
    		bundleProperties = new Hashtable();

    		AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
		    		//set Id
		    		bundleProperties.put("id",new Long(bundle.getBundleId())); //$NON-NLS-1$
		    		
		    		//set location
		    		bundleProperties.put("location",bundle.getLocation()); //$NON-NLS-1$
		    		
		    		//set name
		    		if (bundle.getSymbolicName() != null) {
		    			bundleProperties.put("name",bundle.getSymbolicName()); //$NON-NLS-1$
		    		}
		    		
		    		//set signers
		    		bundleProperties.put("signer",new SignerWrapper(bundle)); //$NON-NLS-1$
		    		
		    		return null;
				}
			});
    	}     		
    	return bundleProperties;
    }

    public static class SignerWrapper extends Object {
    	private Bundle bundle;
    	private String pattern;
    	public SignerWrapper(String pattern) {
    		this.pattern = pattern;    			
    	}
    	SignerWrapper(Bundle bundle) {
    		this.bundle = bundle;
    	}
    	
		public boolean equals(Object arg0) {
			return this == arg0;

		}
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
    	
    	AdminPermission target = (AdminPermission)p;

    	//check actions first - much faster
    	if ((action_mask & target.action_mask)!=target.action_mask) {
    		return false;
    	}

    	//if passed in a filter, puke
    	if (target.filter != null) {
    		throw new RuntimeException("Cannot imply a filter");
    	}
    	
    	//special case - only wildcard implies wildcard
    	if (target.wildcard) {
    		return wildcard;
    	}

    	//check our name 
    	if (filter != null) {
    		return false;
    	} else if (wildcard) {
    		//it's "*"
    		return true;
    	} else {
    		//it's a bundle id
    		return bundle.equals(target.bundle);
    	}
    	    	
    }
    
    /**
     * Returns the canonical string representation of the <code>AdminPermission</code> actions.
     *
     * <p>Always returns present <code>AdminPermission</code> actions in the following order:
     * <code>CLASS</code>, <code>EXECUTE</code>, <code>LIFECYCLE</code>, <code>LISTENER</code>, 
     * <code>METADATA</code>, <code>PERMISSION</code>, <code>RESOLVE</code>, <code>RESOURCE</code>, 
     * <code>STARTLEVEL</code>.
     * @return Canonical string representation of the <code>AdminPermission</code> actions.
     */
	public String getActions() {
		if (actions == null) {
			if (action_mask == ACTION_ALL) {
				actions = "*"; //$NON-NLS-1$
			} else {
				StringBuffer sb = new StringBuffer();
				
				if ((action_mask & ACTION_CLASS) == ACTION_CLASS) {
					sb.append(CLASS);
					sb.append(',');
				}

				if ((action_mask & ACTION_EXECUTE) == ACTION_EXECUTE) {
					sb.append(EXECUTE);
					sb.append(',');
				}
	
				if ((action_mask & ACTION_LIFECYCLE) == ACTION_LIFECYCLE) {
					sb.append(LIFECYCLE);
					sb.append(',');
				}
	
				if ((action_mask & ACTION_LISTENER) == ACTION_LISTENER) {
					sb.append(LISTENER);
					sb.append(',');
				}
				
				if ((action_mask & ACTION_METADATA) == ACTION_METADATA) {
					sb.append(METADATA);
					sb.append(',');
				}
	
				if ((action_mask & ACTION_PERMISSION) == ACTION_PERMISSION) {
					sb.append(PERMISSION);
					sb.append(',');
				}
	
				if ((action_mask & ACTION_RESOLVE) == ACTION_RESOLVE) {
					sb.append(RESOLVE);
					sb.append(',');
				}
	
				if ((action_mask & ACTION_RESOURCE) == ACTION_RESOURCE) {
					sb.append(RESOURCE);
					sb.append(',');
				}
	
				if ((action_mask & ACTION_STARTLEVEL) == ACTION_STARTLEVEL) {
					sb.append(STARTLEVEL);
					sb.append(',');
				}

				if ((action_mask & ACTION_EXTENSIONLIFECYCLE) == ACTION_EXTENSIONLIFECYCLE) {
					sb.append(EXTENSIONLIFECYCLE);
					sb.append(',');
				}

				//remove trailing comma
				if (sb.length() > 0) {
					sb.deleteCharAt(sb.length()-1);
				}
				
				actions = sb.toString();
			}
		}
		return actions;
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
        
        AdminPermission a = (AdminPermission) obj;

        return((action_mask == a.action_mask) &&
        		(wildcard == a.wildcard) &&
        		((bundle == null) == (a.bundle == null)) &&
        		(bundle == null ? true : bundle.equals(a.bundle)) &&
				((filter == null) == (a.filter == null)) &&
				(filter == null ? true : filter.equals(a.filter))
				);
    }

    /**
     * Returns the hash code value for this object.
     *
     * @return Hash code value for this object.
     */
	public int hashCode() {
		return action_mask ^ (filter != null ? filter.hashCode() : bundle.hashCode());
	}
	
    /**
     * Returns a new <code>PermissionCollection</code> object suitable for storing
     * <code>AdminPermission</code>s.
     * 
     * @return A new <code>PermissionCollection</code> object.
     */

    public PermissionCollection newPermissionCollection()
    {
        return(new AdminPermissionCollection());
    }
}

/**
 * Stores a collection of <code>AdminPermission</code>s.
 */
final class AdminPermissionCollection extends PermissionCollection
{
	private static final long serialVersionUID = 3906372644575328048L;
	/**
     * Collection of permissions.
     *
     * @serial
     */
	private Hashtable permissions;

    /**
     * Create an empty AdminPermissions object.
     *
     */

    public AdminPermissionCollection()
    {
        permissions = new Hashtable();        
    }

    /**
     * Adds a permission to the <code>AdminPermission</code> objects. The key for 
     * the hashtable is the name
     *
     * @param permission The <code>AdminPermission</code> object to add.
     *
     * @exception IllegalArgumentException If the permission is not an
     * <code>AdminPermission</code> instance.
     *
     * @exception SecurityException If this <code>AdminPermissionCollection</code>
     * object has been marked read-only.
     */

    public void add(Permission permission)
    {
        if (! (permission instanceof AdminPermission))
            throw new IllegalArgumentException("invalid permission: "+
                                               permission);
        if (isReadOnly())
            throw new SecurityException("attempt to add a Permission to a " +
                                        "readonly AdminCollection");

        AdminPermission ap = (AdminPermission) permission;
        
    	AdminPermission existing = (AdminPermission) permissions.get(ap.getName());
    	
    	if (existing != null){
    		
    		int oldMask = existing.action_mask;
    		int newMask = ap.action_mask;
        
    		if (oldMask != newMask) {
    			permissions.put(existing.getName(),
    					new AdminPermission(existing.getName(), oldMask | newMask));
    		}
    	} else {
    		permissions.put(ap.getName(), ap);
    	}
    }


    /**
     * Determines if the specified permissions implies the permissions
     * expressed in <code>permission</code>.
     *
     * @param permission The Permission object to compare with the <code>AdminPermission</code>
     *  objects in this collection.
     *
     * @return <code>true</code> if <code>permission</code> is implied by an 
     * <code>AdminPermission</code> in this collection, <code>false</code> otherwise.
     */
    public boolean implies(Permission permission)
    {
        if (!(permission instanceof AdminPermission))
            return(false);

        AdminPermission target = (AdminPermission) permission;
        
        //just iterate one by one
        Iterator permItr = permissions.values().iterator();
        
        while(permItr.hasNext()) {
        	if (((AdminPermission)permItr.next()).implies(target)) {
        		return true;
        	}
        }
        return false;
    }
 

    /**
     * Returns an enumeration of all <code>AdminPermission</code> objects in the
     * container.
     *
     * @return Enumeration of all <code>AdminPermission</code> objects.
     */

    public Enumeration elements()
    {
        return(Collections.enumeration(permissions.values()));
    }
}
