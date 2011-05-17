/*
 * Copyright (c) OSGi Alliance (2009, 2010). All Rights Reserved.
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

package org.osgi.test.support.signature;

/**
 * Verify the signatures of all methods in the spec. Check that there no more
 * and less fields, methods, end constructors that are visible.
 */

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.test.support.*;

/**
 * 
 * @version $Id$
 */
public class SignatureTestCase extends OSGiTestCase implements ParserCallback {
	final Set<Object>				members	= new HashSet<Object>();
	Class< ? >						clazz;
	Map<String, Method>				methods;
	Map<String, Constructor< ? >>	constructors;
	Map<String, Field>				fields;
	Set<String>						found;
	Set<String>						missing;
	Object							lastMember;


	public void testSignature() throws Exception {
		Bundle bundle = getContext().getBundle();
		String path = "OSGI-INF/signature";
		found = new HashSet<String>();
		missing = new HashSet<String>();
		Enumeration<URL> e = bundle.findEntries(path, null, true);
		if (e == null)
			fail("No Signature Files found in " + path);

		while (e.hasMoreElements()) {
			URL url = e.nextElement();
			if (!url.toString().endsWith("/")) {
				try {
					InputStream in = url.openStream();
					try {
						ClassParser rdr = new ClassParser(in);
						rdr.go(this);
					}
					finally {
						in.close();
					}
				}
				catch (Exception ioe) {
					fail("Unexpected exception", ioe);
				}
			}
		}
		if (found.isEmpty()) {
			log("Package is not present: " + path);
			return;
		}
		if (!missing.isEmpty())
			fail("Missing classes. Found " + found + " but not " + missing);
	}

	/**
	 * Callback For the class parser to do attributes that are unknown.
	 * 
	 * @param name
	 * @param data
	 * @see org.osgi.test.cases.signature.tbc.ParserCallback#doAttribute(java.lang.String,
	 *      byte[])
	 */
	public Object doAttribute(String name, byte[] data) {
		return null;
	}

	/**
	 * Call back to handle a class file.
	 * 
	 * @param access
	 * @param name
	 * @param superName
	 * @param interfaces
	 * @return
	 * @see org.osgi.test.cases.signature.tbc.ParserCallback#doClass(int,
	 *      java.lang.String, java.lang.String, java.lang.String[])
	 */
	public boolean doClass(int access, String name, String superName,
			String[] interfaces) {
		clazz = null;
		if (!isVisible(access)) {
			return false;
		}

		String className = name.replace('/', '.');
		String superClassName = superName.replace('/', '.');
		log("#Checking class: " + className);
		try {

			try {
				clazz = Class.forName(className);
				members.add(clazz);

				if (clazz.getClassLoader() == getClass().getClassLoader()) {
					// We have gotten our own package
					missing.add(name);
					clazz = null;
					log("ever got here where we have gotten our own package");
					return false;
				}
				found.add(name);
				checkInterfaces(clazz, interfaces);

				int cMods = clazz.getModifiers();
				checkModifiers(access, cMods, ACC_PUBLIC | ACC_FINAL
						| ACC_INTERFACE | ACC_ABSTRACT);

				checkSuperClass(clazz, superClassName);

				methods = getMethods(clazz);
				fields = getFields(clazz);
				constructors = getConstructors(clazz);
				return true;
			}
			catch (ClassNotFoundException cnfe) {
				missing.add(name);
			}
		}
		catch (Exception e) {
			fail("Unexpected exception", e);
		}
		return false;
	}

	Map<String, Field> getFields(Class< ? > c) {
		Map<String, Field> result = new HashMap<String, Field>();
		while (c != null) {
			Field[] f = c.getDeclaredFields();
			for (int i = 0, l = f.length; i < l; i++) {
				if (!isVisible(f[i].getModifiers())) {
					continue;
				}
				String key = f[i].getName();
				if (result.containsKey(key)) {
					continue;
				}
				result.put(key, f[i]);
			}
			c = c.getSuperclass();
		}
		return result;
	}

