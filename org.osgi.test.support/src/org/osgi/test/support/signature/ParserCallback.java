/*
 * $Id$
 * 
 * Copyright (c) The OSGi Alliance (2006). All Rights Reserved.
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

package org.osgi.test.support.signature;

public interface ParserCallback {
	public static final int	ACC_PUBLIC		= 0x0001;
	public static final int	ACC_PRIVATE		= 0x0002;
	public static final int	ACC_PROTECTED	= 0x0004;
	public static final int	ACC_STATIC		= 0x0008;
	public static final int	ACC_FINAL		= 0x0010;
	public static final int	ACC_SUPER		= 0x0020;
	public static final int	ACC_VOLATILE	= 0x0040;
	public static final int	ACC_TRANSIENT	= 0x0080;
	public static final int	ACC_INTERFACE	= 0x0200;
	public static final int	ACC_ABSTRACT	= 0x0400;

	/**
	 * Called once for each class.
	 * 
	 * @param access		Access modifier
	 * @param className		The classname with slashes
	 * @param superName		The super class name with slashes
	 * @param interfaces	The interface class names with slashes
	 * @return true if the file should be parsed, otherwise false
	 */
	boolean doClass(int access, String className, String superName,
			String[] interfaces);

	/**
	 * Called for each method.
	 * 
	 * @param access		Access modifier
	 * @param methodName	name of the method
	 * @param descriptor	Descriptor of the method, e.g. (Ljava/lang/String;)V
	 * @param exceptions	List of exception class names, with slashes
	 */
	void doMethod(int access, String methodName, String descriptor,
			String[] exceptions);

	/**
	 * Called for each field.
	 * 
	 * @param access		Access modifier
	 * @param fieldName		Name of the field
	 * @param descriptor	Type descriptor
	 * @param constant		Constant value
	 */
	void doField(int access, String methodName, String descriptor,
			Object constant);

	/**
	 * Called for attributes that are not recognized.
	 * 
	 * The return value will be stored in the attributes map.
	 * 
	 * @param name		name/type of the attribute
	 * @param data		The binary data
	 * @return the translated object or null.
	 */
	Object doAttribute(String name, byte[] data);

	/**
	 * Called after last bit read
	 *
	 */
	void doEnd();
}
