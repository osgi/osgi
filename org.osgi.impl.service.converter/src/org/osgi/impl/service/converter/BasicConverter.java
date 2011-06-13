package org.osgi.impl.service.converter;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import org.osgi.service.converter.*;

/**
 * This is a basic converter following the original Blueprint and later
 * Converter class without being bound to OSGi.
 */
public class BasicConverter {

	static final Map<Class< ? >, Class< ? >>	implementations	= new IdentityHashMap<Class< ? >, Class< ? >>();

	static <A> void set(Class<A> a, Class< ? extends A> b) {
		implementations.put(a, b);
	}

	static <B, A extends B> Class< ? extends B> get(Class<B> c) {
		Class< ? > result = implementations.get(c);

		if (result == null)
			return c;
		else
			return result.asSubclass(c);
	}

	static {
		set(boolean.class, Boolean.class);
		set(byte.class, Byte.class);
		set(short.class, Short.class);
		set(char.class, Character.class);
		set(int.class, Integer.class);
		set(long.class, Long.class);
		set(float.class, Float.class);
		set(double.class, Double.class);

		set(Collection.class, ArrayList.class);
		set(List.class, ArrayList.class);
		set(Queue.class, LinkedList.class);
		set(Set.class, LinkedHashSet.class);
		set(SortedSet.class, TreeSet.class);
		set(Map.class, LinkedHashMap.class);
		set(SortedMap.class, TreeMap.class);
		set(ConcurrentMap.class, ConcurrentHashMap.class);
		set(Dictionary.class, Hashtable.class);
	}

	/**
	 * @param <Tp>
	 * @param s
	 * @param T
	 * @return A converted value or null
	 * @throws Exception if anything fails
	 */

	@SuppressWarnings("unchecked")
	public <Tp> Tp convert(Object s, ReifiedType<Tp> T) throws Exception {
		Class< ? extends Tp> clazz = T.getRawClass();

		//
		// Step 1.5 Turn primitives into their corresponding type
		// 
		if (clazz.isPrimitive())
			clazz = (Class<Tp>) implementations.get(clazz);

		//
		// Step 1 isAssignable for no generics
		//

		if (clazz.getTypeParameters().length == 0 && clazz.isInstance(s))
			return clazz.cast(s);

		//
		// Step 2 type converters
		//

		Tp result = converters(s, T);
		if (result != null)
			return result;

		//
		// Step 2.5
		//

		if (clazz == String.class)
			return clazz.cast(s.toString());

		//
		// Step 7 Concrete class conversions
		//

		try {
			return Primitives.convert(clazz, s);
		}
		catch (Exception e) {
			// ignore
			// System.out.println(e.getMessage());
		}

		//
		// Step 3 Arrays
		//

		if (clazz.isArray()) {
			// Array is defined as Array<ComponentType>
			return clazz.cast(toArray(s, T.getActualTypeArgument(0)));
		}

		//
		// Step 4 Collections
		// 

		if (Collection.class.isAssignableFrom(clazz)) {
			Class<Collection<Object>> clazzColl = (Class<Collection<Object>>) clazz;
			return clazz.cast(toCollection(s, clazzColl, T
					.getActualTypeArgument(0)));
		}

		//
		// Step 5 Maps and Dictionary
		//
		if (Dictionary.class.isAssignableFrom(clazz)) {
			return clazz.cast(toDictionary(s, (Class<Dictionary>) clazz, T
					.getActualTypeArgument(0), T.getActualTypeArgument(1)));
		}

		if (Map.class.isAssignableFrom(clazz)) {
			return clazz.cast(toMap(s, (Class<Map>) clazz, T
					.getActualTypeArgument(0), T.getActualTypeArgument(1)));
		}

		//
		// Step 8 Must be String
		//

		if (!(s instanceof String))
			return null;

		String input = (String) s;

		if (Enum.class.isAssignableFrom(clazz)) {
			Method m = clazz.getMethod("valueOf", String.class);

			return clazz.cast(m.invoke(null, input));
		}

		if (Class.class.isAssignableFrom(clazz)) {
			return clazz.cast(loadClass(input));
		}

		//
		// Step 10 String constructor
		//

		Constructor< ? extends Tp> constructor = clazz
				.getConstructor(String.class);
		return constructor.newInstance(input);
	}

