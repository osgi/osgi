package testbp;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

@SuppressWarnings("unchecked")
public class BasicConverter {
	List<Converter> converters = new ArrayList<Converter>();
	static Map<Class<?>, Class<?>> interface2Class = new HashMap<Class<?>, Class<?>>();
	static {
		interface2Class.put(List.class, ArrayList.class);
		interface2Class.put(Set.class, LinkedHashSet.class);
		interface2Class.put(Collection.class, ArrayList.class);
		interface2Class.put(Map.class, LinkedHashMap.class);
		interface2Class.put(SortedMap.class, TreeMap.class);
		interface2Class.put(Queue.class, LinkedList.class);
		interface2Class.put(SortedSet.class, TreeSet.class);
		interface2Class.put(ConcurrentMap.class, ConcurrentHashMap.class);
		interface2Class.put(Dictionary.class, Hashtable.class);
		interface2Class.put(byte.class, Byte.class);
		interface2Class.put(short.class, Short.class);
		interface2Class.put(char.class, Character.class);
		interface2Class.put(int.class, Integer.class);
		interface2Class.put(long.class, Long.class);
		interface2Class.put(float.class, Float.class);
		interface2Class.put(double.class, Double.class);
		interface2Class.put(boolean.class, Boolean.class);
		
	}

	public Object convert(Object s, CollapsedType type)
			throws Exception {
		Class<?> S = s.getClass();
		Class<?> T = type.getRawClass();
		
		if (isAssignable(s, type))
			return s;

		for (Converter c : converters) {
			if (c.canConvert(s, type))
				return c.convert(s, type);
		}

		if (T.isArray()) {
			Collection<?> c = getAsCollection(s);
			if (c == null)
				throw new RuntimeException(
						"T=[], but S not array or collection");

			Object[] array = (Object[]) Array.newInstance(T.getComponentType(), c.size());
			int n = 0;
			for (Object o : c)
				array[n++] = convert(o, T.getComponentType());

			return array;
		}

		if (Collection.class.isAssignableFrom(T)) {
			Collection<?> c = getAsCollection(s);
			if (c == null)
				throw new RuntimeException(
						"T=Collection, but S not array or collection");

			if (interface2Class.containsKey(T))
				T = interface2Class.get(T);

			if (T == null || T.isInterface())
				throw new RuntimeException(
						"T=Collection, but no concrete class available: "
								+ T);

			Collection<Object> result = (Collection<Object>) T.newInstance();
			for (Object o : c)
				result.add(convert(o, type.getActualTypeArgument(0)));

			return result;
		}

		if (Map.class.isAssignableFrom(T) || Dictionary.class.isAssignableFrom(T)) {
			if (interface2Class.containsKey(T))
				T = interface2Class.get(T);

			if ( T.isInterface() )
				throw new RuntimeException("Target must implement map but there is no concrete type " + T);
			
			Map<Object,Object> map = (Map<Object,Object>) T.newInstance();

			if (Map.class.isAssignableFrom(S)) {
				Map<?,?> smap = (Map<?,?>) s;
				for (Map.Entry<?, ?> entry : smap.entrySet()) {
					Object key = entry.getKey();
					Object value = entry.getValue();
					key = convert(key, type.getActualTypeArgument(0));
					value = convert(value, type.getActualTypeArgument(1));
					map.put(key,value);
				}
			} else if (Dictionary.class.isAssignableFrom(S)) {
				Dictionary<?,?> smap = (Dictionary<?,?>) s;
				for (Enumeration<?> e=smap.keys(); e.hasMoreElements(); ) {
					Object key = e.nextElement();
					Object value = smap.get(key);					
					key = convert(key, type.getActualTypeArgument(0));
					value = convert(value,type.getActualTypeArgument(1));
					map.put(key,value);
				}
			} else
				throw new RuntimeException(
						"T=Map, but source is not Map or Dictionary");

			return map;
		}
		
		if ( T.isPrimitive() )
			T = interface2Class.get(T);

		if ( Number.class.isAssignableFrom(T) && Number.class.isAssignableFrom(S)) {
			Number snum = (Number) s;
			long l = snum.longValue();
			if ( T == Byte.class ) {
				if ( l >= Byte.MIN_VALUE && l <= Byte.MAX_VALUE )
					return new Byte((byte)l);
			}
			if ( T == Short.class ) {
				if ( l >= Short.MIN_VALUE && l <= Short.MAX_VALUE )
					return new Short((short)l);
			}
			if ( T == Integer.class ) {
				if ( l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE )
					return new Integer((short)l);
			}
			if ( T == Long.class ) {
					return new Long(l);
			}
			if ( T == Double.class ) {
				if ( l >= Double.MIN_VALUE && l <= Double.MAX_VALUE )
					return new Double((double)l);
			}
			if ( T == Float.class ) {
				if ( l >= Float.MIN_VALUE && l <= Float.MAX_VALUE )
					return new Float((float)l);
			}
			throw new RuntimeException("Nummeric conversion failed because target type cannot hold amount: " + s + " to " + T );
		}
		
		if ( ! (s instanceof String) )
			throw new RuntimeException("No conversion found for "+ s + " to " + T );

		String syntax = (String) s;
		if ( T == Pattern.class ) {
			return Pattern.compile(syntax);
		} 
		
		if ( T == Boolean.class ) {
			syntax = syntax.toLowerCase();
			if ( syntax.matches("on|yes|true"))
				return true;
			if ( syntax.matches("off|no|false"))
				return false;
			throw new RuntimeException("T=Boolean, however, source is not on, off, yes, no, true, false");
		}
		
		if ( T == Properties.class ) {
			byte [] bytes = syntax.getBytes("ISO8859-1");
			Properties properties = new Properties();
			ByteArrayInputStream is = new ByteArrayInputStream(bytes);	
			properties.load(is);
			is.close();
		}
		
		if ( T == Class.class ) {
			syntax = syntax.trim();
			return loadClass(syntax);
		}
		
		Constructor<?> constructor = getDisambiguatedConstructor(T, S);
		if (constructor == null)
			throw new RuntimeException("Fail to convert " + s + " to " + T);

		return constructor.newInstance(s);
	}

	private Collection<?> getAsCollection(Object s) {
		if (s.getClass().isArray())
			return Arrays.asList((Object[]) s);
		else if (Collection.class.isAssignableFrom(s.getClass()))
			return (Collection<?>) s;
		else
			return null;
	}

	private boolean isAssignable(Object source, CollapsedType target) {
		return target.size() == 0
				&& target.getRawClass().isAssignableFrom(source.getClass());
	}

	public Object convert(Object source, Type target) throws Exception {
		return convert( source, new GenericType(target));
	}

	
	public boolean canConvert(Object source, CollapsedType type ) {
		// TODO Auto-generated method stub
		return false;
	}

	// For testing
	Class<?> loadClass(String s) throws Exception {
		return getClass().getClassLoader().loadClass(s);
	}

	// TODO need to do proper disambiguation
	private <T> Constructor<T> getDisambiguatedConstructor(Class<T> t, Class<?> s) {
		for (Constructor<T> c : t.getConstructors()) {
			if (c.getParameterTypes().length == 1
					&& c.getParameterTypes()[0] == s) {
				return c;
			}
		}
		for (Constructor<T> c : t.getConstructors()) {
			if (c.getParameterTypes().length == 1
					&& c.getParameterTypes()[0].isAssignableFrom(s)) {
				return c;
			}
		}
		return null;
	}

}
