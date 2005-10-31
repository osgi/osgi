/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 */
package org.osgi.test.cases.useradmin.tbc;

import java.io.IOException;
import java.util.*;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.useradmin.*;
import org.osgi.test.cases.util.*;

public class UserAdminControl extends DefaultTestBundleControl {
	static String[]	methods	= new String[] {"testCreateRole", "testRemoveRole",
			"testGetRole", "testGetRoles", "testGetUser", "testRoleGetType",
			"testRoleChangeProperties", "testRolePutProperties",
			"testRoleGetName", "testUserChangeCredentials",
			"testUserPutCredentials", "testUserHasCredential",
			"testGroupAddMember", "testGroupAddRequiredMembers",
			"testGroupGetMembers", "testGroupGetRequiredMembers",
			"testGroupRemoveMember", "testUserAnyone", "testBasicRequired",
			"testRequiredAuth", "testUserAnyoneBasic",
			"testUserAnyoneRequired", "testAnonymousUser", "testGroupInGroup",
			"testUserAdminEvents"};
	UserAdmin		useradmin;
	Vector rolesToKeep = new Vector();

	public String[] getMethods() {
		return methods;
	}

	public void prepare() throws Exception {
		useradmin = (UserAdmin) getService(UserAdmin.class);
		Role[] roles = useradmin.getRoles(null);
    if (roles != null) {
  		for (int i = 0; i < roles.length; i++) {
  		  rolesToKeep.addElement(roles[i].getName());
  		}
    }
	}

	public void setState() throws Exception {
		clearAllRoles();
	}

	public void testCreateRole() throws Exception {
		Role role = null;
		role = useradmin.createRole("foo-user", Role.USER);
		log("Created", roleToString(role));
		role = useradmin.createRole("foo-user", Role.USER);
		log("Created", roleToString(role));
		role = useradmin.createRole("foo-group", Role.GROUP);
		log("Created", roleToString(role));
		try {
			role = useradmin.createRole("foobar", Role.ROLE);
			log("Created", roleToString(role));
		}
		catch (IllegalArgumentException e) {
			log("Created", IllegalArgumentException.class, e);
		}
	}

	public void testRemoveRole() throws Exception {
		useradmin.createRole("foo-user", Role.USER);
		/* Remove the role once */
		log("Removed", "" + useradmin.removeRole("foo-user"));
		/* And try to remove it again */
		log("Removed", "" + useradmin.removeRole("foo-user"));
	}

	public void testGetRole() throws Exception {
		useradmin.createRole("foo1", Role.USER);
		useradmin.createRole("foo2", Role.USER);
		useradmin.createRole("foo3", Role.GROUP);
		useradmin.createRole("foo4", Role.USER);
		log("getRole", roleToString(useradmin.getRole("foo1")));
		log("getRole", roleToString(useradmin.getRole("foo2")));
		log("getRole", roleToString(useradmin.getRole("foo3")));
		log("getRole", roleToString(useradmin.getRole("foo4")));
		useradmin.removeRole("foo1");
		log("getRole", roleToString(useradmin.getRole("foo1")));
		log("getRole", roleToString(useradmin.getRole("user.anyone")));
	}

	public void testGetRoles() throws Exception {
		Role r1 = useradmin.createRole("role1", Role.GROUP);
		r1.getProperties().put("testgetrolesKey", "r1");
		Role r2 = useradmin.createRole("role2", Role.USER);
		r2.getProperties().put("testgetrolesKey", "r3");
		Role r3 = useradmin.createRole("role3", Role.GROUP);
		r3.getProperties().put("testgetrolesKey", "r3");
		logRoles("Roles", useradmin.getRoles("(testgetrolesKey=r*)"));
	}

