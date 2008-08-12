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
import java.io.*;
import java.security.Permission;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.useradmin.*;
import org.osgi.util.tracker.ServiceTracker;

/**
 * {@link org.osgi.service.useradmin.UserAdmin}implementation.
 * 
 * All modifications of the database are synchronized using a lock object.
 */
public class UserAdminImpl implements UserAdmin {
	/**
	 * The role database. It is represented as a mapping from role names to
	 * roles.
	 */
	protected Hashtable			/* String -> Role */rolemap;
	/**
	 * Database version number. Increases every time the database is modified.
	 * Used to check if an authorization cache should be invalidated.
	 */
	protected long					version			= 0;
	/**
	 * An instance of UserAdminPermin.ADMIN.
	 */
	protected UserAdminPermission	adminPermission	= new UserAdminPermission(
															UserAdminPermission.ADMIN,
															null);
	/**
	 * Property pointing out the file containing the role database information.
	 */
	private final static String		DBPROP			= "org.osgi.impl.service.useradmin.db";
	/**
	 * Local file to use if DBPROP is not set.
	 */
	private final static String		DB				= "useradmin.db";
	/**
	 * The name of the always present role. This role is always implied.
	 */
	protected final static String	ANYONE			= "user.anyone";
	/**
	 * The UserAdmin BundleContext.
	 */
	protected Activator				activator;
	/**
	 * Tracker for listeners to notify when the database is modified.
	 */
	private ServiceTracker			listeners;
	/**
	 * Convenient log wrapper.
	 */
	protected LogTracker			log;
	/**
	 * Role database locking object.
	 */
	protected Object				dblock			= new Object();
	/**
	 * Flag telling if this UserAdmin is active or not. Used by Authorization
	 * objects to check if the UserAdmin that created them is still active. The
	 * state must be a mutable object, hence the placement as an array element.
	 */
	protected boolean[]				alive			= new boolean[] {true};
	/**
	 * The persistent useradmin database.
	 */
	private File					dbfile			= null;
	/**
	 * Queue used to asynchronously deliver the events
	 */
	private EventQueue				queue;

	/**
	 * Initializes this UserAdmin.
	 */
	public UserAdminImpl(Activator activator) {
		this.activator = activator;
		log = activator.log;
		listeners = new ServiceTracker(activator.bc, UserAdminListener.class
				.getName(), null);
		listeners.open();
		// Initialize the database
		rolemap = new Hashtable();
		// Load database from file
		String filename = System.getProperty(DBPROP);
		if (filename == null)
			dbfile = activator.bc.getDataFile(DB);
		else
			dbfile = new File(filename);
		load();
		queue = new EventQueue();
	}

	/**
	 * Creates a role. Implementation of
	 * {@link org.osgi.service.useradmin.UserAdmin#createRole}.
	 * 
	 * @see org.osgi.service.useradmin.Role
	 * @see org.osgi.service.useradmin.User
	 * @see org.osgi.service.useradmin.Group
	 */
	public Role createRole(String name, int type) {
		// Check that the caller has permission to use this method.
		checkPermission(adminPermission);
		synchronized (dblock) {
			Role role = (Role) rolemap.get(name);
			if (role != null)
				return null;
			switch (type) {
				case Role.USER :
					role = new UserImpl(this, name);
					break;
				case Role.GROUP :
					role = new GroupImpl(this, name);
					break;
				default :
					throw new IllegalArgumentException("Bad type: " + type);
			}
			rolemap.put(name, role);
			notifyListeners(UserAdminEvent.ROLE_CREATED, role);
			// Persistently save the database.
			save();
			return role;
		}
	}

	/**
	 * Removes a role. Implementation of
	 * {@link org.osgi.service.useradmin.UserAdmin#removeRole}.
	 */
	public boolean removeRole(String name) {
		// Check that the role is not the user.anyone role
		if (name.equals(ANYONE))
			return false;
		// Check that the caller has permission to use this method.
		checkPermission(adminPermission);
		synchronized (dblock) {
			RoleImpl role = (RoleImpl) rolemap.get(name);
			if (role == null)
				return false;
			notifyListeners(UserAdminEvent.ROLE_REMOVED, role);
			rolemap.remove(name);
			// Remove all references to this role from groups.
			for (Enumeration en = rolemap.elements(); en.hasMoreElements();) {
				((RoleImpl) en.nextElement()).removeReferenceTo(role);
			}
			// Persistently save the database.
			save();
			return true;
		}
	}

	/**
	 * Gets a named role. Implementation of
	 * {@link org.osgi.service.useradmin.UserAdmin#getRole}.
	 */
	public Role getRole(String name) {
		synchronized (dblock) {
			return (Role) rolemap.get(name);
		}
	}

	/**
	 * Gets a user with the specified property set to the specified value.
	 * Implementation of {@link org.osgi.service.useradmin.UserAdmin#getUser}.
	 */
	public User getUser(String key, String value) {
		User user = null;
		synchronized (dblock) {
			// Linear search. A real implementation will do something else.
			for (Enumeration en = rolemap.elements(); en.hasMoreElements();) {
				Role role = (Role) en.nextElement();
				Object pval = role.getProperties().get(key);
				if (pval != null && pval instanceof String
						&& value.equals(pval)) {
					if (user == null && role instanceof User)
						user = (User) role;
					else
						// More than one match!!!
						return null;
				}
			}
		}
		return user;
	}

