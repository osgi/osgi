/*
 * @(#)Preferences.java 1.7 01/07/18
 * $Header$
 *

 *
 * (C) Copyright 1996-2001 Sun Microsystems, Inc.
 *
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 *
 */

package org.osgi.service.prefs;

import java.util.*;
import java.io.*;

/**
 * A node in a hierarchical collection of preference data.
 *
 * <p>This interface allows applications to store and retrieve user and system
 * preference data.  This data is stored
 * persistently in an implementation-dependent backing store.  Typical
 * implementations include flat files, OS-specific registries,
 * directory servers and SQL databases.
 *
 * <p>For each bundle, there is a separate tree of nodes for
 * each user, and one for system preferences. The precise description of
 * "user" and "system" will vary from one bundle to another.  Typical
 * information stored in the user preference tree might include font choice,
 * and color choice for a bundle which interacts with the user via a
 * servlet. Typical information stored in the system preference tree
 * might include installation data, or things like
 * high score information for a game program.
 *
 * <p>Nodes in a preference tree are named in a similar fashion to
 * directories in a hierarchical file system.   Every node in a preference
 * tree has a <i>node name</i> (which is not necessarily unique),
 * a unique <i>absolute path name</i>, and a path name <i>relative</i> to each
 * ancestor including itself.
 *
 * <p>The root node has a node name of the empty <tt>String</tt> object ("").  Every other
 * node has an arbitrary node name, specified at the time it is created.  The
 * only restrictions on this name are that it cannot be the empty string, and
 * it cannot contain the slash character ('/').
 *
 * <p>The root node has an absolute path name of <tt>"/"</tt>.  Children of
 * the root node have absolute path names of <tt>"/" + </tt><i>&lt;node
 * name&gt;</i>.  All other nodes have absolute path names of <i>&lt;parent's
 * absolute path name&gt;</i><tt> + "/" + </tt><i>&lt;node name&gt;</i>.
 * Note that all absolute path names begin with the slash character.
 *
 * <p>A node <i>n</i>'s path name relative to its ancestor <i>a</i>
 * is simply the string that must be appended to <i>a</i>'s absolute path name
 * in order to form <i>n</i>'s absolute path name, with the initial slash
 * character (if present) removed.  Note that:
 * <ul>
 * <li>No relative path names begin with the slash character.
 * <li>Every node's path name relative to itself is the empty string.
 * <li>Every node's path name relative to its parent is its node name (except
 * for the root node, which does not have a parent).
 * <li>Every node's path name relative to the root is its absolute path name
 * with the initial slash character removed.
 * </ul>
 *
 * <p>Note finally that:
 * <ul>
 * <li>No path name contains multiple consecutive slash characters.
 * <li>No path name with the exception of the root's absolute path name
 * end in the slash character.
 * <li>Any string that conforms to these two rules is a valid path name.
 * </ul>
 *
 * <p>Each <tt>Preference</tt> node has zero or more properties
 * associated with it, where a property consists of a name and a value.
 * The bundle writer is free to choose any appropriate names for properties.
 * Their values can be of type <tt>String</tt>, <tt>long</tt>, <tt>int</tt>, <tt>boolean</tt>,
 * <tt>byte[]</tt>, <tt>float</tt>, or <tt>double</tt> but
 * they can always be accessed as if they were <tt>String</tt> objects.
 *
 * <p>All node name and property name comparisons are case-sensitive.
 *
 * <p>All of the methods that modify preference data are permitted to
 * operate asynchronously; they may return immediately, and changes
 * will eventually propagate to the persistent backing store, with
 * an implementation-dependent delay.  The <tt>flush</tt> method
 * may be used to synchronously force updates to the backing store.
 *
 * <p>Implementations must automatically attempt to flush
 * to the backing store any pending
 * updates for a bundle's preferences when the bundle is stopped or otherwise
 * ungets the Preferences Service.
 *
 * <p>The methods in this class may be invoked concurrently by multiple threads
 * in a single Java Virtual Machine (JVM) without the need for external
 * synchronization, and the
 * results will be equivalent to some serial execution.  If this class is used
 * concurrently <i>by multiple JVMs</i> that store their preference data in
 * the same backing store, the data store will not be corrupted, but no
 * other guarantees are made concerning the consistency of the preference
 * data.
 *
 *
 * @version $Revision$
*/

