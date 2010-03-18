package org.osgi.test.cases.jmx.junit;

import java.io.IOException;
import java.util.Arrays;

import javax.management.RuntimeMBeanException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.osgi.framework.BundleContext;
import org.osgi.jmx.service.useradmin.UserAdminMBean;
import org.osgi.service.useradmin.UserAdmin;

public class UserAdminMBeanTestCase extends MBeanGeneralTestCase {
	private UserAdminMBean userManagerMBean;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		super.waitForRegistering(createObjectName(UserAdminMBean.OBJECTNAME));
		userManagerMBean = getMBeanFromServer(UserAdminMBean.OBJECTNAME, UserAdminMBean.class);
	}

	public void testCreateUser() throws IOException {
		int numberOfUsersBefore = 0;
		if(userManagerMBean.listUsers() != null) {
			numberOfUsersBefore = userManagerMBean.listUsers().length;
		}
		
		userManagerMBean.createUser("foo");
		userManagerMBean.createUser("bar");
		userManagerMBean.createUser("knorke");

		assertNotNull(userManagerMBean.getUser("foo"));
		assertNotNull(userManagerMBean.getUser("bar"));
		assertNotNull(userManagerMBean.getUser("knorke"));
		
		assertEquals(numberOfUsersBefore + 3, userManagerMBean.listUsers().length);
	}
	
	public void testCreateGroup() throws IOException {
		String groupName = "board";
		userManagerMBean.createGroup(groupName);
		assertNotNull(userManagerMBean.getGroup(groupName));
	}

	public void testCreateRole() throws IOException {
		try {
			String roleName = "TestRole";
			userManagerMBean.createRole(roleName);
			assertNotNull(userManagerMBean.getRole(roleName));
		} catch(RuntimeException rException) {
			/*
			 * Bug report https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1593
			 */
			rException.printStackTrace();
			assertTrue("exception is thrown " + rException, false);
		}
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
	
	public void testAddAndRemoveGroupMember() throws IOException {
		String roleName = "user.anyone";
		String groupName = "board";
		
		//remove if exists
		for(String rname : (String[])userManagerMBean.getGroup(groupName).get(UserAdminMBean.MEMBERS)) {
			if(rname.equals(roleName)) {
				assertTrue(userManagerMBean.removeMember(groupName, roleName));
			}
		}
		
		//add role
		assertTrue("failed to add role " + roleName + " to group" + groupName + ".", userManagerMBean.addMember(groupName, roleName));
		
		//check that role is added
		boolean found = false;
		for(String rname : (String[])userManagerMBean.getGroup(groupName).get(UserAdminMBean.MEMBERS)) {
			if(rname.equals(roleName)) {
				found = true;
				break;
			}
		}
		assertTrue("role " + roleName + " was not added to group" + groupName + ".", found);
		
		//remove role
		assertTrue("failed to remove user from group", userManagerMBean.removeMember(groupName, roleName));
		
		//check that role is removed
		found = false;
		for(String rname : (String[])userManagerMBean.getGroup(groupName).get(UserAdminMBean.MEMBERS)) {
			if(rname.equals(roleName)) {
				found = true;
				break;
			}
		}
		assertFalse("role " + roleName + " was not remove from group" + groupName + ".", found);
	}
	
	public void testGetGroup() throws IOException {
		String groupname = "Wurstverkäufer";
		userManagerMBean.createGroup(groupname);
		CompositeData group = userManagerMBean.getGroup(groupname);
		assertNotNull("failed to retrieve the previously created group " + groupname + ".", group);
		assertCompositeDataKeys(group, "GROUP_TYPE", new String[] { "Members", "RequiredMembers",
														  "Name"});
		/* 
		 * See bug reported https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1592
		 */		
		
		assertTrue("the Name property for the Group is wrong", groupname.equals(group.get("Name")));
	}
	
	public void testGetAuthorization() throws IOException {
		String username = "foo";
		userManagerMBean.createUser(username);
		CompositeData auth = userManagerMBean.getAuthorization(username);
		assertNotNull(userManagerMBean.getAuthorization(username));
		assertCompositeDataKeys(auth, "AUTORIZATION_TYPE", new String[] { "Name" });
		
		/* 
		 * See bug reported https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1592
		 * The test should be extended after bug is fixed.
		 */
		userManagerMBean.removeRole(username);
	}
	
	public void testListGroups() throws IOException {
		String group1 = "Messdiener";
		String group2 = "Chorknaben";
		userManagerMBean.createGroup(group1);
		userManagerMBean.createGroup(group2);
		String[] groups = userManagerMBean.listGroups();
		assertTrue("failed to get previously created groups.", groups.length >= 2);
		int found = 0;
		for (int i = 0; i < groups.length; i++) {
			if (groups[i].equals(group1) || groups[i].equals(group2)) {
				found++;
			}
		}
		assertTrue("failed to get previously two created groups.", found == 2);		
	}
	
	public void testGetImpliedRoles() throws IOException {
		String username = "Rudi Rüssel";
		userManagerMBean.createUser(username);
		String[] impliedRoles = userManagerMBean.getImpliedRoles(username);
		assertTrue("the list of implied roles was empty", impliedRoles.length > 0);
		boolean found = false;
		for (String uname : userManagerMBean.getImpliedRoles(username)) {
			if (username.equals(uname)) {
				found = true;
				break;
			}
		}
		assertTrue("the list of implied role doesn't contains the created user", found);
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
		
		assertTrue("failed to add member to group", userManagerMBean.addMember(groupname, user1));
		assertTrue("failed to add member to group", userManagerMBean.addMember(groupname, user2));
		assertTrue("failed to add member to group", userManagerMBean.addMember(groupname, user3));
		String[] members  = userManagerMBean.getMembers(groupname);
		assertTrue("Did not get the number of expected members(3) but " + members.length + ".",	members.length == 3);
		
		int found = 0;
		for (int i = 0; i < members.length; i++) {
			if (members[i].equals(user1) || members[i].equals(user2) || members[i].equals(user3)) {
				found++;
			}
		}
		assertTrue("did not found some of the added members ", found == 3);
	}
	
	public void testGetRequiredMembers() throws IOException {
		String groupname = "winners";
		String user1 = "Werner Wurst";
		String user2 = "Klaus Käse";
		userManagerMBean.createGroup(groupname);
		userManagerMBean.createUser(user1);
		userManagerMBean.createUser(user2);

		assertTrue("failed to add required member to group", userManagerMBean.addRequiredMember(groupname, user1));
		assertTrue("failed to add required member to group", userManagerMBean.addRequiredMember(groupname, user2));
		String[] requiredUsers = userManagerMBean.getRequiredMembers(groupname);
		assertTrue("failed to get the right number of required members(2) for group" + groupname + ". expected was: " + requiredUsers.length + " ", requiredUsers.length == 2);
		
		int found = 0;
		for (int i = 0; i < requiredUsers.length; i++) {
			if (requiredUsers[i].equals(user1) || requiredUsers[i].equals(user2)) {
				found++;
			}
		}
		assertTrue("did not found some of the required members ", found == 2);
	}
	
	public void testGetUser() throws IOException {
		String username = "Nudel Nutella";
		userManagerMBean.createUser(username);
		CompositeData user = userManagerMBean.getUser(username);
		assertNotNull("failed to retrieve previously created user", user);
		
		assertCompositeDataKeys(user, "USER_TYPE", new String[] {/* "Credentials", */ "Name", "Type" /*, "Properties" */ });
		
		/*
		 * See reported bug https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1592
		 */
		
		assertTrue("the Name property for the User is wrong", username.equals(user.get("Name")));		
	}

	public void testListUsers() throws IOException {
		int numberOfUsers = userManagerMBean.listUsers().length;
		String user1 = "Hans Meier";
		String user2 = "Werner Schmidt";
		String user3 = "Alois Müller";
		userManagerMBean.createUser(user1);
		userManagerMBean.createUser(user2);
		userManagerMBean.createUser(user3);
		
		String[] allUsers = userManagerMBean.listUsers();
		assertTrue("Could not retrieve the additionally added users", numberOfUsers + 3 == allUsers.length);
		
		int found = 0;
		for (int i = 0; i < allUsers.length; i++) {
			if (allUsers[i].equals(user1) || allUsers[i].equals(user2) || allUsers[i].equals(user3)) {
				found++;
			}
		}
		assertTrue("did not found some of the added users ", found == 3);
	}
	
	public void testRemoveRole() throws IOException {
		userManagerMBean.createUser("Hans Meier");
		userManagerMBean.createUser("Werner Schmidt");
		userManagerMBean.createUser("Alois Müller");
		
		for(String userName : userManagerMBean.listUsers()) {
			assertTrue(userManagerMBean.removeRole(userName));
		}
		assertTrue("failed to remove all users", userManagerMBean.listUsers().length == 0);
	}
	
	public void testAddPropertyString() throws IOException {
		String username = "Frank Fisch";
		String propertyKey = "Geruch";
		String propertyValue = "fischig";
		userManagerMBean.createUser(username);
		userManagerMBean.addPropertyString(propertyKey, propertyValue, username);

		assertTrue("failed to retrieve previously stored property with key " +propertyKey+ " and value " + propertyValue + ".", userManagerMBean.getProperties(username).get(new String[]{propertyKey}).get("Key").equals(propertyKey));
		assertTrue("failed to retrieve previously stored property with key " +propertyKey+ " and value " + propertyValue + ".", userManagerMBean.getProperties(username).get(new String[]{propertyKey}).get("Value").equals(propertyValue));
	}

	public void testAddProperty() throws IOException {
		String username = "Frank Fisch";
		String propertyKey = "Geruch X";
		String propertyValue = "fischig";
		userManagerMBean.createUser(username);
		userManagerMBean.addProperty(propertyKey, propertyValue.getBytes(), username);

		assertTrue("failed to retrieve previously stored property with key " +propertyKey+ " and value " + propertyValue + ".", userManagerMBean.getProperties(username).get(new String[]{propertyKey}).get("Key").equals(propertyKey));
		String value = (String) userManagerMBean.getProperties(username).get(new String[]{propertyKey}).get("Value");
		String type = (String) userManagerMBean.getProperties(username).get(new String[]{propertyKey}).get("Type");		
		byte[] parsedResult = (byte[]) OSGiProperties.parse(value, type);
		assertTrue("failed to retrieve previously stored property with key " +propertyKey+ " and value " + propertyValue + ".", Arrays.equals(parsedResult, propertyValue.getBytes()));
	}
	
	public void testAddAndRemoveCredentialString() throws IOException {
		String username = "foo";
		userManagerMBean.createUser(username);
		userManagerMBean.addCredentialString(UserAdminMBean.CREDENTIALS, "bar",	username);
		assertTrue("failed to retrieve previously added credentials.", userManagerMBean.getCredentials(username).values().size() > 0);
		
		userManagerMBean.removeCredential(UserAdminMBean.CREDENTIALS, username);
		assertTrue("failed to removeCredential for user " + username, userManagerMBean.getCredentials(username).values().size() == 0);
	}

	public void testAddAndRemoveCredential() throws IOException {
		String username = "foo";
		userManagerMBean.createUser(username);
		userManagerMBean.addCredential(UserAdminMBean.CREDENTIALS, "bar".getBytes(),	username);
		assertTrue("failed to retrieve previously added credentials.", userManagerMBean.getCredentials(username).values().size() > 0);
		
		userManagerMBean.removeCredential(UserAdminMBean.CREDENTIALS, username);
		assertTrue("failed to removeCredential for user " + username, userManagerMBean.getCredentials(username).values().size() == 0);
	}

	public void testGetCredentials() throws IOException {
		String username = "Test";
		userManagerMBean.createUser(username);
		userManagerMBean.addCredentialString("test", "bar", username);
		TabularData result = userManagerMBean.getCredentials(username);
		
		assertTabularDataStructure(result, "PROPERTIES_TYPE", "Key", new String[] {"Key", "Value", "Type"});
		
		CompositeData record = result.get(new String[] {"test"});
		assertNotNull(record);
		assertCompositeDataKeys(record, "PROPERTY_TYPE", new String[] {"Key", "Value", "Type"});
		
		String value = (String) record.get("Value");
		assertTrue("value is wrong", "bar".equals(value));
	}

	public void testGetProperties() throws IOException {
		String username = "TestName";
		String propertyKey = "TestKey";
		String propertyValue = "TestValue";
		userManagerMBean.createUser(username);
		userManagerMBean.addPropertyString(propertyKey, propertyValue, username);
		
		TabularData result = userManagerMBean.getProperties(username);
		assertTabularDataStructure(result, "PROPERTIES_TYPE", "Key", new String[] {"Key", "Value", "Type"});
		
		assertTrue("failed to retrieve previously stored property with key " +propertyKey+ " and value " + propertyValue + ".", result.get(new String[]{propertyKey}).get("Key").equals(propertyKey));
		String value = (String) result.get(new String[]{propertyKey}).get("Value");
		assertTrue("failed to retrieve previously stored property with key " +propertyKey+ " and value " + propertyValue + ".", value.equals(propertyValue));
	}	
	
	public void testGetRole() throws IOException {
		String username = "TestName";		
		userManagerMBean.createUser(username);
		CompositeData role = userManagerMBean.getRole(username);
		assertNotNull(role);
		
		/*
		 * Bug reported https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1592
		 */
		assertCompositeDataKeys(role, "ROLE_TYPE", new String[] {"Name", "Type" /* , "Properties" */});		
	}
	
	public void testGetRoleFilter() throws Exception {
		String username = "TestName";		
		userManagerMBean.createUser(username);
		userManagerMBean.addPropertyString("TestPropertyKey", "TestPropertyValue", username);
		String[] roles = userManagerMBean.getRoles("(TestPropertyKey=TestPropertyValue)");
		assertNotNull(roles);
		boolean found = false;
		for (int i = 0; i < roles.length; i++) {
			if (username.equals(roles[i])) {
				found = true;
				break;
			}
		}
		assertTrue("role didn't found by filtering", found);
	}

	public void testGetUserFilter() throws Exception {
		String username = "TestName";		
		userManagerMBean.createUser(username);
		userManagerMBean.addPropertyString("TestPropertyKey", "TestPropertyValue", username);
		String[] users = userManagerMBean.getUsers("(TestPropertyKey=TestPropertyValue)");
		assertNotNull(users);
		boolean found = false;
		for (int i = 0; i < users.length; i++) {
			if (username.equals(users[i])) {
				found = true;
				break;
			}
		}
		assertTrue("user didn't found by filtering", found);
	}

	public void testGetGroupFilter() throws Exception {
		String groupname = "TestGroup";		
		userManagerMBean.createGroup(groupname);
		userManagerMBean.addPropertyString("TestPropertyKey", "TestPropertyValue", groupname);
		String[] groups = userManagerMBean.getGroups("(TestPropertyKey=TestPropertyValue)");
		assertNotNull(groups);
		boolean found = false;
		for (int i = 0; i < groups.length; i++) {
			if (groupname.equals(groups[i])) {
				found = true;
				break;
			}
		}
		assertTrue("group didn't found by filtering", found);
	}
	
	public void testGetUserWithProperty() throws IOException {
		String username = "TestName";
		String propertyKey = "TestKey";
		String propertyValue = "TestValue";
		userManagerMBean.createUser(username);
		userManagerMBean.addPropertyString(propertyKey, propertyValue, username);
		
		String user = userManagerMBean.getUserWithProperty("TestKey", "TestValue");
		assertTrue("wrong user is returned", username.equals(user));
	}
	
	public void testListRoles() throws IOException {
		String group1 = "Test1";
		String group2 = "Test2";
		userManagerMBean.createGroup(group1);
		userManagerMBean.createGroup(group2);
		String[] roles = userManagerMBean.listRoles();
		assertTrue("failed to get previously created groups.", roles.length >= 2);
		int found = 0;
		for (int i = 0; i < roles.length; i++) {
			if (roles[i].equals(group1) || roles[i].equals(group2)) {
				found++;
			}
		}
		assertTrue("failed to get previously two created groups.", found == 2);		
	}
	
	public void testRemoveGroup() throws IOException {
		String group = "TestGroup";
		userManagerMBean.createGroup(group);
		assertNotNull(userManagerMBean.getGroup(group));
		assertTrue(userManagerMBean.removeGroup(group));
	}
	
	public void testRemoveUser() throws IOException {
		String user = "TestUser";
		userManagerMBean.createUser(user);
		assertNotNull(userManagerMBean.getUser(user));
		assertTrue(userManagerMBean.removeUser(user));
	}

	public void testRemoveProperty() throws IOException {
		String username = "TestName";
		String propertyKey = "TestKey";
		String propertyValue = "TestValue";
		userManagerMBean.createUser(username);
		userManagerMBean.addPropertyString(propertyKey, propertyValue, username);
		
		TabularData result = userManagerMBean.getProperties(username);
		assertTabularDataStructure(result, "PROPERTIES_TYPE", "Key", new String[] {"Key", "Value", "Type"});
		
		assertTrue("failed to retrieve previously stored property with key " +propertyKey+ " and value " + propertyValue + ".", result.get(new String[]{propertyKey}).get("Key").equals(propertyKey));
		String value = (String) result.get(new String[]{propertyKey}).get("Value");
		assertTrue("failed to retrieve previously stored property with key " +propertyKey+ " and value " + propertyValue + ".", value.equals(propertyValue));
		
		userManagerMBean.removeProperty(propertyKey, username);
		result = userManagerMBean.getProperties(username);		
		assertTrue("failed to remove previously stored property with key " +propertyKey+ " and value " + propertyValue + ".", result.get(new String[]{propertyKey}) == null);		
	}	
	
	public void testExceptions() {
		/*
		 * Bug report for this method is https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1605
		 */
		assertNotNull(userManagerMBean);
		
		//test listGroups method
		try {
			userManagerMBean.listGroups();			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method listGroups throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		
		//test listRoles method
		try {
			userManagerMBean.listRoles();			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method listRoles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}

		//test listUsers method
		try {
			userManagerMBean.listUsers();			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method listUsers throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}

		//test addCredential method
		try {
			userManagerMBean.addCredential(STRING_NULL, null, STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.addCredential(STRING_EMPTY, new byte[] {}, STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.addCredential(STRING_SPECIAL_SYMBOLS, new byte[] {}, STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}

		//test addCredentialString method
		try {
			userManagerMBean.addCredentialString(STRING_NULL, STRING_NULL, STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.addCredentialString(STRING_EMPTY, STRING_EMPTY, STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}		
		try {
			userManagerMBean.addCredentialString(STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}		
		
		//test addMember method
		try {
			userManagerMBean.addMember(STRING_NULL, STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addMember throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.addMember(STRING_EMPTY, STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addMember throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.addMember(STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addMember throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test addProperty method
		try {
			userManagerMBean.addProperty(STRING_NULL, null, STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addProperty throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.addProperty(STRING_EMPTY, new byte[] {}, STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addProperty throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.addProperty(STRING_SPECIAL_SYMBOLS, new byte[] {}, STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addProperty throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test addPropertyString method		
		try {
			userManagerMBean.addPropertyString(STRING_NULL, STRING_NULL, STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addPropertyString throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.addPropertyString(STRING_EMPTY, STRING_EMPTY, STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addPropertyString throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.addPropertyString(STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addPropertyString throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test addRequiredMember method		
		try {
			userManagerMBean.addRequiredMember(STRING_NULL, STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addRequiredMember throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.addRequiredMember(STRING_EMPTY, STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addRequiredMember throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.addRequiredMember(STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method addRequiredMember throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		
		//test createGroup method
		try {
			userManagerMBean.createGroup(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method createGroup throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.createGroup(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method createGroup throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.createGroup(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method createGroup throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test createRole method
		try {
			userManagerMBean.createRole(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method createRole throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.createRole(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method createRole throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.createRole(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method createRole throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test createUser method
		try {
			userManagerMBean.createUser(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method createUser throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.createUser(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method createUser throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.createUser(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method createUser throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test getAuthorization method
		try {
			userManagerMBean.getAuthorization(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}		
		try {
			userManagerMBean.getAuthorization(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.getAuthorization(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}		
		
		//test getCredentials method		
		try {
			userManagerMBean.getCredentials(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.getCredentials(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.getCredentials(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		
		//test getGroup method
		try {
			userManagerMBean.getGroup(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.getGroup(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}		
		try {
			userManagerMBean.getGroup(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}		
		
		//test getGroups method
		try {
			userManagerMBean.getGroups(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getGroups throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.getGroups(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getGroups throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}	
		try {
			userManagerMBean.getGroups(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getGroups throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}	

		//test getImpliedRoles method
		try {
			userManagerMBean.getImpliedRoles(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.getImpliedRoles(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.getImpliedRoles(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		
		//test getMembers method		
		try {
			userManagerMBean.getMembers(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.getMembers(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.getMembers(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		
		//test getProperties method		
		try {
			userManagerMBean.getProperties(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getProperties throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.getProperties(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getProperties throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.getProperties(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getProperties throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test getRequiredMembers method
		try {
			userManagerMBean.getRequiredMembers(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.getRequiredMembers(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}		
		try {
			userManagerMBean.getRequiredMembers(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}		
		
		//test getRole method
		try {
			userManagerMBean.getRole(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getRole throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.getRole(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getRole throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.getRole(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getRole throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test getRoles method
		try {
			userManagerMBean.getRoles(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getRoles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.getRoles(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getRoles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.getRoles(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getRoles throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		

		//test getUser method
		try {
			userManagerMBean.getUser(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}	
		try {
			userManagerMBean.getUser(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}	
		try {
			userManagerMBean.getUser(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}	
		
		//test getUserWithProperty method
		try {
			userManagerMBean.getUserWithProperty(STRING_NULL, STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getUserWithProperty throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.getUserWithProperty(STRING_EMPTY, STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getUserWithProperty throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.getUserWithProperty(STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getUserWithProperty throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		

		//test getUsers method		
		try {
			userManagerMBean.getUsers(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getUsers throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.getUsers(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getUsers throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.getUsers(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method getUsers throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		

		//test removeCredential method		
		try {
			userManagerMBean.removeCredential(STRING_NULL, STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}		
		try {
			userManagerMBean.removeCredential(STRING_EMPTY, STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}		
		try {
			userManagerMBean.removeCredential(STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}		
		
		//test removeGroup method
		try {
			userManagerMBean.removeGroup(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeGroup throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.removeGroup(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeGroup throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.removeGroup(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeGroup throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test removeMember method
		try {
			userManagerMBean.removeMember(STRING_NULL, STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}
		try {
			userManagerMBean.removeMember(STRING_EMPTY, STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}			
		try {
			userManagerMBean.removeMember(STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeMBeanException e) {
			//spec describes this method could throw IllegalArgumentException; let's check
			assertRootCauseIllegalArgumentException(e);
		}			
		
		//test removeProperty method
		try {
			userManagerMBean.removeProperty(STRING_NULL, STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeProperty throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.removeProperty(STRING_EMPTY, STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeProperty throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.removeProperty(STRING_SPECIAL_SYMBOLS, STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeProperty throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test removeRole method
		try {
			userManagerMBean.removeRole(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeRole throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.removeRole(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeRole throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.removeRole(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeRole throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		
		//test removeUser method
		try {
			userManagerMBean.removeUser(STRING_NULL);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeUser throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}
		try {
			userManagerMBean.removeUser(STRING_EMPTY);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeUser throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}		
		try {
			userManagerMBean.removeUser(STRING_SPECIAL_SYMBOLS);			
		} catch(IOException ioException) {
		} catch(RuntimeException e) {
			e.printStackTrace();
			assertTrue("method removeUser throws runtime exception, but only IOException is allowed; runtime exception is " + e.toString(), false);
		}			
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		super.waitForUnRegistering(createObjectName(UserAdminMBean.OBJECTNAME));
	}	
}