	/**
	 * Gets the role matching the specified filter. Implementation of
	 * {@link org.osgi.service.useradmin.UserAdmin#getRoles}.
	 */
	public Role[] getRoles(String filterstr) throws InvalidSyntaxException {
		synchronized (dblock) {
			Enumeration en = rolemap.elements();
			if (filterstr == null) {
				Role[] roles = new Role[rolemap.size()];
				for (int i = 0; en.hasMoreElements(); i++)
					roles[i] = (Role) en.nextElement();
				return roles;
			}
			else {
				Filter filter = activator.bc.createFilter(filterstr);
				Vector matches = new Vector();
				for (int i = 0; en.hasMoreElements(); i++) {
					Role role = (Role) en.nextElement();
					if (filter.match(role.getProperties()))
						matches.addElement(role);
				}
				if (matches.size() > 0) {
					Role[] roles = new Role[matches.size()];
					matches.copyInto(roles);
					return roles;
				}
				else
					return null;
			}
		}
	}

	/**
	 * Gets an authorization context for the specified user. Implementation of
	 * {@link org.osgi.service.useradmin.UserAdmin#getAuthorization}.
	 */
	public Authorization getAuthorization(User user) {
		return new AuthorizationImpl(this, user);
	}

	/* ----------- Protected methods --------------- */
	/**
	 * Set flag to tell Authorization objects we're dead.
	 */
	protected void die() {
		queue.close();
		alive[0] = false;
	}

	/**
	 * Notifies UserAdminListeners that the role database is modified.
	 */
	protected void notifyListeners(int type, Role role) {
		UserAdminEvent event = new UserAdminEvent(
				activator.uasr.getReference(), type, role);
		Object[] ls = listeners.getServices();
		if (ls == null)
			return;
		queue.publishEvent(ls, event);
	}

	/**
	 * Save the user manager database as a property file.
	 */
	protected void save() {
		// Invalidates all caches
		version++;
		Properties p = new Properties();
		int counter = 0;
		for (Enumeration en = rolemap.elements(); en.hasMoreElements(); counter++) {
			RoleImpl role = (RoleImpl) en.nextElement();
			p.put(counter + "n", role.getName());
			role.properties.save(counter + "p", p);
			switch (role.getType()) {
				case Role.ROLE :
					p.put(counter + "t", "role");
					break;
				case Role.USER :
					p.put(counter + "t", "user");
					break;
				case Role.GROUP :
					p.put(counter + "t", "group");
					break;
			}
			if (role instanceof UserImpl) {
				((UserImpl) role).credentials.save(counter + "c", p);
			}
			if (role instanceof GroupImpl) {
				GroupImpl group = (GroupImpl) role;
				Enumeration en2 = group.basic_members.elements();
				for (int i = 0; en2.hasMoreElements(); i++) {
					p.put(counter + "bm" + i, (String) en2.nextElement());
				}
				en2 = group.required_members.elements();
				for (int i = 0; en2.hasMoreElements(); i++) {
					p.put(counter + "rm" + i, (String) en2.nextElement());
				}
			}
		}
		p.put("version", "" + version);
		try {
			p.store(new FileOutputStream(dbfile),
					"User manager database, version " + version);
		}
		catch (IOException e) {
			log.error("IO error saving user db", e);
		}
	}

	/**
	 * Load the user manager database from a property file.
	 */
	void load() {
		rolemap = new Hashtable();
		// Load the database with the predefined roles
		rolemap.put(ANYONE, new RoleImpl(this, ANYONE, Role.ROLE));
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(dbfile));
			version = Long.parseLong(p.getProperty("version"));
		}
		catch (FileNotFoundException e) {
			// file has not been created yet, ok
			version = 0;
		}
		catch (Exception e) {
			log.error("Error reading user db", e);
		}
		int counter = 0;
		String name;
		for (counter = 0; (name = p.getProperty(counter + "n")) != null; counter++) {
			String type = p.getProperty(counter + "t");
			if (type == null) {
				log.warning("Bad database entry: " + counter + " (" + name
						+ ")");
				continue;
			}
			Role role = null;
			if (type.equals("group")) {
				int t = Role.GROUP;
				GroupImpl group = new GroupImpl(this, name, t);
				role = group;
				String member;
				for (int i = 0; (member = p.getProperty(counter + "bm" + i)) != null; i++) {
					group.basic_members.addElement(member);
				}
				for (int i = 0; (member = p.getProperty(counter + "rm" + i)) != null; i++) {
					group.required_members.addElement(member);
				}
			}
			else
				if (type.equals("user")) {
					role = new UserImpl(this, name);
				}
				else
					if (type.equals("role")) {
						role = new RoleImpl(this, name, Role.ROLE);
					}
					else {
						log.warning("Strange role type: " + type);
					}
			rolemap.put(name, role);
			((RoleImpl) role).properties.load(counter + "p", p);
			if (role instanceof UserImpl)
				((UserImpl) role).credentials.load(counter + "c", p);
		}
	}

	public void checkPermission(Permission permission) {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			sm.checkPermission(permission);
		}
	}
}
