/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
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
package org.osgi.service.event;

import java.io.IOException;
import java.security.*;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A bundle's authority to publish or subscribe to event on a topic.
 * 
 * <p>
 * A topic is a dot-separated string that defines a topic.
 * <p>
 * For example:
 * 
 * <pre>
 * <tt>
 * org.osgi.service.foo.ACTION
 * </tt>
 * </pre>
 * 
 * <p>
 * <tt>TopicPermission</tt> has two actions: <tt>PUBLISH</tt> and
 * <tt>SUBSCRIBE</tt>.
 * 
 * @version $Revision$
 */
public final class TopicPermission extends BasicPermission {
	/**
	 * The action string <tt>publish</tt>.
	 */
	public final static String	PUBLISH				= "publish";
	/**
	 * The action string <tt>subscribe</tt>.
	 */
	public final static String	SUBSCRIBE			= "subscribe";
	private final static int	ACTION_PUBLISH		= 0x00000001;
	private final static int	ACTION_SUBSCRIBE	= 0x00000002;
	private final static int	ACTION_ALL			= ACTION_PUBLISH
															| ACTION_SUBSCRIBE;
	private final static int	ACTION_NONE			= 0;
	private final static int	ACTION_ERROR		= 0x80000000;
	/**
	 * The actions mask.
	 */
	private transient int		action_mask			= ACTION_NONE;
	/**
	 * The actions in canonical form.
	 * 
	 * @serial
	 */
	private String				actions				= null;

	/**
	 * Defines the authority to publich and/or subscribe to a topic within the
	 * EventChannel service.
	 * <p>
	 * The name is specified as a dot-separated string. Wildcards may be used.
	 * For example:
	 * 
	 * <pre>
	 *   org.osgi.service.foo.ACTION
	 *   com.isv.*
	 *   *
	 * </pre>
	 * 
	 * <p>
	 * A bundle that needs to publish events on a topic must have the
	 * appropriate <tt>TopicPermission</tt> for that topic; similarly, a
	 * bundle that needs to subscribe to events on a topic must have the
	 * appropriate <tt>TopicPermssion</tt> for that topic.
	 * <p>
	 * 
	 * @param name Topic name.
	 * @param actions <tt>PUBLISH</tt>,<tt>SUBSCRIBE</tt> (canonical
	 *        order).
	 */
	public TopicPermission(String name, String actions) {
		this(name, getMask(actions));
	}

	/**
	 * Topic private constructor used by TopicPermissionCollection.
	 * 
	 * @param name class name
	 * @param mask action mask
	 */
	TopicPermission(String name, int mask) {
		super(name);
		init(mask);
	}

