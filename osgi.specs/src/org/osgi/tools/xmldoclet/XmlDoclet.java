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

package org.osgi.tools.xmldoclet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.EndElementTree;
import com.sun.source.doctree.EntityTree;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.SeeTree;
import com.sun.source.doctree.StartElementTree;
import com.sun.source.doctree.TextTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.doctree.UnknownBlockTagTree;
import com.sun.source.doctree.ValueTree;
import com.sun.source.util.DocTrees;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

/**
 * Modern JDK 9+ Doclet that produces the same javadoc.xml output as the
 * original com.sun.javadoc-based XmlDoclet. Drop-in replacement that works
 * with JDK 21+.
 */
@SuppressWarnings("javadoc")
public class XmlDoclet implements Doclet {
	Pattern				SECURITY_PATTERN	= Pattern
			.compile("(\\w+)\\[(.+),(\\w+)\\](.*)");
	PrintWriter			pw;
	String				currentPackage;
	String				currentClass;
	DocletEnvironment	env;
	DocTrees			docTrees;
	Elements			elementUtils;
	Types				typeUtils;
	Reporter			reporter;
	String				destDir = ".";

	@Override
	public void init(Locale locale, Reporter reporter) {
		this.reporter = reporter;
	}

	@Override
	public String getName() {
		return "XmlDoclet";
	}