public interface Preferences {

    /**
     * Associates the specified value with the specified key in this
     * node.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @throws NullPointerException if <tt>key</tt> or <tt>value</tt> is <tt>null</tt>.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     */
    public abstract void put(String key, String value);

    /**
     * Returns the value associated with the specified <tt>key</tt> in this
     * node. Returns the specified default if there is no value associated
     * with the <tt>key</tt>, or the backing store is inaccessible.
     *
     * @param key key whose associated value is to be returned.
     * @param def the value to be returned in the event that this
     *        node has no value associated with <tt>key</tt>
     *        or the backing store is inaccessible.
     * @return the value associated with <tt>key</tt>, or <tt>def</tt>
     *         if no value is associated with <tt>key</tt>.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.  (A
     *         <tt>null</tt> default <i>is</i> permitted.)
     */
    public abstract String get(String key, String def);

    /**
     * Removes the value associated with the specified <tt>key</tt> in this
     * node, if any.
     *
     * @param key key whose mapping is to be removed from this node.
     * @see #get(String,String)
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     */
    public abstract void remove(String key);

    /**
     * Removes all of the properties (key-value associations) in this
     * node.  This call has no effect on any descendants
     * of this node.
     *
     * @throws BackingStoreException if this operation cannot be completed
     *         due to a failure in the backing store, or inability to
     *         communicate with it.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see #remove(String)
     */
    public abstract void clear() throws BackingStoreException;

    /**
     * Associates a <tt>String</tt> object representing the specified <tt>int</tt> value with the
     * specified <tt>key</tt> in this node.  The associated string is the
     * one that would be returned if the <tt>int</tt> value were passed to
     * <tt>Integer.toString(int)</tt>.  This method is intended for use in
     * conjunction with {@link #getInt}method.
     *
     * <p>Implementor's note: it is <i>not</i> necessary that the property
     * value be represented by a <tt>String</tt> object in the backing store.  If
     * the backing store supports integer values, it is not unreasonable to
     * use them.  This implementation detail is not visible through the
     * <tt>Preferences</tt> API, which allows the value to be read as an <tt>int</tt>
     * (with <tt>getInt</tt> or a <tt>String</tt> (with <tt>get</tt>) type.
     *
     * @param key key with which the string form of value is to be associated.
     * @param value <tt>value</tt> whose string form is to be associated with <tt>key</tt>.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see #getInt(String,int)
     */
    public abstract void putInt(String key, int value);

    /**
     * Returns the <tt>int</tt> value represented by the <tt>String</tt> object associated with the
     * specified <tt>key</tt> in this node. The <tt>String</tt> object is converted to
     * an <tt>int</tt> as by <tt>Integer.parseInt(String)</tt>.  Returns the
     * specified default if there is no value associated with the <tt>key</tt>,
     * the backing store is inaccessible, or if
     * <tt>Integer.parseInt(String)</tt> would throw a
     * <tt>NumberFormatException</tt> if the associated
     * <tt>value</tt> were passed.  This
     * method is intended for use in conjunction with the {@link #putInt}method.
     *
     * @param key key whose associated value is to be returned as an <tt>int</tt>.
     * @param def the value to be returned in the event that this
     *        node has no value associated with <tt>key</tt>
     *        or the associated value cannot be interpreted as an <tt>int</tt>
     *        or the backing store is inaccessible.
     * @return the <tt>int</tt> value represented by the <tt>String</tt> object associated with
     *         <tt>key</tt> in this node, or <tt>def</tt> if the
     *         associated value does not exist or cannot be interpreted as
     *         an <tt>int</tt> type.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see #putInt(String,int)
     * @see #get(String,String)
     */
    public abstract int getInt(String key, int def);

