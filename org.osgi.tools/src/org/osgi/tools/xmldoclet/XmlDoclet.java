package org.osgi.tools.xmldoclet;

import java.io.*;
import java.util.*;

import com.sun.javadoc.*;

public class XmlDoclet extends Doclet {
	PrintWriter	pw;
	String		currentPackage;

	public static boolean start(RootDoc doc) {
		try {
			XmlDoclet doclet = new XmlDoclet();
			doclet.startx(doc);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}

	public void startx(RootDoc doc) throws Exception {
		FileOutputStream out = new FileOutputStream("javadoc.xml");
		pw = new PrintWriter(new OutputStreamWriter(out, "utf-8"));

		pw.println("<?xml version='1.0' encoding='utf-8'?>");
		pw.println("<top>");

		PackageDoc packages[] = doc.specifiedPackages();
		for (int p = 0; packages != null && p < packages.length; p++)
			print(packages[p]);

		pw.println("</top>");
		pw.close();
		out.close();
	}

	void print(PackageDoc pack) {
		currentPackage = pack.name();
		pw.println("  <package name='" + pack.name() + "' fqn='" + pack.name()
				+ "' qn='" + pack.name() + "'>");
		printComment(pack);
		ClassDoc all[] = pack.allClasses();
		Hashtable<String, String> ht = new Hashtable<String, String>();
		for (int i = 0; i < all.length; i++) {
			ClassDoc c = all[i];
			PackageDoc imports[] = c.importedPackages();
			for (int j = 0; j < imports.length; j++) {
				PackageDoc p = imports[j];
				if (!p.name().equals("java.lang") && ht.get(p) == null) {
					Tag version[] = p.tags("@version");
					String v = toString(version).trim();
					ht.put(p.name(), v);
				}
			}
		}
		for (Enumeration<String> e = ht.keys(); e.hasMoreElements();) {
			String key = e.nextElement();
			String version = ht.get(key);
			pw.println("<import name='" + key
					+ (version.length() == 0 ? "" : "' version='" + version)
					+ "'/>");
		}
		ClassDoc classes[] = pack.allClasses();
		for (int c = 0; classes != null && c < classes.length; c++)
			print(classes[c]);
		pw.println("  </package>");
	}

	void print(ClassDoc clazz) {
		String name = simplify(clazz.name());
		String superclass = null;

		// interface do not have a superclass and cannot implement
		// other interfaces. So an interface can have at most 1 implements
		// record, which should be interpreted as the
		if (clazz.superclass() != null)
			superclass = printType(clazz.superclassType());

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
				+ (clazz.isInterface() ? "' interface='yes" : "") + "'>");

		// printTypeTags(clazz.typeParamTags());

		printComment(clazz);

		ParamTag[] parameters = clazz.typeParamTags();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < parameters.length; i++) {
			printParamTag(sb, parameters[i]);
		}

		// if (!clazz.isInterface()) {
		ClassDoc ptr = clazz;
		Hashtable<ClassDoc, String> ht = new Hashtable<ClassDoc, String>();
		while (ptr != null) {
			ClassDoc interfaces[] = ptr.interfaces();
			Type[] types = ptr.interfaceTypes();
			for (int i = 0; i < interfaces.length; i++) {
				if (ht.get(interfaces[i]) == null) {
					pw.println("<implements name='" + printType(interfaces[i])
							+ "' fqn='" + interfaces[i].qualifiedName()
							+ "' qn='" + printType(types[i]) + "' package='"
							+ interfaces[i].containingPackage().name()
							+ "' local='" + escape((ptr = clazz).toString())
							+ "'/>");
					ht.put(interfaces[i], "");
				}
			}
			ptr = null; // ptr.superclass();
		}
		// }
		ConstructorDoc constructors[] = clazz.constructors();
		for (int cnst = 0; cnst < constructors.length; cnst++)
			print(constructors[cnst]);

		FieldDoc fields[] = clazz.fields();
		for (int f = 0; f < fields.length; f++)
			print(fields[f]);

		MethodDoc methods[] = clazz.methods();
		for (int m = 0; m < methods.length; m++)
			print(methods[m]);

