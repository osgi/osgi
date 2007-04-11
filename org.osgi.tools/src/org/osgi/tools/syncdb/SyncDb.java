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
	PrintStream	report = System.out;
	
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

	List getLdapUsers() throws Exception {
		List answer = Collections.list(ctx.search("ou=People",
				"(objectclass=OSGiPerson)", null));
		List result = new ArrayList();

		for (Iterator i = answer.iterator(); i.hasNext();) {
			SearchResult it = (SearchResult) i.next();
			Attributes attributes = it.getAttributes();
			Map map = new HashMap();
			List attrs = Collections.list(attributes.getAll());
			for (Iterator a = attrs.iterator(); a.hasNext();) {
				Attribute attr = (Attribute) a.next();
				List l = Collections.list(attr.getAll());
				if (attr.getID().equals("isMemberOf"))
					map.put("isMemberOf", l);
				else
					if (attr.getID().equals("mail"))
						map
								.put(attr.getID(), ((String) l.get(0))
										.toLowerCase());
					else
						map.put(attr.getID(), l.get(0));
			}
			result.add(map);
		}
		return result;
	}

	List getNewUsers(String fileName) throws Exception {
		File f = new File(fileName);
		List result = new ArrayList();

		FileReader fr = new FileReader(f);
		BufferedReader rdr = new BufferedReader(fr);
		String line = rdr.readLine();
		while (line != null) {
			String[] parts = line.split("\\s+");
			if (parts.length >= 2 && !line.trim().startsWith("#")) {
				Map map = new HashMap();
				map.put("mail", parts[0].toLowerCase());
				map.put("userPassword", "{SHA}" + sha(parts[1]));
				List isMemberOf = new ArrayList();
				isMemberOf.add("member");
				map.put("isMemberOf", isMemberOf);

				String o = organization((String) map.get("mail"));
				if (o != null)
					map.put("o", o);

				if (parts.length > 2) {
					for (int i = 2; i < parts.length; i++) {
						Matcher m = Pattern.compile("(\\w+)=(\\w+)").matcher(
								parts[i]);
						if (m.matches()) {
							String key = m.group(1);
							String value = m.group(2);
							map.put(key, value);
						}
						else {
							isMemberOf.add(parts[i]);
						}
					}
				}
				result.add(map);
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

	public void run() {
		try {
			init();
			List updated = getNewUsers("members.txt");
			List ldap = getLdapUsers();
			Map diff = new HashMap();

			for (Iterator i = ldap.iterator(); i.hasNext();) {
				Map it = (Map) i.next();
				diff.put(it.get("mail"), it);
			}
			System.out.println(diff);

			for (Iterator i = updated.iterator(); i.hasNext();) {
				Map it = (Map) i.next();
				String mail = (String) it.get("mail");
				if (!diff.containsKey(mail)) {
					create(it);
				}
				else {
					Map map = merge((Map) diff.get(mail), it);
					update(map);
					diff.remove(mail);
				}
			}

			for (Iterator i = diff.keySet().iterator(); i.hasNext();) {
				Map it = (Map) i.next();
				List isMemberOf = (List) it.get("isMemberOf");
				if (isMemberOf != null && isMemberOf.contains("admin"))
					;
				remove(it);
			}
			ctx.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void create(Map map) throws Exception {
		System.out.println("Create " + map);
		ctx.bind(getDn(map), null, getAttributes(map));
	}

	void update(Map map) throws Exception {
		System.out.println("Update " + map);
		map.remove("objectClass");
		ctx.rebind(getDn(map), null, getAttributes(map));
	}

	void remove(Map map) throws Exception {
		System.out.println("Delete " + map);
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
				List values = (List) it.getValue();
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
		return "cn=" + map.get("mail") + ",ou=People";
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
	
	public void setReport( PrintStream out ) {
		report = out;
	}
}