    /**
     * Associates a <tt>String</tt> object representing the specified <tt>long</tt> value with the
     * specified <tt>key</tt> in this node.  The associated <tt>String</tt> object is the
     * one that would be returned if the <tt>long</tt> value were passed to
     * <tt>Long.toString(long)</tt>.  This method is intended for use in
     * conjunction with the {@link #getLong}method.
     *
     * <p>Implementor's note: it is <i>not</i> necessary that the
     * <tt>value</tt> be represented by a <tt>String</tt> type in the backing store.  If
     * the backing store supports <tt>long</tt> values, it is not unreasonable to
     * use them.  This implementation detail is not visible through the
     * <Tt>Preferences</Tt> API, which allows the value to be read as a <tt>long</tt>
     * (with <tt>getLong</tt> or a <tt>String</tt> (with <tt>get</tt>) type.
     *
     * @param key <tt>key</tt> with which the string form of <tt>value</tt> is to be associated.
     * @param value <tt>value</tt> whose string form is to be associated with <tt>key</tt>.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see #getLong(String,long)
     */
    public abstract void putLong(String key, long value);

    /**
     * Returns the <tt>long</tt> value represented by the <tt>String</tt> object associated with the
     * specified <tt>key</tt> in this node.  The <tt>String</tt> object is converted to
     * a <tt>long</tt> as by <tt>Long.parseLong(String)</tt>.  Returns the
     * specified default if there is no value associated with the <tt>key</tt>,
     * the backing store is inaccessible, or if
     * <tt>Long.parseLong(String)</tt> would throw a
     * <tt>NumberFormatException</tt> if the associated
     * <tt>value</tt> were passed.  This
     * method is intended for use in conjunction with the {@link #putLong}method.
     *
     * @param key <tt>key</tt> whose associated value is to be returned as a <tt>long</tt> value.
     * @param def the value to be returned in the event that this
     *        node has no value associated with <tt>key</tt>
     *        or the associated value cannot be interpreted as a <tt>long</tt> type
     *        or the backing store is inaccessible.
     * @return the <tt>long</tt> value represented by the <tt>String</tt> object associated with
     *         <tt>key</tt> in this node, or <tt>def</tt> if the
     *         associated value does not exist or cannot be interpreted as
     *         a <tt>long</tt> type.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see #putLong(String,long)
     * @see #get(String,String)
     */
    public abstract long getLong(String key, long def);

    /**
     * Associates a <tt>String</tt> object representing the specified <tt>boolean</tt> value with the
     * specified key in this node.  The associated string is
     * "true" if the value is <tt>true</tt>, and "false" if it is
     * <tt>false</tt>.  This method is intended for use in conjunction with
     * the {@link #getBoolean}method.
     *
     * <p>Implementor's note: it is <i>not</i> necessary that the
     * value be represented by a string in the backing store.  If
     * the backing store supports <tt>boolean</tt> values, it is not unreasonable to
     * use them.  This implementation detail is not visible through the
     * <Tt>Preferences</Tt> API, which allows the value to be read as a
     * <tt>boolean</tt> (with <tt>getBoolean</tt>) or a <tt>String</tt> (with <tt>get</tt>) type.
     *
     * @param key <tt>key</tt> with which the string form of value is to be associated.
     * @param value value whose string form is to be associated with <tt>key</tt>.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see #getBoolean(String,boolean)
     * @see #get(String,String)
     */
    public abstract void putBoolean(String key, boolean value);

