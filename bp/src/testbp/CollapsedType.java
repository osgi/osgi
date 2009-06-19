package testbp;

/**
 * Provides access to a concrete type and its optional generic type argument.
 * 
 * A Collapsed Type converts a type declaration to a generalized form that is
 * directly useable for conversion.It provides access to the concrete Class of
 * the type as well of the optional type arguments.
 * 
 * In Java 1.4, this class only works on non-generic types. In those cases, a
 * Collapsed Type provides to the class and has no type arguments. Blueprint
 * extender implementations can subclass this class and provide access to the
 * generics type graph if used in a declaration. Such a subclass must
 * <em>collapse<em> the different <code>Type</code> instances into the 
 * collapsed form. That is, a form where the raw Class is available with its optional type arguments.
 *
 *@Immutable
 */

public class CollapsedType {
	final static CollapsedType[] EMPTY = new CollapsedType[0];
	final static CollapsedType ALL = new CollapsedType(Object.class);

	private final Class clazz;
	private final CollapsedType[] parameters;

	/**
	 * Create a Collapsed Type with generic information.
	 * 
	 * This constructor is intended to be used by subclasses that traverse a
	 * <code>Type</code> graph. These subclasses must collapse a type graph as
	 * described in the specification.
	 * 
	 * @param clazz
	 *            The class that represents the collapsed type.
	 * @param parameters
	 *            The type parameters, must not be <code>null</code>.
	 */
	protected CollapsedType(Class clazz, CollapsedType[] parameters) {
		this.clazz = clazz;
		this.parameters = parameters;
	}

	/**
	 * Create a Collapsed Type for a normal Java class without any generics
	 * information.
	 * 
	 * @param clazz
	 *            The class that is the collapsed type.
	 */
	public CollapsedType(Class clazz) {
		this.clazz = clazz;
		this.parameters = EMPTY;
	}

	/**
	 * Access to the raw class.
	 * 
	 * The raw class represents the concrete class that is associated with a
	 * type declaration. This class could have been deduced from the generics
	 * type graph of the declaration. For example, in the following example:
	 * 
	 * <pre>
	 * Map&lt;String, Object&gt; map;
	 * </pre>
	 * 
	 * The raw class is the Map class.
	 * 
	 * @return the collapsed raw class that represents this type.
	 */
	public Class getRawClass() {
		return clazz;
	}

	/**
	 * Access to a type argument.
	 * 
	 * The type argument refers to a argument in a generic type declaration
	 * given by index <code>i</code>. This method returns a Collapsed Type
	 * that has Object as class when no generic type information is available.
	 * Any object is assignable to Object and therefore no conversion is then
	 * necessary, this is compatible with older Javas.
	 * 
	 * For example, in the following example:
	 * 
	 * <pre>
	 * Map&lt;String, Object&gt; map;
	 * </pre>
	 * 
	 * The type argument 0 is <code>String</code>, and type argument 1 is
	 * <code>Object</code>.
	 * 
	 * @param i
	 *            The index of the type argument
	 * @return A Collapsed Type that represents a type argument. If
	 */
	public CollapsedType getActualTypeArgument(int i) {
		if (parameters.length == 0)
			return ALL;

		return parameters[i];
	}

	/**
	 * Number of type arguments.
	 * 
	 * @return number of type arguments
	 */
	public int size() {
		return parameters.length;
	}
}
