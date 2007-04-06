/*
 * $Id$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.tools.refers;

import java.io.*;
import java.util.*;

public class Refers {
	SortedSet		list = new TreeSet();
	
	static byte	SkipTable[]	= {0, // 0 non existent
							-1, // 1 CONSTANT_utf8 UTF 8, handled in
							// method
							-1, // 2
							4, // 3 CONSTANT_Integer
							4, // 4 CONSTANT_Float
							8, // 5 CONSTANT_Long (index +=2!)
							8, // 6 CONSTANT_Double (index +=2!)
							-1, // 7 CONSTANT_Class
							2, // 8 CONSTANT_String
							4, // 9 CONSTANT_FieldRef
							4, // 10 CONSTANT_MethodRef
							4, // 11 CONSTANT_InterfaceMethodRef
							4 // 12 CONSTANT_NameAndType
							};

	void collect(String filename) throws Exception {
		FileInputStream in = new FileInputStream(filename);
		Collection c = parseClassFile(new DataInputStream(in));
		list.addAll(c);
	}
	
	void print() {
		for ( Iterator i = list.iterator(); i.hasNext(); ) {
			System.out.println( i.next() );
		}
	}

	/**
	 * Parse a class file and find the other classes that are referenced.
	 * 
	 * This function is recursively called for each class and all the classes it
	 * refers to. The format of the class file is described in the "Java Virtual
	 * Machine Specification".
	 * 
	 * Notice that CONSTANT_Long and CONSTANT_Double use TWO (2) positions in
	 * the constant pool. This took me a couple of hours to figure out.
	 */
	Collection parseClassFile(DataInputStream in) throws IOException {
		Vector classes = new Vector();
		Hashtable pool = new Hashtable();
		try {
			int magic = in.readInt();
			if (magic != 0xCAFEBABE)
				throw new IOException(
						"Not a valid class file (no CAFEBABE header)");
			in.readShort(); // minor version
			in.readShort(); // major version
			int count = in.readUnsignedShort();
			process: for (int i = 1; i < count; i++) {
				byte tag = in.readByte();
				switch (tag) {
					case 0 :
						break process;
					case 1 :
						// CONSTANT_Utf8
						String name = in.readUTF();
						pool.put(new Integer(i), name);
						break;
					// A Class constant is just a short reference in
					// the constant pool
					case 7 :
						// CONSTANT_Class
						Integer index = new Integer(in.readShort());
						classes.add(index);
						break;
					// For some insane optimization reason are
					// the long and the double two entries in the
					// constant pool. See 4.4.5
					case 5 :
					// CONSTANT_Long
					case 6 :
						// CONSTANT_Double
						in.skipBytes(8);
						i++;
						break;
					// We get the skip count for each record type
					// from the SkipTable. This will also automatically
					// abort when
					default :
						if (tag == 2)
							throw new IOException("Invalid tag " + tag);
						in.skipBytes(SkipTable[tag]);
						break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		//
		// Now iterate over all classes we found and
		// parse those as well. We skip duplicates
		//
		Collection set = new HashSet();
		for (Enumeration e = classes.elements(); e.hasMoreElements();) {
			Integer n = (Integer) e.nextElement();
			String next = (String) pool.get(n);
			if (next != null) {
				String normalized = normalize(next);
				if (normalized != null) {
					set.add(normalized);
				}
			}
			else
				throw new IllegalArgumentException("Invalid class, parent=");
		}
		return set;
	}

	/**
	 * Decode a class name from a class reference.
	 * 
	 * Java encodes array information in the name of the class. This means that
	 * we should get rid of this. The following rules apply:
	 * 
	 * <pre>
	 *   
	 *    
	 *     
	 *      
	 *       
	 *        
	 *         
	 *          
	 *           
	 *            
	 *             
	 *              
	 *                     	 [L*               =&gt; remove [L, try again
	 *                     	 [* &amp;&amp; length=2     =&gt; ignore (int,byte,char etc class)
	 *                     	 [*                =&gt; remove [, try again
	 *                     ;                =&gt; remove ;, try again (do not know why)
	 *                     	 A i in skip | &lt;i&gt;* =&gt; ignore
	 *               
	 *              
	 *             
	 *            
	 *           
	 *          
	 *         
	 *        
	 *       
	 *      
	 *     
	 *    
	 * </pre>
	 * 
	 * Notice that we ALWAYS suffix the name with class. E.g. normalize cannot
	 * be used for normal resource names.
	 */
	String normalize(String s) {
		if (s.startsWith("[L"))
			return normalize(s.substring(2));
		if (s.startsWith("["))
			if (s.length() == 2)
				return null;
			else
				return normalize(s.substring(1));
		if (s.endsWith(";"))
			return normalize(s.substring(0, s.length() - 1));
		return s + ".class";
	}



	public static void main(String[] args) throws Exception {
		Refers refers = new Refers();
		for (int i = 0; i < args.length; i++) {
			refers.collect(args[i]);
		}
		refers.print();
	}

}