    /**
     * Returns the <tt>boolean</tt> value represented by the <tt>String</tt> object associated with the
     * specified <tt>key</tt> in this node.  Valid strings
     * are "true", which represents <tt>true</tt>, and "false", which
     * represents <tt>false</tt>. Case is ignored, so, for example, "TRUE"
     * and "False" are also valid.  This method is intended for use in
     * conjunction with the {@link #putBoolean}method.
     *
     * <p>Returns the specified default if there is no value
     * associated with the <tt>key</tt>, the backing store is inaccessible, or if the
     * associated value is something other than "true" or
     * "false", ignoring case.
     *
     * @param key <tt>key</tt> whose associated value is to be returned as a <tt>boolean</tt>.
     * @param def the value to be returned in the event that this
     *        node has no value associated with <tt>key</tt>
     *        or the associated value cannot be interpreted as a <tt>boolean</tt>
     *        or the backing store is inaccessible.
     * @return the <tt>boolean</tt> value represented by the <tt>String</tt> object associated with
     *         <tt>key</tt> in this node, or <tt>null</tt> if the
     *         associated value does not exist or cannot be interpreted as
     *         a <tt>boolean</tt>.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see #get(String,String)
     * @see #putBoolean(String,boolean)
     */
    public abstract boolean getBoolean(String key, boolean def);

    /**
     * Associates a <tt>String</tt> object representing the specified <tt>float</tt> value with the
     * specified <tt>key</tt> in this node.  The associated <tt>String</tt> object is the
     * one that would be returned if the <tt>float</tt> value were passed to
     * <tt>Float.toString(float)</tt>.  This method is intended for use in
     * conjunction with the {@link #getFloat}method.
     *
     * <p>Implementor's note: it is <i>not</i> necessary that the
     * value be represented by a string in the backing store.  If
     * the backing store supports <tt>float</tt> values, it is not unreasonable to
     * use them.  This implementation detail is not visible through the
     * <Tt>Preferences</Tt> API, which allows the value to be read as a
     * <tt>float</tt> (with <tt>getFloat</tt>) or a <tt>String</tt> (with <tt>get</tt>) type.
     *
     * @param key <tt>key</tt> with which the string form of value is to be associated.
     * @param value value whose string form is to be associated with <tt>key</tt>.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see #getFloat(String,float)
     */
    public abstract void putFloat(String key, float value);

    /**
     * Returns the float <tt>value</tt> represented by the <tt>String</tt> object associated with the
     * specified <tt>key</tt> in this node.  The <tt>String</tt> object is converted to a
     * <tt>float</tt> value as by <tt>Float.parseFloat(String)</tt>.  Returns the specified
     * default if there is no value associated with the <tt>key</tt>, the backing store
     * is inaccessible, or if <tt>Float.parseFloat(String)</tt> would throw a
     * <tt>NumberFormatException</tt> if the associated value were passed.
     * This method is intended for use in conjunction with the {@link #putFloat}method.
     *
     * @param key <tt>key</tt> whose associated value is to be returned as a <tt>float</tt> value.
     * @param def the value to be returned in the event that this
     *        node has no value associated with <tt>key</tt>
     *        or the associated value cannot be interpreted as a <tt>float</tt> type
     *        or the backing store is inaccessible.
     * @return the <tt>float</tt> value represented by the string associated with
     *         <tt>key</tt> in this node, or <tt>def</tt> if the
     *         associated value does not exist or cannot be interpreted as
     *         a <tt>float</tt> type.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     * @see #putFloat(String,float)
     * @see #get(String,String)
     */
    public abstract float getFloat(String key, float def);