	/**
	 * 
	 * @param <Arg>
	 * @param s
	 * @param argType
	 * @return a new array
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <Arg> Object toArray(Object s, ReifiedType<Arg> argType)
			throws Exception {
		Collection<Arg> l = new ArrayList<Arg>();
		Collection<Arg> c = toCollection(s, l.getClass(), argType);
		Class<Arg> argClass = argType.getRawClass();
		if (argClass.isPrimitive()) {
			Object values = Array.newInstance(argClass, c.size());
			int n = 0;
			for (Object m : c)
				Array.set(values, n++, convert(m, argType));
			return values;
		}
		else {
			Arg[] values = (Arg[]) Array.newInstance(argClass, c.size());
			return c.toArray(values);
		}
	}

	/**
	 * Convert a collection
	 * 
	 * @param <Arg>
	 * @param s
	 * @param class1
	 * @param argType
	 * @return a new collection
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <Arg> Collection<Arg> toCollection(Object s,
			Class< ? extends Collection> class1, ReifiedType<Arg> argType)
			throws Exception {

		if (implementations.containsKey(class1))
			class1 = (Class<Collection<Arg>>) implementations.get(class1);

		assert Collection.class.isAssignableFrom(class1);

		Collection<Arg> instance = class1.newInstance();

		if (s.getClass().isArray()) {
			if (s.getClass().getComponentType().isPrimitive()) {
				int length = Array.getLength(s);
				for (int i = 0; i < length; i++) {
					Object mc = convert(Array.get(s, i), argType);

					instance.add((Arg) mc);
				}
			}
			else {
				Object[] sArray = (Object[]) s;
				for (Object member : sArray)
					instance.add(convert(member, argType));
			}
			return instance;
		}

		if (s instanceof Collection) {
			Collection< ? > sCollection = (Collection< ? >) s;
			for (Object member : sCollection) {
				instance.add(convert(member, argType));
			}
			return instance;
		}

		return null;
	}

	/**
	 * Convert s to a map
	 * 
	 * @param s
	 * @param T
	 * @param keyType
	 * @param valueType
	 * @param <M>
	 * @param <K>
	 * @param <V>
	 * @return a new map
	 * @throws Exception
	 */

	protected <M extends Map<K, V>, K, V> Map<K, V> toMap(Object s, Class<M> T,
			ReifiedType<K> keyType, ReifiedType<V> valueType) throws Exception {

		Class< ? extends M> factory = get(T);

		Map<K, V> result = factory.newInstance();

		if (s instanceof Dictionary< ? , ? >) {
			Dictionary< ? , ? > dict = (Dictionary< ? , ? >) s;

			for (Enumeration< ? > e = dict.keys(); e.hasMoreElements();) {
				Object key = e.nextElement();
				Object value = dict.get(key);
				result.put(convert(key, keyType), convert(value, valueType));
			}
		}
		else {
			Map< ? , ? > map = (Map< ? , ? >) s;

			for (Map.Entry< ? , ? > entry : map.entrySet()) {
				result.put(convert(entry.getKey(), keyType), convert(entry
						.getValue(), valueType));
			}
		}
		return result;
	}

	/**
	 * @param <M>
	 * @param <K>
	 * @param <V>
	 * @param s
	 * @param T
	 * @param keyType
	 * @param valueType
	 * @return a new Dictionary
	 * @throws Exception
	 */
	protected <M extends Dictionary<K, V>, K, V> Dictionary<K, V> toDictionary(
			Object s, Class<M> T, ReifiedType<K> keyType,
			ReifiedType<V> valueType) throws Exception {

		Class< ? extends Dictionary<K, V>> factory = get(T);
		Dictionary<K, V> result = factory.newInstance();

		if (s instanceof Dictionary< ? , ? >) {
			Dictionary< ? , ? > dict = (Dictionary< ? , ? >) s;

			for (Enumeration< ? > e = dict.keys(); e.hasMoreElements();) {
				Object key = e.nextElement();
				Object value = dict.get(key);
				result.put(convert(key, keyType), convert(value, valueType));
			}
		}
		else {
			Map< ? , ? > map = (Map< ? , ? >) s;

			for (Map.Entry< ? , ? > entry : map.entrySet()) {
				result.put(convert(entry.getKey(), keyType), convert(entry
						.getValue(), valueType));
			}
		}
		return result;
	}

	/**
	 * @param <Tp>
	 * @param s
	 * @param argType
	 * @return The converted value or null
	 */
	protected <Tp> Tp converters(Object s, ReifiedType<Tp> argType) {
		// This is just a basic converter
		return null;
	}

	/**
	 * @param className
	 * @return A newly loaded class
	 * @throws ClassNotFoundException
	 */
	protected Class< ? > loadClass(String className)
			throws ClassNotFoundException {
		return getClass().getClassLoader().loadClass(className);
	}
}
