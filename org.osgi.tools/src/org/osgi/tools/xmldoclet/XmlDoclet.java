package org.osgi.tools.xmldoclet;

import java.io.*;
import java.util.*;

import com.sun.javadoc.*;

public class XmlDoclet extends Doclet {
	PrintWriter pw;
	String currentPackage;

	public static boolean start(RootDoc doc) {
		try {
			XmlDoclet doclet = new XmlDoclet();
			doclet.startx(doc);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
		Hashtable ht = new Hashtable();
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
		for (Enumeration e = ht.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String version = (String) ht.get(key);
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
			superclass = clazz.superclass().name();

		if (clazz.isInterface() && clazz.interfaces().length > 0) {
			superclass = clazz.interfaces()[0].name();
		}

		pw.println("  <class name='" + name + "' fqn='" + clazz.qualifiedName()
				+ "' qn='" + clazz.name() + "' package='"
				+ clazz.containingPackage().name() + "' modifiers='"
				+ clazz.modifiers() + (clazz.isClass() ? " class" : "")
				+ (superclass != null ? "' superclass='" + superclass : "")
				+ (clazz.isInterface() ? "' interface='yes" : "") + "'>");
		printComment(clazz);

		if (!clazz.isInterface()) {
			ClassDoc ptr = clazz;
			Hashtable ht = new Hashtable();
			while (ptr != null) {
				ClassDoc interfaces[] = ptr.interfaces();
				for (int i = 0; i < interfaces.length; i++) {
					if (ht.get(interfaces[i]) == null) {
						pw.println("<implements name='" + interfaces[i].name()
								+ "' fqn='" + interfaces[i].qualifiedName()
								+ "' qn='" + interfaces[i].name()
								+ "' package='"
								+ interfaces[i].containingPackage().name()
								+ "' local='" + (ptr = clazz) + "'/>");
						ht.put(interfaces[i], "");
					}
				}
				ptr = null; // ptr.superclass();
			}
		}
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

	void print(ConstructorDoc cnst) {
		pw.println("    <method name='" + cnst.name() + "' fqn='"
				+ cnst.qualifiedName() + "' qn='"
				+ cnst.containingClass().name() + "." + cnst.name()
				+ flatten(cnst.signature()) + "' package='"
				+ cnst.containingPackage().name() + "' modifiers='"
				+ cnst.modifiers() + "' signature='" + cnst.signature()
				+ "' flatSignature='" + flatten(cnst.signature())
				+ "' isConstructor='true'>");
		printMember(cnst);
		pw.println("     </method>");
	}

	void print(MethodDoc cnst) {
		pw.println("    <method name='" + cnst.name() + "' fqn='"
				+ cnst.qualifiedName() + "' qn='"
				+ cnst.containingClass().name() + "." + cnst.name()
				+ flatten(cnst.signature()) + "' package='"
				+ cnst.containingPackage().name() + "' modifiers='"
				+ cnst.modifiers() + "' typeName='"
				+ cnst.returnType().typeName() + "' qualifiedTypeName='"
				+ cnst.returnType().qualifiedTypeName() + "' dimension='"
				+ cnst.returnType().dimension() + "' signature='"
				+ cnst.signature() + "' flatSignature='"
				+ flatten(cnst.signature()) + "'>");
		printMember(cnst);
		pw.println("     </method>");
	}

	void print(FieldDoc cnst) {
		String constantValueExpression = null;
		try {
			constantValueExpression = cnst.constantValueExpression();
		} catch (Error e) {
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
				+ cnst.type().typeName()
				+ "' qualifiedTypeName='"
				+ cnst.type().qualifiedTypeName()
				+ (constantValueExpression != null ? "' constantValue='"
						+ escape(constantValueExpression) : "") + "'>");
		printComment(cnst);
		pw.println("     </field>");
	}

	void printMember(ExecutableMemberDoc x) {
		printComment(x);
		Parameter parameters[] = x.parameters();
		for (int i = 0; i < parameters.length; i++) {
			print(parameters[i]);
		}
		ClassDoc exceptions[] = x.thrownExceptions();
		for (int i = 0; i < exceptions.length; i++) {
			pw.println("<exception name='" + exceptions[i].name() + "' fqn='"
					+ exceptions[i].qualifiedName() + "'/>");
		}
	}

	void print(Parameter param) {
		pw.println("<parameter name='" + param.name() + "' dimension='"
				+ param.type().dimension() + "' typeName='"
				+ param.type().typeName() + "' fqn='"
				+ param.type().qualifiedTypeName() + "'/>");
	}

	void printThrows(StringBuffer sb, ThrowsTag tag) {
		sb.append("   <throws name='"
				+ simplify(tag.exceptionName())
				+ (tag.exception() != null ? "' exception='"
						+ tag.exception().qualifiedName() : "") + "'>");
		sb.append(html(toString(tag.inlineTags())));
		sb.append("   </throws>");
	}

	void printParam(StringBuffer sb, ParamTag tag) {
		sb.append("   <param name='" + tag.parameterName() + "'>");
		sb.append(html(toString(tag.inlineTags())));
		sb.append("   </param>");
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
			sb.append("   <a>");
			sb.append(text.substring(1, text.length() - 1));
			sb.append("</a>");
		} else if (text.trim().startsWith("<")) {
			sb.append(text);
		} else {
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
		return sb.toString();
	}

	void printComment(Doc doc) {
		pw.println("   <lead>");
		Tag[] lead = doc.firstSentenceTags();
		pw.println(html(toString(lead)));
		pw.println("   </lead>");
		pw.println("   <description>");
		// System.out.println( "toString: " + html(toString( doc.inlineTags() ))
		// );
		pw.println(html(toString(doc.inlineTags())));
		pw.println("   </description>");
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
		} else if (tag instanceof ParamTag)
			printParam(sb, (ParamTag) tag);
		else if (tag instanceof ThrowsTag)
			printThrows(sb, (ThrowsTag) tag);
		else if (tag instanceof SeeTag)
			printSee(sb, (SeeTag) tag);
		else
			sb.append("<" + tag.kind().substring(1) + ">"
					+ html(toString(tag.inlineTags())) + "</"
					+ tag.kind().substring(1) + ">");
	}

	String simplify(String name) {
		if (name.startsWith("java.") || name.startsWith("org.osgi.")
				|| name.startsWith(currentPackage)) {
			int n = name.lastIndexOf('.');
			name = name.substring(n + 1);
		}
		return name;
	}

	String flatten(String signature) {
		StringTokenizer st = new StringTokenizer(signature, "(, )");
		StringBuffer out = new StringBuffer();
		out.append("(");
		String del = "";
		while (st.hasMoreTokens()) {
			String type = st.nextToken();
			out.append(del);
			out.append(simplify(type));
			del = ",";
		}
		out.append(")");
		return out.toString();
	}

	String escape(String in) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			switch (c) {
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
