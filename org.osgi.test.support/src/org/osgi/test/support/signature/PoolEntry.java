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
import java.io.IOException;

public class PoolEntry {
	public final static int	CONSTANT_Utf8				= 1;
	public final static int	CONSTANT_Integer			= 3;
	public final static int	CONSTANT_Float				= 4;
	public final static int	CONSTANT_Long				= 5;
	public final static int	CONSTANT_Double				= 6;
	public final static int	CONSTANT_Class				= 7;
	public final static int	CONSTANT_String				= 8;
	public final static int	CONSTANT_FieldRef			= 9;
	public final static int	CONSTANT_MethodRef			= 10;
	public final static int	CONSTANT_InterfaceMethodRef	= 11;
	public final static int	CONSTANT_NameAndType		= 12;
	public final static int	CONSTANT_MethodHandle		= 15;
	public final static int	CONSTANT_MethodType			= 16;
	public final static int	CONSTANT_InvokeDynamic		= 18;

	int							tag;
	int							a;
	int							b;
	Object						o;
	PoolEntry					pool[];

	PoolEntry(PoolEntry[] cp, DataInputStream in) throws IOException {
		pool = cp;
		tag = in.readUnsignedByte();
		switch (tag) {
			case CONSTANT_Utf8 : // Utf8
				o = in.readUTF();
				break;

			case CONSTANT_Integer : // Integer
				o = Integer.valueOf(in.readInt());
				break;

			case CONSTANT_Float : // Float
				o = Float.valueOf(in.readFloat());
				break;

			case CONSTANT_Long : // Long
				o = Long.valueOf(in.readLong());
				break;

			case CONSTANT_Double : // Double
				o = Double.valueOf(in.readDouble());
				break;

			case CONSTANT_Class : // Class
				a = in.readUnsignedShort(); // name index
				break;

			case CONSTANT_String : // String
				a = in.readUnsignedShort(); // string index
				break;

			case CONSTANT_FieldRef : // Field Ref
			case CONSTANT_MethodRef : // Method Ref
			case CONSTANT_InterfaceMethodRef : // Interface Method Ref
				a = in.readUnsignedShort(); // class index
				b = in.readUnsignedShort(); // name + type index
				break;

			case CONSTANT_NameAndType : // Name and Type
				a = in.readUnsignedShort(); // name index
				b = in.readUnsignedShort(); // descriptor index
				break;
				
			case CONSTANT_MethodHandle : // Method Handle
				a = in.readUnsignedByte(); // reference kind
				b = in.readUnsignedShort(); // reference index
				break;
				
			case CONSTANT_MethodType : // Method Type
				a = in.readUnsignedShort(); // descriptor index
				break;

			case CONSTANT_InvokeDynamic : // Invoke Dynamic
				a = in.readUnsignedShort(); // bootstrap method attribute index
				b = in.readUnsignedShort(); // name and type index
				break;

			default :
				throw new IllegalArgumentException("Invalid constant pool: "
						+ tag);
		}
	}

	public String getClassName() {
		check(tag == CONSTANT_Class, "Excpected class name ref");
		return pool[a].getUtf8();
	}

	public String getString() {
		check(tag == CONSTANT_String, "Expected string");
		return pool[a].getUtf8();
	}

	public String getUtf8() {
		check(tag == CONSTANT_Utf8, "Expected string");
		return (String) o;
	}

	private void check(boolean c, String m) {
		if (!c) {
			throw new IllegalArgumentException("Invalid format: " + m);
		}
	}

	public Object getValue() {
		if ( tag == CONSTANT_String )
			return pool[a].getUtf8();
		
		check(tag == CONSTANT_Long || tag == CONSTANT_Float
				|| tag == CONSTANT_Double || tag == CONSTANT_Integer
				|| tag == CONSTANT_String, "Expected constant");
		return o;
	}

	public String getMethodName() {
		check(tag==CONSTANT_NameAndType, "Method name requires name and type");
		return pool[a].getUtf8();
	}
	public String getMethodDescriptor() {
		check(tag==CONSTANT_NameAndType, "Method descriptor requires name and type");
		return pool[b].getUtf8();
	}
	
	static String [] NAMES = {
		"<0>                    ",
		"UTF8<1>                ",
		"<2>                    ",
		"INTEGER<3>             ",
		"FLOAT<4>               ",
		"LONG<5>                ",
		"DOUBLE<6>              ",
		"CLASS<7>               ",
		"STRING<8>              ",
		"FIELDREF<9>            ",
		"METHODREF<10>          ",
		"INTERFACEMETHODREF<11> ",
							"NAMEANDTYPE<12>        ",
							"<13>                   ",
							"<14>                   ",
							"METHODHANDLE<15>       ",
							"METHODTYPE<16>         ",
							"<17>                   ",
							"INVOKEDYNAMIC<18>      "
	};
	@Override
	public String toString() {
	
		switch (tag) {
			case CONSTANT_Utf8 : // Utf8
			case CONSTANT_Integer : // Integer
			case CONSTANT_Float : // Float
			case CONSTANT_Long : // Long
			case CONSTANT_Double : // Double
				return NAMES[tag] +  o;


			case CONSTANT_String : // String
			case CONSTANT_Class : // Class
				return NAMES[tag] + a + ": " + String.valueOf(pool[a]);

			case CONSTANT_FieldRef : // Field Ref
			case CONSTANT_MethodRef : // Method Ref
			case CONSTANT_InterfaceMethodRef : // Interface Method Ref
				return NAMES[tag] + a + "," + b + ": " + String.valueOf(pool[a]) + " : " + String.valueOf(pool[b]);

			case CONSTANT_NameAndType : // Name and Type
				return NAMES[tag] + a + "," + b + ": " + String.valueOf(pool[a]) + " : " + String.valueOf(pool[b]);

			case CONSTANT_MethodHandle : // Method Handle
			case CONSTANT_InvokeDynamic : // Invoke Dynamic
				return NAMES[tag] + a + "," + b + ": " + String.valueOf(pool[b]);

			case CONSTANT_MethodType : // Method Type
				return NAMES[tag] + a + ": " + String.valueOf(pool[a]);
		}
		return "?";
	}
}
