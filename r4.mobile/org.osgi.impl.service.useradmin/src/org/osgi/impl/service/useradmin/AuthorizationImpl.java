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
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.useradmin.*;

/**
 * {@link org.osgi.service.useradmin.Authorization}implementation.
 */
public class AuthorizationImpl implements Authorization {
	/**
	 * Link to the UserAdmin creating this Authorization instance.
	 */
	private UserAdminImpl	ua;
	/**
	 * The user this Authorization context was created for.
	 */
	protected User			user;
	/**
	 * The UserAdmin version number for which this instance's caches are valid.
	 */
	protected long			ua_version;
	/**
	 * Cache holding the roles checked for implication sofar. Must be recomputed
	 * if the UserAdmin database is changed.
	 */
	private Hashtable		/* Role */cache	= new Hashtable();
	/**
	 * The working set of roles, i.e., the roles that we currently are working
	 * on. Used for loop detection.
	 */
	private Vector			/* Role */working	= new Vector();
	/**
	 * The UserAdmin will set the first element of this array to false when it
	 * is going away. All references to the UserAdmin from Authorization objects
	 * must be guarded by a check that the UserAdmin is still alive.
	 */
	private boolean[]		alive;

	protected AuthorizationImpl(UserAdminImpl ua, User user) {
		this.ua = ua;
		this.user = user;
		this.alive = ua.alive;
		// Reset cache when the user manager is updated
		ua_version = ua.version;
	}

	/**
	 * Gets the name of user this AuthorizationImpl was created for.
	 * Implementation of
	 * {@link org.osgi.service.useradmin.Authorization#getName}.
	 */
	public String getName() {
		// Invalidate this object if the UserAdmin has disappeared.
		if (!alive[0])
			return null;
		return user == null ? null : user.getName();
	}

	/**
	 * Checks if this Authorization context implies the specified role.
	 * Implementation of
	 * {@link org.osgi.service.useradmin.Authorization#hasRole}.
	 */
	public boolean hasRole(String name) {
		// Invalidate this object if the UserAdmin has disappeared.
		if (!alive[0])
			return false;
		// Synchronize on the UserAdmin to ensure no changes are made
		// while we are working.
		synchronized (ua) {
			checkCacheValidity();
			Role r = ua.getRole(name);
			return r != null && ((RoleImpl) r).impliedBy(this);
		}
	}

	/**
	 * Gets the roles implied by this Authorization context. Implementation of
	 * {@link org.osgi.service.useradmin.Authorization#getRoles}.
	 */
	public String[] getRoles() {
		// Invalidate this object if the UserAdmin has disappeared.
		if (!alive[0])
			return null;
		// Synchronize on the UserAdmin to ensure no changes are made
		// while we are working.
		synchronized (ua) {
			checkCacheValidity();
			Role[] roles = null;
			try {
				roles = ua.getRoles(null);
			}
			catch (InvalidSyntaxException e) { /* Impossible */
			}
			for (int i = 0; i < roles.length; i++) {
				// The following call will update the cache.
				((RoleImpl) roles[i]).impliedBy(this);
			}
			// Now check the updated cache
			Vector v = new Vector();
			for (Enumeration en = cache.keys(); en.hasMoreElements();) {
				Role role = (Role) en.nextElement();
				if (((Boolean) cache.get(role)).booleanValue()) {
					String name = role.getName();
					if (!name.equals(Role.USER_ANYONE))
						v.addElement(role.getName());
				}
			}
			String[] g = new String[v.size()];
			v.copyInto(g);
			return g;
		}
	}

	/* -------- Protected and private methods -------- */
	protected Boolean cachedHaveRole(Role role) {
		return (Boolean) cache.get(role);
	}

	protected void workingOnRole(Role role) {
		working.addElement(role);
	}

	protected boolean isWorkingOnRole(Role role) {
		return working.contains(role);
	}

	protected boolean cacheHaveRole(Role role, boolean have) {
		cache.put(role, new Boolean(have));
		working.removeElement(role);
		return have;
	}

	private void checkCacheValidity() {
		if (ua_version != ua.version) {
			invalidateCache();
		}
	}

	private void invalidateCache() {
		ua_version = ua.version;
		cache = new Hashtable();
		working = new Vector();
	}
}