	public void testGetUser() throws Exception {
		Role r1 = useradmin.createRole("user1", Role.USER);
		r1.getProperties().put("testGetUser", "u1");
		r1.getProperties().put("foobar", "xxx");
		Role r2 = useradmin.createRole("user2", Role.USER);
		r2.getProperties().put("testGetUser", "u3");
		Role r3 = useradmin.createRole("user3", Role.USER);
		r3.getProperties().put("testGetUser", "u3");
		r3.getProperties().put("foobar", "xxx");
		log("getUser", roleToString(useradmin.getUser("unexisting-key",
				"foobar")));
		log("getUser", roleToString(useradmin.getUser("testGetUser", "u1")));
		log("getUser", roleToString(useradmin.getUser("foobar", "xxx")));
	}

	public void testRoleGetType() throws Exception {
		log("Role.USER", "" + Role.USER);
		log("Role.GROUP", "" + Role.GROUP);
		log("Role.ROLE", "" + Role.ROLE);
		Role role = useradmin.createRole("r1", Role.USER);
		log("getType", "" + role.getType());
		role = useradmin.createRole("r2", Role.GROUP);
		log("getType", "" + role.getType());
	}

	public void testRoleChangeProperties() throws Exception {
		Role role = useradmin.createRole("r1", Role.USER);
		Dictionary p = role.getProperties();
		p.put("testKey", "testValue");
		log("changed property", (String) role.getProperties().get("testKey"));
	}

	public void testRolePutProperties() throws Exception {
		Role role = useradmin.createRole("r1", Role.USER);
		Dictionary p = role.getProperties();
		Object[][] keysAndValues = { {"testKey", "testValue"},
				{new Integer(1), "123"}, {"key", new byte[] {0, 0, 0}}};
		logPuts("putProperties", p, keysAndValues);
	}

	public void testRoleGetName() throws Exception {
		log("getName", roleToString(useradmin.createRole("r1", Role.GROUP)));
	}

	public void testUserChangeCredentials() throws Exception {
		User user = (User) useradmin.createRole("u1", Role.USER);
		Dictionary cred = user.getCredentials();
		cred.put("testKey", "testValue");
		log("getCredentials", (String) user.getCredentials().get("testKey"));
	}

	public void testUserPutCredentials() throws Exception {
		User user = (User) useradmin.createRole("u1", Role.USER);
		Dictionary p = user.getCredentials();
		Object[][] keysAndValues = { {"testKey", "testValue"},
				{new Integer(1), "123"}, {"key", new byte[] {0, 0, 0}}};
		logPuts("putCredentials", p, keysAndValues);
	}

	public void testUserHasCredential() throws Exception {
		User user = (User) useradmin.createRole("u1", Role.USER);
		user.getCredentials().put("password", "1234");
		user.getCredentials().put("pwd2", new byte[] {0, 0, 0});
		log("hasCredential", "" + user.hasCredential("password", "1234"));
		log("hasCredential", "" + user.hasCredential("xxx", new Integer(1)));
		log("hasCredential", ""
				+ user.hasCredential("pwd2", new byte[] {0, 0, 0}));
	}

	public void testGroupAddMember() throws Exception {
		Group group = (Group) useradmin.createRole("group", Role.GROUP);
		Role user = useradmin.createRole("user1", Role.USER);
		log("adding member", "" + group.addMember(user));
		log("adding existing member", "" + group.addMember(user));
	}

	public void testGroupAddRequiredMembers() throws Exception {
		Group group = (Group) useradmin.createRole("group", Role.GROUP);
		Role user = useradmin.createRole("user1", Role.USER);
		log("adding member", "" + group.addRequiredMember(user));
		log("adding existing member", "" + group.addRequiredMember(user));
	}

	public void testGroupGetMembers() throws Exception {
		Group group = (Group) useradmin.createRole("group", Role.GROUP);
		logRoles("getMembers", group.getMembers());
		Role u1 = useradmin.createRole("user1", Role.USER);
		Role u2 = useradmin.createRole("user2", Role.USER);
		group.addMember(u1);
		group.addMember(u2);
		logRoles("getMembers", group.getMembers());
	}

