/*
 * $Id$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.signature.tbc;

/**
 * Verify the signatures of all methods in the spec. Check that there no more
 * and less fields, methods, end constructors that are visible.
 */
import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * 
 * @version $Revision$
 */
public class TestControl extends DefaultTestBundleControl implements
		ParserCallback {
	Class		clazz;
	Method		methods[];
	Constructor	constructors[];
	Field		fields[];
	Class		inner[];
	Set			found;
	Set			missing;
	boolean		ignore;

	/** Callback
	 *  For the class parser to do attributes that are unknown.
	 * @param name
	 * @param data
	 * @see org.osgi.test.cases.signature.tbc.ParserCallback#doAttribute(java.lang.String, byte[])
	 */
	public Object doAttribute(String name, byte[] data) {
		return null;
	}

	/**
	 * Call back to handle a class file.
	 * @param access
	 * @param name
	 * @param superName
	 * @param interfaces
	 * @return
	 * @see org.osgi.test.cases.signature.tbc.ParserCallback#doClass(int, java.lang.String, java.lang.String, java.lang.String[])
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
					return false;
				}
				found.add(name);
				checkInterfaces(clazz, interfaces);

				int cMods = clazz.getModifiers();
				checkModifiers(access, cMods, ACC_PUBLIC + ACC_FINAL
						+ ACC_INTERFACE + ACC_ABSTRACT);

				checkSuperClass(clazz, superClassName);

				methods = clazz.getDeclaredMethods();
				fields = clazz.getDeclaredFields();
				constructors = clazz.getDeclaredConstructors();
				inner = clazz.getDeclaredClasses();
				return true;
			}
			catch (ClassNotFoundException cnfe) {
				missing.add(name);
			}
		}
		catch (Exception e) {
			fail("Unexpected exception: " + e);
		}
		return false;
	}

	public void doField(int access, String name, String desiredDescriptor,
			Object constant) {
		if (!isVisible(access))
			return;

		for (int i = 0; i < fields.length; i++) {
			if (fields[i] != null && fields[i].getName().equals(name)) {
				int cMods = fields[i].getModifiers();
				checkModifiers(access, cMods, ACC_PUBLIC + ACC_PRIVATE
						+ ACC_PROTECTED + ACC_STATIC + ACC_FINAL);

				Class type = fields[i].getType();
				StringBuffer sb = new StringBuffer();
				createTypeDescriptor(sb, type);
				assertEquals(
						"Field " + getClassName(clazz) + "." + name,
						desiredDescriptor,
						sb.toString());

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

		if (clazz == null)
			return;

		// TODO Not sure what to do here?
		// We currently have no visible classes in our API
		// so we can skip it for now.
	}

	public void doMethod(int access, String name, String desc,
			String[] exceptions) {

		if (!isVisible(access))
			return;

		log("#visit " + getClassName(clazz) + "." + name + " " + desc );

		if (name.equals("<init>"))
			checkConstructors(access, name, desc, exceptions);
		else
			checkMethods(access, name, desc, exceptions);
	}

	/**
	 * @throws IOException
	 * @specification <remove>Specification</remove>
	 * @interface <remove>Related interface, e.g. org.osgi.util.measurement</remove>
	 * @specificationVersion <remove>Version nr of the specification</remove>
	 * @methods <remove>Related method(s)</remove>
	 */
	public void testSignature() throws IOException {
		BundleContext bc = super.getContext();
		Bundle b = bc.getBundle();
		String signatures = (String) b.getHeaders().get("Signature-Packages");
		StringTokenizer st = new StringTokenizer(signatures, " ,");
		int n = st.countTokens();
		for (int i=0; i<n; i++ ) {
			String signature = st.nextToken().replace('.', '/');
			doPackage(b, signature, this);
			progress(100 * (i+1)/n);
		}
	}

	public void doEnd() {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i] != null) {
				int cMods = methods[i].getModifiers();
				if (Modifier.isPublic(cMods) || Modifier.isProtected(cMods))
					fail("Extra visible method " + getClassName(clazz) + "."
							+ methods[i]);
			}
		}
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] != null) {
				int cMods = fields[i].getModifiers();
				if (Modifier.isPublic(cMods) || Modifier.isProtected(cMods))
					fail("Extra visible field " + getClassName(clazz) + "."
							+ fields[i]);
			}
		}
		for (int i = 0; i < constructors.length; i++) {
			if (constructors[i] != null) {
				int cMods = constructors[i].getModifiers();
				if (Modifier.isPublic(cMods) || Modifier.isProtected(cMods))
					fail("Extra visible constructor " + getClassName(clazz)
							+ "." + constructors[i]);
			}
		}
	}

	/**
	 * Traverse the paths in out bundle. Visit each class and verify against the
	 * imported classes.
	 * 
	 * @param fin
	 * @param cv
	 * @throws IOException
	 */

	void doPackage(Bundle bundle, String path, ParserCallback cv)
			throws IOException {
		found = new HashSet();
		missing = new HashSet();
		Enumeration e = bundle.findEntries(path, "*.class", false);
		while (e.hasMoreElements()) {
			URL url = (URL) e.nextElement();
			try {
				InputStream in = url.openStream();
				ClassParser rdr = new ClassParser(in);
				rdr.go(this);
				in.close();
			}
			catch (Exception ioe) {
				ioe.printStackTrace();
				fail("Unexpected exception " + ioe);
			}
		}
		if (found.isEmpty()) {
			log("#Package is not present: " + path);
			return;
		}
		if (!missing.isEmpty())
			fail("Missing classes. Found " + found + " but not " + missing);
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
					checkModifiers(access, cMods, ACC_PUBLIC + ACC_PRIVATE
							+ ACC_PROTECTED + ACC_STATIC + ACC_FINAL
							+ ACC_ABSTRACT);
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
		if ( exceptions == null && (exceptionTypes==null || exceptionTypes.length==0))
			return;
		
		Set set = new TreeSet(Arrays.asList(exceptions));
		for (int i = 0; exceptionTypes !=null && i < exceptionTypes.length; i++) {
			String name = exceptionTypes[i].getName().replace('.','/');
			if (!set.remove(name))
				fail("Superfluous Exception " + exceptionTypes[i]);
		}
		if ( ! set.isEmpty() )
			fail("Missing declared exceptions: " + set );
	}

	private void checkInterfaces(Class clazz, String[] interfaces) {
		Class implemented[] = clazz.getInterfaces();
		outer: for (int i = 0; i < interfaces.length; i++) {
			String ifname = interfaces[i].replace('/', '.');
			for (int c = 0; c < implemented.length; c++) {
				if (implemented[c] != null
						&& implemented[c].getName().equals(ifname)) {
					implemented[c] = null;
					continue outer;
				}
			}
			fail("Missing interface, class " + getClassName(clazz) + " misses "
					+ ifname);
		}
		for (int i = 0; i < implemented.length; i++)
			assertNull("Extra interface: " + getClassName(clazz)
					+ " implements " + implemented[i], implemented[i]);
	}

	private void checkMethods(int access, String name,
			String desiredDescriptor, String[] exceptions) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i] != null && methods[i].getName().equals(name)) {
				String cDesc = getMethodDescriptor(methods[i]);
				if (cDesc.equals(desiredDescriptor)) {
					int cMods = methods[i].getModifiers();
					checkModifiers(access, cMods, ACC_PUBLIC + ACC_PRIVATE
							+ ACC_PROTECTED + ACC_STATIC + ACC_FINAL
							+ ACC_ABSTRACT);
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

	private void checkSuperClass(Class clazz, String superClassName) {
		Class superClass = clazz.getSuperclass();
		if (superClass != null)
			assertEquals(
					"Super class",
					superClassName,
					superClass.getName());
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
				else if (type == char.class)
					sb.append("C");
				else if (type == double.class)
					sb.append("D");
				else if (type == float.class)
					sb.append("F");
				else if (type == int.class)
					sb.append("I");
				else if (type == long.class)
					sb.append("J");
				else if (type == short.class)
					sb.append("S");
				else if (type == boolean.class)
					sb.append("Z");
				else if (type == void.class)
					sb.append("V");
				else
					throw new IllegalArgumentException(
							"Unknown primitive type " + type);
			}
			else {
				sb.append("L");
				sb.append(type.getName().replace('.', '/'));
				sb.append(";");
			}
		}
	}

	private String getClassName(Class clazz) {
		if (clazz.isArray())
			return getClassName(clazz.getComponentType()) + "[]";
		return clazz.getName();
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