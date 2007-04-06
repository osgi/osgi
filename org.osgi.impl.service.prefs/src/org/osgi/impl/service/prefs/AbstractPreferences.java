/*
 * @(#)AbstractPreferences.java	1.4 01/07/18
 * $Id$
 *
 
 * 
 * (C) Copyright 1996-2001 Sun Microsystems, Inc. 
 * Copyright (c) IBM Corporation (2004)
 * 
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS 
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 * 
 */
package org.osgi.impl.service.prefs;

import java.util.StringTokenizer;
import org.osgi.service.prefs.*;

/**
 * This class provides a skeletal implementation of the <tt>Preferences</tt>
 * interface.
 * 
 * <p>
 * <strong>This class is for <tt>Preferences</tt> implementers only. Normal
 * users of the <tt>Preferences</tt> facility should have no need to consult
 * this documentation. </strong>
 * 
 * @version $Revision$
 */
public abstract class AbstractPreferences implements Preferences {
	/**
	 * Our name relative to parent.
	 */
	private final String					name;
	/**
	 * Our absolute path name.
	 */
	private final String					absolutePath;
	/**
	 * Our parent node.
	 */
	private final AbstractPreferences		parent;
	/**
	 * Our root node.
	 */
	protected/* final */AbstractPreferences	root;					// Relative
																	  // to this
																	  // node
	// commented out the final modifier to stop fastjavac from complaining
	/**
	 * This field is used to keep track of whether or not this node has been
	 * removed. Once it's set to true, it will never be reset to false.
	 */
	private boolean							removed	= false;
	/**
	 * An object whose monitor is used to lock this node. This object is used in
	 * preference to the node itself to reduce the likelihood of intentional or
	 * unintentional denial of service due to a locked node. To avoid deadlock,
	 * a node is <i>never </i> locked by a thread that holds a lock on a
	 * descendant of that node.
	 */
	protected final Object					lock	= new Object();

	/**
	 * Creates a preference node with the specified parent and the specified
	 * name relative to its parent.
	 * 
	 * @param parent the parent of this preference node, or null if this is the
	 *        root.
	 * @param name the name of this preference node, relative to its parent, or
	 *        <tt>""</tt> if this is the root.
	 * @throws IllegalArgumentException if <tt>name</tt> contains a slash (
	 *         <tt>'/'</tt>), or <tt>parent</tt> is <tt>null</tt> and
	 *         name isn't <tt>""</tt>.
	 */
	protected AbstractPreferences(AbstractPreferences parent, String name) {
		if (parent == null) {
			if (!name.equals(""))
				throw new IllegalArgumentException("Root name '" + name
						+ "' must be \"\"");
			absolutePath = "/";
			root = this;
		}
		else {
			if (name.indexOf('/') != -1)
				throw new IllegalArgumentException("Name '" + name
						+ "' contains '/'");
			if (name.equals(""))
				throw new IllegalArgumentException("Illegal name: empty string");
			root = parent.root;
			absolutePath = (parent == root ? "/" + name : parent.absolutePath()
					+ "/" + name);
		}
		this.name = name;
		this.parent = parent;
	}

