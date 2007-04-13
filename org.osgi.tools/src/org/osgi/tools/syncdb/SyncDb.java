/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2007). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.tools.syncdb;

import java.io.*;
import java.security.*;
import java.util.*;
import java.util.regex.*;

import javax.naming.*;
import javax.naming.directory.*;

public class SyncDb implements Runnable {
	DirContext	ctx;
	String		providerUrl;
	String		password;
	String		principal;
	String		file;
	String		merge = null;
	PrintStream	report	= System.out;
	String 		base = "ou=People";

	/**
	 * Initialize the DirContext to talk to the LDAP server.
	 * 
	 * @throws Exception
	 */
	void init() throws Exception {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, providerUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put(Context.SECURITY_PRINCIPAL, principal);
		ctx = new InitialDirContext(env);
	}

	/**
	 * Retrieve a list of all LDAP user, which is the the set of OSGiPerson
	 * object that are below ou=People.
	 * 
	 * @return List of Map objects containing the LDAP users
	 * 
	 * @throws Exception
	 */
	List getLdapUsers() throws Exception {
		List ldapRecords = Collections.list(ctx.search(base,
				"(objectclass=OSGiPerson)", null));
		List result = new ArrayList();

		for (Iterator i = ldapRecords.iterator(); i.hasNext();) {
			SearchResult ldapRecord = (SearchResult) i.next();
			Attributes attributes = ldapRecord.getAttributes();
			List attrs = Collections.list(attributes.getAll());
			Map record = extractAttributes(attrs);
			result.add(record);
		}
		return result;
	}

	/**
	 * Convert the JNDI attributes to somethingthat is easier to use in Java: a
	 * Map
	 * 
	 * @param attrs A list with Attribute
	 * @return a Map with String -> Object
	 * @throws NamingException
	 */
	private Map extractAttributes(List attrs) throws NamingException {
		Map map = new HashMap();
		for (Iterator a = attrs.iterator(); a.hasNext();) {
			Attribute attr = (Attribute) a.next();
			List l = Collections.list(attr.getAll());
			if (attr.getID().equalsIgnoreCase("isMemberOf")) {
				if (l.contains("admin") || l.contains("test")) {
					System.out.println("Skipping admins: " + l);
					continue;
				}
				map.put("isMemberOf", l);
			}
			else
				if (attr.getID().equalsIgnoreCase("userPassword")) {
					String pw = new String((byte[]) l.get(0));
					map.put(attr.getID(), pw);
				}
				else
					if (attr.getID().equalsIgnoreCase("cn"))
						map
								.put(attr.getID(), ((String) l.get(0))
										.toLowerCase());
					else
						map.put(attr.getID(), l.get(0));
		}
		return map;
	}

	/**
	 * Parse the text file and create a list of incomding information
	 * 
	 * @param fileName The file to read
	 * @return A List of Map
	 * @throws Exception
	 */
	Map getIncomingUsers(String fileName, Map existing ) throws Exception {
		File f = new File(fileName);
		Map result = new HashMap();

		FileReader fr = new FileReader(f);
		BufferedReader rdr = new BufferedReader(fr);
		String line = rdr.readLine();
		while (line != null) {
			String[] parts = line.split("\\s+");
			if (parts.length >= 2 && !line.trim().startsWith("#")) {
				String cn = parts[0].toLowerCase();
				String password = parts[1];
				Map record = (Map) existing.get(cn); 
				if ( record == null ) {
					record = new HashMap();
					record.put("mail", cn);
					record.put("cn", cn);
					if ( !"x".equals(password))
						record.put("userPassword", "{SHA}" + sha(password));
					String o = organization(cn);
					if (o != null)
						record.put("o", o);
				}
				
				Set isMemberOf = (Set) record.get("isMemberOf");
				if ( isMemberOf == null )
					isMemberOf = new HashSet();
				
				isMemberOf.add("member");
				record.put("isMemberOf", isMemberOf);

				if (parts.length > 2) {
					for (int i = 2; i < parts.length; i++) {
						Matcher m = Pattern.compile("(\\w+)=(\\w+)").matcher(
								parts[i]);
						if (m.matches()) {
							String key = m.group(1);
							String value = m.group(2);
							record.put(key, value);
						}
						else {
							if (parts[i].trim().length() > 0)
								isMemberOf.add(parts[i]);
						}
					}
				}
				result.put(cn,record);
			}
			line = rdr.readLine();
		}
		return result;
	}

