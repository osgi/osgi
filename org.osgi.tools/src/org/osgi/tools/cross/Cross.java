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

package org.osgi.tools.cross;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.osgi.test.script.Tag;

public class Cross {
	static Map			interfacesAPI	= new HashMap();
	static Map			methodsAPI		= new HashMap();
	static String		pattern			= "";
	static PrintStream	out				= System.out;
	static Tag			result			= new Tag("testmatrix");
	static boolean		xml				= true;

	public static void main(String args[]) throws IOException, Exception {
		System.err.println("Cross Reference Test Cases | OSGi v1.0");
		System.err.flush();
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (args[i].equals("-pattern")) {
					pattern = args[++i];
					result.addContent(new Tag("pattern", pattern));
				}
				else if (args[i].equals("-o")) {
					out = new PrintStream(new FileOutputStream(args[++i]));
					if (args[i].endsWith(".html"))
						xml = false;
				}
				else if (args[i].equals("-target")) {
					result.addContent(new Tag("target", new File(args[i + 1])
							.getName()));
					// Is a JAR file that contains the spec.
					// The API is the set of public and protected
					// methods. Multiple jars can be specified
					traverse(args[++i], new API(methodsAPI, interfacesAPI,
							pattern));
				}
				else {
					System.err.println("Unknown option " + args[i]);
					System.err.println("cross [-pattern prefix] jar ...");
				}
			}
			else {
				result
						.addContent(new Tag("input", new File(args[i])
								.getName()));
				traverse(args[i], new XRef(methodsAPI, interfacesAPI));
			}
		}

		String pckge = "";
		String clazz = "";
		Tag pckgeTag = null;
		Tag clazzTag = null;

		for (Iterator it = new TreeSet(methodsAPI.keySet()).iterator(); it
				.hasNext();) {
			Method method = (Method) it.next();
			if (!pckge.equals(method.pckge)) {
				pckge = method.pckge;
				pckgeTag = new Tag("package");
				pckgeTag.addAttribute("name", method.pckge);
				if (method.jar != null)
					pckgeTag.addAttribute("jar", method.jar);
				result.addContent(pckgeTag);
			}
			if (!clazz.equals(method.clazz)) {
				clazz = method.clazz;
				clazzTag = new Tag("class");
				clazzTag.addAttribute("name", method.clazz
						.substring(method.clazz.lastIndexOf('.') + 1));
				pckgeTag.addContent(clazzTag);
			}

			Tag methodTag = makeMethodTag(method);
			clazzTag.addContent(methodTag);
			setCollectionTag(methodTag, "callers", (Collection) methodsAPI
					.get(method));
			setCollectionTag(
					methodTag,
					"implementers",
					(Collection) interfacesAPI.get(method));
		}

		if (!xml) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(bout));
			result.print(0, pw);
			pw.close();
			// TODO use templates and cache these, this
			// is horribly inefficient.
			ByteArrayInputStream bin = new ByteArrayInputStream(bout
					.toByteArray());
			Source data = new StreamSource(bin);
			URL url = Cross.class.getResource("testmatrix.xsl");
			Source style = new StreamSource(url.openStream());
			Result output = new StreamResult(out);
			// create Transformer and perform the tranfomation
			TransformerFactory fact = /* net.sf.saxon. */TransformerFactory
					/* Impl */.newInstance();
			fact.setURIResolver(new URIResolver() {
				public Source resolve(String href, String base)
						throws TransformerException {
					// TODO Auto-generated method stub
					return null;
				}
			});
			Transformer xslt = fact.newTransformer(style);
			xslt.transform(data, output);
			out.close();
		}
		else {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
			pw.println("<?xml version='1.0'?>");
			pw
			.println("<?xml-stylesheet type='text/xsl' title='Matrix' href='https://www.osgi.org/www/testmatrix.xsl'?>");
			result.print(0, pw);
			pw.flush();
			out.close();
		}
	}

	static void setCollectionTag(Tag parent, String name, Collection set) {
		if (set == null || set.isEmpty())
			return;
		Tag holder = new Tag(name);
		parent.addContent(holder);
		for (Iterator c = set.iterator(); c.hasNext();) {
			Method method = (Method) c.next();
			holder.addContent(makeMethodTag(method));
		}
	}

	static Tag makeMethodTag(Method method) {
		Tag tag = new Tag("method");
		tag.addAttribute("pckge", method.pckge);
		tag.addAttribute("class", method.clazz);
		tag.addAttribute("name", method.name);
		tag.addAttribute("proto", method.proto);
		if (method.jar != null)
			tag.addAttribute("jar", method.jar);

		StringBuffer sb = new StringBuffer();
		sb.append(doSignature(Type.getReturnType(method.proto).toString()));
		sb.append(" ");
		sb.append(method.name);
		sb.append("(");
		Type[] types = Type.getArgumentTypes(method.proto);
		String del = "";
		for (int i = 0; i < types.length; i++) {
			sb.append(del);
			del = ", ";
			sb.append(doSignature(types[i].toString()));

		}
		sb.append(")");

		tag.addAttribute("pretty", sb.toString());
		return tag;
	}

	static String doSignature(String signature) {
		StringBuffer sb = new StringBuffer();
		int dimension = 0;

		while (signature.charAt(dimension) == '[') {
			dimension++;
		}

		char c = signature.charAt(dimension);
		switch (c) {
			case '[' :
				dimension++;
				break;

			case 'L' :
				int end = signature.indexOf(';', dimension);
				String type = signature.substring(dimension + 1, end);
				sb.append(mkShort(type));
				break;

			case 'B' :
				sb.append("byte");
				break;

			case 'C' :
				sb.append("char");
				break;

			case 'D' :
				sb.append("double");
				break;

			case 'F' :
				sb.append("float");
				break;

			case 'I' :
				sb.append("int");
				break;

			case 'J' :
				sb.append("long");
				break;

			case 'S' :
				sb.append("short");
				break;

			case 'V' :
				sb.append("void");
				break;

			case 'Z' :
				sb.append("boolean");
				break;

			default :
				throw new IllegalArgumentException("Unknown type: " + c
						+ " in '" + signature + "' " + "  "
						+ signature.substring(dimension - 1));
		}
		while (dimension-- > 0)
			sb.append("[]");

		return sb.toString();
	}

	static String mkShort(String name) {
		name = name.replace('/', '.');
		int n = name.lastIndexOf('.');
		if (name.startsWith("java.lang.")
				|| name.startsWith("org.osgi.service")
				|| name.startsWith("org.osgi.framework")
				|| name.startsWith("info.dmtree")) {
			return name.substring(n + 1);
		}
		else
			return name;
	}

	static void traverse(String path, DefaultAdapter cv)
			throws FileNotFoundException, IOException {
		File file = new File(path);
		if (!file.exists()) {
			System.err.println("JAR does not exist: " + file.getAbsolutePath());
		}
		else {
			InputStream fin = new FileInputStream(file);
			traverse(file.getName(), fin, cv);
		}
	}

	static void traverse(String name, InputStream fin, DefaultAdapter cv)
			throws IOException {
		ZipInputStream in = new ZipInputStream(fin);
		ZipEntry entry = in.getNextEntry();
		while (entry != null) {
			if (entry.getName().endsWith(".class")) {
				ClassReader rdr = new ClassReader(in);
				cv.currentJar = name;
				rdr.accept(cv, false);
			}
			else if (entry.getName().endsWith(".jar")) {
				traverse(name + " : " + entry.getName(), in, cv);
			}
			entry = in.getNextEntry();
		}
	}
}