    /**
     * Associates a <tt>String</tt> object representing the specified <tt>double</tt> value with the
     * specified <tt>key</tt> in this node.  The associated <tt>String</tt> object is the
     * one that would be returned if the <tt>double</tt> value were passed to
     * <tt>Double.toString(double)</tt>.  This method is intended for use in
     * conjunction with the {@link #getDouble}method
     *
     * <p>Implementor's note: it is <i>not</i> necessary that the
     * value be represented by a string in the backing store.  If
     * the backing store supports <tt>double</tt> values, it is not unreasonable to
     * use them.  This implementation detail is not visible through the
     * <Tt>Preferences</Tt> API, which allows the value to be read as a
     * <tt>double</tt> (with <tt>getDouble</tt>) or a <tt>String</tt> (with <tt>get</tt>) type.
     *
     * @param key <tt>key</tt> with which the string form of value is to be associated.
     * @param value value whose string form is to be associated with <tt>key</tt>.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see #getDouble(String,double)
     */
    public abstract void putDouble(String key, double value);

    /**
     * Returns the <tt>double</tt> value represented by the <tt>String</tt> object associated with the
     * specified <tt>key</tt> in this node. The <tt>String</tt> object is converted to a
     * <tt>double</tt> value as by <tt>Double.parseDouble(String)</tt>.
     * Returns the specified
     * default if there is no value associated with the <tt>key</tt>, the backing store
     * is inaccessible, or if <tt>Double.parseDouble(String)</tt> would throw a
     * <tt>NumberFormatException</tt> if the associated value were passed.
     * This method is intended for use in conjunction with the {@link #putDouble} method.
     *
     * @param key <tt>key</tt> whose associated value is to be returned as a <tt>double</tt> value.
     * @param def the value to be returned in the event that this
     *        node has no value associated with <tt>key</tt>
     *        or the associated value cannot be interpreted as a <tt>double</tt> type
     *        or the backing store is inaccessible.
     * @return the <tt>double</tt> value represented by the <tt>String</tt> object associated with
     *         <tt>key</tt> in this node, or <tt>def</tt> if the
     *         associated value does not exist or cannot be interpreted as
     *         a <tt>double</tt> type.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the the {@link #removeNode()}method.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     * @see #putDouble(String,double)
     * @see #get(String,String)
     */
    public abstract double getDouble(String key, double def);

    /**
     * Associates a <tt>String</tt> object representing the specified <tt>byte[]</tt> with the
     * specified <tt>key</tt> in this node.  The associated <tt>String</tt> object
     * the <i>Base64</i> encoding of the <tt>byte[]</tt>, as defined in <a
     * href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>, Section 6.8,
     * with one minor change: the string will consist solely of characters
     * from the <i>Base64 Alphabet</i>; it will not contain any newline
     * characters.
     * This method is intended for use in conjunction with
     * the {@link #getByteArray}method.
     *
     * <p>Implementor's note: it is <i>not</i> necessary that the
     * value be represented by a <tt>String</tt> type in the backing store.  If
     * the backing store supports <tt>byte[]</tt> values, it is not unreasonable to
     * use them.  This implementation detail is not visible through the
     * <Tt>Preferences</Tt> API, which allows the value to be read as an
     * a <tt>byte[]</tt> object (with <tt>getByteArray</tt>) or a <tt>String</tt> object
     * (with <tt>get</tt>).
     *
     * @param key <tt>key</tt> with which the string form of <tt>value</tt> is to be associated.
     * @param value <tt>value</tt> whose string form is to be associated with <tt>key</tt>.
     * @throws NullPointerException if <tt>key</tt> or <tt>value</tt>
     * is <tt>null</tt>.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see #getByteArray(String,byte[])
     * @see #get(String,String)
     */
    public abstract void putByteArray(String key, byte[] value);

