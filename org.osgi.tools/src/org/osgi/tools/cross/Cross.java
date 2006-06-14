/*
 * $Header$
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

package org.osgi.tools.cross;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import org.objectweb.asm.*;

public class Cross {
	static Map	interfacesAPI	= new HashMap();
	static Map	methodsAPI	= new HashMap();
	static String pattern = "";

	public static void main(String args[]) throws IOException {
		System.err.println("Cross Reference Test Cases | OSGi v1.0");
		System.err.flush();
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (args[i].equals("-pattern"))
					pattern = args[++i];
				else if (args[i].equals("-target")) {
					// Is a JAR file that contains the spec.
					// The API is the set of public and protected
					// methods. Multiple jars can be specified
					traverse(args[++i], new API(methodsAPI, interfacesAPI, pattern));
				}
				else {
					System.err.println("Unknown option " + args[i]);
					System.err.println("cross [-pattern prefix] jar ...");
				}
			}
			else {
				traverse(args[i], new XRef(methodsAPI));
			}
		}

		for (Iterator it = new TreeSet(methodsAPI.keySet()).iterator(); it
				.hasNext();) {
			Method name = (Method) it.next();
			System.out.print(name);
			Collection c = (Collection) methodsAPI.get(name);
			System.out.print("\t");
			if (c.isEmpty())
				System.out.print("XXX");
			else
				System.out.print(c.size());
			System.out.println();
		}
	}

	static void traverse(String path, ClassVisitor cv)
			throws FileNotFoundException, IOException {
		File file = new File(path);
		if (!file.exists()) {
			System.err.println("JAR does not exist: " + file.getAbsolutePath());
		}
		else {
			InputStream fin = new FileInputStream(file);
			traverse(fin, cv);
		}
	}

	static void traverse(InputStream fin, ClassVisitor cv) throws IOException {
		ZipInputStream in = new ZipInputStream(fin);
		ZipEntry entry = in.getNextEntry();
		while (entry != null) {
			if (entry.getName().endsWith(".class")) {
				ClassReader rdr = new ClassReader(in);
				rdr.accept(cv, false);
			}
			else if (entry.getName().endsWith(".jar")) {
				System.out.println("JAR: " + entry.getName());
				traverse(in, cv);
			}
			entry = in.getNextEntry();
		}
	}
}
