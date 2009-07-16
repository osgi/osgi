/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.useradmin.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.osgi.service.useradmin.UserAdminEvent;
import org.osgi.service.useradmin.UserAdminListener;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class UserAdminControl extends DefaultTestBundleControl {
	private UserAdmin	useradmin;
	private List		rolesToKeep;

	protected void setUp() throws Exception {
		useradmin = (UserAdmin) getService(UserAdmin.class);
		rolesToKeep = asList(useradmin.getRoles(null));
	}

	protected void tearDown() throws Exception {
		List roles = new ArrayList(asList(useradmin.getRoles(null)));
		roles.removeAll(rolesToKeep);
		for (Iterator iter = roles.iterator(); iter.hasNext();) {
			Role role = (Role) iter.next();
			useradmin.removeRole(role.getName());
		}
		ungetService(useradmin);
	}

	private List asList(Role[] roles) {
		if (roles == null) {
			return Collections.EMPTY_LIST;
		}
		return Arrays.asList(roles);
	}

	public void testCreateRole() throws Exception {
		Role role = null;
		role = useradmin.createRole("foo-user", Role.USER);
		assertEquals("incorrect role name", "foo-user", roleToString(role));
		role = useradmin.createRole("foo-user", Role.USER);
		assertNull("incorrect role name", roleToString(role));
		role = useradmin.createRole("foo-group", Role.GROUP);
		assertEquals("incorrect role name", "foo-group", roleToString(role));
		try {
			role = useradmin.createRole("foobar", Role.ROLE);
			fail("expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testRemoveRole() throws Exception {
		useradmin.createRole("foo-user", Role.USER);
		/* Remove the role once */
		assertTrue("role not removed", useradmin.removeRole("foo-user"));
		/* And try to remove it again */
		assertFalse("role removed", useradmin.removeRole("foo-user"));
	}

	public void testGetRole() throws Exception {
		useradmin.createRole("foo1", Role.USER);
		useradmin.createRole("foo2", Role.USER);
		useradmin.createRole("foo3", Role.GROUP);
		useradmin.createRole("foo4", Role.USER);
		assertEquals("incorrect role name", "foo1", roleToString(useradmin
				.getRole("foo1")));
		assertEquals("incorrect role name", "foo2", roleToString(useradmin
				.getRole("foo2")));
		assertEquals("incorrect role name", "foo3", roleToString(useradmin
				.getRole("foo3")));
		assertEquals("incorrect role name", "foo4", roleToString(useradmin
				.getRole("foo4")));
		useradmin.removeRole("foo1");
		assertNull("incorrect role name", roleToString(useradmin
				.getRole("foo1")));
		assertEquals("incorrect role name", "user.anyone",
				roleToString(useradmin.getRole("user.anyone")));
	}

	public void testGetRoles() throws Exception {
		Role r1 = useradmin.createRole("role1", Role.GROUP);
		r1.getProperties().put(getName(), "r1");
		Role r2 = useradmin.createRole("role2", Role.USER);
		r2.getProperties().put(getName(), "r3");
		Role r3 = useradmin.createRole("role3", Role.GROUP);
		r3.getProperties().put(getName(), "r3");
		String[] result = sortRoleNames(useradmin.getRoles("(" + getName()
				+ "=r*)"));
		assertEquals("wrong number of roles", 3, result.length);
		assertEquals("wrong role name", "role1", result[0]);
		assertEquals("wrong role name", "role2", result[1]);
		assertEquals("wrong role name", "role3", result[2]);
	}

	public void testGetUser() throws Exception {
		Role r1 = useradmin.createRole("user1", Role.USER);
		r1.getProperties().put(getName(), "u1");
		r1.getProperties().put("foobar", "xxx");
		Role r2 = useradmin.createRole("user2", Role.USER);
		r2.getProperties().put(getName(), "u3");
		Role r3 = useradmin.createRole("user3", Role.USER);
		r3.getProperties().put(getName(), "u3");
		r3.getProperties().put("foobar", "xxx");
		assertNull("role not null", roleToString(useradmin.getUser(
				"unexisting-key", "foobar")));
		assertEquals("could not find role", "user1", roleToString(useradmin
				.getUser(getName(), "u1")));
		assertNull("role not null", roleToString(useradmin.getUser("foobar",
				"xxx")));
	}

	public void testRoleGetType() throws Exception {
		assertConstant(new Integer(Role.USER), "USER", Role.class);
		assertConstant(new Integer(Role.GROUP), "GROUP", Role.class);
		assertConstant(new Integer(Role.ROLE), "ROLE", Role.class);
		Role role = useradmin.createRole("r1", Role.USER);
		assertEquals(role.getType(), Role.USER);
		role = useradmin.createRole("r2", Role.GROUP);
		assertEquals(role.getType(), Role.GROUP);
	}

	public void testRoleChangeProperties() throws Exception {
		Role role = useradmin.createRole("r1", Role.USER);
		Dictionary p = role.getProperties();
		p.put("testKey", "testValue");
		assertEquals("property not set", "testValue", (String) role
				.getProperties().get("testKey"));
	}

	public void testRolePutProperties() throws Exception {
		Role role = useradmin.createRole("r1", Role.USER);
		Dictionary p = role.getProperties();
		p.put("testKey", "testValue");
		p.put("key", new byte[] {0, 0, 0});
		try {
			p.put(new Integer(1), "123");
			fail("expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testRoleGetName() throws Exception {
		assertEquals("incorrect role name", "r1", roleToString(useradmin
				.createRole("r1", Role.GROUP)));
	}

	public void testUserChangeCredentials() throws Exception {
		User user = (User) useradmin.createRole("u1", Role.USER);
		Dictionary cred = user.getCredentials();
		cred.put("testKey", "testValue");
		assertEquals("incorrect value", "testValue", (String) user
				.getCredentials().get("testKey"));
	}

	public void testUserPutCredentials() throws Exception {
		User user = (User) useradmin.createRole("u1", Role.USER);
		Dictionary p = user.getCredentials();
		p.put("testKey", "testValue");
		p.put("key", new byte[] {0, 0, 0});
		try {
			p.put(new Integer(1), "123");
			fail("expected IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testUserHasCredential() throws Exception {
		User user = (User) useradmin.createRole("u1", Role.USER);
		user.getCredentials().put("password", "1234");
		user.getCredentials().put("pwd2", new byte[] {0, 0, 0});
		assertTrue("missing credential", user.hasCredential("password", "1234"));
		assertFalse("credential found that was not present", user
				.hasCredential("xxx", new Integer(1)));
		assertTrue("missing credential", user.hasCredential("pwd2", new byte[] {
				0, 0, 0}));
	}

	public void testGroupAddMember() throws Exception {
		Group group = (Group) useradmin.createRole("group", Role.GROUP);
		Role user = useradmin.createRole("user1", Role.USER);
		assertTrue("adding member to group failed", group.addMember(user));
		assertFalse("adding existing member to group succeed", group
				.addMember(user));
	}

	public void testGroupAddRequiredMembers() throws Exception {
		Group group = (Group) useradmin.createRole("group", Role.GROUP);
		Role user = useradmin.createRole("user1", Role.USER);
		assertTrue("adding member to group failed", group
				.addRequiredMember(user));
		assertFalse("adding existing member to group succeed", group
				.addRequiredMember(user));
	}

	public void testGroupGetMembers() throws Exception {
		Group group = (Group) useradmin.createRole("group", Role.GROUP);
		String[] result = sortRoleNames(group.getMembers());
		assertEquals("wrong number of roles", 0, result.length);
		Role u1 = useradmin.createRole("user1", Role.USER);
		Role u2 = useradmin.createRole("user2", Role.USER);
		group.addMember(u1);
		group.addMember(u2);
		result = sortRoleNames(group.getMembers());
		assertEquals("wrong number of roles", 2, result.length);
		assertEquals("wrong role name", "user1", result[0]);
		assertEquals("wrong role name", "user2", result[1]);
	}

	public void testGroupGetRequiredMembers() throws Exception {
		Group group = (Group) useradmin.createRole("group", Role.GROUP);
		String[] result = sortRoleNames(group.getRequiredMembers());
		assertEquals("wrong number of roles", 0, result.length);
		Role u1 = useradmin.createRole("user1", Role.USER);
		Role u2 = useradmin.createRole("user2", Role.USER);
		group.addRequiredMember(u1);
		group.addRequiredMember(u2);
		result = sortRoleNames(group.getRequiredMembers());
		assertEquals("wrong number of roles", 2, result.length);
		assertEquals("wrong role name", "user1", result[0]);
		assertEquals("wrong role name", "user2", result[1]);
	}

	public void testGroupRemoveMember() throws Exception {
		Group group = (Group) useradmin.createRole("group", Role.GROUP);
		Role u1 = useradmin.createRole("user1", Role.USER);
		Role u2 = useradmin.createRole("user2", Role.USER);
		group.addMember(u1);
		group.addMember(u2);
		group.removeMember(u2);
		String[] result = sortRoleNames(group.getMembers());
		assertEquals("wrong number of roles", 1, result.length);
		assertEquals("wrong role name", "user1", result[0]);
	}

	public void testUserAnyone() {
		Role user1 = useradmin.createRole("user1", Role.USER);
		Group foorole = (Group) useradmin.createRole("foorole", Role.GROUP);
		foorole.addRequiredMember(user1);
		foorole.addMember(useradmin.getRole("user.anyone"));
		Authorization auth = useradmin.getAuthorization((User) user1);
		assertTrue("role not present", auth.hasRole("foorole"));
		String roles[] = auth.getRoles();
		if (roles != null) {
			for (int i = 0; i < roles.length; i++) {
				if (roles[i].equals("user.anyone")) {
					fail("user.anyone found");
				}
			}
		}
	}

	public void testBasicRequired() {
		setupRoles();
		User user1 = (User) useradmin.getRole("user1");
		User user2 = (User) useradmin.getRole("user2");
		User user3 = (User) useradmin.getRole("user3");
		Group required = (Group) useradmin.getRole("requiredgroup");
		assertNotNull(required);
		Group testrole = (Group) useradmin.getRole("testrole");
		assertNotNull(testrole);
		Authorization auth = useradmin.getAuthorization(user1);
		assertTrue("hasRole testrole", auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user2);
		assertTrue("hasRole testrole", auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user3);
		assertFalse("hasRole testrole", auth.hasRole("testrole"));
	}

	public void testRequiredAuth() {
		setupRoles();
		User user1 = (User) useradmin.getRole("user1");
		User user2 = (User) useradmin.getRole("user2");
		User user3 = (User) useradmin.getRole("user3");
		Group required = (Group) useradmin.getRole("requiredgroup");
		assertNotNull(required);
		Group testrole = (Group) useradmin.getRole("testrole");
		assertNotNull(testrole);
		testrole.removeMember(user1);
		testrole.removeMember(user2);
		testrole.removeMember(user3);
		testrole.removeMember(useradmin.getRole("user.anyone"));
		Authorization auth = useradmin.getAuthorization(user1);
		assertFalse("hasRole testrole", auth.hasRole("testrole"));
	}

	public void testUserAnyoneBasic() {
		setupRoles();
		User user1 = (User) useradmin.getRole("user1");
		User user2 = (User) useradmin.getRole("user2");
		User user3 = (User) useradmin.getRole("user3");
		User user4 = (User) useradmin.getRole("user4");
		Group required = (Group) useradmin.getRole("requiredgroup");
		Group testrole = (Group) useradmin.getRole("testrole");
		testrole.removeMember(user1);
		testrole.removeMember(user2);
		testrole.removeMember(user3);
		Role anyone = useradmin.getRole("user.anyone");
		required.removeMember(useradmin.getRole("user.anyone"));
		required.removeMember(user3);
		testrole.addMember(anyone);
		Authorization auth = useradmin.getAuthorization(user1);
		assertTrue("hasRole testrole", auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user2);
		assertTrue("hasRole testrole", auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user3);
		assertFalse("hasRole testrole", auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user4);
		assertFalse("hasRole testrole", auth.hasRole("testrole"));
	}

	public void testUserAnyoneRequired() {
		setupRoles();
		User user1 = (User) useradmin.getRole("user1");
		User user2 = (User) useradmin.getRole("user2");
		User user3 = (User) useradmin.getRole("user3");
		User user4 = (User) useradmin.getRole("user4");
		Group required = (Group) useradmin.getRole("requiredgroup");
		Group testrole = (Group) useradmin.getRole("testrole");
		required.removeMember(user1);
		required.removeMember(user2);
		required.removeMember(user3);
		required.addMember(useradmin.getRole("user.anyone"));
		testrole.removeMember(user2);
		testrole.removeMember(user3);
		testrole.addMember(user4);
		Authorization auth = useradmin.getAuthorization(user1);
		assertTrue("hasRole testrole", auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user2);
		assertFalse("hasRole testrole", auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user3);
		assertFalse("hasRole testrole", auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user4);
		assertTrue("hasRole testrole", auth.hasRole("testrole"));
	}

	public void testAnonymousUser() {
		setupRoles();
		Authorization auth = useradmin.getAuthorization(null);
		assertNull("anonymous user", auth.getName());
		useradmin.removeRole("anygroup");
		assertFalse("hasRole anygroup", auth.hasRole("anygroup"));
		Group anygroup = (Group) useradmin.createRole("anygroup", Role.GROUP);
		anygroup.addMember(useradmin.getRole("user.anyone"));
		anygroup.addRequiredMember(useradmin.getRole("user.anyone"));
		assertTrue("hasRole anygroup", auth.hasRole("anygroup"));
		assertFalse("hasRole requiredgroup", auth.hasRole("requiredgroup"));
		assertTrue("hasRole user.anyone", auth.hasRole("user.anyone"));
	}

	public void testGroupInGroup() {
		User user1 = (User) useradmin.createRole("user1", Role.USER);
		Group group1 = (Group) useradmin.createRole("group1", Role.GROUP);
		assertTrue(group1.addRequiredMember(group1));
		assertTrue(group1.addMember(user1));
		Authorization auth = useradmin.getAuthorization(user1);
		assertFalse("hasRole group1", auth.hasRole("group1"));
	}

	public void testUserAdminEvents() throws Exception {
		UserAdminEventCollector uaec = new UserAdminEventCollector(
				UserAdminEvent.ROLE_CREATED | UserAdminEvent.ROLE_CHANGED
						| UserAdminEvent.ROLE_REMOVED);
		ServiceRegistration sr = getContext().registerService(
				UserAdminListener.class.getName(), uaec, null);
		try {
			/* Create a new role */
			useradmin.createRole("user1", Role.USER);
			List events = uaec.getList(1, 1000 * OSGiTestCaseProperties
					.getScaling());
			assertEquals("missing event", 1, events.size());
			UserAdminEvent event = (UserAdminEvent) events.get(0);
			assertEquals("wrong event type", UserAdminEvent.ROLE_CREATED, event
					.getType());
			assertEquals("wrong event role", "user1", roleToString(event
					.getRole()));

			/* Change a role */
			Role role = useradmin.getRole("user1");
			role.getProperties().put("newKey", "xxxxx");
			events = uaec
					.getList(1, 1000 * OSGiTestCaseProperties.getScaling());
			assertEquals("missing event", 1, events.size());
			event = (UserAdminEvent) events.get(0);
			assertEquals("wrong event type", UserAdminEvent.ROLE_CHANGED, event
					.getType());
			assertEquals("wrong event role", "user1", roleToString(event
					.getRole()));

			/* Remove a role */
			useradmin.removeRole("user1");
			events = uaec
					.getList(1, 1000 * OSGiTestCaseProperties.getScaling());
			assertEquals("missing event", 1, events.size());
			event = (UserAdminEvent) events.get(0);
			assertEquals("wrong event type", UserAdminEvent.ROLE_REMOVED, event
					.getType());
			assertEquals("wrong event role", "user1", roleToString(event
					.getRole()));
		}
		finally {
			sr.unregister();
		}
	}

	/** *** Helper methods **** */
	private void setupRoles() {
		useradmin.createRole("user1", Role.USER);
		useradmin.createRole("user2", Role.USER);
		useradmin.createRole("user3", Role.USER);
		useradmin.createRole("user4", Role.USER);
		useradmin.createRole("testrole", Role.GROUP);
		useradmin.createRole("requiredgroup", Role.GROUP);
		User user1 = (User) useradmin.getRole("user1");
		User user2 = (User) useradmin.getRole("user2");
		User user3 = (User) useradmin.getRole("user3");
		Group required = (Group) useradmin.getRole("requiredgroup");
		Group testrole = (Group) useradmin.getRole("testrole");
		required.addMember(user1);
		required.addMember(user2);
		required.addMember(user3);
		testrole.addRequiredMember(required);
		testrole.addMember(user1);
		testrole.addMember(user2);
	}

	private String roleToString(Role role) {
		String roleString = null;
		if (role != null) {
			roleString = role.getName();
		}
		return roleString;
	}

	private String[] sortRoleNames(Role[] roles) {
		if (roles == null) {
			return new String[] {};
		}
		String[] roleStrings = new String[roles.length];
		/* Get name of every role */
		for (int i = 0; i < roles.length; i++) {
			roleStrings[i] = roleToString(roles[i]);
		}
		/*
		 * The order of the roles isn't specified, so we better sort them.
		 */
		Arrays.sort(roleStrings);
		return roleStrings;
	}
}