	/**
	 * Called by constructors and when deserialized.
	 * 
	 * @param mask action mask
	 */
	private void init(int mask) {
		if ((mask == ACTION_NONE) || ((mask & ACTION_ALL) != mask)) {
			throw new IllegalArgumentException("invalid action string");
		}
		action_mask = mask;
	}

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
			return (mask);
		}
		char[] a = actions.toCharArray();
		int i = a.length - 1;
		if (i < 0)
			return (mask);
		while (i != -1) {
			char c;
			// skip whitespace
			while ((i != -1)
					&& ((c = a[i]) == ' ' || c == '\r' || c == '\n'
							|| c == '\f' || c == '\t'))
				i--;
			// check for the known strings
			int matchlen;
			if (i >= 8 && (a[i - 8] == 's' || a[i - 8] == 'S')
					&& (a[i - 7] == 'u' || a[i - 7] == 'U')
					&& (a[i - 6] == 'b' || a[i - 6] == 'B')
					&& (a[i - 5] == 's' || a[i - 5] == 'S')
					&& (a[i - 4] == 'c' || a[i - 4] == 'C')
					&& (a[i - 3] == 'r' || a[i - 3] == 'R')
					&& (a[i - 2] == 'i' || a[i - 2] == 'I')
					&& (a[i - 1] == 'b' || a[i - 1] == 'B')
					&& (a[i] == 'e' || a[i] == 'E')) {
				matchlen = 9;
				mask |= ACTION_SUBSCRIBE;
			}
			else
				if (i >= 6 && (a[i - 6] == 'p' || a[i - 6] == 'P')
						&& (a[i - 5] == 'u' || a[i - 5] == 'U')
						&& (a[i - 4] == 'b' || a[i - 4] == 'B')
						&& (a[i - 3] == 'l' || a[i - 3] == 'L')
						&& (a[i - 2] == 'i' || a[i - 2] == 'I')
						&& (a[i - 1] == 's' || a[i - 1] == 'S')
						&& (a[i] == 'h' || a[i] == 'H')) {
					matchlen = 7;
					mask |= ACTION_PUBLISH;
				}
				else {
					// parse error
					throw new IllegalArgumentException("invalid permission: "
							+ actions);
				}
			// make sure we didn't just match the tail of a word
			// like "ackbarfpublish". Also, skip to the comma.
			seencomma = false;
			while (i >= matchlen && !seencomma) {
				switch (a[i - matchlen]) {
					case ',' :
						seencomma = true;
					/* FALLTHROUGH */
					case ' ' :
					case '\r' :
					case '\n' :
					case '\f' :
					case '\t' :
						break;
					default :
						throw new IllegalArgumentException(
								"invalid permission: " + actions);
				}
				i--;
			}
			// point i at the location of the comma minus one (or -1).
			i -= matchlen;
		}
		if (seencomma) {
			throw new IllegalArgumentException("invalid permission: " + actions);
		}
		return (mask);
	}

	/**
	 * Determines if the specified permission is implied by this object.
	 * 
	 * <p>
	 * This method checks that the topic name of the target is implied by the
	 * topic name of this object. The list of <tt>TopicPermission</tt> actions
	 * must either match or allow for the list of the target object to imply the
	 * target <tt>TopicPermission</tt> action.
	 * <p>
	 * 
	 * <pre>
	 *   x.y.*,&quot;publish&quot; -&gt; x.y.z,&quot;publish&quot; is true
	 *   *,&quot;subscribe&quot; -&gt; x.y,&quot;subscribe&quot;   is true
	 *   *,&quot;publish&quot; -&gt; x.y,&quot;subscribe&quot;     is false
	 *   x.y,&quot;publish&quot; -&gt; x.y.z,&quot;publish&quot;   is false
	 * </pre>
	 * 
	 * @param p The target permission to interrogate.
	 * @return <tt>true</tt> if the specified <tt>TopicPermission</tt>
	 *         action is implied by this object; <tt>false</tt> otherwise.
	 */
	public boolean implies(Permission p) {
		if (p instanceof TopicPermission) {
			TopicPermission target = (TopicPermission) p;
			return (((action_mask & target.action_mask) == target.action_mask) && super
					.implies(p));
		}
		return (false);
	}

	/**
	 * Returns the canonical string representation of the
	 * <tt>TopicPermission</tt> actions.
	 * 
	 * <p>
	 * Always returns present <tt>TopicPermission</tt> actions in the
	 * following order: <tt>PUBLISH</tt>,<tt>SUBSCRIBE</tt>.
	 * 
	 * @return Canonical string representation of the <tt>TopicPermission</tt>
	 *         actions.
	 */
	public String getActions() {
		if (actions == null) {
			StringBuffer sb = new StringBuffer();
			boolean comma = false;
			if ((action_mask & ACTION_PUBLISH) == ACTION_PUBLISH) {
				sb.append(PUBLISH);
				comma = true;
			}
			if ((action_mask & ACTION_SUBSCRIBE) == ACTION_SUBSCRIBE) {
				if (comma)
					sb.append(',');
				sb.append(SUBSCRIBE);
			}
			actions = sb.toString();
		}
		return (actions);
	}

	/**
	 * Returns a new <tt>PermissionCollection</tt> object suitable for storing
	 * <tt>TopicPermission</tt> objects.
	 * 
	 * @return A new <tt>PermissionCollection</tt> object.
	 */
	public PermissionCollection newPermissionCollection() {
		return (new TopicPermissionCollection());
	}

	/**
	 * Determines the equality of two <tt>TopicPermission</tt> objects.
	 * 
	 * This method checks that specified Topic has the same Topic name and
	 * <tt>TopicPermission</tt> actions as this <tt>TopicPermission</tt>
	 * object.
	 * 
	 * @param obj The object to test for equality with this
	 *        <tt>TopicPermission</tt> object.
	 * @return <tt>true</tt> if <tt>obj</tt> is a <tt>TopicPermission</tt>,
	 *         and has the same Topic name and actions as this
	 *         <tt>TopicPermission</tt> object; <tt>false</tt> otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return (true);
		}
		if (!(obj instanceof TopicPermission)) {
			return (false);
		}
		TopicPermission p = (TopicPermission) obj;
		return ((action_mask == p.action_mask) && getName().equals(p.getName()));
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return A hash code value for this object.
	 */
	public int hashCode() {
		return (getName().hashCode() ^ getActions().hashCode());
	}

	/**
	 * Returns the current action mask.
	 * <p>
	 * Used by the TopicPermissionCollection class.
	 * 
	 * @return Current action mask.
	 */
	int getMask() {
		return (action_mask);
	}

	/**
	 * WriteObject is called to save the state of this permission object to a
	 * stream. The actions are serialized, and the superclass takes care of the
	 * name.
	 */
	private synchronized void writeObject(java.io.ObjectOutputStream s)
			throws IOException {
		// Write out the actions. The superclass takes care of the name
		// call getActions to make sure actions field is initialized
		if (actions == null)
			getActions();
		s.defaultWriteObject();
	}

	/**
	 * readObject is called to restore the state of this permission from a
	 * stream.
	 */
	private synchronized void readObject(java.io.ObjectInputStream s)
			throws IOException, ClassNotFoundException {
		// Read in the action, then initialize the rest
		s.defaultReadObject();
		init(getMask(actions));
	}
}