	String sha(String password) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA");
		md.update(password.getBytes("UTF-8"));
		return Base64.toBase64(md.digest());
	}

	/**
	 * Read the update text file containing new member information and read the
	 * database. Compare all the records. Create new records, update existing
	 * records that are changed, and delete non longer existing records.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		List created = new ArrayList();
		List deleted = new ArrayList();
		List updated = new ArrayList();
		List untouched = new ArrayList();
		sync(created, updated, untouched, deleted);
	}

	public void sync(List created, List updated, List untouched, List deleted) {
		try {
			init();
			Map incomingRecords = getIncomingUsers(file, new HashMap());
			if ( merge != null )
				incomingRecords = getIncomingUsers(merge, incomingRecords);
			List currentRecords = getLdapUsers();
			Map currentRecordsByCn = new HashMap();

			for (Iterator i = currentRecords.iterator(); i.hasNext();) {
				Map it = (Map) i.next();
				currentRecordsByCn.put(it.get("cn"), it);
			}

			//
			// check the incoming file against the existing
			// records
			//
			for (Iterator i = incomingRecords.values().iterator(); i.hasNext();) {
				Map incomingRecord = (Map) i.next();
				String cn = (String) incomingRecord.get("cn");
				if (!currentRecordsByCn.containsKey(cn)) {
					// There is no existing user, so we
					// must create it
					create(incomingRecord);
					created.add(cn);
				}
				else {
					//
					// Incoming already exists, check if we need to
					// update, only when the incoming information has
					// different values from the current information
					//
					boolean processed = false;
					Map currentRecord = (Map) currentRecordsByCn.get(cn);
					for (Iterator io = incomingRecord.entrySet().iterator(); io
							.hasNext();) {
						Map.Entry incomingAttribute = (Map.Entry) io.next();
						Object incomingValue = incomingAttribute.getValue();
						Object incomingKey = incomingAttribute.getKey();
						Object currentValue = currentRecord.get(incomingKey);
						if (!equals(incomingValue, currentValue)) {
							report.print("Due to: " + incomingKey + " " + incomingValue + " != " + currentValue );
							// Mismatch, so we must update the
							// database with the updated record because
							// we seem to have different info now.
							Map mergedRecord = merge(currentRecord,
									incomingRecord);
							update(mergedRecord);
							updated.add(cn);
							processed = true;
							break;
						}
					}
					if (!processed)
						untouched.add(cn);
					// We remove it so that the end only the
					// to be deleted records remain
					currentRecordsByCn.remove(cn);
				}
			}

			//
			// Any remaining records are not in the incoming stream
			// so we should delete them. However, if it is an admin or
			// a test user, we do not delete it.
			//
			for (Iterator i = currentRecordsByCn.values().iterator(); i
					.hasNext();) {
				Map currentRecord = (Map) i.next();
				remove(currentRecord);
				deleted.add(currentRecord.get("cn"));
			}
			ctx.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	void create(Map map) throws Exception {
		String dn = getDn(map);
		System.out.println("Create " + dn);
		ctx.bind(getDn(map), null, getAttributes(map));
	}

	void update(Map map) throws Exception {
		String dn = getDn(map);
		System.out.println("Update " + dn);
		map.remove("objectClass");
		ctx.rebind(dn, null, getAttributes(map));
	}

	void remove(Map map) throws Exception {
		String dn = getDn(map);
		System.out.println("Delete " + dn);
		ctx.unbind(getDn(map));
	}

	Attributes getAttributes(Map map) throws Exception {
		Attributes attributes = new BasicAttributes();
		attributes.put("objectclass", "OSGiPerson");
		attributes.put("cn", map.get("mail"));
		attributes.put("sn", map.get("mail"));
		for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
			Map.Entry it = (Map.Entry) i.next();

			if (it.getValue() instanceof Collection) {
				Set values = (Set) it.getValue();
				BasicAttribute b = new BasicAttribute((String) it.getKey());
				for (Iterator a = values.iterator(); a.hasNext();) {
					String group = (String) a.next();
					b.add(group);
				}
				attributes.put(b);
			}
			else
				attributes.put((String) it.getKey(), it.getValue());
		}
		return attributes;
	}

	String getDn(Map map) {
		return "cn=" + map.get("cn") + "," + base;
	}

	static Pattern	EMAIL	= Pattern
									.compile("[a-z0-9._%-]+@([a-z0-9-]+\\.)*([a-z0-9-]+)\\.com");

	String organization(String mail) {
		Matcher m = EMAIL.matcher(mail);
		if (m.matches())
			return m.group(2).toUpperCase();
		else
			return null;
	}

	boolean equals(Object a, Object b) {
		if (a == null || b == null)
			return a == b;

		if (a instanceof Collection && b instanceof Collection) {
			Collection aa = (Collection) a;
			Collection bb = (Collection) b;
			if (aa.size() != bb.size())
				return false;

			for (Iterator i = aa.iterator(); i.hasNext();) {
				Object aaa = (Object) i.next();
				if (!bb.contains(aaa))
					return false;
			}
		}
		else
			return a.equals(b);
		return true;
	}

	Map merge(Map a, Map b) {
		Map c = new HashMap();
		c.putAll(a);
		c.putAll(b);
		return c;
	}

	public void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setReport(PrintStream out) {
		report = out;
	}
	
	public void setMerge(String file ) {
		merge = file;
	}
	public void setBase(String base ) {
		this.base = base;
	}
}