	@Override
	public Set<? extends Option> getSupportedOptions() {
		return Set.of(new Option() {
			@Override public int getArgumentCount() { return 1; }
			@Override public String getDescription() { return "Output directory"; }
			@Override public Kind getKind() { return Kind.STANDARD; }
			@Override public List<String> getNames() { return List.of("-d"); }
			@Override public String getParameters() { return "<directory>"; }
			@Override
			public boolean process(String option, List<String> arguments) {
				destDir = arguments.get(0);
				return true;
			}
		});
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public boolean run(DocletEnvironment environment) {
		this.env = environment;
		this.docTrees = environment.getDocTrees();
		this.elementUtils = environment.getElementUtils();
		this.typeUtils = environment.getTypeUtils();
		try {
			start();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	void start() throws Exception {
		File file = new File(destDir, "javadoc.xml");
		file.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(file);
		pw = new PrintWriter(new OutputStreamWriter(out, "utf-8"));

		pw.println("<?xml version='1.0' encoding='utf-8'?>");
		pw.println("<top>");

		// Collect packages from included elements
		Map<String, PackageElement> packages = new TreeMap<>();
		for (Element element : env.getIncludedElements()) {
			if (element.getKind() == ElementKind.PACKAGE) {
				PackageElement pkg = (PackageElement) element;
				packages.put(pkg.getQualifiedName().toString(), pkg);
			} else if (element instanceof TypeElement) {
				PackageElement pkg = elementUtils.getPackageOf(element);
				packages.put(pkg.getQualifiedName().toString(), pkg);
			}
		}

		for (PackageElement packageElement : packages.values()) {
			print(packageElement);
		}

		pw.println("</top>");
		pw.close();
		out.close();
		System.out.println("Generated file: " + file);
	}

	void print(PackageElement pack) {
		currentPackage = pack.getQualifiedName().toString();
		pw.println("  <package name='" + currentPackage + "' fqn='" + currentPackage
				+ "' qn='" + currentPackage + "'>");

		printAnnotations(pack.getAnnotationMirrors());
		printComment(pack);

		// Get all types in this package
		List<TypeElement> classes = pack.getEnclosedElements().stream()
				.filter(e -> e instanceof TypeElement)
				.map(e -> (TypeElement) e)
				.filter(env::isIncluded)
				.sorted(Comparator.comparing(t -> t.getSimpleName().toString()))
				.collect(Collectors.toList());

		for (TypeElement clazz : classes) {
			print(clazz);
		}
		pw.println("  </package>");
	}

	enum CType {
		CLASS,
		INTERFACE,
		ENUM,
		ANNOTATION
	}

	void print(TypeElement clazz) {
		currentClass = clazz.getSimpleName().toString();
		String name = simplify(currentClass);
		String superclass = null;
		CType ctype = CType.CLASS;

		TypeMirror superMirror = clazz.getSuperclass();
		if (superMirror != null && superMirror.getKind() != TypeKind.NONE
				&& !superMirror.toString().equals("java.lang.Object")) {
			superclass = printType(superMirror);
		}

		if (clazz.getKind() == ElementKind.ANNOTATION_TYPE)
			ctype = CType.ANNOTATION;
		else if (clazz.getKind() == ElementKind.ENUM)
			ctype = CType.ENUM;
		else if (clazz.getKind() == ElementKind.INTERFACE)
			ctype = CType.INTERFACE;

		if (clazz.getKind() == ElementKind.INTERFACE && !clazz.getInterfaces().isEmpty()) {
			superclass = printType(clazz.getInterfaces().get(0));
		}

		StringBuilder generics = new StringBuilder();
		printTypeParams(generics, clazz.getTypeParameters(), 0);

		String modifiers = modifiersString(clazz);
		boolean isClass = clazz.getKind() == ElementKind.CLASS;
		boolean isInterface = clazz.getKind() == ElementKind.INTERFACE;

		pw.println("  <class name='" + name /**/
				+ "' fqn='" + clazz.getQualifiedName() /**/
				+ "' qn='" + clazz.getSimpleName() /**/
				+ "' package='" + elementUtils.getPackageOf(clazz).getQualifiedName() /**/
				+ "' typeParam='" + generics /**/
				+ "' modifiers='" + modifiers /**/
				+ (isClass ? " class" : "")
				+ (superclass != null ? "' superclass='" + superclass : "")
				+ (isInterface ? "' interface='yes" : "") + "' kind='"
				+ ctype + "'>");

		printAnnotations(clazz.getAnnotationMirrors());
		printComment(clazz);

		// Type parameter tags
		DocCommentTree docTree = docTrees.getDocCommentTree(clazz);
		if (docTree != null) {
			StringBuilder sb = new StringBuilder();
			for (DocTree tag : docTree.getBlockTags()) {
				if (tag instanceof ParamTree pt && pt.isTypeParameter()) {
					printParamTag(sb, pt);
				}
			}
		}

		// Interfaces
		List<? extends TypeMirror> interfaces = clazz.getInterfaces();
		for (TypeMirror iface : interfaces) {
			Element ifaceElement = typeUtils.asElement(iface);
			if (ifaceElement instanceof TypeElement te) {
				pw.println("<implements name='" + printType(iface)
						+ "' fqn='" + te.getQualifiedName()
						+ "' qn='" + printType(iface) + "' package='"
						+ elementUtils.getPackageOf(te).getQualifiedName()
						+ "' local='" + escape(clazz.toString())
						+ "'/>");
			}
		}

		boolean doDDF = DDFNode.isDDF(clazz, elementUtils);

		// Enum constants
		if (ctype == CType.ENUM) {
			for (Element member : clazz.getEnclosedElements()) {
				if (member.getKind() == ElementKind.ENUM_CONSTANT) {
					printField((VariableElement) member);
				}
			}
		}

		// Constructors
		if (ctype == CType.CLASS || ctype == CType.ENUM) {
			for (Element member : clazz.getEnclosedElements()) {
				if (member.getKind() == ElementKind.CONSTRUCTOR) {
					ExecutableElement ctor = (ExecutableElement) member;
					if (isDocumentable(ctor)) {
						printConstructor(ctor);
					}
				}
			}
		}

		// Annotation elements
		if (ctype == CType.ANNOTATION) {
			for (Element member : clazz.getEnclosedElements()) {
				if (member.getKind() == ElementKind.METHOD) {
					ExecutableElement method = (ExecutableElement) member;
					AnnotationValue deflt = method.getDefaultValue();
					printMethod(method, false, deflt);
				}
			}
		}

		// Methods
		if (ctype != CType.ANNOTATION) {
			for (Element member : clazz.getEnclosedElements()) {
				if (member.getKind() == ElementKind.METHOD) {
					ExecutableElement method = (ExecutableElement) member;
					if (isDocumentable(method)) {
						printMethod(method, doDDF, null);
					}
				}
			}
		}

		// Fields
		for (Element member : clazz.getEnclosedElements()) {
			if (member.getKind() == ElementKind.FIELD) {
				VariableElement field = (VariableElement) member;
				if (isDocumentable(field)) {
					printField(field);
				}
			}
		}

		pw.println("  </class>");
	}

	boolean isDocumentable(Element e) {
		Set<Modifier> mods = e.getModifiers();
		return mods.contains(Modifier.PUBLIC) || mods.contains(Modifier.PROTECTED);
	}

	/**
	 * Print annotations.
	 */
	private void printAnnotations(List<? extends AnnotationMirror> annotations) {
		for (AnnotationMirror annotation : annotations) {
			printAnnotation(annotation);
		}
	}

	/**
	 * Print an annotation.
	 */
	private void printAnnotation(AnnotationMirror annotation) {
		TypeElement annotationType = (TypeElement) annotation.getAnnotationType().asElement();
		String qualifiedName = annotationType.getQualifiedName().toString();
		pw.print("<" + qualifiedName + ">");

		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
				annotation.getElementValues().entrySet()) {
			String elemName = entry.getKey().getSimpleName().toString();
			pw.print("<" + elemName + ">");
			Object value = entry.getValue().getValue();
			if ("org.osgi.annotation.versioning.Version".equals(qualifiedName)) {
				Version v = new Version((String) value);
				pw.print(v.toSpecificationString());
			} else {
				printAnnotationValue(value);
			}
			pw.println("</" + elemName + ">");
		}
		pw.println("</" + qualifiedName + ">");
	}

	private void printAnnotationValue(Object o) {
		if (o instanceof Boolean || o instanceof Byte || o instanceof Short
				|| o instanceof Character || o instanceof Integer
				|| o instanceof Long || o instanceof Float || o instanceof Double) {
			pw.print(o);
		} else if (o instanceof String) {
			pw.print(escape((String) o));
		} else if (o instanceof TypeMirror) {
			// .class reference
			Element elem = typeUtils.asElement((TypeMirror) o);
			if (elem instanceof TypeElement te) {
				pw.print(te.getQualifiedName());
			} else {
				pw.print(o);
			}
		} else if (o instanceof VariableElement ve) {
			// Enum constant
			pw.print(ve.getSimpleName());
		} else if (o instanceof AnnotationMirror am) {
			printAnnotation(am);
		} else if (o instanceof List<?> list) {
			// Array of annotation values
			for (Object item : list) {
				pw.print("<value>");
				if (item instanceof AnnotationValue av) {
					printAnnotationValue(av.getValue());
				} else {
					printAnnotationValue(item);
				}
				pw.print("</value>");
			}
		} else {
			System.err.println("Unexpected type in annotation: " + o.getClass());
		}
	}

	// --- Type printing ---

	void printTypeParam(StringBuilder sb, TypeMirror t, int level) {
		if (t == null) {
			sb.append("null");
			return;
		}

		if (t.getKind() == TypeKind.TYPEVAR) {
			TypeVariable var = (TypeVariable) t;
			sb.append(var.asElement().getSimpleName());
			if (level == 0) {
				TypeMirror upper = var.getUpperBound();
				if (upper != null && !upper.toString().equals("java.lang.Object")) {
					sb.append(" extends ");
					printTypeParam(sb, upper, level + 1);
				}
			}
			return;
		}
		if (t.getKind() == TypeKind.DECLARED) {
			DeclaredType dt = (DeclaredType) t;
			sb.append(dt.asElement().getSimpleName());
			List<? extends TypeMirror> args = dt.getTypeArguments();
			if (!args.isEmpty()) {
				printTypeArgs(sb, args, level + 1);
			}
			return;
		}
		if (t.getKind() == TypeKind.WILDCARD) {
			WildcardType wc = (WildcardType) t;
			sb.append("?");
			TypeMirror ext = wc.getExtendsBound();
			TypeMirror sup = wc.getSuperBound();
			if (ext != null) {
				sb.append(" extends ");
				printTypeParam(sb, ext, level + 1);
			} else if (sup != null) {
				sb.append(" super ");
				printTypeParam(sb, sup, level + 1);
			}
			return;
		}
		if (t.getKind() == TypeKind.INTERSECTION) {
			javax.lang.model.type.IntersectionType it = (javax.lang.model.type.IntersectionType) t;
			String del = "";
			for (TypeMirror bound : it.getBounds()) {
				sb.append(del);
				printTypeParam(sb, bound, level + 1);
				del = " & ";
			}
			return;
		}
		if (t.getKind() == TypeKind.ARRAY) {
			ArrayType at = (ArrayType) t;
			printTypeParam(sb, at.getComponentType(), level);
			sb.append("[]");
			return;
		}
		// Primitive or other
		sb.append(t.toString());
	}

	private void printTypeArgs(StringBuilder sb, List<? extends TypeMirror> args, int level) {
		String del = "&lt;";
		for (TypeMirror arg : args) {
			sb.append(del);
			del = ", ";
			printTypeParam(sb, arg, level);
		}
		if (!args.isEmpty())
			sb.append("&gt;");
	}

	private void printTypeParams(StringBuilder sb, List<? extends TypeParameterElement> params, int level) {
		if (params.isEmpty()) return;
		String del = "&lt;";
		for (TypeParameterElement param : params) {
			sb.append(del);
			del = ", ";
			sb.append(param.getSimpleName());
			List<? extends TypeMirror> bounds = param.getBounds();
			// Filter out java.lang.Object
			bounds = bounds.stream()
					.filter(b -> !b.toString().equals("java.lang.Object"))
					.collect(Collectors.toList());
			if (!bounds.isEmpty() && level == 0) {
				sb.append(" extends ");
				String bdel = "";
				for (TypeMirror bound : bounds) {
					sb.append(bdel);
					printTypeParam(sb, bound, level + 1);
					bdel = " & ";
				}
			}
		}
		sb.append("&gt;");
	}

	String printType(TypeMirror type) {
		StringBuilder sb = new StringBuilder();
		printTypeParam(sb, type, 0);
		return sb.toString();
	}

	String printType(TypeMirror type, int level) {
		StringBuilder sb = new StringBuilder();
		printTypeParam(sb, type, level);
		return sb.toString();
	}

	// --- Constructor printing ---

	void printConstructor(ExecutableElement ctor) {
		StringBuilder typeArgs = new StringBuilder();
		printTypeParams(typeArgs, ctor.getTypeParameters(), 0);
		TypeElement containingClass = (TypeElement) ctor.getEnclosingElement();
		String className = containingClass.getSimpleName().toString();
		String signature = buildSignature(ctor);
		String flatSig = flatten(signature);

		pw.println("    <method name='"
				+ className //
				+ "' fqn='"
				+ escape(containingClass.getQualifiedName() + "." + className) //
				+ "' qn='"
				+ className
				+ "."
				+ className
				+ escape(flatSig) //
				+ "' package='"
				+ elementUtils.getPackageOf(ctor).getQualifiedName()
				+ "' typeArgs='"
				+ typeArgs //
				+ "' modifiers='"
				+ modifiersString(ctor) //
				+ "' signature='" + escape(signature)
				+ "' flatSignature='" + escape(flatSig)
				+ "' isConstructor='true'>");

		printAnnotations(ctor.getAnnotationMirrors());
		printMember(ctor);
		pw.println("     </method>");
	}

	// --- Method printing ---

	static Pattern	DDF_SCOPE		= Pattern
			.compile("@org.osgi.dmt.ddf.Scope\\(org.osgi.dmt.ddf.Scope.SCOPE.(.+)\\)");
	static Pattern	DDF_NODETYPE	= Pattern
			.compile("@org.osgi.dmt.ddf.NodeType\\(\"(.+)\"\\)");

	void printMethod(ExecutableElement method, boolean doDDF, AnnotationValue deflt) {
		TypeMirror returnType = method.getReturnType();
		String dimension = getDimension(returnType);
		StringBuilder typeArgs = new StringBuilder();
		printTypeParams(typeArgs, method.getTypeParameters(), 0);

		TypeElement containingClass = (TypeElement) method.getEnclosingElement();
		String signature = buildSignature(method);
		String flatSig = flatten(signature);

		pw.println("    <method name='"
				+ method.getSimpleName()
				+ "' fqn='"
				+ containingClass.getQualifiedName() + "." + method.getSimpleName()
				+ "' qn='"
				+ containingClass.getSimpleName()
				+ "."
				+ method.getSimpleName()
				+ escape(flatSig)
				+ "' package='"
				+ elementUtils.getPackageOf(method).getQualifiedName()
				+ "' modifiers='"
				+ modifiersString(method)
				+ "' typeName='"
				+ printType(returnType)
				+ "' qualifiedTypeName='"
				+ escape(qualifiedTypeName(returnType))
				+ "' typeArgs='"
				+ typeArgs
				+ "' dimension='"
				+ dimension
				+ "' signature='"
				+ escape(signature)
				+ "' flatSignature='"
				+ escape(flatSig)
				+ (deflt == null ? "" : "' default='"
						+ simplify(deflt.toString()) + "' longDefault='"
						+ deflt)
				+ "'>");
		printAnnotations(method.getAnnotationMirrors());
		printMember(method);

		if (doDDF) {
			DDFNode child = new DDFNode(null, method.getSimpleName().toString(),
					method.getReturnType().toString());
			for (AnnotationMirror ad : method.getAnnotationMirrors()) {
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

	// --- Field printing ---

	void printField(VariableElement field) {
		TypeMirror fieldType = field.asType();
		String dimension = getDimension(fieldType);
		Object constVal = field.getConstantValue();
		String constantValueExpression = constVal != null ? constantToString(constVal) : null;

		TypeElement containingClass = (TypeElement) field.getEnclosingElement();

		pw.println("    <field name='"
				+ field.getSimpleName()
				+ "' fqn='"
				+ containingClass.getQualifiedName() + "." + field.getSimpleName()
				+ "' qn='"
				+ containingClass.getSimpleName()
				+ "."
				+ field.getSimpleName()
				+ "' package='"
				+ elementUtils.getPackageOf(field).getQualifiedName()
				+ "' modifiers='"
				+ modifiersString(field)
				+ "' typeName='"
				+ printType(fieldType)
				+ "' qualifiedTypeName='"
				+ escape(qualifiedTypeName(fieldType))
				+ "' dimension='"
				+ dimension
				+ (constantValueExpression != null ? "' constantValue='"
						+ escape(constantValueExpression) : "")
				+ "'>");
		printAnnotations(field.getAnnotationMirrors());
		printComment(field);
		pw.println("     </field>");
	}

	// --- Member printing (shared for constructors and methods) ---

	void printMember(ExecutableElement x) {
		printComment(x);

		List<? extends VariableElement> parameters = x.getParameters();
		for (int i = 0; i < parameters.size(); i++) {
			printParameter(parameters.get(i), x.isVarArgs() && i == parameters.size() - 1);
		}
		for (TypeMirror exception : x.getThrownTypes()) {
			pw.println("<exception name='" + simpleTypeName(exception)
					+ "' fqn='" + qualifiedTypeName(exception) + "'/>");
		}
	}

	void printParameter(VariableElement param, boolean vararg) {
		TypeMirror paramType = param.asType();
		String dimension = vararg ? "..." : getDimension(paramType);

		pw.println("    <parameter name='" + param.getSimpleName() + "' dimension='"
				+ dimension + "' typeName='" + printType(paramType, 1)
				+ "' fqn='" + escape(qualifiedTypeName(paramType))
				+ "' varargs='" + vararg + "'/>");
	}

	// --- Comment/Tag printing ---

	void printComment(Element element) {
		DocCommentTree docTree = docTrees.getDocCommentTree(element);
		List<ExecutableElement> overrides = (element instanceof ExecutableElement)
				? overriddenMethod((ExecutableElement) element) : Collections.emptyList();

		// Lead (first sentence)
		String text = docTree != null ? docTreeListToString(docTree.getFirstSentence()) : "";
		if (text.isEmpty() && element instanceof ExecutableElement) {
			for (ExecutableElement m : overrides) {
				DocCommentTree mDoc = docTrees.getDocCommentTree(m);
				if (mDoc != null) {
					text = docTreeListToString(mDoc.getFirstSentence());
					if (!text.isEmpty()) break;
				}
			}
		}
		if (text.isEmpty()) {
			pw.println("   <lead/>");
		} else {
			pw.println("   <lead>");
			pw.println(html(text));
			pw.println("   </lead>");
		}

		// Description (full body)
		text = docTree != null ? docTreeListToString(docTree.getFullBody()) : "";
		if (text.isEmpty() && element instanceof ExecutableElement) {
			for (ExecutableElement m : overrides) {
				DocCommentTree mDoc = docTrees.getDocCommentTree(m);
				if (mDoc != null) {
					text = docTreeListToString(mDoc.getFullBody());
					if (!text.isEmpty()) break;
				}
			}
		}
		if (text.isEmpty()) {
			pw.println("   <description/>");
		} else {
			pw.println("   <description>");
			pw.println(html(text));
			pw.println("   </description>");
		}

		// Block tags
		if (docTree == null) return;

		List<? extends DocTree> blockTags = docTree.getBlockTags();

		if (!(element instanceof ExecutableElement)) {
			// For non-methods, just print all tags
			StringBuilder sb = new StringBuilder();
			for (DocTree tag : blockTags) {
				printTag(sb, tag, element);
			}
			pw.println(sb);
			return;
		}

		// Handle inheritance of comments for methods
		ExecutableElement method = (ExecutableElement) element;

		// Type param tags first
		List<ParamTree> typeParamTags = new ArrayList<>();
		for (DocTree tag : blockTags) {
			if (tag instanceof ParamTree pt && pt.isTypeParameter()) {
				typeParamTags.add(pt);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (ParamTree tag : typeParamTags) {
			printParamTag(sb, tag);
		}
		pw.println(sb);

		// Param tags with inheritance
		List<ParamTree> paramTags = new ArrayList<>();
		for (DocTree tag : blockTags) {
			if (tag instanceof ParamTree pt && !pt.isTypeParameter()) {
				paramTags.add(pt);
			}
		}
		sb = new StringBuilder();
		int j = 0;
		for (VariableElement param : method.getParameters()) {
			String paramName = param.getSimpleName().toString();
			if (j < paramTags.size()) {
				ParamTree tag = paramTags.get(j);
				if (paramName.equals(tag.getName().toString())) {
					j++;
					continue;
				}
			}
			ParamTree tag = inheritParamTag(paramName, overrides);
			if (tag != null) {
				paramTags.add(j, tag);
				j++;
			}
		}
		for (ParamTree tag : paramTags) {
			printParamTag(sb, tag);
		}
		pw.println(sb);

		// Return tags with inheritance
		sb = new StringBuilder();
		List<ReturnTree> returnTags = new ArrayList<>();
		for (DocTree tag : blockTags) {
			if (tag instanceof ReturnTree rt) {
				returnTags.add(rt);
			}
		}
		if (returnTags.isEmpty() && !"void".equals(method.getReturnType().toString())) {
			ReturnTree inherited = inheritReturnTag(overrides);
			if (inherited != null) returnTags.add(inherited);
		}
		for (ReturnTree rt : returnTags) {
			sb.append("<return>")
					.append(html(docTreeListToString(rt.getDescription())))
					.append("</return>\n");
		}
		pw.println(sb);

		// Throws tags with inheritance
		sb = new StringBuilder();
		List<ThrowsTree> throwsTags = new ArrayList<>();
		for (DocTree tag : blockTags) {
			if (tag instanceof ThrowsTree tt) {
				throwsTags.add(tt);
			}
		}
		thrown: for (TypeMirror thrown : method.getThrownTypes()) {
			String thrownName = thrown.toString();
			for (ThrowsTree tag : throwsTags) {
				String tagName = throwsTypeName(tag);
				if (thrownName.equals(tagName)) {
					continue thrown;
				}
			}
			ThrowsTree tag = inheritThrowsTag(thrownName, overrides);
			if (tag != null) throwsTags.add(tag);
		}
		for (ThrowsTree tt : throwsTags) {
			printThrows(sb, tt);
		}
		pw.println(sb);

		// Remaining tags
		sb = new StringBuilder();
		Set<DocTree> handled = new LinkedHashSet<>();
		handled.addAll(typeParamTags);
		handled.addAll(paramTags);
		handled.addAll(returnTags);
		handled.addAll(throwsTags);
		for (DocTree tag : blockTags) {
			if (!handled.contains(tag)) {
				printTag(sb, tag, element);
			}
		}
		pw.println(sb);
	}

	void printThrows(StringBuilder sb, ThrowsTree tag) {
		String exName = simplify(tag.getExceptionName().toString());
		sb.append("<throws name='").append(exName);
		// Try to resolve the exception type
		String desc = docTreeListToString(tag.getDescription());
		sb.append("'>").append(html(desc)).append("</throws>\n");
	}

	void printParamTag(StringBuilder sb, ParamTree tag) {
		String name = tag.getName().toString();
		sb.append("<param name='");
		if (tag.isTypeParameter()) {
			sb.append("&lt;");
		}
		sb.append(name);
		if (tag.isTypeParameter()) {
			sb.append("&gt;");
		}
		String text = docTreeListToString(tag.getDescription());
		if (text.length() == 0)
			sb.append("'/>\n");
		else {
			sb.append("'>").append(html(text)).append("</param>\n");
		}
	}

	void printTag(StringBuilder sb, DocTree tag, Element holder) {
		if (tag instanceof ParamTree pt) {
			printParamTag(sb, pt);
			return;
		}
		if (tag instanceof ThrowsTree tt) {
			printThrows(sb, tt);
			return;
		}
		if (tag instanceof ReturnTree rt) {
			sb.append("<return>")
					.append(html(docTreeListToString(rt.getDescription())))
					.append("</return>\n");
			return;
		}
		if (tag instanceof SeeTree st) {
			printSee(sb, st);
			return;
		}
		if (tag instanceof UnknownBlockTagTree ubt) {
			String tagName = ubt.getTagName();
			if (tagName.equals("security")) {
				handleSecurityTag(sb, ubt, holder);
				return;
			}
			if (tagName.equals("version")) {
				sb.append("<version>");
				Version v = new Version(docTreeListToString(ubt.getContent()));
				sb.append(v.toSpecificationString());
				sb.append("</version>");
				return;
			}
			sb.append("<").append(tagName).append(">")
					.append(html(docTreeListToString(ubt.getContent())))
					.append("</").append(tagName).append(">");
			return;
		}
		// Other block tags
		String kind = tag.getKind().name().toLowerCase();
		sb.append("<").append(kind).append(">")
				.append(tag.toString())
				.append("</").append(kind).append(">");
	}

	void handleSecurityTag(StringBuilder sb, UnknownBlockTagTree tag, Element holder) {
		String s = docTreeListToString(tag.getContent()).replace('\n', ' ').replace('\r', ' ');
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
			return;
		}
		throw new IllegalArgumentException(
				"@security tag invalid: '" + s + "', matching pattern is " + SECURITY_PATTERN + " " + m);
	}

	void printSee(StringBuilder sb, SeeTree tag) {
		String text = tag.toString().substring(5).trim(); // Remove "@see "
		if (text.startsWith("\"")) {
			sb.append("<a>");
			sb.append(text.substring(1, text.length() - 1));
			sb.append("</a>");
		} else if (text.startsWith("<")) {
			sb.append(text);
		} else {
			// Reference - simplified resolution
			sb.append("<a href='#").append(makeName(text)).append("'>");
			sb.append(makeName(text));
			sb.append("</a>");
		}
	}

	// --- DocTree to string conversion ---

	String docTreeListToString(List<? extends DocTree> trees) {
		if (trees == null || trees.isEmpty()) return "";
		StringBuilder sb = new StringBuilder();
		for (DocTree tree : trees) {
			docTreeToString(sb, tree);
		}
		return sb.toString().trim();
	}

	void docTreeToString(StringBuilder sb, DocTree tree) {
		switch (tree.getKind()) {
			case TEXT:
				sb.append(((TextTree) tree).getBody());
				break;
			case CODE:
				sb.append("{@code ");
				sb.append(escape(((LiteralTree) tree).getBody().getBody()));
				sb.append("}");
				break;
			case LITERAL:
				sb.append(escape(((LiteralTree) tree).getBody().getBody()));
				break;
			case LINK:
			case LINK_PLAIN: {
				LinkTree link = (LinkTree) tree;
				String label = docTreeListToString(link.getLabel());
				sb.append("{@link ");
				sb.append(link.getReference());
				if (!label.isEmpty()) {
					sb.append(" ").append(label);
				}
				sb.append("}");
				break;
			}
			case VALUE: {
				ValueTree vt = (ValueTree) tree;
				sb.append("{@value ");
				if (vt.getReference() != null) {
					sb.append(vt.getReference());
				}
				sb.append("}");
				break;
			}
			case START_ELEMENT: {
				StartElementTree start = (StartElementTree) tree;
				sb.append("<").append(start.getName());
				for (DocTree attr : start.getAttributes()) {
					sb.append(" ").append(attr);
				}
				sb.append(start.isSelfClosing() ? "/>" : ">");
				break;
			}
			case END_ELEMENT:
				sb.append("</").append(((EndElementTree) tree).getName()).append(">");
				break;
			case ENTITY:
				sb.append("&").append(((EntityTree) tree).getName()).append(";");
				break;
			case INHERIT_DOC:
				sb.append("{@inheritDoc}");
				break;
			default:
				sb.append(tree.toString());
				break;
		}
	}

	// --- Override inheritance ---

	List<ExecutableElement> overriddenMethod(ExecutableElement method) {
		List<ExecutableElement> results = new ArrayList<>();
		TypeElement containingClass = (TypeElement) method.getEnclosingElement();
		collectOverrides(method, containingClass, results);
		return results;
	}

	void collectOverrides(ExecutableElement method, TypeElement clazz, List<ExecutableElement> results) {
		// Check interfaces
		for (TypeMirror iface : clazz.getInterfaces()) {
			Element ifaceElem = typeUtils.asElement(iface);
			if (ifaceElem instanceof TypeElement te) {
				for (Element m : te.getEnclosedElements()) {
					if (m instanceof ExecutableElement ee
							&& elementUtils.overrides(method, ee, (TypeElement) method.getEnclosingElement())) {
						results.add(ee);
						return;
					}
				}
				collectOverrides(method, te, results);
				if (!results.isEmpty()) return;
			}
		}
		// Check superclass
		TypeMirror superMirror = clazz.getSuperclass();
		if (superMirror != null && superMirror.getKind() == TypeKind.DECLARED) {
			Element superElem = typeUtils.asElement(superMirror);
			if (superElem instanceof TypeElement te) {
				for (Element m : te.getEnclosedElements()) {
					if (m instanceof ExecutableElement ee
							&& elementUtils.overrides(method, ee, (TypeElement) method.getEnclosingElement())) {
						results.add(ee);
						return;
					}
				}
				collectOverrides(method, te, results);
			}
		}
	}

	ParamTree inheritParamTag(String paramName, List<ExecutableElement> overrides) {
		for (ExecutableElement m : overrides) {
			DocCommentTree doc = docTrees.getDocCommentTree(m);
			if (doc != null) {
				for (DocTree tag : doc.getBlockTags()) {
					if (tag instanceof ParamTree pt && !pt.isTypeParameter()
							&& paramName.equals(pt.getName().toString())) {
						return pt;
					}
				}
			}
		}
		return null;
	}

	ThrowsTree inheritThrowsTag(String throwsName, List<ExecutableElement> overrides) {
		for (ExecutableElement m : overrides) {
			DocCommentTree doc = docTrees.getDocCommentTree(m);
			if (doc != null) {
				for (DocTree tag : doc.getBlockTags()) {
					if (tag instanceof ThrowsTree tt) {
						String name = throwsTypeName(tt);
						if (throwsName.equals(name)) {
							return tt;
						}
					}
				}
			}
		}
		return null;
	}

	ReturnTree inheritReturnTag(List<ExecutableElement> overrides) {
		for (ExecutableElement m : overrides) {
			DocCommentTree doc = docTrees.getDocCommentTree(m);
			if (doc != null) {
				for (DocTree tag : doc.getBlockTags()) {
					if (tag instanceof ReturnTree rt) {
						return rt;
					}
				}
			}
		}
		return null;
	}

	static final String		JAVAIDENTIFIER	= "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
	static final Pattern	FQCN			= Pattern.compile(JAVAIDENTIFIER + "(\\." + JAVAIDENTIFIER + ")*");

	String throwsTypeName(ThrowsTree tag) {
		String name = tag.getExceptionName().toString();
		Matcher m = FQCN.matcher(name);
		if (m.find()) {
			name = m.group();
		}
		if (name.indexOf('.') < 0) {
			name = "java.lang." + name;
		}
		return name;
	}

	// --- Utility methods ---

	String html(String text) {
		HtmlCleaner cleaner = new HtmlCleaner(currentPackage + "."
				+ currentClass, text);
		return cleaner.clean();
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

	String modifiersString(Element element) {
		return element.getModifiers().stream()
				.map(Modifier::toString)
				.collect(Collectors.joining(" "));
	}

	String buildSignature(ExecutableElement method) {
		StringBuilder sb = new StringBuilder("(");
		String del = "";
		for (VariableElement param : method.getParameters()) {
			sb.append(del);
			sb.append(param.asType().toString());
			del = ",";
		}
		sb.append(")");
		return sb.toString();
	}

	String getDimension(TypeMirror type) {
		if (type.getKind() == TypeKind.ARRAY) {
			return "[]";
		}
		return "";
	}

	String qualifiedTypeName(TypeMirror type) {
		if (type.getKind() == TypeKind.DECLARED) {
			DeclaredType dt = (DeclaredType) type;
			return ((TypeElement) dt.asElement()).getQualifiedName().toString();
		}
		if (type.getKind() == TypeKind.ARRAY) {
			return qualifiedTypeName(((ArrayType) type).getComponentType()) + "[]";
		}
		return type.toString();
	}

	String simpleTypeName(TypeMirror type) {
		if (type.getKind() == TypeKind.DECLARED) {
			DeclaredType dt = (DeclaredType) type;
			return dt.asElement().getSimpleName().toString();
		}
		return type.toString();
	}

	String constantToString(Object value) {
		if (value instanceof String) {
			return "\"" + value + "\"";
		}
		if (value instanceof Character) {
			return "'" + value + "'";
		}
		if (value instanceof Long) {
			return value + "L";
		}
		if (value instanceof Float) {
			return value + "f";
		}
		if (value instanceof Double) {
			return value + "d";
		}
		return value.toString();
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
		List<String> parts = new ArrayList<>();

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
				case '\'' :
					sb.append("&apos;");
					break;
				default :
					sb.append(c);
			}
		}
		return sb.toString();
	}
}