	Map<String, Method> getMethods(Class< ? > c) {
		Map<String, Method> result = new HashMap<String, Method>();
		while (c != null) {
			Method[] m = c.getDeclaredMethods();
			for (int i = 0, l = m.length; i < l; i++) {
				if (!isVisible(m[i].getModifiers())) {
					continue;
				}
				String key = m[i].getName() + getMethodDescriptor(m[i]);
				if (result.containsKey(key)) {
					continue;
				}
				result.put(key, m[i]);
			}
			c = c.getSuperclass();
		}
		return result;
	}

	Map<String, Constructor< ? >> getConstructors(Class< ? > c) {
		Map<String, Constructor< ? >> result = new HashMap<String, Constructor< ? >>();
		Constructor< ? >[] m = c.getDeclaredConstructors();
		for (int i = 0, l = m.length; i < l; i++) {
			if (!isVisible(m[i].getModifiers())) {
				continue;
			}
			StringBuffer sb = new StringBuffer();
			sb.append("(");
			getDescriptor(sb, m[i].getParameterTypes());
			sb.append(")V");
			String key = sb.toString();
			if (result.containsKey(key)) {
				continue;
			}
			result.put(key, m[i]);
		}
		return result;
	}

	void log(String string) {
		System.out.println(string);
	}

