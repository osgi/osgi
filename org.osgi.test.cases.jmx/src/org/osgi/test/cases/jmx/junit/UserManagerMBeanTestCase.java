package org.osgi.test.cases.jmx.junit;

import java.io.IOException;

import org.osgi.jmx.JmxConstants;
import org.osgi.jmx.service.useradmin.UserManagerMBean;

public class UserManagerMBeanTestCase extends MBeanGeneralTestCase {
	private UserManagerMBean userManagerMBean;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		super.waitForRegistering(createObjectName(JmxConstants.UA_SERVICE));
		userManagerMBean = getMBeanFromServer(JmxConstants.UA_SERVICE, UserManagerMBean.class);
	}

	public void testCreateUser() throws IOException {
		
		int numberOfUsersBefore = 0;
		if(userManagerMBean.getUsers() != null) {
			numberOfUsersBefore = userManagerMBean.getUsers().length;
		}
		
		userManagerMBean.createUser("foo");
		userManagerMBean.createUser("bar");
		userManagerMBean.createUser("knorke");
		assertNotNull(userManagerMBean.getUser("foo"));
		assertNotNull(userManagerMBean.getUser("bar"));
		assertNotNull(userManagerMBean.getUser("knorke"));
		
		assertEquals(numberOfUsersBefore + 3, userManagerMBean.getUsers().length);
	}
	
	public void testCreateGroup() throws IOException {
		String groupName = "board";
		userManagerMBean.createGroup(groupName);
		assertNotNull(userManagerMBean.getGroup(groupName));
	}
	
	public void testAddRequiredMember() throws IOException {
		String groupName = "board";
		String userName = "orgel";
		
		userManagerMBean.createGroup(groupName);
		userManagerMBean.createUser(userName);
		
		assertTrue(userManagerMBean.addRequiredMember(groupName, userName));
		
		boolean foundUserName = false;
		for(String returnedUserName : userManagerMBean.getRequiredMembers(groupName)) {
			if(returnedUserName.equals(userName))
				foundUserName = true;
		}
		assertTrue("Did not find the required member of group " + groupName + ".", foundUserName);
	}
	


	public void testAddAndRemoveCredential() throws IOException {
		String username = "foo";
		String illegalUsername = "gnobbelbra";
		/*
		 * FIXME: https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1407
		 */
		
		try {
			userManagerMBean.getCredentials(illegalUsername);
			fail("Failed to throw IllegalArgumentException because of non existent user "+ illegalUsername +".");
		}
		catch(IOException ioex) {
			//just catch and forget the provoced exception
		}
		catch(IllegalArgumentException ia) {
			//just catch and forget the provoced exception
		}
		userManagerMBean.createUser(username);
		userManagerMBean.addCredential(UserManagerMBean.ENCODED_CREDENTIALS, "bar", username);
		assertTrue("failed to retrieve previously added credentials.", userManagerMBean.getCredentials(username).values().size() > 0);
		
		userManagerMBean.removeCredential(UserManagerMBean.ENCODED_CREDENTIALS, username);
		assertTrue("failed to removeCredential for user " + username, userManagerMBean.getCredentials(username).values().size() == 0);
	}

	public void testAddAndRemoveGroupMember() throws IOException {
		String roleName = "user.anyone";
		String groupName = "board";
		
		for(String rname : (String[])userManagerMBean.getGroup(groupName).get(UserManagerMBean.GROUP_MEMBERS)) {
			if(rname.equals(roleName)) {
				assertTrue(userManagerMBean.removeMember(groupName, roleName));
			}
		}
		assertTrue("failed to add role " + roleName + " to group" + groupName + ".", userManagerMBean.addMember(groupName, roleName));
		assertTrue("failed to remove user from group", userManagerMBean.removeMember(groupName, roleName));
	}

	public void testAddProperty() throws IOException {
		String username = "Frank Fisch";
		String propertyKey = "Geruch";
		String propertyValue = "fischig";
		userManagerMBean.createUser(username);
		userManagerMBean.addProperty(propertyKey, propertyValue, username);

		assertTrue("failed to retrieve previously stored property with key " +propertyKey+ " and value " + propertyValue + ".", userManagerMBean.getProperties(username).get(new String[]{propertyKey}).get("Key").equals(propertyKey));
		assertTrue("failed to retrieve previously stored property with key " +propertyKey+ " and value " + propertyValue + ".", userManagerMBean.getProperties(username).get(new String[]{propertyKey}).get("Value").equals(propertyValue));
	}

	public void testGetAuthorization() throws IOException {
		String username = "foo";
		userManagerMBean.createUser(username);
		assertNotNull(userManagerMBean.getAuthorization(username));
		userManagerMBean.removeRole(username);
	}
	
	public void testGetGroup() throws IOException {
		String groupname = "Wurstverkäufer";
		userManagerMBean.createGroup(groupname);
		assertNotNull("failed to retrieve the previously created group " + groupname + ".", userManagerMBean.getGroup(groupname));
	}

	public void testGetGroups() throws IOException {
		String group1 = "Messdiener";
		String group2 = "Chorknaben";
		userManagerMBean.createGroup(group1);
		userManagerMBean.createGroup(group2);
		assertTrue("failed to get previously created groups.", userManagerMBean.getGroups().length >= 2);
	}

	public void testGetImpliedRoles() throws IOException {
		String username = "Rudi Rüssel";
		userManagerMBean.createUser(username);
		
		assertTrue("the list of implied roles was empty", userManagerMBean
				.getImpliedRoles(username).length > 0);
		for (String uname : userManagerMBean.getImpliedRoles(username)) {
			System.out.println(uname);
		}
	}

	public void testGetMembers() throws IOException {
		String groupname = "loosers";
		String user1 = "Werner Wurst";
		String user2 = "Klaus Käse";
		String user3 = "Bernd Brot";
		userManagerMBean.createGroup(groupname);
		userManagerMBean.createUser(user1);
		userManagerMBean.createUser(user2);
		userManagerMBean.createUser(user3);
		
		assertTrue("failed to add member to group", userManagerMBean.addMember(
				groupname, user1));
		assertTrue("failed to add member to group", userManagerMBean.addMember(
				groupname, user2));
		assertTrue("failed to add member to group", userManagerMBean.addMember(
				groupname, user3));
		assertTrue("Did not get the number of expected members(3) but "
				+ userManagerMBean.getMembers(groupname).length + ".",
				userManagerMBean.getMembers(groupname).length == 3);
	}

	public void testGetRequiredMembers() throws IOException {
		String groupname = "winners";
		String user1 = "Werner Wurst";
		String user2 = "Klaus Käse";
		userManagerMBean.createGroup(groupname);
		userManagerMBean.createUser(user1);
		userManagerMBean.createUser(user2);

		assertTrue("failed to add required member to group", userManagerMBean
				.addRequiredMember(groupname, user1));
		assertTrue("failed to add required member to group", userManagerMBean
				.addRequiredMember(groupname, user2));
		int numberOfRequiredUsers = userManagerMBean.getRequiredMembers(groupname).length;
		assertTrue(
				"failed to get the right number of required members(2) for group"
						+ groupname + ". expected was: "
						+ numberOfRequiredUsers + " ",
				numberOfRequiredUsers == 2); 
		
	}
	
	public void testGetUser() throws IOException {
		String username = "Nudel Nutella";
		userManagerMBean.createUser(username);
		assertNotNull("failed to retrieve previously created user", userManagerMBean.getUser(username));
	}

	public void testGetUsers() throws IOException {
		int nuberOfUsers = userManagerMBean.getUsers().length;
		userManagerMBean.createUser("Hans Meier");
		userManagerMBean.createUser("Werner Schmidt");
		userManagerMBean.createUser("Alois Müller");
		assertTrue("Could not retrieve the additionally added users",
				nuberOfUsers + 3 == userManagerMBean.getUsers().length);
	}
	
	public void testRemoveRole() throws IOException {

		userManagerMBean.createUser("Hans Meier");
		userManagerMBean.createUser("Werner Schmidt");
		userManagerMBean.createUser("Alois Müller");
		
		for(String userName : userManagerMBean.getUsers()) {
			userManagerMBean.removeRole(userName);
		}
		assertTrue("failed to remove all users", userManagerMBean.getUsers().length == 0);
	}

	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(JmxConstants.UA_SERVICE));
	}
}