	public void testGroupGetRequiredMembers() throws Exception {
		Group group = (Group) useradmin.createRole("group", Role.GROUP);
		logRoles("getRequiredMembers", group.getRequiredMembers());
		Role u1 = useradmin.createRole("user1", Role.USER);
		Role u2 = useradmin.createRole("user2", Role.USER);
		group.addRequiredMember(u1);
		group.addRequiredMember(u2);
		logRoles("getRequiredMembers", group.getRequiredMembers());
	}

	public void testGroupRemoveMember() throws Exception {
		Group group = (Group) useradmin.createRole("group", Role.GROUP);
		Role u1 = useradmin.createRole("user1", Role.USER);
		Role u2 = useradmin.createRole("user2", Role.USER);
		group.addMember(u1);
		group.addMember(u2);
		group.removeMember(u2);
		logRoles("members after remove", group.getMembers());
	}

	public void testUserAnyone() {
		Role user1 = useradmin.createRole("user1", Role.USER);
		Group foorole = (Group) useradmin.createRole("foorole", Role.GROUP);
		foorole.addRequiredMember(user1);
		foorole.addMember(useradmin.getRole("user.anyone"));
		Authorization auth = useradmin.getAuthorization((User) user1);
		log("hasRole foorole", "" + auth.hasRole("foorole"));
		String roles[] = auth.getRoles();
		boolean found = false;
		if (roles != null) {
			for (int i = 0; i < roles.length; i++) {
				if (roles[i].equals("user.anyone")) {
					found = true;
				}
			}
		}
		log("user.anyone found", found ? "true" : "false");
	}

	public void testBasicRequired() {
		setupRoles();
		User user1 = (User) useradmin.getRole("user1");
		User user2 = (User) useradmin.getRole("user2");
		User user3 = (User) useradmin.getRole("user3");
		Group required = (Group) useradmin.getRole("requiredgroup");
		Group testrole = (Group) useradmin.getRole("testrole");
		Authorization auth = useradmin.getAuthorization(user1);
		log("hasRole testrole", "" + auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user2);
		log("hasRole testrole", "" + auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user3);
		log("hasRole testrole", "" + auth.hasRole("testrole"));
	}

	public void testRequiredAuth() {
		setupRoles();
		User user1 = (User) useradmin.getRole("user1");
		User user2 = (User) useradmin.getRole("user2");
		User user3 = (User) useradmin.getRole("user3");
		Group required = (Group) useradmin.getRole("requiredgroup");
		Group testrole = (Group) useradmin.getRole("testrole");
		testrole.removeMember(user1);
		testrole.removeMember(user2);
		testrole.removeMember(user3);
		testrole.removeMember(useradmin.getRole("user.anyone"));
		Authorization auth = useradmin.getAuthorization(user1);
		log("hasRole testrole", "" + auth.hasRole("testrole"));
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
		log("hasRole testrole", "" + auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user2);
		log("hasRole testrole", "" + auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user3);
		log("hasRole testrole", "" + auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user4);
		log("hasRole testrole", "" + auth.hasRole("testrole"));
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
		log("hasRole testrole", "" + auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user2);
		log("hasRole testrole", "" + auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user3);
		log("hasRole testrole", "" + auth.hasRole("testrole"));
		auth = useradmin.getAuthorization(user4);
		log("hasRole testrole", "" + auth.hasRole("testrole"));
	}

	public void testAnonymousUser() {
		setupRoles();
		Authorization auth = useradmin.getAuthorization(null);
		log("anonymous user", auth.getName());
		useradmin.removeRole("anygroup");
		log("hasRole anygroup", "" + auth.hasRole("anygroup"));
		Group anygroup = (Group) useradmin.createRole("anygroup", Role.GROUP);
		anygroup.addMember(useradmin.getRole("user.anyone"));
		anygroup.addRequiredMember(useradmin.getRole("user.anyone"));
		log("hasRole anygroup", "" + auth.hasRole("anygroup"));
		log("hasRole requiredgroup", "" + auth.hasRole("requiredgroup"));
		log("hasRole user.anyone", "" + auth.hasRole("user.anyone"));
	}