    /**
     * Returns the <tt>byte[]</tt> value represented by the <tt>String</tt> object associated with
     * the specified <tt>key</tt> in this node.  Valid <tt>String</tt> objects are
     * <i>Base64</i> encoded binary data, as defined in
	 * <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>, Section 6.8,
     * with one minor change: the string must consist solely of characters
     * from the <i>Base64 Alphabet</i>; no newline characters or
     * extraneous characters are permitted.  This method is intended for use
     * in conjunction with the {@link #putByteArray} method.
     *
     * <p>Returns the specified default if there is no value
     * associated with the <tt>key</tt>, the backing store is inaccessible, or if the
     * associated value is not a valid Base64 encoded byte array
     * (as defined above).
     *
     * @param key <tt>key</tt> whose associated value is to be returned as a <tt>byte[]</tt> object.
     * @param def the value to be returned in the event that this
     *        node has no value associated with <tt>key</tt>
     *        or the associated value cannot be interpreted as a <tt>byte[]</tt> type,
     *        or the backing store is inaccessible.
     * @return the <tt>byte[]</tt> value represented by the <tt>String</tt> object associated with
     *         <tt>key</tt> in this node, or <tt>def</tt> if the
     *         associated value does not exist or cannot be interpreted as
     *         a <tt>byte[]</tt>.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>. (A
     *         <tt>null</tt> value for <tt>def</tt> <i>is</i> permitted.)
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see #get(String,String)
     * @see #putByteArray(String,byte[])
     */
    public abstract byte[] getByteArray(String key, byte[] def);

    /**
     * Returns all of the keys that have an associated value in this
     * node.  (The returned array will be of size zero if
     * this node has no preferences and not <tt>null</tt>!)
     *
     * @return an array of the keys that have an associated value in this
     *         node.
     * @throws BackingStoreException if this operation cannot be completed
     *         due to a failure in the backing store, or inability to
     *         communicate with it.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     */
    public abstract String[] keys() throws BackingStoreException;

    /**
     * Returns the names of the children of this node.
     * (The returned array will be of size zero if this node has
     * no children and not <tt>null</tt>!)
     *
     * @return the names of the children of this node.
     * @throws BackingStoreException if this operation cannot be completed
     *         due to a failure in the backing store, or inability to
     *         communicate with it.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     */
    public abstract String[] childrenNames() throws BackingStoreException;

    /**
     * Returns the parent of this node, or <tt>null</tt> if this is
     * the root.
     *
     * @return the parent of this node.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     */
    public abstract Preferences parent();

    /**
     * Returns a named <tt>Preferences</tt> object (node), creating it and any of its ancestors
     * if they do not already exist.  Accepts a relative or absolute pathname.
     * Absolute pathnames (which begin with <tt>'/'</tt>) are interpreted
     * relative to the root of this node.  Relative pathnames
     * (which begin with any character other than <tt>'/'</tt>) are
     * interpreted relative to this node itself.  The empty string
     * (<tt>""</tt>) is a valid relative pathname, referring to this
     * node itself.
     *
     * <p>If the returned node did not exist prior to this call, this node and
     * any ancestors that were created by this call are not guaranteed
     * to become persistent until the <tt>flush</tt> method is called on
     * the returned node (or one of its descendants).
     *
     * @param pathName the path name of the <tt>Preferences</tt> object to return.
     * @return the specified <tt>Preferences</tt> object.
     * @throws IllegalArgumentException if the path name is invalid.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @throws NullPointerException if path name is <tt>null</tt>.
     * @see #flush()
     */
    public abstract Preferences node(String pathName);

    /**
     * Returns true if the named node exists.  Accepts a relative
     * or absolute pathname.  Absolute pathnames (which begin with
     * <tt>'/'</tt>) are interpreted relative to the root of this
     * node.  Relative pathnames (which begin with any character other than
     * <tt>'/'</tt>) are interpreted relative to this node itself.
     * The pathname <tt>""</tt> is valid, and refers to this node
     * itself.
     *
     * <p>If this node (or an ancestor) has already been removed with the
     * {@link #removeNode()}method, it <i>is</i> legal to invoke this method,
     * but only with the pathname <tt>""</tt>; the invocation will return
     * <tt>false</tt>.  Thus, the idiom <tt>p.nodeExists("")</tt> may be
     * used to test whether <tt>p</tt> has been removed.
     *
     * @param pathName the path name of the node whose existence
     *        is to be checked.
     * @return true if the specified node exists.
     * @throws BackingStoreException if this operation cannot be completed
     *         due to a failure in the backing store, or inability to
     *         communicate with it.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method and
     *         <tt>pathname</tt> is not the empty string (<tt>""</tt>).
     * @throws IllegalArgumentException if the path name is invalid
     *         (i.e., it contains multiple consecutive slash
     *         characters, or ends with a slash character and is more than
     *         one character long).
     */
    public abstract boolean nodeExists(String pathName)
        throws BackingStoreException;

