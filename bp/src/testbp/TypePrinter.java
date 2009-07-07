package testbp;

import java.lang.reflect.*;
import java.util.*;

/**
 * Prints out a type
 *
 */
public class TypePrinter {
	static public void print(Type type) {
		print(type, 0, new HashSet<Type>(), -1);
	}
	static void print(Type type, int indent, Set<Type> hadit, int n) {
		if (hadit.contains(type)) {
			indent(indent);
			if (n >= 0)
				System.out.print(n + ": ");
			System.out.println("Recursive " + type);
			return;
		}
		hadit.add(type);
		int nn = 0;

		if (type instanceof ParameterizedType) {
			header("ParameterizedType " + type, indent, n);
			ParameterizedType pt = (ParameterizedType) type;
			indent(indent);
			System.out.println(" Actual type arguments");
			nn = 0;
			for (Type tt : pt.getActualTypeArguments()) {
				print(tt, indent + 1, hadit, nn++);
			}
			indent(indent);
			System.out.println(" Owner type");
			print(pt.getOwnerType(), indent + 1, hadit, -1);
			indent(indent);
			System.out.println(" Raw type");
			print(pt.getRawType(), indent + 1, hadit, -1);
		}
		if (type instanceof WildcardType) {
			header("Wildcard Type " + type, indent, n);
			WildcardType t = (WildcardType) type;
			indent(indent);
			System.out.println(" Lower bounds");
			nn = 0;
			for (Type tt : t.getLowerBounds()) {
				print(tt, indent + 1, hadit, nn++);
			}
			indent(indent);
			System.out.println(" Upper bounds");
			nn = 0;
			for (Type tt : t.getUpperBounds()) {
				print(tt, indent + 1, hadit, nn++);
			}
		}
		if (type instanceof TypeVariable) {
			header("Type Variable " + type, indent, n);
			TypeVariable<?> t = (TypeVariable<?>) type;
			indent(indent);
			System.out.println(" Bounds ");
			nn = 0;
			for (Type tt : t.getBounds()) {
				print(tt, indent + 1, hadit, nn++);
			}
		}
		if (type instanceof GenericArrayType) {
			header("Generic Array Type " + type, indent, n);
			System.out.println("TypeVariable " + type);
			GenericArrayType t = (GenericArrayType) type;
			print(t.getGenericComponentType(), indent + 1, hadit, -1);
		}
		if (type instanceof Class) {
			indent(indent);
			if (n >= 0)
				System.out.print(n + ": ");
			System.out.println(type);
		}
		hadit.remove(type);
	}

	private static void header(String string, int indent, int n) {
		indent(indent);
		if (n >= 0)
			System.out.print(n + ": ");
		System.out.println(string);
	}

	private static void indent(int indent) {
		while (indent-- >= 0)
			System.out.print("  ");
	}

}
