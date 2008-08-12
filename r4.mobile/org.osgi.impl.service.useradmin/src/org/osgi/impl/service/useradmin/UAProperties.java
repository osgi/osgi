package org.osgi.impl.service.useradmin;

/*
 * $Header$
 *
 * OSGi UserAdmin Reference Implementation.
 *
 * OSGi Confidential.
 *
 * (c) Copyright Gatespace AB 2000-2001.
 *
 * This source code is owned by Gatespace AB (www.gatespace.com), and is 
 * being distributed to OSGi MEMBERS as MEMBER LICENSED MATERIALS under
 * the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 *
 */
import java.util.*;
import org.osgi.service.useradmin.*;

/**
 * A dictionary with permissions checks for setting, changing, and deleting
 * values.
 * <p>
 * Role properties are instances of this class. Changes to these objects are
 * propagated to the {@link UserAdmin}service and made persistent. Only keys of
 * the type <code>String</code> are supported, and only values of type
 * <code>String</code> or of type <code>byte[]</code> are supported. If an
 * attempt is made to store a value of a type other than what is supported, an
 * exception of type <code>IllegalArgumentException</code> will be raised.
 * <p>
 * A {@link UserAdminPermission}with the action <code>changeProperty</code>
 * is required to change a Role's properties.
 */
public class UAProperties extends Dictionary {
	/**
	 * A link to the role having these properties.
	 */
	protected RoleImpl	role;
	/**
	 * The actual property map. Mapped from lowercased keys for efficient
	 * lookup. The real keys are stored in keymap.
	 */
	protected Hashtable /* String -> Object */ht		= new Hashtable();
	/**
	 * Mapping from lowercased keys to the original keys.
	 */
	protected Hashtable /* String -> String */keymap	= new Hashtable();

	public UAProperties(RoleImpl role) {
		this.role = role;
	}

	public Enumeration elements() {
		Vector v = new Vector();
		synchronized (role.ua.dblock) {
			for (Enumeration en = ht.keys(); en.hasMoreElements();) {
				try {
					v.addElement(get(en.nextElement()));
				}
				catch (SecurityException e) {
					// Ignore elements we don't have access to.
					// This class will never throw an exception (in the "get"
					// method), but the subclass UACredentials will.
				}
			}
		}
		return v.elements();
	}

	public Object get(Object key) {
		synchronized (role.ua.dblock) {
			// No security check for properties
			String lckey = ((String) key).toLowerCase();
			Object value = ht.get(lckey);
			if (value == null)
				return null;
			else {
				if (value instanceof byte[])
					// Clone byte arrays, so callers cannot change the database
					// by modifying the returned array.
					return ((byte[]) value).clone();
				else
					return value;
			}
		}
	}

	public boolean isEmpty() {
		return ht.isEmpty();
	}

	public Enumeration keys() {
		synchronized (role.ua.dblock) {
			return keymap.elements();
		}
	}

	public int size() {
		return ht.size();
	}

	public Object remove(Object key) {
		synchronized (role.ua.dblock) {
			if (key instanceof String) {
				// Check that the caller is allowed to remove the property.
				role.ua.checkPermission(new UserAdminPermission((String) key,
						getChangeAction()));
				String lckey = ((String) key).toLowerCase();
				Object res = ht.remove(lckey);
				keymap.remove(lckey);
				// Notify listeners that a role has changed.
				role.ua.notifyListeners(UserAdminEvent.ROLE_CHANGED, role);
				// Persistently save the database.
				role.ua.save();
				return res;
			}
			else
				throw new IllegalArgumentException(
						"The key must be a String, got " + key.getClass());
		}
	}

	public Object put(Object key, Object value) {
		synchronized (role.ua.dblock) {
			if (key instanceof String) {
				if (value instanceof String || value instanceof byte[]) {
					role.ua.checkPermission(new UserAdminPermission(
							(String) key, getChangeAction()));
					String lckey = ((String) key).toLowerCase();
					Object res = ht
							.put(lckey, value instanceof String ? new String(
									(String) value) : ((byte[]) value).clone());
					keymap.put(lckey, key);
					role.ua.notifyListeners(UserAdminEvent.ROLE_CHANGED, role);
					// Persistently save the database.
					role.ua.save();
					return res;
				}
				else
					throw new IllegalArgumentException(
							"The value must be of type String or byte[],  got "
									+ value.getClass());
			}
			else
				throw new IllegalArgumentException(
						"The key must be a String, got " + key.getClass());
		}
	}

	public String toString() {
		return "#Properties";
	}

	/**
	 * The permission need to modify the properties.
	 */
	protected String getChangeAction() {
		return UserAdminPermission.CHANGE_PROPERTY;
	}

	/**
	 * Stores this dictionary in the specified Properties object, where all keys
	 * are prefixed by the specified name.
	 */
	public void save(String name, Properties p) {
		Enumeration en = ht.keys();
		for (int i = 0; en.hasMoreElements(); i++) {
			Object lckey = en.nextElement();
			String key = (String) keymap.get(lckey);
			p.put(name + i + "n", key);
			Object obj = ht.get(lckey);
			String vname, value;
			if (obj instanceof String) {
				vname = name + i + "vs";
				value = (String) obj; // Should encode special chars
			}
			else {
				vname = name + i + "vb";
				value = new String((byte[]) obj); // Should base64-encode
			}
			p.put(vname, value);
		}
	}

	/**
	 * Loads values from the specified Properties object, with keys prefixed by
	 * the specified name, into this dictionary.
	 */
	public void load(String name, Properties p) {
		String key;
		for (int i = 0; (key = p.getProperty(name + i + "n")) != null; i++) {
			String lckey = key.toLowerCase();
			String val = p.getProperty(name + i + "vs");
			if (val != null) {
				// Should unencode special characters!
				ht.put(lckey, val);
				keymap.put(lckey, key);
				continue;
			}
			val = p.getProperty(name + i + "vb");
			if (val != null) {
				ht.put(lckey, val.getBytes());
				keymap.put(lckey, key);
				continue;
			}
			System.err.println("Bad property: " + name);
		}
	}
}