/**
 * Stores a set of <tt>TopicPermission</tt> permissions.
 * 
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 */
final class TopicPermissionCollection extends PermissionCollection {
	/**
	 * Table of permissions.
	 * 
	 * @serial
	 */
	private Hashtable	permissions;
	/**
	 * Boolean saying if "*" is in the collection.
	 * 
	 * @serial
	 */
	private boolean		all_allowed;

	/**
	 * Create an empty TopicPermissions object.
	 *  
	 */
	public TopicPermissionCollection() {
		permissions = new Hashtable();
		all_allowed = false;
	}

	/**
	 * Adds a permission to the <tt>TopicPermission</tt> objects. The key for
	 * the hash is the name.
	 * 
	 * @param permission The <tt>TopicPermission</tt> object to add.
	 * 
	 * @exception IllegalArgumentException If the permission is not a
	 *            <tt>TopicPermission</tt> instance.
	 * 
	 * @exception SecurityException If this <tt>TopicPermissionCollection</tt>
	 *            object has been marked read-only.
	 */
	public void add(Permission permission) {
		if (!(permission instanceof TopicPermission))
			throw new IllegalArgumentException("invalid permission: "
					+ permission);
		if (isReadOnly())
			throw new SecurityException("attempt to add a Permission to a "
					+ "readonly PermissionCollection");
		TopicPermission pp = (TopicPermission) permission;
		String name = pp.getName();
		TopicPermission existing = (TopicPermission) permissions.get(name);
		if (existing != null) {
			int oldMask = existing.getMask();
			int newMask = pp.getMask();
			if (oldMask != newMask) {
				permissions.put(name, new TopicPermission(name, oldMask
						| newMask));
			}
		}
		else {
			permissions.put(name, permission);
		}
		if (!all_allowed) {
			if (name.equals("*"))
				all_allowed = true;
		}
	}

	/**
	 * Determines if the specified permissions implies the permissions expressed
	 * in <tt>permission</tt>.
	 * 
	 * @param permission The Permission object to compare with this
	 *        <tt>TopicPermission</tt> object.
	 * 
	 * @return <tt>true</tt> if <tt>permission</tt> is a proper subset of a
	 *         permission in the set; <tt>false</tt> otherwise.
	 */
	public boolean implies(Permission permission) {
		if (!(permission instanceof TopicPermission))
			return false;
		TopicPermission pp = (TopicPermission) permission;
		TopicPermission x;
		int desired = pp.getMask();
		int effective = 0;
		// short circuit if the "*" Permission was added
		if (all_allowed) {
			x = (TopicPermission) permissions.get("*");
			if (x != null) {
				effective |= x.getMask();
				if ((effective & desired) == desired)
					return true;
			}
		}
		// strategy:
		// Check for full match first. Then work our way up the
		// name looking for matches on a.b.*
		String name = pp.getName();
		x = (TopicPermission) permissions.get(name);
		if (x != null) {
			// we have a direct hit!
			effective |= x.getMask();
			if ((effective & desired) == desired)
				return true;
		}
		// work our way up the tree...
		int last, offset;
		offset = name.length() - 1;
		while ((last = name.lastIndexOf(".", offset)) != -1) {
			name = name.substring(0, last + 1) + "*";
			x = (TopicPermission) permissions.get(name);
			if (x != null) {
				effective |= x.getMask();
				if ((effective & desired) == desired)
					return true;
			}
			offset = last - 1;
		}
		// we don't have to check for "*" as it was already checked
		// at the top (all_allowed), so we just return false
		return false;
	}

	/**
	 * Returns an enumeration of all <tt>TopicPermission</tt> objects in the
	 * container.
	 * 
	 * @return Enumeration of all <tt>TopicPermission</tt> objects.
	 */
	public Enumeration elements() {
		return permissions.elements();
	}
}