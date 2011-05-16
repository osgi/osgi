package org.osgi.test.support.signature.generic;


/**
 * A signature describes the type hierarchy of a construct with a type like a
 * Field, a Method, a Constructor, or Class. It is defined by the
 * java.lang.reflect Type classes as well as the signature descriptors in the
 * class files. The signature class can be constructed from one of the
 * constructs or from a descriptor. As the the descriptor have only local
 * information, this is not an exact representation but good enough to verify
 * that two signatures are equal even if they change type variable names.
 */
public class Signature {
//	public enum Construct {
//		CLASS, CONSTRUCTOR, METHOD, FIELD
//	};
//
//	public static final Type[]			EMPTY_TYPE	= new Type[0];
//	final Map<String, TypeVariableImpl>	scope		= new HashMap<String, TypeVariableImpl>();
//	final Map<String, ClassImpl>		classes		= new HashMap<String, ClassImpl>();
//
//	final String						name;
//	final TypeVariableImpl				locals[];
//	final Type[]						arguments;
//	final Type							mainType;
//	final Type[]						interfaces;
//	final Construct						construct;
//
//	/*
//	 * Represents the Class in our hierarchy.
//	 */
//	static class ClassImpl implements Type {
//		static final ClassImpl	OBJECT	= new ClassImpl("java.lang.Object");
//		final String			name;
//		ClassImpl				owner;											// outer
//
//		public ClassImpl(String name) {
//			this.name = name;
//		}
//
//		public ClassImpl(Class< ? > type) {
//			this(type.getName());
//		}
//	}
//
//	/**
//	 * The work horse of the generics that models the use of a generic type. It
//	 * instantiates a generic class. It therefore has the actual value for its
//	 * arguments. The owner is the outer type that can hold additional variable
//	 * definitions. The raw type is the class, not sure why this is not class
//	 * but I guess they did not want to bind Class to the Type API because it is
//	 * final? We use ClassImpl to represent the raw class.
//	 */
//	static class ParameterizedTypeImpl implements ParameterizedType {
//		Type[]	arguments;
//		Type	raw;
//		Type	owner;
//
//		public ParameterizedTypeImpl(ParameterizedType type) {
//			raw = convert(type.getRawType());
//			arguments = convert(type.getActualTypeArguments());
//			owner = convert(type.getOwnerType());
//		}
//
//		public ParameterizedTypeImpl(ClassImpl ci, Type[] array) {
//			// TODO Auto-generated constructor stub
//		}
//
//		public Type[] getActualTypeArguments() {
//			return arguments;
//		}
//
//		public Type getOwnerType() {
//			return owner;
//		}
//
//		public Type getRawType() {
//			return raw;
//		}
//
//	}
//
//	/**
//	 * Provides arrays made of typed components.
//	 */
//	static class GenericArrayTypeImpl implements GenericArrayType {
//		Type	componentType;
//
//		public GenericArrayTypeImpl(GenericArrayType type) {
//			componentType = convert(type.getGenericComponentType());
//		}
//
//		GenericArrayTypeImpl(Type type) {
//			componentType = type;
//		}
//
//		public Type getGenericComponentType() {
//			return componentType;
//		}
//	}
//
//	/**
//	 * Handles the wildcards like ? extends A and ? super B, and ?.
//	 */
//	static class WildcardTypeImpl implements WildcardType {
//		Type[]	lowerBounds;
//		Type[]	upperBounds;
//
//		public WildcardTypeImpl(WildcardType type) {
//			lowerBounds = convert(type.getLowerBounds());
//			upperBounds = convert(type.getUpperBounds());
//		}
//
//		public WildcardTypeImpl(Type[] lower, Type[] upper) {
//			// TODO Auto-generated constructor stub
//		}
//
//		public Type[] getLowerBounds() {
//			return lowerBounds;
//		}
//
//		public Type[] getUpperBounds() {
//			return upperBounds;
//		}
//
//	}
//
//	/**
//	 * A Type variable is declared in a declaration. The only declarations that
//	 * exists are for a class (class X<A,B>), a Constructor (<A,B> X() {}), and
//	 * a Method, which is the same as the constructor. Declarations are
//	 * inherited from the class and its outer classes. In a signature, the same
//	 * named type variable must refer to the same variable.
//	 * 
//	 * In this implementation, we treat the signature stand alone for the
//	 * construct. This implies that we use references to type variables that are
//	 * not declared as an indication to create a variable on demand. For each
//	 * name in a variable that is not in its declaration the same instance of
//	 * the TypeVariable will be used.
//	 */
//	static class TypeVariableImpl implements TypeVariable<Signature> {
//		Type[]	bounds;
//		String	name;
//
//		public TypeVariableImpl(TypeVariable< ? > typeVariable) {
//			this.name = typeVariable.getName();
//			bounds = convert(typeVariable.getBounds());
//		}
//
//		public TypeVariableImpl(String name2) {
//			// TODO Auto-generated constructor stub
//		}
//
//		public Type[] getBounds() {
//			return bounds;
//		}
//
//		public Signature getGenericDeclaration() {
//			return null;
//		}
//
//		public String getName() {
//			return name;
//		}
//
//	}
//
//	static class Rover {
//		final String	s;
//		int				i;
//
//		public Rover(String s) {
//			this.s = s;
//			i = 0;
//		}
//
//		char peek() {
//			return s.charAt(i);
//		}
//
//		char take() {
//			return s.charAt(i++);
//		}
//
//		char take(char c) {
//			char x = s.charAt(i++);
//			if (c != x)
//				throw new IllegalStateException("take() expected " + c
//						+ " but got + " + x);
//			return x;
//		}
//
//		public String upTo(String except) {
//			int start = i;
//			while (except.indexOf(peek()) < 0)
//				take();
//			return s.substring(start, i);
//		}
//
//		public boolean isEOF() {
//			return i >= s.length();
//		}
//
//	}
//
//	public Signature(Method member) {
//		this.name = member.getName();
//		this.construct = Construct.METHOD;
//		this.locals = declare(member);
//		this.mainType = convert(member.getGenericReturnType());
//		this.interfaces = null;
//
//		List<Type> arguments = new ArrayList<Type>();
//		for (Type type : member.getGenericParameterTypes()) {
//			arguments.add(convert(type));
//		}
//		this.arguments = arguments.toArray(EMPTY_TYPE);
//	}
//
//	private static Type convert(Type type) {
//		if (type instanceof GenericArrayType) {
//			return new GenericArrayTypeImpl((GenericArrayType) type);
//		}
//		else
//			if (type instanceof ParameterizedType) {
//				return new ParameterizedTypeImpl((ParameterizedType) type);
//			}
//			else
//				if (type instanceof TypeVariable) {
//					return new TypeVariableImpl((TypeVariable< ? >) type);
//				}
//				else
//					if (type instanceof WildcardType) {
//						return new WildcardTypeImpl((WildcardType) type);
//					}
//					else
//						return new ClassImpl((Class< ? >) type);
//	}
//
//	private static Type[] convert(Type[] types) {
//		Type[] out = new Type[types.length];
//		for (int i = 0; i < out.length; i++)
//			out[i] = convert(types[i]);
//		return out;
//	}
//
//	private TypeVariableImpl[] declare(GenericDeclaration member) {
//		TypeVariable< ? >[] tvs = member.getTypeParameters();
//		TypeVariableImpl locals[] = new TypeVariableImpl[tvs.length];
//		for (int i = 0; i < tvs.length; i++)
//			locals[i] = new TypeVariableImpl(tvs[i]);
//		return locals;
//	}
//
//	public Signature(Constructor< ? > member) {
//		this.name = member.getName();
//		this.construct = Construct.CONSTRUCTOR;
//		this.locals = declare(member);
//		this.arguments = convert(member.getGenericParameterTypes());
//		this.mainType = null;
//		this.interfaces = null;
//	}
//
//	public Signature(Field member) {
//		this.name = member.getName();
//		this.construct = Construct.FIELD;
//		this.mainType = convert(member.getGenericType());
//		this.locals = null;
//		this.arguments = null;
//		this.interfaces = null;
//	}
//
//	public Signature(Class< ? > c) {
//		this.name = c.getName();
//		this.construct = Construct.CLASS;
//		this.locals = declare(c);
//		this.mainType = convert(c.getGenericSuperclass());
//		this.interfaces = convert(c.getGenericInterfaces());
//		this.arguments = null;
//	}
//
//	public Signature(String name, String descriptor, Construct construct) {
//		this.name = name;
//		this.construct=construct;
//		Rover rover = new Rover(descriptor);
//		this.locals = declare(rover);
//		switch( construct ) {
//			case METHOD:
//				this.arguments = arguments(rover);
//				this.mainType = reference(rover,true);
//				break;
//				
//			case CONSTRUCTOR:
//				this.arguments = arguments(rover);
//				this.mainType = null;
//				break;
//
//			case FIELD:
//				this.arguments = null;
//				this.mainType = reference(rover,true);
//				break;
//				
//			case CLASS:
//				this.arguments = null;
//				this.mainType = reference(rover,false);
//				this.interfaces = interfaces(rover);
//				break;
//				
//		if ( rover.peek() == '(') {
//			rover.take('(');
//				List<Type> args = new ArrayList<Type>();
//
//				// method
//				while (rover.peek() != ')') {
//					args.add(reference(rover, true));
//				}
//				rover.take(')');
//				arguments = args.toArray(EMPTY_TYPE);
//				gd.mainType = reference(rover, true); // return type
//			}
//			else {
//				// field or class
//				gd.mainType = reference(rover, true); // field type or super class
//
//				if (!rover.isEOF()) {
//					List<Type> interfaces = new ArrayList<Type>();
//					while (!rover.isEOF()) {
//
//						interfaces.add(reference(rover, false));
//					}
//				}
//			}
//
//		}
//	}
//
//	public TypeVariable< ? >[] getTypeParameters() {
//		return locals;
//	}
//
//	public Construct getConstruct() {
//		return construct;
//	}
//
//	private TypeVariableImpl[] declare(Rover rover) {
//		List<TypeVariableImpl> locals = new ArrayList<Signature.TypeVariableImpl>();
//
//		char c = rover.peek();
//		if (c == '<') {
//			rover.take('<');
//			List<TypeVariableImpl> tvis = new ArrayList<TypeVariableImpl>();
//
//			while (rover.peek() != '>') {
//				String name = rover.upTo(":");
//				TypeVariableImpl tvi = getTypeVariableImpl(name);
//				tvis.add(tvi);
//
//				List<Type> bounds = new ArrayList<Type>();
//
//				typeVar: while (rover.take() == ':') {
//
//					switch (rover.peek()) {
//						case ':' : // empty class cases
//							bounds.add(getClassImpl("java.lang.Object"));
//							continue typeVar;
//
//						default :
//							bounds.add(referenceOrVariable(rover));
//							break;
//					}
//				}
//				tvi.bounds = bounds.toArray(EMPTY_TYPE);
//			}
//		}
//		return locals.toArray(new TypeVariableImpl[locals.size()]);
//	}
//
//	private ClassImpl getClassImpl(String name) {
//		ClassImpl ci = classes.get(name);
//		if (ci != null)
//			return ci;
//
//		ci = new ClassImpl(name);
//		classes.put(name, ci);
//		return ci;
//	}
//
//	private TypeVariableImpl getTypeVariableImpl(String name) {
//		for (TypeVariableImpl tvi : locals) {
//			if (tvi.getName().equals(name))
//				return tvi;
//		}
//
//		TypeVariableImpl tvi = scope.get(name);
//		if (tvi != null)
//			return tvi;
//
//		tvi = new TypeVariableImpl(name);
//		scope.put(name, tvi);
//		return tvi;
//	}
//
//	private Type referenceOrVariable(Rover rover) {
//		if (rover.peek() == 'T') {
//			String name = rover.upTo(";");
//			rover.take(';');
//			return getTypeVariableImpl(name);
//		}
//		else
//			return reference(rover, false);
//	}
//
//	private Type reference(Rover rover, boolean primitivesAllowed) {
//
//		if (rover.peek() == '[') {
//			rover.take('[');
//			return new GenericArrayTypeImpl(reference(rover, true));
//		}
//
//		char type = rover.take();
//		if (type == 'L') {
//			String fqnb = rover.upTo("<;");
//			ClassImpl ci = getClassImpl(fqnb);
//			Type result = body(ci, rover);
//			rover.take(';');
//			return result;
//		}
//		else {
//			if (!primitivesAllowed)
//				throw new IllegalStateException(
//						"Primitives are not allowed here");
//
//			return getClassImpl(Character.toString(type));
//		}
//	}
//
//	private Type body(ClassImpl ci, Rover rover) {
//		if (rover.peek() != '<')
//			return ci;
//
//		rover.take('<');
//
//		List<Type> arguments = new ArrayList<Type>();
//
//		while (rover.peek() != '>') {
//			switch (rover.peek()) {
//				case 'L' :
//				case '[' :
//					arguments.add(reference(rover, false));
//					break;
//
//				case '+' : // extends
//				case '-' : // super
//					char wct = rover.take();
//					Type[] upper = EMPTY_TYPE;
//					Type[] lower = EMPTY_TYPE;
//
//					List<Type> bounds = new ArrayList<Type>();
//
//					if (rover.peek() == 'T') {
//						rover.take('T');
//						String name = rover.upTo(";");
//						bounds.add(getTypeVariableImpl(name));
//						rover.take(';');
//
//					}
//					else
//						bounds.add(reference(rover, false));
//
//					if (wct == '+')
//						upper = bounds.toArray(EMPTY_TYPE);
//					else
//						lower = bounds.toArray(EMPTY_TYPE);
//
//					arguments.add(new WildcardTypeImpl(lower, upper));
//					break;
//
//				case '*' : // wildcard
//					arguments.add(new WildcardTypeImpl(EMPTY_TYPE, EMPTY_TYPE));
//					break;
//			}
//		}
//		rover.take('>');
//		return new ParameterizedTypeImpl(ci, arguments.toArray(EMPTY_TYPE));
//	}

}
