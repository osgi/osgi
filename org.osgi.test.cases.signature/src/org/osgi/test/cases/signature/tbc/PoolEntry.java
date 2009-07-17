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

package org.osgi.test.cases.signature.tbc;

import java.io.*;

public class PoolEntry {
	public final static int		CONSTANT_Utf8				= 1;
	public final static short	CONSTANT_Integer			= 3;
	public final static short	CONSTANT_Float				= 4;
	public final static short	CONSTANT_Long				= 5;
	public final static short	CONSTANT_Double				= 6;
	public final static short	CONSTANT_Class				= 7;
	public final static short	CONSTANT_String				= 8;
	public final static short	CONSTANT_FieldRef			= 9;
	public final static short	CONSTANT_MethodRef			= 10;
	public final static short	CONSTANT_InterfaceMethodRef	= 11;
	public final static short	CONSTANT_NameAndType		= 12;

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
				o = new Integer(in.readInt());
				break;

			case CONSTANT_Float : // Float
				o = new Float(in.readFloat());
				break;

			case CONSTANT_Long : // Long
				o = new Long(in.readLong());
				break;

			case CONSTANT_Double : // Double
				o = new Double(in.readDouble());
				break;

			case CONSTANT_Class : // Class
				a = in.readUnsignedShort();
				break;

			case CONSTANT_String : // String
				a = in.readUnsignedShort();
				break;

			case CONSTANT_FieldRef : // Field Ref
			case CONSTANT_MethodRef : // Method Ref
			case CONSTANT_InterfaceMethodRef : // Interface Method Ref
				a = in.readUnsignedShort(); // class index
				b = in.readUnsignedShort(); // name + type index
				break;

			case CONSTANT_NameAndType : // Name and Type
				a = in.readUnsignedShort();
				b = in.readUnsignedShort();
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
		"NAMEANDTYPE<12>        "
	};
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
				return NAMES[tag] + a;

			case CONSTANT_FieldRef : // Field Ref
			case CONSTANT_MethodRef : // Method Ref
			case CONSTANT_InterfaceMethodRef : // Interface Method Ref
				return NAMES[tag] + a + "," + b + ": " + pool[a].toString() + " : " + pool[b].toString();

			case CONSTANT_NameAndType : // Name and Type
				return NAMES[tag] + a + "," + b + ": " + pool[a].toString() + " : " + pool[b].toString();
		}
		return "?";
	}
}
