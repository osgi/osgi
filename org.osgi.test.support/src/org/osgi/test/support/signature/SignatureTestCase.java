/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.framework.Bundle;
import org.osgi.test.support.OSGiTestCase;

/**
 * 
 * @version $Revision: 7050 $
 */
public abstract class SignatureTestCase extends OSGiTestCase implements
		ParserCallback {
	private Class		clazz;
	private Method		methods[];
	private Constructor	constructors[];
	private Field		fields[];
	private Class		inner[];
	private Set			found;
	private Set			missing;

	public void testSignature() {
		Bundle bundle = getContext().getBundle();
		String path = "OSGI-INF/signature";
		found = new HashSet();
		missing = new HashSet();
		Enumeration e = bundle.findEntries(path, null, true);
		if (e == null)
			fail("No Signature Files found in " + path);
	
		while (e.hasMoreElements()) {
			URL url = (URL) e.nextElement();
			if (!url.toString().endsWith("/")) {
				try {
					InputStream in = url.openStream();
					ClassParser rdr = new ClassParser(in);
					rdr.go(this);
					in.close();
				}
				catch (Exception ioe) {
					fail("Unexpected exception", ioe);
				}
			}
		}
		if (found.isEmpty()) {
			log("#Package is not present: " + path);
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

				methods = clazz.getMethods();
				fields = clazz.getFields();
				constructors = clazz.getConstructors();
				inner = clazz.getClasses();
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

	private void log(String string) {
		System.out.println(string);
	}

	public void doField(int access, String name, String desiredDescriptor,
			Object constant) {
		if (!isVisible(access))
			return;

		for (int i = 0; i < fields.length; i++) {
			if (fields[i] != null && fields[i].getName().equals(name)) {
				int cMods = fields[i].getModifiers();
				checkModifiers(access, cMods, ACC_PUBLIC | ACC_PRIVATE
						| ACC_PROTECTED | ACC_STATIC | ACC_FINAL);

				Class type = fields[i].getType();
				StringBuffer sb = new StringBuffer();
				createTypeDescriptor(sb, type);
				assertEquals("Field " + getClassName(clazz) + "." + name,
						desiredDescriptor, sb.toString());

				if (constant != null)
					try {
						assertEquals("Constant value:", constant, fields[i]
								.get(null));
					}
					// These can probably be ignored
					catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
					catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				fields[i] = null;
				return;
			}
		}
		// Field not found!
		fail("Could not find field: " + getClassName(clazz) + "." + name);
	}

	public void doInnerClass(String name, String outerName, String innerName,
			int access) {
		if (!isVisible(access))
			return;

		if (inner == null)
			return;

		// TODO we do now have inner interface in ListenerHook.ListenerInfo
		// TODO Not sure what to do here?
		// We currently have no visible classes in our API
		// so we can skip it for now.
	}

	public void doMethod(int access, String name, String desc,
			String[] exceptions) {

		if (!isVisible(access))
			return;

		log("#visit " + getClassName(clazz) + "." + name + " " + desc);

		if (name.equals("<init>"))
			checkConstructors(access, name, desc, exceptions);
		else
			checkMethods(access, name, desc, exceptions);
	}

	/**
	 * We removed the check to see if there is too much
	 */
	public void doEnd() {
		// empty
	}

	private void checkConstructors(int access, String name,
			String desiredDescriptor, String[] exceptions) {
		for (int i = 0; i < constructors.length; i++) {
			if (constructors[i] != null) {
				StringBuffer sb = new StringBuffer();
				sb.append("(");
				getDescriptor(sb, constructors[i].getParameterTypes());
				sb.append(")V");
				String actualDescriptor = sb.toString();
				if (actualDescriptor.equals(desiredDescriptor)) {
					int cMods = constructors[i].getModifiers();
					checkModifiers(access, cMods, ACC_PUBLIC | ACC_PRIVATE
							| ACC_PROTECTED | ACC_STATIC | ACC_FINAL
							| ACC_ABSTRACT);
					checkExceptions(exceptions, constructors[i]
							.getExceptionTypes());
					constructors[i] = null;
					return;
				}
			}
		}
		// Method not found!
		fail("Could not find constructor: " + getClassName(clazz) + "." + name
				+ " " + desiredDescriptor);

	}

	private void checkExceptions(String[] exceptions, Class[] exceptionTypes) {
		if (exceptions == null
				&& (exceptionTypes == null || exceptionTypes.length == 0))
			return;

		Set set = new TreeSet(Arrays.asList(exceptions));
		for (int i = 0; exceptionTypes != null && i < exceptionTypes.length; i++) {
			String name = exceptionTypes[i].getName().replace('.', '/');
			if (!set.remove(name))
				fail("Superfluous Exception " + exceptionTypes[i]);
		}
		if (!set.isEmpty())
			fail("Missing declared exceptions: " + set);
	}

	private void checkInterfaces(Class c, String[] interfaces) {
		Class implemented[] = c.getInterfaces();
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

	private void checkMethods(int access, String name,
			String desiredDescriptor, String[] exceptions) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i] != null && methods[i].getName().equals(name)) {
				String cDesc = getMethodDescriptor(methods[i]);
				if (cDesc.equals(desiredDescriptor)) {
					int cMods = methods[i].getModifiers();
					checkModifiers(access, cMods, ACC_PUBLIC | ACC_PRIVATE
							| ACC_PROTECTED | ACC_STATIC | ACC_FINAL
							| ACC_ABSTRACT);
					checkExceptions(exceptions, methods[i].getExceptionTypes());
					methods[i] = null;
					return;
				}
			}
		}
		// Method not found!
		fail("Could not find method: " + getClassName(clazz) + "." + name);
	}

	private void checkModifiers(int access, int cMods, int mask) {
		access &= mask;
		cMods &= mask;
		assertEquals("Relevant access modifiers", access, cMods);
	}

	private void checkSuperClass(Class c, String superClassName) {
		Class superClass = c.getSuperclass();
		if (superClass != null)
			assertEquals("Super class", superClassName, superClass.getName());
	}

	private void createTypeDescriptor(StringBuffer sb, Class type) {
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

	private String getClassName(Class c) {
		if (c.isArray())
			return getClassName(c.getComponentType()) + "[]";
		return c.getName();
	}

	private void getDescriptor(StringBuffer sb, Class[] parameters) {
		for (int i = 0; i < parameters.length; i++) {
			createTypeDescriptor(sb, parameters[i]);
		}
	}

	private String getMethodDescriptor(Method method) {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		getDescriptor(sb, method.getParameterTypes());
		sb.append(")");
		createTypeDescriptor(sb, method.getReturnType());
		return sb.toString();
	}

	private boolean isVisible(int access) {
		return (access & (ACC_PUBLIC | ACC_PROTECTED)) != 0;
	}
}