	public void testGroupInGroup() {
		User user1 = (User) useradmin.createRole("user1", Role.USER);
		Group group1 = (Group) useradmin.createRole("group1", Role.GROUP);
		group1.addRequiredMember(group1);
		group1.addMember(user1);
		Authorization auth = useradmin.getAuthorization(user1);
		log("hasRole group1", "" + auth.hasRole("group1"));
	}

	public void testUserAdminEvents() {
		Semaphore semaphore = new Semaphore();
		RoleListener roleListener = new RoleListener(this, semaphore,
				UserAdminEvent.ROLE_REMOVED);
		ServiceRegistration sr = null;
		sr = getContext().registerService(UserAdminListener.class.getName(),
				roleListener, null);
		/* Create a new role */
		useradmin.createRole("user1", Role.USER);
		log("waiting", "createRoleEvent");
		/* Change a role */
		Role role = useradmin.getRole("user1");
		role.getProperties().put("newKey", "xxxxx");
		/* Remove a role */
		useradmin.removeRole("user1");
		/* Wait until the RoleListener got all events */
		semaphore.waitForSignal();
		sr.unregister();
	}

	/** *** Helper methods **** */
	void setupRoles() {
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
		required.addMember(useradmin.getRole("user1"));
		required.addMember(useradmin.getRole("user2"));
		required.addMember(useradmin.getRole("user3"));
		testrole.addRequiredMember(required);
		testrole.addMember(user1);
		testrole.addMember(user2);
	}

	String roleToString(Role role) {
		String roleString = null;
		if (role != null) {
			roleString = role.getName();
		}
		return roleString;
	}

	public void logRoles(String testText, Role[] roles) throws IOException {
		StringBuffer logString = new StringBuffer();
		if (roles != null) {
			String[] roleStrings = new String[roles.length];
			/* Get name of every role */
			for (int i = 0; i < roles.length; i++) {
				roleStrings[i] = roleToString(roles[i]);
			}
			/*
			 * The order of the roles isn't specified, so we better sort them.
			 */
			Arrays.sort(roleStrings);
			/* Build the string */
			logString.append(roles.length + " roles: ");
			logString.append(arrayToString(roleStrings));
		}
		else {
			logString.append("No roles");
		}
		log(testText, logString.toString());
	}

	void logPuts(String testText, Dictionary dictionary,
			Object[][] keysAndValues) {
		for (int i = 0; i < keysAndValues.length; i++) {
			try {
				dictionary.put(keysAndValues[i][0], keysAndValues[i][1]);
				log(testText, "ok");
			}
			catch (IllegalArgumentException e) {
				log(testText, IllegalArgumentException.class, e);
			}
		}
	}

	void clearAllRoles() throws Exception {
//		System.out.println("Clearing all roles");
		Role[] roles = useradmin.getRoles(null);
		for (int i = 0; i < roles.length; i++) {
      if (!rolesToKeep.contains(roles[i].getName())) {
        useradmin.removeRole(roles[i].getName());
      } else {
//        System.out.println("Role removal skipped : "+roles[i].getName());
      }
		}
    
//		/* Chech that all roles (except user.anyone) is cleared */
//		roles = useradmin.getRoles(null);
//		if (roles == null) {
//			throw new RuntimeException("Default roles are not present");
//		}
//		else
//			if (roles.length != 1) {
//				logRoles("Can not remove these roles", roles);
//				throw new RuntimeException(
//						"Can not remove all roles needed for testing");
//			}
//			else
//				if (!roles[0].getName().equals("user.anyone")) {
//					logRoles("Incorrect roles", roles);
//					throw new RuntimeException(
//							"Can not remove all needed for testing");
//				}
	}

	String eventTypeToString(int type) {
		String typeString = null;
		switch (type) {
			case 1 :
				typeString = "ROLE_CREATED";
				break;
			case 2 :
				typeString = "ROLE_CHANGED";
				break;
			case 4 :
				typeString = "ROLE_REMOVED";
				break;
			default :
				typeString = "UNKNOWN";
				break;
		}
		return typeString;
	}
}
