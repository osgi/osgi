package org.osgi.service.converter.generic;

import java.lang.reflect.*;

import org.osgi.service.converter.*;

/**
 * A GenericType provides a concrete implementation of a {@link ReifiedType}
 * that bases its decisions on the generic runtime information in the Java 5 and
 * later VMs. It is instantiated with a Type argument, the implementation will
 * then analyze the Type structure and collapse all variables, wildcards, and
 * arrays to a consistent model of a raw class and 0 or more type GenericType
 * type arguments.
 * 
 * Though Java erases much of the generic type information, method arguments,
 * return types of methods, field types, and super class/interface implements
 * definitions retain their generic information. In the following example:
 * 
 * <pre>
 * Collection&lt;String&gt;	c;
 * </pre>
 * 
 * The field {@code c} contains a {@link ParameterizedType} that has
 * {@link ParameterizedType#getRawType() a raw type} that is {@code
 * Collection.class} and {@link ParameterizedType#getActualTypeArguments() a
 * type argument at index 0} that is {@code String.class}.
 * 
 * The generic type information can be extremely useful for type conversions
 * because it maintains the constraints specified by the programmer. For
 * example, if a method returns {@code Collection<String>} than it is important
 * that the type conversion actually places strings in the collections.
 * 
 * The converter service provides a {@link ReifiedType} class that implements
 * the basic idea of this class in a Java 4 world where no generic types are
 * present. However, it does allow Java 5 types to be modeled correctly. This
 * class provides the capabilities to easily provide ReifiedTypes calculated from
 * the Java 5 generics Type information. This class is maintained in extra
 * package so the {@code org.osgi.service.converter} package can be compiled for
 * Java 1.4.
 * 
 * @param <T> The type argument for this GenericType
 */
public class GenericType<T> extends ReifiedType<T> {
	final static GenericType< ? >[]	EMPTY	= new GenericType[0];
	final Type						type;
	GenericType< ? >				arguments[];

	/**
	 * Public constructor.
	 * 
	 * The constructor will analyze the type argument and find the type
	 * arguments, if any, as well as the raw class.
	 * 
	 * @param type The type argument to be analyzed.
	 */
	public GenericType(Type type) {
		super((Class<T>) getConcreteClass(type));
		this.type = type;
	}

	/**
	 * Return the actual type argument for this type.
	 * 
	 * A type argument is the arguments between the &lt; and &gt; brackets. For
	 * example:
	 * 
	 * <pre>
	 * Map&lt;K, V&gt;	map;
	 * </pre>
	 * 
	 * In this example, K and V are the type arguments. Each class has a hard
	 * number of type arguments. The GenericType class always returns
	 * GenericType instances.
	 * 
	 * @param index The index of the type argument. This value must be less than
	 *        {@link #size()}, if not a ReifiedType on Object class is returned.
	 * @return The reified type for the index type argument
	 */
	public ReifiedType< ? > getActualTypeArgument(int index) {
		synchronized (this) {
			if (arguments == null) {
				arguments = argumentsOf(type);
			}
		}
		if (index >= arguments.length)
			return super.getActualTypeArgument(index);
		else
			return arguments[index];
	}

	/*
	 * Helper function to calculate the parameters of a type.
	 * 
	 * @param type The root type
	 * 
	 * @return a Generic Type array with the arguments.
	 */
	private static GenericType< ? >[] argumentsOf(Type type) {
		// Class is a terminal
		if (type instanceof Class< ? >)
			return EMPTY;

		if (type instanceof GenericArrayType) {
			GenericArrayType t = (GenericArrayType) type;
			return new GenericType[] {new GenericType<Object>(t
					.getGenericComponentType())};

		}
		ParameterizedType pt = (ParameterizedType) type;
		Type[] parameters = pt.getActualTypeArguments();
		GenericType< ? >[] gts = new GenericType[parameters.length];
		for (int i = 0; i < gts.length; i++) {
			gts[i] = new GenericType<Object>(parameters[i]);
		}
		return gts;
	}

	/*
	 * Get the concrete class of a Type by traversing the type structure until a
	 * concrete class is found.
	 * 
	 * @param type the type to travers
	 * @return A class object
	 */
	private static Class< ? > getConcreteClass(Type type) {
		Type ntype = collapse(type);
		if (ntype instanceof Class< ? >)
			return (Class< ? >) ntype;

		if (ntype instanceof ParameterizedType)
			return getConcreteClass(collapse(((ParameterizedType) ntype)
					.getRawType()));

		throw new RuntimeException("Unknown type " + type);
	}

	/*
	 * Collapse the types until a concrete class or ParameterizedType is found.
	 *  
	 * @param target the type to collapse
	 * @return a class or ParameterizedType
	 */
	static Type collapse(Type target) {
		if (target instanceof Class< ? > || target instanceof ParameterizedType) {
			return target;
		}
		else
			if (target instanceof TypeVariable< ? >) {
				return collapse(((TypeVariable< ? >) target).getBounds()[0]);
			}
			else
				if (target instanceof GenericArrayType) {
					Type t = collapse(((GenericArrayType) target)
							.getGenericComponentType());
					while (t instanceof ParameterizedType)
						t = collapse(((ParameterizedType) t).getRawType());
					return Array.newInstance((Class< ? >) t, 0).getClass();
				}
				else
					if (target instanceof WildcardType) {
						WildcardType wct = (WildcardType) target;
						if (wct.getLowerBounds().length == 0)
							return collapse(wct.getUpperBounds()[0]);
						else
							return collapse(wct.getLowerBounds()[0]);
					}
		throw new RuntimeException("Cannot collapse to a basic type " + target);
	}
}
