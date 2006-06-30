/*
 * $Header$
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

import org.objectweb.asm.*;
import org.objectweb.asm.Type;
import org.osgi.framework.*;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * 
 * @version $Revision$
 */
public class TestControl extends DefaultTestBundleControl implements
		ClassVisitor {
	Class		clazz;
	Method		methods[];
	Constructor	constructors[];
	Field		fields[];
	Class		inner[];

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
		while (st.hasMoreTokens()) {
			String signature = st.nextToken().replace('.', '/');
			traverse(b, signature, this);
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

	void traverse(Bundle bundle, String path, ClassVisitor cv)
			throws IOException {
		Enumeration e = bundle.findEntries(path, "*.class", true);
		while (e.hasMoreElements()) {
			URL url = (URL) e.nextElement();
			if (url.getPath().indexOf('$') < 0) {
				try {
					InputStream in = url.openStream();
					ClassReader rdr = new ClassReader(in);
					rdr.accept(cv, false);
					in.close();
				}
				catch (IOException ioe) {
					fail("Unexpected exception " + ioe);
				}
			}
			else
				log("#Skipping class: " + url.getPath());
		}
	}

	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		clazz = null;
		if (!isVisible(access))
			return;

		String className = name.replace('/', '.');
		String superClassName = superName.replace('/', '.');
		log("#Checking class: " + className);
		try {

			clazz = Class.forName(className);
			checkInterfaces(clazz, interfaces);

			int cMods = clazz.getModifiers();
			checkModifiers(access, cMods);

			checkSuperClass(clazz, superClassName);

			methods = clazz.getDeclaredMethods();
			fields = clazz.getDeclaredFields();
			constructors = clazz.getDeclaredConstructors();
			inner = clazz.getDeclaredClasses();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Class not found in signature check: " + className + " " + e);
		}
	}

	private boolean isVisible(int access) {
		return (access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) != 0;
	}

	/**
	 * Annotations are ignored
	 * 
	 * @param desc
	 * @param visible
	 * @return
	 * @see org.objectweb.asm.ClassVisitor#visitAnnotation(java.lang.String,
	 *      boolean)
	 */
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

		return null;
	}

	/**
	 * Attributes are ignored
	 * 
	 * @param attr
	 * @see org.objectweb.asm.ClassVisitor#visitAttribute(org.objectweb.asm.Attribute)
	 */
	public void visitAttribute(Attribute attr) {
		// TODO Auto-generated method stub

	}

	/**
	 * Verify that we have no methods, fields, or constructors that are visible
	 * left.
	 * 
	 * @see org.objectweb.asm.ClassVisitor#visitEnd()
	 */
	public void visitEnd() {
		if (clazz == null)
			return;

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

	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		if (!isVisible(access))
			return null;

		if (clazz == null)
			return null;

		for (int i = 0; i < fields.length; i++) {
			if (fields[i] != null && fields[i].getName().equals(name)) {
				int cMods = fields[i].getModifiers();
				checkModifiers(access, cMods);

				Class type = fields[i].getType();
				String typeName = Type.getType(desc).getClassName();
				assertEquals(
						"Field " + getClassName(clazz) + "." + name,
						typeName,
						getClassName(type));

				fields[i] = null;
				return null;
			}
		}
		// Field not found!
		fail("Could not find field: " + getClassName(clazz) + "." + name);
		return null;
	}

	public void visitInnerClass(String name, String outerName,
			String innerName, int access) {
		if (!isVisible(access))
			return;

		if (clazz == null)
			return;

		// Not sure what to do here?
		// We currently have no visible classes in our API
		// so we can skip it for now.
	}

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		if (!isVisible(access))
			return null;

		if (clazz == null)
			return null;

		log("#visit " + getClassName(clazz) + "." + name);

		if (name.equals("<init>"))
			checkConstructors(access, name, desc, signature, exceptions);
		else
			checkMethods(access, name, desc, signature, exceptions);

		return null;
	}

	public void visitOuterClass(String owner, String name, String desc) {
		// TODO What is this?
	}

	public void visitSource(String source, String debug) {
	}

	private void checkMethods(int access, String name, String desc,
			String signature, String[] exceptions) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i] != null && methods[i].getName().equals(name)) {
				String cDesc = Type.getMethodDescriptor(methods[i]);
				if (cDesc.equals(desc)) {
					int cMods = methods[i].getModifiers();
					checkModifiers(access, cMods);
					methods[i] = null;
					return;
				}
			}
		}
		// Method not found!
		fail("Could not find method: " + getClassName(clazz) + "." + name);
	}

	private void checkConstructors(int access, String name, String desc,
			String signature, String[] exceptions) {
		Type[] arguments = Type.getArgumentTypes(desc);
		next: for (int i = 0; i < constructors.length; i++) {
			if (constructors[i] != null) {
				Class[] cArguments = constructors[i].getParameterTypes();
				if (arguments.length == cArguments.length) {
					for (int a = 0; a < arguments.length; a++) {
						String arg = arguments[a].getClassName();
						String cArg = getClassName(cArguments[a]);
						if (!arg.equals(cArg)) {
							continue next;
						}
					}
					constructors[i] = null;
					return;
				}
			}
		}
		// Method not found!
		fail("Could not find constructor: " + getClassName(clazz) + "." + name
				+ " " + desc);
	}

	private void checkSuperClass(Class clazz, String superClassName) {
		Class superClass = clazz.getSuperclass();
		if (superClass != null)
			assertEquals(
					"Super class does not match ",
					superClassName,
					superClass.getName());
	}

	private void checkModifiers(int access, int cMods) {
		assertEquals(
				"Interface must be equal ",
				(access & Opcodes.ACC_INTERFACE) != 0 ? 1 : 0,
				java.lang.reflect.Modifier.isInterface(cMods) ? 1 : 0);

		assertEquals(
				"Abstract must be equal ",
				(access & Opcodes.ACC_ABSTRACT) != 0 ? 1 : 0,
				java.lang.reflect.Modifier.isAbstract(cMods) ? 1 : 0);

		assertEquals(
				"Public must be equal ",
				(access & Opcodes.ACC_PUBLIC) != 0 ? 1 : 0,
				java.lang.reflect.Modifier.isPublic(cMods) ? 1 : 0);

		assertEquals(
				"Protected must be equal ",
				(access & Opcodes.ACC_PROTECTED) != 0 ? 1 : 0,
				java.lang.reflect.Modifier.isProtected(cMods) ? 1 : 0);

		assertEquals(
				"Final must be equal ",
				(access & Opcodes.ACC_FINAL) != 0 ? 1 : 0,
				java.lang.reflect.Modifier.isFinal(cMods) ? 1 : 0);

		assertEquals(
				"Static must be equal ",
				(access & Opcodes.ACC_STATIC) != 0 ? 1 : 0,
				java.lang.reflect.Modifier.isStatic(cMods) ? 1 : 0);
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

	private String getClassName(Class clazz) {
		if (clazz.isArray())
			return getClassName(clazz.getComponentType()) + "[]";
		return clazz.getName();
	}

}