    /**
     * Removes this node and all of its descendants, invalidating
     * any properties contained in the removed nodes.  Once a node has been
     * removed, attempting any method other than <tt>name()</tt>,
     * <tt>absolutePath()</tt> or
     * <tt>nodeExists("")</tt> on the corresponding <tt>Preferences</tt>
     * instance will fail with an <tt>IllegalStateException</tt>.  (The
     * methods defined on <tt>Object</tt> can still be invoked on a node after
     * it has been removed; they will not throw
     * <tt>IllegalStateException</tt>.)
     *
     * <p>The removal is not guaranteed to be persistent until the
     * <tt>flush</tt> method is called on the parent of this node.
     * (It is illegal to remove the root node.)
     *
     * @throws IllegalStateException if this node (or an ancestor) has already
     *         been removed with the {@link #removeNode()}method.
     * @throws RuntimeException if this is a root node.
     * @throws BackingStoreException if this operation cannot be
     *         completed due to a failure in the backing store, or
     *         inability to communicate with it.
     * @see #flush()
     */
    public abstract void removeNode()
    throws BackingStoreException;

    /**
     * Returns this node's name, relative to its parent.
     *
     * @return this node's name, relative to its parent.
     */
    public abstract String name();

    /**
     * Returns this node's absolute path name.  Note that:
     * <ul>
     *     <li>Root node - The path name of the root node is <tt>"/"</tt>.
     *     <li>Slash at end - Path names other than that of the root node may not end in
     *     slash (<tt>'/'</tt>).
     *     <li>Unusual names - <tt>"."</tt> and <tt>".."</tt> have <i>no</i> special significance
     *     in path names.
     *     <li>Illegal names - The only illegal path names are those that contain multiple
     *     consecutive slashes, or that end in slash and are not the root.
     * </ul>
     *
     * @return this node's absolute path name.
     */
    public abstract String absolutePath();

    /**
     * Forces any changes in the contents of this node and its
     * descendants to the persistent store.
     *
     * <p>Once this method returns
     * successfully, it is safe to assume that all changes made in the
     * subtree rooted at this node prior to the method invocation have become
     * permanent.
     *
     * <p>Implementations are free to flush changes into the persistent store
     * at any time.  They do not need to wait for this method to be called.
     *
     * <p>When a flush occurs on a newly created node, it is made persistent,
     * as are any ancestors (and descendants) that have yet to be made
     * persistent.  Note however that any properties value changes in
     * ancestors are <i>not</i> guaranteed to be made persistent.
     *
     * @throws BackingStoreException if this operation cannot be completed
     *         due to a failure in the backing store, or inability to
     *         communicate with it.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see    #sync()
     */
    public abstract void flush() throws BackingStoreException;

    /**
     * Ensures that future reads from this node and its
     * descendants reflect any changes that were committed to the persistent
     * store (from any VM) prior to the <tt>sync</tt> invocation.  As a
     * side-effect, forces any changes in the contents of this node
     * and its descendants to the persistent store, as if the <tt>flush</tt>
     * method had been invoked on this node.
     *
     * @throws BackingStoreException if this operation cannot be completed
     *         due to a failure in the backing store, or inability to
     *         communicate with it.
     * @throws IllegalStateException if this node (or an ancestor) has been
     *         removed with the {@link #removeNode()}method.
     * @see    #flush()
     */
    public abstract void sync() throws BackingStoreException;

}
