/*
 * Copyright (c) OSGi Alliance (2004, 2015). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.tools.xmldoclet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

@SuppressWarnings("javadoc")
public class XmlDoclet extends Doclet {
	Pattern			SECURITY_PATTERN	= Pattern
			.compile("(\\w+)\\[(.+),(\\w+)\\](.*)");
	PrintWriter		pw;
	String			currentPackage;
	String			currentClass;
	final RootDoc	root;

	public static boolean start(RootDoc doc) {
		try {
			XmlDoclet doclet = new XmlDoclet(doc);
			doclet.start();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}

	private XmlDoclet(RootDoc root) {
		this.root = root;
	}

	public void start() throws Exception {
		File file = new File(getDestDir(), "javadoc.xml");
		FileOutputStream out = new FileOutputStream(file);
		pw = new PrintWriter(new OutputStreamWriter(out, "utf-8"));

		pw.println("<?xml version='1.0' encoding='utf-8'?>");
		pw.println("<top>");

		Set<PackageDoc> packages = new TreeSet<PackageDoc>();
		PackageDoc specifiedPackages[] = root.specifiedPackages();
		for (int p = 0; specifiedPackages != null && p < specifiedPackages.length; p++) {
			packages.add(specifiedPackages[p]);
		}

		ClassDoc specifiedClasses[] = root.specifiedClasses();
		for (int c = 0; specifiedClasses != null && c < specifiedClasses.length; c++) {
			packages.add(specifiedClasses[c].containingPackage());
		}

		for (PackageDoc packageDoc : packages) {
			print(packageDoc);
		}

		pw.println("</top>");
		pw.close();
		out.close();
		System.out.println("Generated file: " + file);
	}

	File getDestDir() {
		String[][] options = root.options();
		for (int i = 0; i < options.length; i++) {
			if (options[i][0].equals("-d")) {
				return new File(options[i][1]);
			}
		}
		return null;
	}

	void print(PackageDoc pack) {
		currentPackage = pack.name();
		pw.println("  <package name='" + pack.name() + "' fqn='" + pack.name()
				+ "' qn='" + pack.name() + "'>");

		printAnnotations(pack.annotations());
		printComment(pack);
		ClassDoc classes[] = pack.allClasses();
		for (int c = 0; classes != null && c < classes.length; c++)
			print(classes[c]);
		pw.println("  </package>");
	}

	enum CType {
		CLASS,
		INTERFACE,
		ENUM,
		ANNOTATION
	}

	void print(ClassDoc clazz) {
		currentClass = clazz.name();
		String name = simplify(clazz.name());
		String superclass = null;
		CType ctype = CType.CLASS;

		// interface do not have a superclass and cannot implement
		// other interfaces. So an interface can have at most 1 implements
		// record, which should be interpreted as the
		if (clazz.superclass() != null)
			superclass = printType(clazz.superclassType());

		if (clazz.isAnnotationType())
			ctype = CType.ANNOTATION;
		else if (clazz.isEnum())
			ctype = CType.ENUM;
		else if (clazz.isInterface())
			ctype = CType.INTERFACE;

		if (clazz.isInterface() && clazz.interfaces().length > 0) {
			superclass = printType(clazz.interfaceTypes()[0]);
		}

		StringBuilder generics = new StringBuilder();
		print(generics, clazz.typeParameters(), 0);

		pw.println("  <class name='" + name /**/
				+ "' fqn='" + clazz.qualifiedName() /**/
				+ "' qn='" + clazz.name() /**/
				+ "' package='" + clazz.containingPackage().name() /**/
				+ "' typeParam='" + generics /**/
				+ "' modifiers='" + clazz.modifiers() /**/
				+ (clazz.isClass() ? " class" : "")
				+ (superclass != null ? "' superclass='" + superclass : "")
				+ (clazz.isInterface() ? "' interface='yes" : "") + "' kind='"
				+ ctype + "'>");

		printAnnotations(clazz.annotations());

		// printTypeTags(clazz.typeParamTags());

		printComment(clazz);

		ParamTag[] parameters = clazz.typeParamTags();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parameters.length; i++) {
			printParamTag(sb, parameters[i]);
		}

		ClassDoc interfaces[] = clazz.interfaces();
		Type[] types = clazz.interfaceTypes();
		for (int i = 0; i < interfaces.length; i++) {
			pw.println("<implements name='" + printType(interfaces[i])
					+ "' fqn='" + interfaces[i].qualifiedName()
					+ "' qn='" + printType(types[i]) + "' package='"
					+ interfaces[i].containingPackage().name()
					+ "' local='" + escape(clazz.toString())
					+ "'/>");
		}

		boolean doDDF = DDFNode.isDDF(clazz);

		if (ctype == CType.ENUM)
			for (FieldDoc field : clazz.enumConstants()) {
				print(field);
			}

		if (ctype == CType.CLASS || ctype == CType.ENUM) {
			ConstructorDoc constructors[] = clazz.constructors();
			for (int cnst = 0; cnst < constructors.length; cnst++)
				print(constructors[cnst]);
		}

		if (ctype == CType.ANNOTATION) {
			AnnotationTypeDoc annotation = (AnnotationTypeDoc) clazz;
			if (annotation != null)
				for (AnnotationTypeElementDoc element : annotation.elements()) {
					print(element, false, element.defaultValue());
				}

		}

		MethodDoc methods[] = clazz.methods();
		for (int m = 0; m < methods.length; m++)
			print(methods[m], doDDF, null);

		FieldDoc fields[] = clazz.fields();
		for (int f = 0; f < fields.length; f++)
			print(fields[f]);

		pw.println("  </class>");
	}

	/**
	 * Print the annotations
	 * 
	 * @param annotations
	 */
	private void printAnnotations(AnnotationDesc[] annotations) {
		for (AnnotationDesc annotation : annotations) {
			printAnnotation(annotation);
		}
	}

	/**
	 * Print an annotation.
	 * 
	 * @param annotation
	 */
	private void printAnnotation(AnnotationDesc annotation) {
		AnnotationTypeDoc annotationType = annotation.annotationType();
		pw.print("<" + annotationType.qualifiedName() + ">");

		for (AnnotationDesc.ElementValuePair pair : annotation.elementValues()) {
			AnnotationTypeElementDoc element = pair.element();
			pw.print("<" + element.name() + ">");
			AnnotationValue value = pair.value();
			Object o = value.value();
			if ("org.osgi.annotation.versioning.Version".equals(annotationType.qualifiedName())) {
				// If the @Version annotation, check it is a valid version
				// and only use the major and minor version number in the spec.
				Version v = new Version((String) o);
				pw.print(v.toSpecificationString());
			} else {
				printAnnotationValue(o);
			}
			pw.println("</" + element.name() + ">");
		}
		pw.println("</" + annotationType.qualifiedName() + ">");
	}

	// Can be
	// primitive wrapper
	// String
	// TypeDoc (.class)
	// FieldDoc (enum)
	// AnnotationDesc (annotation ref)
	// AnnotationValue[] (assume an array?)

	private void printAnnotationValue(Object o) {
		Class<?> c = o.getClass();
		if (c == Boolean.class || c == Byte.class || c == Short.class
				|| c == Character.class || c == Integer.class
				|| c == Long.class || c == Float.class || c == Double.class) {
			pw.print(o);
		} else if (c == String.class) {
			pw.print(escape((String) o));
		} else if (o instanceof ClassDoc) {
			ClassDoc cd = (ClassDoc) o;
			pw.print(cd.qualifiedName());
		} else if (o instanceof FieldDoc) {
			FieldDoc fd = (FieldDoc) o;
			pw.print(fd.name());
		} else if (o instanceof AnnotationDesc) {
			printAnnotation((AnnotationDesc) o);
		} else if (o instanceof AnnotationValue[]) {
			for (AnnotationValue av : (AnnotationValue[]) o) {
				pw.print("<value>");
				printAnnotationValue(av.value());
				pw.print("</value>");
			}
		} else
			System.err
					.println("Unexpected type in annotation: "
							+ c);

	}

	void print(StringBuilder sb, Type t, int level) {
		if (t == null) {
			sb.append("null");
			return;
		}

		TypeVariable var = t.asTypeVariable();
		if (var != null) {
			sb.append(var.typeName());
			Type bounds[] = var.bounds();
			if (level == 0) {
				String del = " extends ";
				for (Type x : bounds) {
					sb.append(del);
					print(sb, x, level + 1);
					del = " & ";
				}
			}
			return;
		}
		ParameterizedType ptype = t.asParameterizedType();
		if (ptype != null) {
			sb.append(ptype.typeName());
			print(sb, ptype.typeArguments(), level + 1);
			return;
		}
		WildcardType wc = t.asWildcardType();
		if (wc != null) {
			Type extend[] = wc.extendsBounds();
			Type zuper[] = wc.superBounds();
			Type print[] = new Type[0];
			sb.append("?");
			String del = "";
			if (extend != null) {
				del = " extends ";
				print = extend;
			} else if (zuper != null) {
				del = " super ";
				print = zuper;
			}
			for (Type x : print) {
				sb.append(del);
				print(sb, x, level + 1);
				del = " & ";
			}
			return;
		}
		sb.append(t.typeName());
	}

	private void print(StringBuilder sb, Type ptype[], int level) {
		String del = "&lt;";
		for (Type arg : ptype) {
			sb.append(del);
			del = ",";
			print(sb, arg, level);
		}
		if (del != "&lt;")
			sb.append("&gt;");
	}

	String printType(Type type) {
		StringBuilder sb = new StringBuilder();
		print(sb, type, 0);
		return sb.toString();
	}

	String printType(Type type, int level) {
		StringBuilder sb = new StringBuilder();
		print(sb, type, level);
		return sb.toString();
	}

	void print(ConstructorDoc ctor) {
		StringBuilder typeArgs = new StringBuilder();
		print(typeArgs, ctor.typeParameters(), 0);
		pw.println("    <method name='"
				+ ctor.name() //
				+ "' fqn='"
				+ escape(ctor.qualifiedName()) //
				+ "' qn='"
				+ ctor.containingClass().name()
				+ "."
				+ ctor.name()
				+ escape(flatten(ctor.signature())) //
				+ "' package='"
				+ ctor.containingPackage().name()
				+ "' typeArgs='"
				+ typeArgs //
				+ "' modifiers='"
				+ ctor.modifiers() //
				+ "' signature='" + escape(ctor.signature())
				+ "' flatSignature='" + escape(flatten(ctor.signature()))
				+ "' isConstructor='true'>");

		printAnnotations(ctor.annotations());
		printMember(ctor);
		pw.println("     </method>");
	}

	static Pattern	DDF_SCOPE		= Pattern
			.compile("@org.osgi.dmt.ddf.Scope\\(org.osgi.dmt.ddf.Scope.SCOPE.(.+)\\)");
	static Pattern	DDF_NODETYPE	= Pattern
			.compile("@org.osgi.dmt.ddf.NodeType\\(\"(.+)\"\\)");

	void print(MethodDoc method, boolean doDDF, Object deflt) {
		String dimension = method.returnType().dimension();
		StringBuilder typeArgs = new StringBuilder();
		print(typeArgs, method.typeParameters(), 0);

		pw.println("    <method name='"
				+ method.name()
				+ "' fqn='"
				+ method.qualifiedName()
				+ "' qn='"
				+ method.containingClass().name()
				+ "."
				+ method.name()
				+ escape(flatten(method.signature()))
				+ "' package='"
				+ method.containingPackage().name()
				+ "' modifiers='"
				+ method.modifiers()
				+ "' typeName='"
				+ printType(method.returnType())
				+ "' qualifiedTypeName='"
				+ escape(method.returnType().qualifiedTypeName())
				+ "' typeArgs='"
				+ typeArgs
				+ "' dimension='"
				+ dimension
				+ "' signature='"
				+ escape(method.signature())
				+ "' flatSignature='"
				+ escape(flatten(method.signature()))
				+ (deflt == null ? "" : "' default='"
						+ simplify(deflt.toString()) + "' longDefault='"
						+ deflt)
				+ "'>");
		printAnnotations(method.annotations());
		printMember(method);

		if (doDDF) {
			DDFNode child = new DDFNode(null, method.name(), method.returnType()
					.toString());
			for (AnnotationDesc ad : method.annotations()) {
				String pattern = ad.toString();
				Matcher m = DDF_SCOPE.matcher(pattern);
				if (m.matches()) {
					child.scope = m.group(1);
					continue;
				}
				m = DDF_NODETYPE.matcher(pattern);
				if (m.matches()) {
					child.mime = m.group(1);
					continue;
				}
			}
			child.print(pw, "");
		}
		pw.println("     </method>");
	}

	void print(FieldDoc field) {
		String dimension = field.type().dimension();
		String constantValueExpression = field.constantValueExpression();

		pw.println("    <field name='"
				+ field.name()
				+ "' fqn='"
				+ field.qualifiedName()
				+ "' qn='"
				+ field.containingClass().name()
				+ "."
				+ field.name()
				+ "' package='"
				+ field.containingPackage().name()
				+ "' modifiers='"
				+ field.modifiers()
				+ "' typeName='"
				+ printType(field.type())
				+ "' qualifiedTypeName='"
				+ escape(field.type().qualifiedTypeName())
				+ "' dimension='"
				+ dimension
				+ (constantValueExpression != null ? "' constantValue='"
						+ escape(constantValueExpression) : "")
				+ "'>");
		printAnnotations(field.annotations());
		printComment(field);
		pw.println("     </field>");
	}

	void printMember(ExecutableMemberDoc x) {
		printComment(x);

		Parameter parameters[] = x.parameters();
		for (int i = 0; i < parameters.length; i++) {
			print(parameters[i], x.isVarArgs() && i == parameters.length - 1);
		}
		ClassDoc exceptions[] = x.thrownExceptions();
		for (int i = 0; i < exceptions.length; i++) {
			pw.println("<exception name='" + exceptions[i].name() + "' fqn='"
					+ exceptions[i].qualifiedName() + "'/>");
		}
	}

	void print(Parameter param, boolean vararg) {
		String dimension = vararg ? " ..." : param.type().dimension();

		pw.println("    <parameter name='" + param.name() + "' dimension='"
				+ dimension + "' typeName='" + printType(param.type(), 1)
				+ "' fqn='" + escape(param.type().qualifiedTypeName())
				+ "' varargs='" + vararg + "'/>");
	}

	void printThrows(StringBuilder sb, ThrowsTag tag) {
		sb.append("   <throws name='"
				+ simplify(tag.exceptionName())
				+ (tag.exception() != null ? "' exception='"
						+ tag.exception().qualifiedName() : "")
				+ "'>");
		sb.append(html(toString(tag.inlineTags())));
		sb.append("   </throws>");
	}

	void printParamTag(StringBuilder sb, ParamTag tag) {
		String name = tag.parameterName();

		if (tag.isTypeParameter())
			name = "&lt;" + name + "&gt;";

		String text = toString(tag.inlineTags()).trim();
		if (text.length() == 0)
			sb.append("   <param name='" + name + "'/>");
		else {
			sb.append("   <param name='" + name + "'>");
			sb.append(html(text));
			sb.append("</param>\n");
		}
	}

	void printSee(StringBuilder sb, SeeTag tag) {
		String ref = "";
		String file = "";
		String text = tag.text().trim();
		// String text = tag.label().trim();

		if (tag.referencedMember() != null) {
			file = tag.referencedMember().containingPackage().name();
			String signature = "";
			if (tag.referencedMember() instanceof ExecutableMemberDoc) {
				ExecutableMemberDoc member = (ExecutableMemberDoc) tag
						.referencedMember();
				signature = flatten(member.signature());
			}
			ref = tag.referencedMember().containingClass().name() + "."
					+ tag.referencedMember().name() + signature;
		} else if (tag.referencedClass() != null) {
			file = tag.referencedClass().containingPackage().name();
			ref = tag.referencedClass().name();
		} else if (tag.referencedPackage() != null) {
			file = tag.referencedPackage().name();
			ref = tag.referencedPackage().name();
		} else
			ref = "UNKNOWN REF " + tag;

		if (currentPackage.equals(file))
			file = "";

		if (text.startsWith("\"")) {
			sb.append("<a>");
			sb.append(text.substring(1, text.length() - 1));
			sb.append("</a>");
		} else if (text.trim().startsWith("<")) {
			sb.append(text);
		} else {
			sb.append("<a href='" + file + "#" + ref + "'>");
			// Check if we use the label (if there is one) or
			// convert the text part to something readable.
			if (tag.label().trim().length() > 0)
				sb.append(tag.label());
			else
				sb.append(makeName(text));
			sb.append("</a>");
		}
	}

	String makeName(String name) {
		if (name.startsWith("#"))
			return name.substring(1);

		int n = name.indexOf("#");
		if (n > 0)
			return name.substring(0, n) + "." + name.substring(n + 1);
		else
			return name;
	}

	void print(StringBuilder sb, Tag tags[]) {
		for (int i = 0; i < tags.length; i++) {
			printX(sb, tags[i]);
		}
	}

	String toString(Tag tags[]) {
		StringBuilder sb = new StringBuilder();
		print(sb, tags);
		return sb.toString().trim();
	}

	void printComment(Doc doc) {
		Tag[] lead = doc.firstSentenceTags();
		String text = toString(lead).trim();
		if (text.length() != 0) {
			pw.println("   <lead>");
			pw.println(html(text));
			pw.println("   </lead>");
		} else
			pw.println("   <lead/>");

		text = toString(doc.inlineTags()).trim();
		if (text.length() != 0) {
			pw.println("   <description>");
			pw.println(html(text));
			pw.println("   </description>");
		} else
			pw.println("   <description/>");

		Tag tags[] = doc.tags();
		pw.println(toString(tags));
	}

	String html(String text) {
		HtmlCleaner cleaner = new HtmlCleaner(currentPackage + "."
				+ currentClass, text);
		return cleaner.clean();
	}

	void printX(StringBuilder sb, Tag tag) {
		if (tag.kind().equals("Text")) {
			sb.append(tag.text());
		} else {
			if (tag instanceof ParamTag)
				printParamTag(sb, (ParamTag) tag);
			else if (tag instanceof ThrowsTag)
				printThrows(sb, (ThrowsTag) tag);
			else if (tag instanceof SeeTag)
				printSee(sb, (SeeTag) tag);
			else {
				if (tag.kind().equals("@literal")) {
					sb.append(escape(toString(tag.inlineTags())));
				} else if (tag.kind().equals("@code")) {
					sb.append("<code>");
					sb.append(escape(toString(tag.inlineTags())));
					sb.append("</code>");
				} else if (tag.kind().equals("@value")) {
					FieldDoc field = getReferredField(tag);
					if (field != null) {
						sb.append(escape(field.constantValueExpression()));
					} else
						root.printError("No value for "
								+ tag.text());
				} else if (tag.kind().equals("@security")) {
					StringBuilder sb2 = new StringBuilder();
					print(sb2, tag.inlineTags());
					for (int i = 0; i < sb2.length(); i++)
						if (sb2.charAt(i) == '\n'
								|| sb2.charAt(i) == '\r')
							sb2.setCharAt(i, ' ');
					String s = sb2.toString();

					Matcher m = SECURITY_PATTERN.matcher(s);
					if (m.matches()) {
						String permission = m.group(1);
						String resource = m.group(2);
						String actions = m.group(3);
						String remainder = m.group(4);

						sb.append("\n<security name='");
						sb.append(escape(permission));
						sb.append("' resource='");
						sb.append(escape(resource));
						sb.append("' actions='");
						sb.append(escape(actions));
						sb.append("'>");
						sb.append(remainder);
						sb.append("</security>");
					} else
						throw new IllegalArgumentException(
								"@security tag invalid: '"
										+ s
										+ "', matching pattern is "
										+ SECURITY_PATTERN
										+ " " + m);
				} else if (tag.name().equals("@inheritDoc")) {
					Doc holder = tag.holder();
					if (holder instanceof MethodDoc) {

						MethodDoc method = (MethodDoc) holder;
						MethodDoc zuper = method
								.overriddenMethod(); // works only for classes
						if (zuper == null) {
							ClassDoc clazz = method.containingClass();
							outer: for (ClassDoc interf : clazz.interfaces()) {
								for (MethodDoc md : interf.methods()) {
									if (method.overrides(md)) {
										zuper = md;
										break outer;
									}
								}
							}
						}
						if (zuper != null && zuper != method) {
							String text = toString(zuper.inlineTags()).trim();
							if (text.length() != 0) {
								sb.append(html(text));
							}
						}
					} else {
						sb.append("<inheritDoc/>");
					}
				} else if (tag.kind().equals("@version")) {
					sb.append("<version>");
					Version v = new Version(toString(tag.inlineTags()));
					sb.append(v.toSpecificationString());
					sb.append("</version>");
				} else {
					sb.append("<"
							+ tag.kind().substring(1)
							+ ">"
							+ html(toString(tag
									.inlineTags()))
							+ "</"
							+ tag.kind().substring(1)
							+ ">");
				}
			}
		}
	}

	/**
	 * Find a reference to a field.
	 */
	static Pattern MEMBER_REFERENCE = Pattern.compile("\\s*(.+)?#(.+)\\s*");

	private FieldDoc getReferredField(Tag value) {
		Doc holder = value.holder();

		Matcher m = MEMBER_REFERENCE.matcher(value.text());
		if (m.matches()) {
			// either ref or class#ref
			String clazz = m.group(1);
			String member = m.group(2);
			ClassDoc parent;
			if (holder instanceof ClassDoc)
				parent = (ClassDoc) holder;
			else
				parent = ((MemberDoc) holder).containingClass();

			if (clazz != null) {
				ClassDoc found = parent.findClass(clazz);
				if (found == null) {
					root.printError("Referred field value in " + parent.name()
							+ " " + clazz + " not found");
					return null;
				}
				parent = found;
			}

			for (FieldDoc field : parent.fields()) {
				if (field.name().equals(member))
					return field;
			}
			return null;
		} else {
			// No reference
			if (holder instanceof FieldDoc)
				return (FieldDoc) holder;

		}
		root.printError("Referred field value in " + holder + " "
				+ value.text());
		return null;
	}

	String simplify(String name) {
		if (name.equals(currentPackage))
			return name;

		if (name.startsWith("java.") || name.startsWith("org.osgi.") || name.startsWith(currentPackage)) {
			int n;
			if (name.endsWith(".class")) {
				n = name.lastIndexOf('.', name.length() - 7);
			} else if (name.endsWith("...")) {
				n = name.lastIndexOf('.', name.length() - 4);
			} else
				n = name.lastIndexOf('.');
			name = name.substring(n + 1);
		}
		return name;
	}

	String flatten(String signature) {
		List<String> parts = new ArrayList<String>();

		int i = 1;
		int begin = i;
		outer: while (i < signature.length()) {
			switch (signature.charAt(i)) {
				case '<' :
					i = skipGenericParameters(signature, i + 1);
					break;

				case ' ' :
					begin = i + 1;
					break;

				case ',' :
					if (begin < i) {
						parts.add(stripGenericParameters(signature.substring(begin, i)));
					}
					begin = i + 1;
					break;

				case ')' :
					if (begin < i) {
						parts.add(stripGenericParameters(signature.substring(begin, i)));
					}
					break outer;
			}
			i++;
		}
		StringBuilder sb = new StringBuilder();
		String del = "";
		sb.append("(");
		for (String s : parts) {
			sb.append(del);
			sb.append(simplify(s));
			del = ",";
		}
		sb.append(")");
		return sb.toString();
	}

	String stripGenericParameters(String part) {
		int i = part.indexOf('<');
		if (i < 0) {
			return part;
		}
		int e = skipGenericParameters(part, i + 1);
		return part.substring(0, i) + part.substring(e + 1);
	}

	int skipGenericParameters(String s, int n) {
		while (n < s.length() && s.charAt(n) != '>') {
			if (s.charAt(n) == '<') {
				n = skipGenericParameters(s, n + 1);
				// n -> closing '>', so next incr. is necessary
			}
			n++;
		}
		return n;
	}

	String escape(String in) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			switch (c) {
				case '&' :
					sb.append("&amp;");
					break;
				case '<' :
					sb.append("&lt;");
					break;
				case '>' :
					sb.append("&gt;");
					break;
				default :
					sb.append(c);
			}
		}
		return sb.toString();
	}

	public static int optionLength(String option) {
		if (option.equals("-d")) {
			return 2;
		}
		return 0;
	}

	// public static boolean validOptions(String options[][],
	// DocErrorReporter reporter) {
	// for (int i = 0; i < options.length; i++) {
	// System.out.print("option " + i + ": ");
	// for (int j = 0; j < options[i].length; j++) {
	// System.out.print(options[i][j]);
	// System.out.print(" ");
	// }
	// System.out.println();
	// }
	// return true;
	// }
}