		pw.println("  </class>");
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
			}
			else
				if (zuper != null) {
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

	void print(ConstructorDoc cnst) {
		StringBuilder typeArgs = new StringBuilder();
		print(typeArgs, cnst.typeParameters(), 0);
		pw.println("    <method name='"
				+ cnst.name() //
				+ "' fqn='"
				+ escape(cnst.qualifiedName()) //
				+ "' qn='"
				+ cnst.containingClass().name()
				+ "."
				+ cnst.name()
				+ escape(flatten(cnst.signature())) //
				+ "' package='"
				+ cnst.containingPackage().name()
				+ "' typeArgs='"
				+ typeArgs //
				+ "' modifiers='"
				+ cnst.modifiers() //
				+ "' signature='" + escape(cnst.signature())
				+ "' flatSignature='" + escape(flatten(cnst.signature()))
				+ "' isConstructor='true'>");

		printMember(cnst);
		pw.println("     </method>");
	}

	void print(MethodDoc cnst) {
		String dimension = cnst.returnType().dimension();
		StringBuilder typeArgs = new StringBuilder();
		print(typeArgs, cnst.typeParameters(), 0);

		pw.println("    <method name='" + cnst.name() + "' fqn='"
				+ cnst.qualifiedName() + "' qn='"
				+ cnst.containingClass().name() + "." + cnst.name()
				+ escape(flatten(cnst.signature())) + "' package='"
				+ cnst.containingPackage().name() + "' modifiers='"
				+ cnst.modifiers() + "' typeName='"
				+ printType(cnst.returnType()) + "' qualifiedTypeName='"
				+ escape(cnst.returnType().qualifiedTypeName())
				+ "' typeArgs='" + typeArgs + "' dimension='" + dimension
				+ "' signature='" + escape(cnst.signature())
				+ "' flatSignature='" + escape(flatten(cnst.signature()))
				+ "'>");
		printMember(cnst);
		pw.println("     </method>");
	}

	void print(FieldDoc cnst) {
		String constantValueExpression = null;
		try {
			constantValueExpression = cnst.constantValueExpression();
		}
		catch (Error e) {
			// Is Java 1.4, so we accept a failure
		}

		pw.println("    <field name='"
				+ cnst.name()
				+ "' fqn='"
				+ cnst.qualifiedName()
				+ "' qn='"
				+ cnst.containingClass().name()
				+ "."
				+ cnst.name()
				+ "' package='"
				+ cnst.containingPackage().name()
				+ "' modifiers='"
				+ cnst.modifiers()
				+ "' typeName='"
				+ printType(cnst.type())
				+ "' qualifiedTypeName='"
				+ escape(cnst.type().qualifiedTypeName())
				+ (constantValueExpression != null ? "' constantValue='"
						+ escape(constantValueExpression) : "") + "'>");
		printComment(cnst);
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

	void printThrows(StringBuffer sb, ThrowsTag tag) {
		sb.append("   <throws name='"
				+ simplify(tag.exceptionName())
				+ (tag.exception() != null ? "' exception='"
						+ tag.exception().qualifiedName() : "") + "'>");
		sb.append(html(toString(tag.inlineTags())));
		sb.append("   </throws>");
	}

	void printParamTag(StringBuffer sb, ParamTag tag) {
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

	void printSee(StringBuffer sb, SeeTag tag) {
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
		}
		else
			if (tag.referencedClass() != null) {
				file = tag.referencedClass().containingPackage().name();
				ref = tag.referencedClass().name();
			}
			else
				if (tag.referencedPackage() != null) {
					file = tag.referencedPackage().name();
					ref = tag.referencedPackage().name();
				}
				else
					ref = "UNKNOWN REF " + tag;

		if (currentPackage.equals(file))
			file = "";

		if (text.startsWith("\"")) {
			sb.append("   <a>");
			sb.append(text.substring(1, text.length() - 1));
			sb.append("</a>");
		}
		else
			if (text.trim().startsWith("<")) {
				sb.append(text);
			}
			else {
				sb.append("   <a href='" + file + "#" + ref + "'>");
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

	void print(StringBuffer sb, Tag tags[]) {
		for (int i = 0; i < tags.length; i++) {
			printX(sb, tags[i]);
		}
	}

	String toString(Tag tags[]) {
		StringBuffer sb = new StringBuffer();
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
		}
		else
			pw.println("   <lead/>");

		text = toString(doc.inlineTags()).trim();
		if (text.length() != 0) {
			pw.println("   <description>");
			pw.println(html(text));
			pw.println("   </description>");
		}
		else
			pw.println("   <description/>");

		Tag tags[] = doc.tags();
		pw.println(toString(tags));
	}

	String html(String text) {
		HtmlCleaner cleaner = new HtmlCleaner(text);
		return cleaner.clean();
	}

	void printX(StringBuffer sb, Tag tag) {
		if (tag.kind().equals("Text")) {
			sb.append(tag.text());
		}
		else
			if (tag instanceof ParamTag)
				printParamTag(sb, (ParamTag) tag);
			else
				if (tag instanceof ThrowsTag)
					printThrows(sb, (ThrowsTag) tag);
				else
					if (tag instanceof SeeTag)
						printSee(sb, (SeeTag) tag);
					else
						sb.append("<" + tag.kind().substring(1) + ">"
								+ html(toString(tag.inlineTags())) + "</"
								+ tag.kind().substring(1) + ">");
	}

	String simplify(String name) {
		if (name.startsWith("java.") || name.startsWith("org.osgi.")
				|| name.startsWith(currentPackage)) {
			int n;
			if ( name.endsWith("...")) {
				n= name.lastIndexOf('.', name.length()-4);
			} else
				n= name.lastIndexOf('.');
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
					parts.add(signature.substring(begin, i));
					i = skip(signature, i + 1);
					begin=i+1;
					break;

				case ' ' :
					begin = i+1;
					break;
					
				case ',' :
					if (begin < i) {
						parts.add(signature.substring(begin, i));
					}
					begin = i + 1;
					break;

				case ')' :
					if (begin < i) {
						parts.add(signature.substring(begin, i));
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

	int skip(String s, int n) {
		while (n < s.length() && s.charAt(n) != '>') {
			if (s.charAt(n)=='<' ) 
				n = skip(s, n + 1);
			else
				n++;
		}
		return n;
	}

	String escape(String in) {
		StringBuffer sb = new StringBuffer();
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
}
