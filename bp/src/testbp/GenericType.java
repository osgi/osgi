package testbp;

import java.lang.reflect.*;

public class GenericType extends CollapsedType {
	final static GenericType[] EMPTY= new GenericType[0];
	
	public GenericType(Type type) {
		super(getConcreteClass(type), parametersOf(collapse(type)));
	}
	
	static GenericType[] parametersOf(Type type ) {
		if ( type instanceof Class )
			return EMPTY;
		ParameterizedType pt = (ParameterizedType) type;
		Type [] parameters = pt.getActualTypeArguments();
		GenericType[] gts = new GenericType[parameters.length];
		for ( int i =0; i<gts.length; i++) {
			gts[i] = new GenericType(parameters[i]);
		}
		return gts;
	}
	
	static Class<?> getConcreteClass(Type type) {
		Type ntype = collapse(type);
		if ( ntype instanceof Class )
			return (Class<?>) ntype;
		
		if ( ntype instanceof ParameterizedType )
			return getConcreteClass(collapse(((ParameterizedType)ntype).getRawType()));

		throw new RuntimeException("Unknown type " + type );
	}

	static Type collapse(Type target) {
		if (target instanceof Class || target instanceof ParameterizedType ) {
			return target;
		} else if (target instanceof TypeVariable) {
			return collapse(((TypeVariable<?>) target).getBounds()[0]);
		} else if (target instanceof GenericArrayType) {
			Type t = collapse(((GenericArrayType) target)
					.getGenericComponentType());
			while ( t instanceof ParameterizedType )
				t = collapse(((ParameterizedType)t).getRawType());
			return Array.newInstance((Class<?>)t, 0).getClass();
		} else if (target instanceof WildcardType) {
			WildcardType wct = (WildcardType) target;
			if (wct.getLowerBounds().length == 0)
				return collapse(wct.getUpperBounds()[0]);
			else
				return collapse(wct.getLowerBounds()[0]);
		}
		throw new RuntimeException("Huh? " + target);
	}

}
