/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.support.signature;

public interface ParserCallback {
	public static final int	ACC_PUBLIC			= 0x0001;
	public static final int	ACC_PRIVATE			= 0x0002;
	public static final int	ACC_PROTECTED		= 0x0004;
	public static final int	ACC_STATIC			= 0x0008;
	public static final int	ACC_FINAL			= 0x0010;
	public static final int	ACC_SUPER			= 0x0020;
	public static final int	ACC_SYNCHRONIZED	= 0x0020;
	public static final int	ACC_VOLATILE		= 0x0040;
	public static final int	ACC_BRIDGE			= 0x0040;
	public static final int	ACC_TRANSIENT		= 0x0080;
	public static final int	ACC_VARARGS			= 0x0080;
	public static final int	ACC_NATIVE			= 0x0100;
	public static final int	ACC_INTERFACE		= 0x0200;
	public static final int	ACC_ABSTRACT		= 0x0400;
	public static final int	ACC_STRICT			= 0x0800;
	public static final int	ACC_SYNTHETIC		= 0x1000;
	public static final int	ACC_ANNOTATION		= 0x2000;
	public static final int	ACC_ENUM			= 0x4000;

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