	public void doField(int access, String name, String desiredDescriptor,
			Object constant) {
		if (!isVisible(access))
			return;

		log("#visit " + getClassName(clazz) + "." + name + " "
				+ desiredDescriptor);

		Field f = fields.remove(name);
		lastMember = f;

		if (f == null) {
			// Field not found!
			fail("Could not find field: " + getClassName(clazz) + "." + name);
		}
		members.add(f);

		int cMods = f.getModifiers();
		checkModifiers(access, cMods, ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED
				| ACC_STATIC | ACC_FINAL);

		Class< ? > type = f.getType();
		StringBuffer sb = new StringBuffer();
		createTypeDescriptor(sb, type);
		assertEquals("Field " + getClassName(clazz) + "." + name,
				desiredDescriptor, sb.toString());

		if (constant != null) {
			try {
				assertEquals("Constant value:", constant, f.get(null));
			}
			// These can probably be ignored
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public void doMethod(int access, String name, String desc,
			String[] exceptions) {

		if (!isVisible(access))
			return;

		log("#visit " + getClassName(clazz) + "." + name + " " + desc);

		if (name.equals("<init>"))
			checkConstructor(access, name, desc, exceptions);
		else
			checkMethod(access, name, desc, exceptions);

	}

	public void doEnd() {
		/**
		 * We removed the check to see if there is too much
		 */

//		if (signatures != null) {
//			// check if we had members that had signatures
//			for (Object member : members) {
//				String signature = signatures.getSignature(member);
//				if (signature.indexOf('<') >= 0)
//					fail("A construct under test has a generic signature that was not found in the specification "
//							+ signature);
//			}
//		}
	}

	void checkConstructor(int access, String name, String desiredDescriptor,
			String[] exceptions) {
		String key = desiredDescriptor;
		Constructor< ? > m = constructors.remove(key);
		lastMember = m;
		if (m == null) {
			// Method not found!
			fail("Could not find constructor: " + getClassName(clazz) + "."
					+ name + " " + desiredDescriptor);
		}
		members.add(m);

		int cMods = m.getModifiers();
		checkModifiers(access, cMods, ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED
				| ACC_STATIC | ACC_FINAL | ACC_ABSTRACT);
		checkExceptions(exceptions, m.getExceptionTypes());
	}

	void checkExceptions(String[] exceptions, Class< ? >[] exceptionTypes) {
		if (exceptions == null
				&& (exceptionTypes == null || exceptionTypes.length == 0))
			return;

		Set<String> set = new TreeSet<String>(Arrays.asList(exceptions));
		for (int i = 0; exceptionTypes != null && i < exceptionTypes.length; i++) {
			String name = exceptionTypes[i].getName().replace('.', '/');
			if (!set.remove(name))
				fail("Superfluous Exception " + exceptionTypes[i]);
		}
		if (!set.isEmpty())
			fail("Missing declared exceptions: " + set);
	}

	protected void checkInterfaces(Class< ? > c, String[] interfaces) {
		Class< ? > implemented[] = c.getInterfaces();
		outer: for (int i = 0; i < interfaces.length; i++) {
			String ifname = interfaces[i].replace('/', '.');
			for (int j = 0; j < implemented.length; j++) {
				if (implemented[j] != null
						&& implemented[j].getName().equals(ifname)) {
					implemented[j] = null;
					continue outer;
				}
			}
			fail("Missing interface, class " + getClassName(c) + " misses "
					+ ifname);
		}

	}

	protected void checkMethod(int access, String name,
			String desiredDescriptor, String[] exceptions) {
		String key = name + desiredDescriptor;
		Method m = methods.remove(key);
		lastMember = m;
		if (m == null) {
			// Method not found!
			fail("Could not find method: " + getClassName(clazz) + "." + name);
		}
		members.add(m);
		int cMods = m.getModifiers();
		checkModifiers(access, cMods, ACC_PUBLIC | ACC_PRIVATE | ACC_PROTECTED
				| ACC_STATIC | ACC_FINAL | ACC_ABSTRACT);
		checkExceptions(exceptions, m.getExceptionTypes());

	}

	protected void checkModifiers(int access, int cMods, int mask) {
		access &= mask;
		cMods &= mask;
		assertEquals("Relevant access modifiers", access, cMods);
	}

	protected void checkSuperClass(Class< ? > c, String superClassName) {
		Class< ? > superClass = c.getSuperclass();
		if (superClass != null)
			assertEquals("Super class", superClassName, superClass.getName());
	}

	void createTypeDescriptor(StringBuffer sb, Class< ? > type) {
		if (type.isArray()) {
			sb.append("[");
			createTypeDescriptor(sb, type.getComponentType());
		}
		else {
			if (type.isPrimitive()) {
				if (type == byte.class)
					sb.append("B");
				else
					if (type == char.class)
						sb.append("C");
					else
						if (type == double.class)
							sb.append("D");
						else
							if (type == float.class)
								sb.append("F");
							else
								if (type == int.class)
									sb.append("I");
								else
									if (type == long.class)
										sb.append("J");
									else
										if (type == short.class)
											sb.append("S");
										else
											if (type == boolean.class)
												sb.append("Z");
											else
												if (type == void.class)
													sb.append("V");
												else
													throw new IllegalArgumentException(
															"Unknown primitive type "
																	+ type);
			}
			else {
				sb.append("L");
				sb.append(type.getName().replace('.', '/'));
				sb.append(";");
			}
		}
	}

	String getClassName(Class< ? > c) {
		if (c.isArray())
			return getClassName(c.getComponentType()) + "[]";
		return c.getName();
	}

	void getDescriptor(StringBuffer sb, Class< ? >[] parameters) {
		for (int i = 0; i < parameters.length; i++) {
			createTypeDescriptor(sb, parameters[i]);
		}
	}

	String getMethodDescriptor(Method method) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		getDescriptor(sb, method.getParameterTypes());
		sb.append(")");
		createTypeDescriptor(sb, method.getReturnType());
		return sb.toString();
	}

	boolean isVisible(int access) {
		return (access & (ACC_PUBLIC | ACC_PROTECTED)) != 0;
	}

	/**
	 * Check the current signature. If no signature was set, we ignore this
	 * check. This means we always pass on environments that do not have the
	 * generic methods, i.e. pre 1.5.
	 * @throws Exception 
	 */
	public void doSignature(String signature) throws Exception {
//		if (lastMember == null)
//			return;
//
//		members.remove(lastMember);
//		if (!signatures.hasGenerics())
//			return;
//
//		log("# " + signature + " " + lastMember);
//
//		String underTest = signatures.normalize(signatures
//				.getSignature(lastMember));
//		if (underTest.indexOf('<') >= 0) {
//			String specification = signatures.normalize(signature);
//			assertEquals(specification, underTest);
//		}
	}

	/**
	 * Once the members are done the following attribute will be about the
	 * class. We calculated the class signature in the first step so now we must
	 * set the checked signature to the class signature so that
	 * {@link #doSignature(String)} will check the appropriate signature. A bit
	 * convoluted but then who cares.
	 */

	public void doMembersDone() {
		lastMember = clazz;
	}
}
