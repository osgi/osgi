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

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
	 * @throws Exception when we run into an io error.
	 */
	
	public void go(ParserCallback cb) throws Exception {
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

	private boolean doClass(ParserCallback cb) throws Exception {
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

	private void doMethods(ParserCallback cb) throws Exception {
		int methodsCount = in.readUnsignedShort();
		for (int i = 0; i < methodsCount; i++) {
			int access = in.readUnsignedShort();
			String name = getUtf8();
			String descriptor = getUtf8();

			Map<String, Object> map = doAttributes(cb);
			cb.doMethod(access, name, descriptor, (String[]) map
					.get("Exceptions"));
		}
	}

	private void doFields(ParserCallback cb) throws Exception {
		int fieldsCount = in.readUnsignedShort();
		for (int i = 0; i < fieldsCount; i++) {
			int access = in.readUnsignedShort();
			String fieldName = getUtf8();
			String fieldDescriptor = getUtf8();
			Map<String, Object> map = doAttributes(cb);
			cb.doField(access, fieldName, fieldDescriptor, map
					.get("ConstantValue"));
		}
	}

	private void readConstantPool() throws Exception {
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

	private String getClassName() throws Exception {
		int n = in.readUnsignedShort();
		return constPool[n].getClassName();
	}

	private String getUtf8() throws Exception {
		return constPool[in.readUnsignedShort()].getUtf8();
	}

	private Object getValue() throws Exception {
		return constPool[in.readUnsignedShort()].getValue();
	}

	private Map<String, Object> doAttributes(ParserCallback cb)
			throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
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