	/**
	 * Implements the <tt>put</tt> method as per the specification in
	 * {@link Preferences#put(String,String)}.
	 * 
	 * <p>
	 * This implementation checks that the key and value are legal, obtains this
	 * preference node's lock, checks that the node has not been removed, and
	 * invokes {@link #putSpi(String,String)}.
	 * 
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 * @throws NullPointerException if key or value is <tt>null</tt>.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public void put(String key, String value) {
		if (key == null || value == null)
			throw new NullPointerException();
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");
			putSpi(key, value);
		}
	}

	/**
	 * Implements the <tt>get</tt> method as per the specification in
	 * {@link Preferences#get(String,String)}.
	 * 
	 * <p>
	 * This implementation obtains this preference node's lock, checks that the
	 * node has not been removed, invokes {@link#getSpi(String)}, and returns
	 * the result, unless the <tt>getSpi</tt> invocation returns <tt>null</tt>
	 * in which case this invocation returns <tt>def</tt>.
	 * 
	 * @param key key whose associated value is to be returned.
	 * @param def the value to be returned in the event that this preference
	 *        node has no value associated with <tt>key</tt>.
	 * @return the value associated with <tt>key</tt>, or <tt>def</tt> if
	 *         no value is associated with <tt>key</tt>.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 * @throws NullPointerException if key is <tt>null</tt>. (A<tt>null</tt>
	 *         default <i>is </i> permitted.)
	 */
	public String get(String key, String def) {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");
			String result = getSpi(key);
			return (result == null ? def : result);
		}
	}

	/**
	 * Implements the <tt>remove(String)</tt> method as per the specification
	 * in {@link Preferences#remove(String)}.
	 * 
	 * <p>
	 * This implementation obtains this preference node's lock, checks that the
	 * node has not been removed, and invokes {@link #removeSpi(String)}.
	 * 
	 * @param key key whose mapping is to be removed from the preference node.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public void remove(String key) {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");
			removeSpi(key);
		}
	}

	/**
	 * Implements the <tt>clear</tt> method as per the specification in
	 * {@link Preferences#clear()}.
	 * 
	 * <p>
	 * This implementation obtains this preference node's lock, invokes
	 * {@link #keys()}to obtain an array of keys, and iterates over the array
	 * invoking {@link #remove(String)}on each key.
	 * 
	 * @throws BackingStoreException if this operation cannot be completed due
	 *         to a failure in the backing store, or inability to communicate
	 *         with it.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public void clear() throws BackingStoreException {
		synchronized (lock) {
			String[] keys = keys();
			for (int i = 0; i < keys.length; i++)
				remove(keys[i]);
		}
	}

	/**
	 * Implements the <tt>putInt</tt> method as per the specification in
	 * {@link Preferences#putInt(String,int)}.
	 * 
	 * <p>
	 * This implementation translates <tt>value</tt> to a string with
	 * {@link Integer#toString(int)}and invokes {@link #put(String,String)}on
	 * the result.
	 * 
	 * @param key key with which the string form of value is to be associated.
	 * @param value value whose string form is to be associated with key.
	 * @throws NullPointerException if key is <tt>null</tt>.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public void putInt(String key, int value) {
		put(key, Integer.toString(value));
	}

	/**
	 * Implements the <tt>getInt</tt> method as per the specification in
	 * {@link Preferences#getInt(String,int)}.
	 * 
	 * <p>
	 * This implementation invokes {@link #get(String,String) <tt>get(key,null)
	 * </tt>}. If the return value is non-null, the implementation attempts to
	 * translate it to an <tt>int</tt> with {@link Integer#parseInt(String)}.
	 * If the attempt succeeds, the return value is returned by this method.
	 * Otherwise, <tt>def</tt> is returned.
	 * 
	 * @param key key whose associated value is to be returned as an int.
	 * @param def the value to be returned in the event that this preference
	 *        node has no value associated with <tt>key</tt> or the associated
	 *        value cannot be interpreted as an int.
	 * @return the int value represented by the string associated with <tt>key
	 *         </tt> in this preference node, or <tt>def</tt> if the
	 *         associated value does not exist or cannot be interpreted as an
	 *         int.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public int getInt(String key, int def) {
		int result = def;
		try {
			String value = get(key, null);
			if (value != null)
				result = Integer.parseInt(value);
		}
		catch (NumberFormatException e) {
			// Ignoring exception causes specified default to be returned
		}
		return result;
	}

	/**
	 * Implements the <tt>putLong</tt> method as per the specification in
	 * {@link Preferences#putLong(String,long)}.
	 * 
	 * <p>
	 * This implementation translates <tt>value</tt> to a string with
	 * {@link Long#toString(int)}and invokes {@link #put(String,String)}on the
	 * result.
	 * 
	 * @param key key with which the string form of value is to be associated.
	 * @param value value whose string form is to be associated with key.
	 * @throws NullPointerException if key is <tt>null</tt>.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public void putLong(String key, long value) {
		put(key, Long.toString(value));
	}

	/**
	 * Implements the <tt>getLong</tt> method as per the specification in
	 * {@link Preferences#getLong(String,long)}.
	 * 
	 * <p>
	 * This implementation invokes {@link #get(String,String) <tt>get(key,null)
	 * </tt>}. If the return value is non-null, the implementation attempts to
	 * translate it to a <tt>long</tt> with {@link Long#parseLong(String)}.
	 * If the attempt succeeds, the return value is returned by this method.
	 * Otherwise, <tt>def</tt> is returned.
	 * 
	 * @param key key whose associated value is to be returned as a long.
	 * @param def the value to be returned in the event that this preference
	 *        node has no value associated with <tt>key</tt> or the associated
	 *        value cannot be interpreted as a long.
	 * @return the long value represented by the string associated with <tt>key
	 *         </tt> in this preference node, or <tt>def</tt> if the
	 *         associated value does not exist or cannot be interpreted as a
	 *         long.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public long getLong(String key, long def) {
		long result = def;
		try {
			String value = get(key, null);
			if (value != null)
				result = Long.parseLong(value);
		}
		catch (NumberFormatException e) {
			// Ignoring exception causes specified default to be returned
		}
		return result;
	}

	/**
	 * Implements the <tt>putBoolean</tt> method as per the specification in
	 * {@link Preferences#putBoolean(String,boolean)}.
	 * 
	 * <p>
	 * This implementation translates <tt>value</tt> to a string with
	 * {@link String#valueOf(boolean)}and invokes {@link #put(String,String)}
	 * on the result.
	 * 
	 * @param key key with which the string form of value is to be associated.
	 * @param value value whose string form is to be associated with key.
	 * @throws NullPointerException if key is <tt>null</tt>.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public void putBoolean(String key, boolean value) {
		put(key, String.valueOf(value));
	}

	/**
	 * Implements the <tt>getBoolean</tt> method as per the specification in
	 * {@link Preferences#getBoolean(String,boolean)}.
	 * 
	 * <p>
	 * This implementation invokes {@link #get(String,String) <tt>get(key,null)
	 * </tt>}. If the return value is non-null, it is compared with <tt>"true"
	 * </tt> using {@link String#equalsIgnoreCase(String)}. If the comparison
	 * returns <tt>true</tt>, this invocation returns <tt>true</tt>.
	 * Otherwise, the original return value is compared with <tt>"false"</tt>,
	 * again using {@link String#equalsIgnoreCase(String)}. If the comparison
	 * returns <tt>true</tt>, this invocation returns <tt>false</tt>.
	 * Otherwise, this invocation returns <tt>def</tt>.
	 * 
	 * @param key key whose associated value is to be returned as a boolean.
	 * @param def the value to be returned in the event that this preference
	 *        node has no value associated with <tt>key</tt> or the associated
	 *        value cannot be interpreted as a boolean.
	 * @return the boolean value represented by the string associated with <tt>
	 *         key</tt> in this preference node, or <tt>null</tt> if the
	 *         associated value does not exist or cannot be interpreted as a
	 *         boolean.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public boolean getBoolean(String key, boolean def) {
		boolean result = def;
		String value = get(key, null);
		if (value != null) {
			if (value.equalsIgnoreCase("true"))
				result = true;
			else
				if (value.equalsIgnoreCase("false"))
					result = false;
		}
		return result;
	}

	/**
	 * Implements the <tt>putFloat</tt> method as per the specification in
	 * {@link Preferences#putFloat(String,float)}.
	 * 
	 * <p>
	 * This implementation translates <tt>value</tt> to a string with
	 * {@link Float#toString(float)}and invokes {@link #put(String,String)}on
	 * the result.
	 * 
	 * @param key key with which the string form of value is to be associated.
	 * @param value value whose string form is to be associated with key.
	 * @throws NullPointerException if key is <tt>null</tt>.
	 * @throws IllegalArgumentException if <tt>key.length()</tt> exceeds
	 *         <tt>MAX_KEY_LENGTH</tt>.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public void putFloat(String key, float value) {
		put(key, Float.toString(value));
	}

	/**
	 * Implements the <tt>getFloat</tt> method as per the specification in
	 * {@link Preferences#getFloat(String,float)}.
	 * 
	 * <p>
	 * This implementation invokes {@link #get(String,String) <tt>get(key,null)
	 * </tt>}. If the return value is non-null, the implementation attempts to
	 * translate it to an <tt>float</tt> with {@link Float#valueOf(String)}.
	 * If the attempt succeeds, the return value is returned by this method.
	 * Otherwise, <tt>def</tt> is returned.
	 * 
	 * @param key key whose associated value is to be returned as a float.
	 * @param def the value to be returned in the event that this preference
	 *        node has no value associated with <tt>key</tt> or the associated
	 *        value cannot be interpreted as a float.
	 * @return the float value represented by the string associated with <tt>
	 *         key</tt> in this preference node, or <tt>def</tt> if the
	 *         associated value does not exist or cannot be interpreted as a
	 *         float.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
	 */
	public float getFloat(String key, float def) {
		float result = def;
		try {
			String value = get(key, null);
			if (value != null)
				// result = Float.parseFloat(value);
				result = Float.valueOf(value).floatValue();
		}
		catch (NumberFormatException e) {
			// Ignoring exception causes specified default to be returned
		}
		return result;
	}

	/**
	 * Implements the <tt>putDouble</tt> method as per the specification in
	 * {@link Preferences#putDouble(String,double)}.
	 * 
	 * <p>
	 * This implementation translates <tt>value</tt> to a string with
	 * {@link Double#toString(double)}and invokes {@link #put(String,String)}
	 * on the result.
	 * 
	 * @param key key with which the string form of value is to be associated.
	 * @param value value whose string form is to be associated with key.
	 * @throws NullPointerException if key is <tt>null</tt>.
	 * @throws IllegalArgumentException if <tt>key.length()</tt> exceeds
	 *         <tt>MAX_KEY_LENGTH</tt>.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public void putDouble(String key, double value) {
		put(key, Double.toString(value));
	}

	/**
	 * Implements the <tt>getDouble</tt> method as per the specification in
	 * {@link Preferences#getDouble(String,double)}.
	 * 
	 * <p>
	 * This implementation invokes {@link #get(String,String) <tt>get(key,null)
	 * </tt>}. If the return value is non-null, the implementation attempts to
	 * translate it to an <tt>double</tt> with {@link Double#valueOf(String)}.
	 * If the attempt succeeds, the return value is returned by this method.
	 * Otherwise, <tt>def</tt> is returned.
	 * 
	 * @param key key whose associated value is to be returned as a double.
	 * @param def the value to be returned in the event that this preference
	 *        node has no value associated with <tt>key</tt> or the associated
	 *        value cannot be interpreted as a double.
	 * @return the double value represented by the string associated with <tt>
	 *         key</tt> in this preference node, or <tt>def</tt> if the
	 *         associated value does not exist or cannot be interpreted as a
	 *         double.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
	 */
	public double getDouble(String key, double def) {
		double result = def;
		try {
			String value = get(key, null);
			if (value != null)
				// result = Double.parseDouble(value);
				result = Double.valueOf(value).doubleValue();
		}
		catch (NumberFormatException e) {
			// Ignoring exception causes specified default to be returned
		}
		return result;
	}

	/**
	 * Implements the <tt>putByteArray</tt> method as per the specification in
	 * {@link Preferences#putByteArray(String,byte[])}.
	 * 
	 * @param key key with which the string form of value is to be associated.
	 * @param value value whose string form is to be associated with key.
	 * @throws NullPointerException if key or value is <tt>null</tt>.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public void putByteArray(String key, byte[] value) {
		put(key, Base64.byteArrayToBase64(value));
	}

	/**
	 * Implements the <tt>getByteArray</tt> method as per the specification in
	 * {@link Preferences#getByteArray(String,byte[])}.
	 * 
	 * @param key key whose associated value is to be returned as a byte array.
	 * @param def the value to be returned in the event that this preference
	 *        node has no value associated with <tt>key</tt> or the associated
	 *        value cannot be interpreted as a byte array.
	 * @return the byte array value represented by the string associated with
	 *         <tt>key</tt> in this preference node, or <tt>def</tt> if the
	 *         associated value does not exist or cannot be interpreted as a
	 *         byte array.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public byte[] getByteArray(String key, byte[] def) {
		byte[] result = def;
		try {
			String value = get(key, null);
			if (value != null)
				result = Base64.base64ToByteArray(value);
		}
		catch (IllegalArgumentException e) {
			// Ignoring exception causes specified default to be returned
		}
		return result;
	}

	/**
	 * Implements the <tt>keys</tt> method as per the specification in
	 * {@link Preferences#keys()}.
	 * 
	 * <p>
	 * This implementation obtains this preference node's lock, checks that the
	 * node has not been removed and invokes {@link #keysSpi()}.
	 * 
	 * @return an array of the keys that have an associated value in this
	 *         preference node.
	 * @throws BackingStoreException if this operation cannot be completed due
	 *         to a failure in the backing store, or inability to communicate
	 *         with it.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public String[] keys() throws BackingStoreException {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");
			return keysSpi();
		}
	}

	/**
	 * Implements the <tt>childrenNames</tt> method as per the specification
	 * in {@link Preferences#childrenNames()}.
	 * 
	 * <p>
	 * This implementation obtains this preference node's lock, checks that the
	 * node has not been removed and invokes {@link #childrenNamesSpi()}.
	 * 
	 * @return the children of this preference node.
	 * @throws BackingStoreException if this operation cannot be completed due
	 *         to a failure in the backing store, or inability to communicate
	 *         with it.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public String[] childrenNames() throws BackingStoreException {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");
			return childrenNamesSpi();
		}
	}

	/**
	 * Implements the <tt>parent</tt> method as per the specification in
	 * {@link Preferences#parent()}.
	 * 
	 * <p>
	 * This implementation obtains this preference node's lock, checks that the
	 * node has not been removed and returns the parent value that was passed to
	 * this node's constructor.
	 * 
	 * @return the parent of this preference node.
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public Preferences parent() {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");
			return parent;
		}
	}

	/**
	 * Implements the <tt>node</tt> method as per the specification in
	 * {@link Preferences#node(String)}.
	 * 
	 * <p>
	 * This implementation obtains this preference node's lock and checks that
	 * the node has not been removed. If <tt>path</tt> is <tt>""</tt>, this
	 * node is returned; if <tt>path</tt> is <tt>"/"</tt>, this node's root
	 * is returned. Otherwise, this breaks <tt>path</tt> into tokens and
	 * recursively traverses the path from this node (or from the root, if the
	 * first character of <tt>path</tt> is <tt>'/'</tt>) to the named node,
	 * "consuming" a name and a slash from <tt>path</tt> at each step of the
	 * traversal. At each step, the current node is locked and the {@link
	 * #childSpi(String)} method is invoked. When there are no more tokens, the
	 * value returned by <tt>child</tt> is returned by this method. If during
	 * the traversal, two <tt>"/"</tt> tokens occur consecutively, or the
	 * final token is <tt>"/"</tt> (rather than a name>, an appropriate
	 * <tt>IllegalArgumentException</tt> is thrown.
	 * 
	 * @param path the path name of the preference node to return.
	 * @return the specified preference node.
	 * @throws IllegalArgumentException if the path name is invalid (i.e., it
	 *         contains multiple consecutive slash characters, or ends with a
	 *         slash character and is more than one character long).
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method.
	 */
	public Preferences node(String path) {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");
			if (path.equals(""))
				return this;
			if (path.equals("/"))
				return root;
			if (path.charAt(0) != '/')
				return node(new StringTokenizer(path, "/", true));
		}
		// Absolute path. Note that we've dropped our lock to avoid deadlock
		return root.node(new StringTokenizer(path.substring(1), "/", true));
	}

	/**
	 * tokenizer contains <name>{'/' <name>}*
	 */
	private Preferences node(StringTokenizer path) {
		String token = path.nextToken();
		if (token.equals("/")) // Check for consecutive slashes
			throw new IllegalArgumentException("Consecutive slashes in path");
		synchronized (lock) {
			AbstractPreferences child = childSpi(token);
			if (!path.hasMoreTokens())
				return child;
			path.nextToken(); // Consume slash
			if (!path.hasMoreTokens())
				throw new IllegalArgumentException("Path ends with slash");
			return child.node(path);
		}
	}

	/**
	 * Implements the <tt>nodeExists</tt> method as per the specification in
	 * {@link Preferences#nodeExists(String)}.
	 * 
	 * <p>
	 * This implementation is very similar to {@link #node(String)}, except
	 * that {@link #getChild(String)}is used instead of {@link
	 * #childSpi(String)}.
	 * 
	 * @param path the path name of the node whose existence is to be checked.
	 * @return true if the specified node exists.
	 * @throws BackingStoreException if this operation cannot be completed due
	 *         to a failure in the backing store, or inability to communicate
	 *         with it.
	 * @throws IllegalArgumentException if the path name is invalid (i.e., it
	 *         contains multiple consecutive slash characters, or ends with a
	 *         slash character and is more than one character long).
	 * @throws IllegalStateException if this node (or an ancestor) has been
	 *         removed with the {@link #removeNode()}method and
	 *         <tt>pathname</tt> is not the empty string (<tt>""</tt>).
	 */
	public boolean nodeExists(String path) throws BackingStoreException {
		synchronized (lock) {
			if (path.equals(""))
				return !removed;
			if (removed)
				throw new IllegalStateException("Node has been removed.");
			if (path.equals("/"))
				return true;
			if (path.charAt(0) != '/')
				return nodeExists(new StringTokenizer(path, "/", true));
		}
		// Absolute path. Note that we've dropped our lock to avoid deadlock
		return root
				.nodeExists(new StringTokenizer(path.substring(1), "/", true));
	}

	/**
	 * tokenizer contains <name>{'/' <name>}*
	 */
	private boolean nodeExists(StringTokenizer path)
			throws BackingStoreException {
		String token = path.nextToken();
		if (token.equals("/")) // Check for consecutive slashes
			throw new IllegalArgumentException("Consecutive slashes in path");
		synchronized (lock) {
			AbstractPreferences child = getChild(token);
			if (child == null)
				return false;
			if (!path.hasMoreTokens())
				return true;
			path.nextToken(); // Consume slash
			if (!path.hasMoreTokens())
				throw new IllegalArgumentException("Path ends with slash");
			return child.nodeExists(path);
		}
	}

	/**
	 * Implements the <tt>removeNode()</tt> method as per the specification in
	 * {@link Preferences#removeNode()}.
	 * 
	 * <p>
	 * This implementation locks this node's parent, and
	 * calls a recursive helper method that traverses the subtree rooted at this
	 * node. The recursive call locks the node on which it was called and calls
	 * itself on each of its children in turn. The it removes the node using
	 * {@link #removeSpi()}and marks the node as removed. Note that the helper
	 * method is always invoked with all ancestors up to the "closest
	 * non-removed ancestor" locked.
	 * 
	 * @throws IllegalStateException if this node (or an ancestor) has already
	 *         been removed with the {@link #removeNode()}method.
	 */
	public void removeNode() throws BackingStoreException {
		Object parentLock = (this == root) ? lock : parent.lock;	// RFC 60
		synchronized (parentLock) {
			remove2();
		}
	}

	// Called with locks on all nodes on path from "removal root" to this
	private void remove2() throws BackingStoreException {
		synchronized (lock) {
			String[] children = childrenNames();
			for (int i = 0; i < children.length; i++)
				((AbstractPreferences) node(children[i])).remove2();
			// Now we have no descendants - it's time to die!
			removeSpi();
			removed = true;
		}
	}

	/**
	 * Implements the <tt>name</tt> method as per the specification in
	 * {@link Preferences#name()}.
	 * 
	 * <p>
	 * This implementation merely returns the name that was passed to this
	 * node's constructor.
	 * 
	 * @return this preference node's name, relative to its parent.
	 */
	public String name() {
		return name;
	}

	/**
	 * Implements the <tt>absolutePath</tt> method as per the specification in
	 * {@link Preferences#absolutePath()}.
	 * 
	 * <p>
	 * This implementation merely returns the absolute path name that was
	 * computed at the time that this node was constructed (based on the name
	 * that was passed to this node's constructor, and the names that were
	 * passed to this node's ancestors' constructors).
	 * 
	 * @return this preference node's absolute path name.
	 */
	public String absolutePath() {
		return absolutePath;
	}

	// "SPI" METHODS
	/**
	 * Put the given key-value association into this preference node. It is
	 * guaranteed that <tt>key</tt> and <tt>value</tt> are non-null and of
	 * legal length. Also, it is guaranteed that this node has not been removed.
	 * (The implementor needn't check for any of these things.)
	 * 
	 * <p>
	 * This method is invoked with the lock on this node held.
	 */
	protected abstract void putSpi(String key, String value);

	/**
	 * Return the value associated with the specified key at this preference
	 * node, or null if there is no association for this key, or the association
	 * cannot be determined at this time. It is guaranteed that <tt>key</tt>
	 * is non-null. Also, it is guaranteed that this node has not been removed.
	 * (The implementor needn't check for either of these things.)
	 * 
	 * <p>
	 * This method is invoked with the lock on this node held.
	 */
	protected abstract String getSpi(String key);

	/**
	 * Remove the association (if any) for the specified key at this preference
	 * node. It is guaranteed that <tt>key</tt> is non-null. Also, it is
	 * guaranteed that this node has not been removed. (The implementor needn't
	 * check for either of these things.)
	 * 
	 * <p>
	 * This method is invoked with the lock on this node held.
	 */
	protected abstract void removeSpi(String key);

	/**
	 * Removes this preference node, invalidating it and any preferences that it
	 * contains. The named child will have no descendants at the time this
	 * invocation is made (i.e., the {@link Preferences#removeNode()}method
	 * invokes this method repeatedly in a bottom-up fashion, removing each of a
	 * node's descendants before removing the node itself).
	 * 
	 * <p>
	 * This method is invoked with the lock held on this node and its parent
	 * (and all ancestors that are being removed as a result of a single
	 * invocation to {@link Preferences#removeNode()}).
	 * 
	 * <p>
	 * The removal of a node needn't become persistent until the <tt>flush</tt>
	 * method is invoked on an ancestor of this node.
	 */
	protected abstract void removeSpi() throws BackingStoreException;

	/**
	 * Returns all of the keys that have an associated value in this preference
	 * node. (The returned array will be of size zero if this node has no
	 * preferences.) It is guaranteed that this node has not been removed.
	 * 
	 * <p>
	 * This method is invoked with the lock on this node held.
	 * 
	 * @return an array of the keys that have an associated value in this
	 *         preference node.
	 * @throws BackingStoreException if this operation cannot be completed due
	 *         to a failure in the backing store, or inability to communicate
	 *         with it.
	 */
	protected abstract String[] keysSpi() throws BackingStoreException;

	/**
	 * Returns the names of the children of this preference node. (The returned
	 * array will be of size zero if this node has no children.)
	 * 
	 * <p>
	 * This method is invoked with the lock on this node held.
	 * 
	 * @return an array of the keys that have an associated value in this
	 *         preference node.
	 * @throws BackingStoreException if this operation cannot be completed due
	 *         to a failure in the backing store, or inability to communicate
	 *         with it.
	 */
	protected abstract String[] childrenNamesSpi() throws BackingStoreException;

	/**
	 * Returns the named child if it exists, or null if it does not. It is
	 * guaranteed that <tt>child</tt> is non-null, non-empty and does not
	 * contain the slash character ('/'). Also, it is guaranteed that this node
	 * has not been removed. (The implementor needn't check for any of these
	 * things if he chooses to override this method.)
	 * 
	 * @param nodeName name of the child to be searched for.
	 * @return the named child if it exists, or null if it does not.
	 * @throws BackingStoreException if this operation cannot be completed due
	 *         to a failure in the backing store, or inability to communicate
	 *         with it.
	 */
	protected AbstractPreferences getChild(String nodeName)
			throws BackingStoreException {
		synchronized (lock) {
			String[] kids = childrenNames();
			for (int i = 0; i < kids.length; i++)
				if (kids[i].equals(nodeName))
					return (AbstractPreferences) node(kids[i]);
		}
		return null;
	}

	/**
	 * Returns the named child of this preference node, creating it if it does
	 * not already exist. It is guaranteed that <tt>child</tt> is non-null,
	 * non-empty and does not contain the slash character ('/'). Also, it is
	 * guaranteed that this node has not been removed. (The implementor needn't
	 * check for any of these things.)
	 * 
	 * <p>
	 * The implementer must ensure that the returned node has not been removed.
	 * If a like-named child of this node was previously removed, the
	 * implementer must return a newly constructed <tt>AbstractPreferences</tt>
	 * node; once removed, an <tt>AbstractPreferences</tt> node cannot be
	 * "resuscitated."
	 * 
	 * <p>
	 * If this method causes a node to be created, this node is not guaranteed
	 * to be persistent until the <tt>flush</tt> method is invoked on this
	 * node or one of its ancestors (or descendants).
	 * 
	 * <p>
	 * This method is invoked with the lock on this node held.
	 * 
	 * @param name The name of the child node to return, relative to this
	 *        preference node.
	 * @return The named child node.
	 */
	protected abstract AbstractPreferences childSpi(String name);

	/**
	 * Returns the absolute path name of this preferences node.
	 */
	public String toString() {
		return ("Preference Node: " + absolutePath());
	}

	/**
	 * Returns <tt>true</tt> if this node (or an ancestor) has been removed
	 * with the {@link #removeNode()}method, otherwise false.
	 */
	protected boolean isRemoved() {
		synchronized (lock) {
			return removed;
		}
	}
}
