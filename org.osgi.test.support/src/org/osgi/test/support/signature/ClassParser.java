/*
 * $Id: ClassParser.java 4756 2007-04-06 02:42:28Z bjhargrave $
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

import java.io.*;
import java.util.*;

public class ClassParser {
	DataInputStream			in;
	protected int			magic;
	protected int			majorVersion;
	protected int			minorVersion;
	protected int			constPoolCount;
	protected PoolEntry[]	constPool;

	public ClassParser(InputStream in) {
		this.in = new DataInputStream(in);
	}

	/**
	 * Parse a class file.
	 * 
	 * @param cb Callback class
	 * @throws IOException when we run into an io error.
	 */
	
	public void go(ParserCallback cb) throws IOException {
		magic = in.readInt();
		check(magic == 0xCAFEBABE, "Invalid magic");
		minorVersion = in.readUnsignedShort();
		majorVersion = in.readUnsignedShort();
		
		readConstantPool();
		
		if ( ! doClass(cb) )
			return;

		doFields(cb);
		doMethods(cb);
		
		doAttributes(cb);
		
		cb.doEnd();
	}

	private boolean doClass(ParserCallback cb) throws IOException {
		int accessFlags = in.readUnsignedShort();
		String thisClass = getClassName();
		String superClass = getClassName();
		int interfacesCount = in.readUnsignedShort();
		String[] interfaces = new String[interfacesCount];
		for (int i = 0; i < interfacesCount; i++) {
			interfaces[i] = getClassName();
		}
		return cb.doClass(accessFlags, thisClass, superClass, interfaces);
	}

	private void doMethods(ParserCallback cb) throws IOException {
		int methodsCount = in.readUnsignedShort();
		for (int i = 0; i < methodsCount; i++) {
			int access = in.readUnsignedShort();
			String name = getUtf8();
			String descriptor = getUtf8();

			Map map = doAttributes(cb);
			cb.doMethod(access, name, descriptor, (String[]) map
					.get("Exceptions"));
		}
	}

	private void doFields(ParserCallback cb) throws IOException {
		int fieldsCount = in.readUnsignedShort();
		for (int i = 0; i < fieldsCount; i++) {
			int access = in.readUnsignedShort();
			String fieldName = getUtf8();
			String fieldDescriptor = getUtf8();
			Map map = doAttributes(cb);
			cb.doField(access, fieldName, fieldDescriptor, map
					.get("ConstantValue"));
		}
	}

	private void readConstantPool() throws IOException {
		constPoolCount = in.readUnsignedShort();
		constPool = new PoolEntry[constPoolCount];
		for (int i = 1; i < constPoolCount; i++) {
			PoolEntry entry = new PoolEntry(constPool, in);
			constPool[i] = entry;
			if (entry.tag == PoolEntry.CONSTANT_Double
					|| entry.tag == PoolEntry.CONSTANT_Long)
				i++;
		}
	}

	private void check(boolean b, String msg) {
		if (!b)
			throw new IllegalArgumentException(msg);
	}

	private String getClassName() throws IOException {
		int n = in.readUnsignedShort();
		return constPool[n].getClassName();
	}

	private String getUtf8() throws IOException {
		return constPool[in.readUnsignedShort()].getUtf8();
	}

	private Object getValue() throws IOException {
		return constPool[in.readUnsignedShort()].getValue();
	}

	private Map doAttributes(ParserCallback cb) throws IOException {
		Map map = new HashMap();
		int attributesCount = in.readUnsignedShort();
		for (int j = 0; j < attributesCount; j++) {
			String attributeName = getUtf8();
			int attributeLength = in.readInt();
			if (attributeLength < 0)
				throw new IllegalArgumentException(
						"Does not support attributes > 2gb");

			if (attributeName.equals("ConstantValue")) {
				map.put(attributeName, getValue());
			}
			else if (attributeName.equals("Exceptions")) {
				int numberOfExceptions = in.readUnsignedShort();
				String[] exceptions = new String[numberOfExceptions];
				for (int k = 0; k < numberOfExceptions; k++) {
					exceptions[k] = getClassName();
				}
				map.put(attributeName, exceptions);
			}
			else {
				in.skip(attributeLength);
			}
		}
		return map;
	}

}
