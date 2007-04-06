/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.cm.tbc;

import java.lang.reflect.*;

/**
 * This class represents a method call.
 * 
 * <p>
 * A method call is represented by the name of the method, the type of the
 * parameters and the actual parameters.
 * <p>
 * When <code>invoke</code> is called, all "reflection magic" is taken care of
 * inside this class.
 */
public class MethodCall {
	private String					name;
	private Class					clazz;
	private Class[]					paramClasses;
	private Object[]				paramObjects;
	private static final Class[]	NO_CLASSES	= new Class[0];
	private static final Object[]	NO_OBJECTS	= new Object[0];

	/**
	 * Construct a new MethodCall with no parameters
	 * 
	 * @param name the name of the method
	 */
	public MethodCall(Class clazz, String name) {
		this.name = name;
		this.clazz = clazz;
		this.paramClasses = NO_CLASSES;
		this.paramObjects = NO_OBJECTS;
	}

	/**
	 * Constructs a new MethodCall with one parameter.
	 * 
	 * @param name the name of the method
	 * @param paramClass the type of the paramater
	 * @param paramObject the parameter
	 */
	public MethodCall(Class clazz, String name, Class paramClass,
			Object paramObject) {
		this(clazz, name);
		this.paramClasses = new Class[] {paramClass};
		this.paramObjects = new Object[] {paramObject};
	}

	/**
	 * Constructs a new MethodCall with more than one parameter.
	 * 
	 * @param name the name of the method
	 * @param paramClasses an array containing the paramatertypes.
	 * @param paramObjects an array containing the parameters
	 */
	public MethodCall(Class clazz, String name, Class[] paramClasses,
			Object[] paramObjects) {
		this(clazz, name);
		this.paramClasses = paramClasses;
		this.paramObjects = paramObjects;
	}

	/**
	 * Returns the name of the method.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Invokes the method with the specified parameters.
	 * 
	 * @param o the object to invoke the method on
	 * @return whatever the method returns (see
	 *         java.lang.reflect.Method.invoke() for details)
	 * @throws Throwable rethrows anything that was thrown by the reflective
	 *         invoke call. < b>Note! </b>
	 *         <code>InvocationTargetException</code> is unwrapped and the
	 *         "real" Exception is rethrown!
	 */
	public Object invoke(Object o) throws Throwable {
		Object returnObject = null;
		try {
			Class useClass = clazz;
			if (useClass == null)
				useClass = o.getClass();
			Method m = useClass.getDeclaredMethod(name, paramClasses);
			returnObject = m.invoke(o, paramObjects);
		}
		catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
		return returnObject;
	}
}
