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

package org.osgi.util.converter;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author $Id$
 */
class ConvertingImpl extends AbstractSpecifying<Converting>
		implements Converting, InternalConverting {
	private static final Map<Class< ? >,Class< ? >>	INTERFACE_IMPLS;
	// Interfaces with no methods are also not considered
	private static final Collection<Class< ? >>		NO_MAP_VIEW_TYPES;
	static {

		Map<Class< ? >,Class< ? >> cim = new HashMap<>();
		cim.put(Collection.class, ArrayList.class);
		// Lists
		cim.put(List.class, ArrayList.class);
		// Sets
		cim.put(Set.class, LinkedHashSet.class); // preserves insertion order
		cim.put(NavigableSet.class, TreeSet.class);
		cim.put(SortedSet.class, TreeSet.class);
		// Queues
		cim.put(Queue.class, LinkedList.class);
		cim.put(Deque.class, LinkedList.class);

		Map<Class< ? >,Class< ? >> iim = new HashMap<>(cim);
		// Maps
		iim.put(Map.class, LinkedHashMap.class); // preserves insertion order
		iim.put(ConcurrentMap.class, ConcurrentHashMap.class);
		iim.put(ConcurrentNavigableMap.class, ConcurrentSkipListMap.class);
		iim.put(NavigableMap.class, TreeMap.class);
		iim.put(SortedMap.class, TreeMap.class);

		Set<Class< ? >> nmv = new HashSet<>(cim.keySet());
		nmv.addAll(Arrays.<Class< ? >> asList(String.class, Class.class,
				Comparable.class, CharSequence.class, Map.Entry.class));
		// The following classes are only available from Java 12 onwards
		addClassIfAvailable("java.lang.constant.Constable", nmv);
		addClassIfAvailable("java.lang.constant.ConstantDesc", nmv);

		INTERFACE_IMPLS = Collections.unmodifiableMap(iim);
		NO_MAP_VIEW_TYPES = Collections.unmodifiableSet(nmv);
	}

	private static void addClassIfAvailable(String cls,
			Collection<Class< ? >> collection) {
		try {
			Class< ? > clazz = ConvertingImpl.class.getClassLoader()
					.loadClass(cls);
			collection.add(clazz);
		} catch (Exception ex) {
			// Class not available, to nothing
		}
	}

	private final InternalConverter	initialConverter;
	private volatile Object			object;
	private volatile Class< ? >		sourceClass;
	private volatile Class< ? >		targetClass;
	private volatile Type[]			typeArguments;
	private volatile Type			targetType;

	ConvertingImpl(InternalConverter converter, Object obj) {
		initialConverter = converter;
		object = obj;
	}

	@Override
	public <T> T to(Class<T> cls) {
		Type type = cls;
		return to(type);
	}

	@Override
	public <T> T to(TypeReference<T> ref) {
		return to(ref.getType());
	}

	@Override
	public <T> T to(Type type) {
		return to(type, initialConverter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T to(Type type, InternalConverter converter) {
		// Wildcard types are strange - we immediately resolve them to something
		// that we can actually use.
		if (type instanceof WildcardType) {
			WildcardType wt = (WildcardType) type;
			Type[] lowerBounds = wt.getLowerBounds();
			if (lowerBounds.length != 0) {
				// This is a ? super X generic, why on earth would you do this?
				throw new ConversionException("The type variable "
						+ wt.toString()
						+ " cannot be used with the converter. The use of <? super ...> is highly ambiguous.");
			} else {
				type = wt.getUpperBounds()[0];
			}
		}

		Class< ? > cls = null;
		if (type instanceof Class) {
			cls = (Class< ? >) type;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Type rt = pt.getRawType();
			typeArguments = pt.getActualTypeArguments();
			if (rt instanceof Class)
				cls = (Class< ? >) rt;
		} else if (type instanceof GenericArrayType) {
			GenericArrayType pt = (GenericArrayType) type;
			Type rt = pt.getGenericComponentType();
			if (rt instanceof Class)
				cls = (Class< ? >) rt;
			else if (rt instanceof ParameterizedType) {
				Type rt2 = ((ParameterizedType) rt).getRawType();
				if (rt2 instanceof Class) {
					cls = (Class< ? >) rt2;
				}
			}

		}
		targetType = type;
		if (cls == null)
			return null;

		if (object == null)
			return (T) handleNull(cls, converter);

		targetClass = Util.primitiveToBoxed(cls);
		if (targetAsClass == null)
			targetAsClass = targetClass;

		sourceClass = sourceAsClass != null ? sourceAsClass : object.getClass();

		if (!isCopyRequiredType(targetAsClass)
				&& targetAsClass.isAssignableFrom(sourceClass)) {
			return (T) object;
		}

		Object res = trySpecialCases(converter);
		if (res != null)
			return (T) res;

		if (targetAsClass.isArray()) {
			return convertToArray(targetAsClass.getComponentType(),
					targetAsClass.getComponentType(), converter);
		} else if (type instanceof GenericArrayType) {
			return convertToArray(targetAsClass,
					((GenericArrayType) type).getGenericComponentType(),
					converter);
		} else if (Collection.class.isAssignableFrom(targetAsClass)) {
			return convertToCollectionType(converter);
		} else if (isMapType(targetAsClass, targetAsJavaBean, targetAsDTO)) {
			return (T) convertToMapType(converter);
		}

		// At this point we know that the target is a 'singular' type: not a
		// map, collection or array
		if (Collection.class.isAssignableFrom(sourceClass)) {
			return (T) convertCollectionToSingleValue(targetAsClass, converter);
		} else if (isMapType(sourceClass, sourceAsJavaBean, sourceAsDTO)) {
			return (T) convertMapToSingleValue(targetAsClass, converter);
		} else if (object instanceof Map.Entry) {
			return (T) convertMapEntryToSingleValue(targetAsClass, converter);
		} else if ((object = asBoxedArray(object)) instanceof Object[]) {
			return (T) convertArrayToSingleValue(targetAsClass, converter);
		}

		Object res2 = tryStandardMethods();
		if (res2 != null) {
			return (T) res2;
		} else {
			if (hasDefault)
				return (T) converter.convert(defaultValue)
						.sourceAs(sourceAsClass)
						.targetAs(targetAsClass)
						.to(targetClass);
			else
				throw new ConversionException(
						"Cannot convert " + object + " to " + targetAsClass);
		}
	}

	private Object convertArrayToSingleValue(Class< ? > cls,
			InternalConverter converter) {
		Object[] arr = (Object[]) object;
		if (arr.length == 0)
			return null;
		else
			return converter.convert(arr[0]).to(cls);
	}

	private Object convertCollectionToSingleValue(Class< ? > cls,
			InternalConverter converter) {
		Collection< ? > coll = (Collection< ? >) object;
		if (coll.size() == 0)
			return null;
		else
			return converter.convert(coll.iterator().next()).to(cls);
	}

	private Object convertMapToSingleValue(Class< ? > cls,
			InternalConverter converter) {
		Map< ? , ? > m = mapView(object, sourceClass, converter);
		if (m.size() > 0) {
			return converter.convert(m.entrySet().iterator().next()).to(cls);
		} else {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private Object convertMapEntryToSingleValue(Class< ? > cls,
			InternalConverter converter) {
		Map.Entry entry = (Map.Entry) object;

		Class keyCls = entry.getKey() != null ? entry.getKey().getClass()
				: null;
		Class valueCls = entry.getValue() != null ? entry.getValue().getClass()
				: null;

		if (cls.equals(keyCls)) {
			return converter.convert(entry.getKey()).to(cls);
		} else if (cls.equals(valueCls)) {
			return converter.convert(entry.getValue()).to(cls);
		} else if (keyCls != null && cls.isAssignableFrom(keyCls)) {
			return converter.convert(entry.getKey()).to(cls);
		} else if (valueCls != null && cls.isAssignableFrom(valueCls)) {
			return converter.convert(entry.getValue()).to(cls);
		} else if (entry.getKey() instanceof String) {
			return converter.convert(entry.getKey()).to(cls);
		} else if (entry.getValue() instanceof String) {
			return converter.convert(entry.getValue()).to(cls);
		}

		return converter
				.convert(converter.convert(entry.getKey()).to(String.class))
				.to(cls);
	}

	@SuppressWarnings("unchecked")
	private <T> T convertToArray(Class< ? > componentClz, Type componentType,
			InternalConverter converter) {
		Collection< ? > collectionView = collectionView(converter);
		Iterator< ? > iterator = collectionView.iterator();
		try {
			Object array = Array.newInstance(componentClz,
					collectionView.size());
			for (int i = 0; i < collectionView.size()
					&& iterator.hasNext(); i++) {
				Object next = iterator.next();
				Object converted = converter.convert(next).to(componentType);
				Array.set(array, i, converted);
			}
			return (T) array;
		} catch (Exception e) {
			throw new ConversionException(
					"Cannot iterate over " + collectionView.getClass(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T convertToCollectionType(InternalConverter converter) {
		Collection< ? > res = convertToCollectionDelegate(converter);
		if (res != null)
			return (T) res;

		return convertToCollection(converter);
	}

	@SuppressWarnings("unchecked")
	private <T> T convertToOptionalType(InternalConverter converter) {

		if (typeArguments != null) {
			Object o = converter.convert(object).to(typeArguments[0]);
			return (T) Optional.ofNullable(o);
		}

		return (T) Optional.ofNullable(object);

	}

	@SuppressWarnings("unchecked")
	private <T> T convertToOptionalInt(InternalConverter converter) {

		Integer i = converter.convert(object).to(Integer.class);
		return (T) i == null ? (T) OptionalInt.empty()
				: (T) OptionalInt.of(i.intValue());

	}

	@SuppressWarnings("unchecked")
	private <T> T convertToOptionalDouble(InternalConverter converter) {

		Double d = converter.convert(object).to(Double.class);
		return (T) d == null ? (T) OptionalDouble.empty()
				: (T) OptionalDouble.of(d.doubleValue());
	}

	@SuppressWarnings("unchecked")
	private <T> T convertToOptionalLong(InternalConverter converter) {

		Long l = converter.convert(object).to(Long.class);
		return (T) l == null ? (T) OptionalLong.empty()
				: (T) OptionalLong.of(l.longValue());

	}

	private Collection< ? > convertToCollectionDelegate(
			InternalConverter converter) {
		if (!liveView)
			return null;

		if (List.class.equals(targetClass)
				|| Collection.class.equals(targetClass)) {
			if (sourceClass.isArray()) {
				return ListDelegate.forArray(object, this, converter);
			} else if (Collection.class.isAssignableFrom(sourceClass)) {
				return ListDelegate.forCollection((Collection< ? >) object,
						this, converter);
			}
		} else if (Set.class.equals(targetClass)) {
			if (sourceClass.isArray()) {
				return SetDelegate.forCollection(
						ListDelegate.forArray(object, this, converter), this,
						converter);
			} else if (Collection.class.isAssignableFrom(sourceClass)) {
				return SetDelegate.forCollection((Collection< ? >) object, this,
						converter);
			}
		}
		return null;
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	private <T> T convertToCollection(InternalConverter converter) {
		Collection< ? > cv = collectionView(converter);
		Class< ? > targetElementType = null;
		if (typeArguments != null && typeArguments.length > 0
				&& typeArguments[0] instanceof Class) {
			targetElementType = (Class< ? >) typeArguments[0];
		}

		Class< ? > ctrCls = INTERFACE_IMPLS.get(targetAsClass);
		Class< ? > targetCls;
		if (ctrCls != null)
			targetCls = ctrCls;
		else
			targetCls = targetAsClass;

		Collection instance = (Collection) createMapOrCollection(targetCls,
				cv.size());
		if (instance == null)
			return null;

		for (Object o : cv) {
			if (targetElementType != null) {
				try {
					o = converter.convert(o).to(targetElementType);
				} catch (ConversionException ce) {
					if (hasDefault) {
						return (T) defaultValue;
					}
				}
			}

			instance.add(o);
		}

		return (T) instance;
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	private <T> T convertToDTO(Class< ? > sourceCls, Class< ? > targetAsCls,
			InternalConverter converter) {
		Map m = mapView(object, sourceCls, converter);

		try {
			String prefix = Util.getPrefix(targetAsCls);

			T dto = (T) targetClass.newInstance();

			List<String> names = getNames(targetAsClass);
			for (Map.Entry entry : (Set<Map.Entry>) m.entrySet()) {
				Object key = entry.getKey();
				if (key == null)
					continue;

				String fieldName = Util.mangleName(prefix, key.toString(),
						names);
				if (fieldName == null)
					continue;

				Field f = null;
				try {
					f = targetAsCls.getField(fieldName);
				} catch (NoSuchFieldException e) {
					// There is no field with this name
					if (keysIgnoreCase) {
						// If enabled, try again but now ignore case
						for (Field fs : targetAsCls.getFields()) {
							if (fs.getName().equalsIgnoreCase(fieldName)) {
								f = fs;
								break;
							}
						}

						if (f == null) {
							for (Field fs : targetAsCls.getFields()) {
								if (fs.getName().equalsIgnoreCase(fieldName)) {
									f = fs;
									break;
								}
							}
						}
					}
				}

				if (f != null) {
					Object val = entry.getValue();
					// Force strict DTO type (constructible)
					if (sourceAsDTO && DTOUtil.isDTOType(f.getType(), false))
						val = converter.convert(val)
								.sourceAsDTO()
								.to(f.getType());
					else {
						Type genericType = reifyType(f.getGenericType(),
								targetAsClass, typeArguments);
						val = converter.convert(val).to(genericType);
					}
					f.set(dto, val);
				}
			}

			return dto;
		} catch (Exception e) {
			throw new ConversionException("Cannot create DTO " + targetClass,
					e);
		}
	}

	static Type reifyType(Type typeToReify, Class< ? > ownerClass,
			Type[] typeArgs) {

		if (typeToReify instanceof TypeVariable) {
			String name = ((TypeVariable< ? >) typeToReify).getName();
			for (int i = 0; i < ownerClass.getTypeParameters().length; i++) {
				TypeVariable< ? > typeVariable = ownerClass
						.getTypeParameters()[i];
				if (typeVariable.getName().equals(name)) {
					return typeArgs[i];
				}
			}

			// The direct type variable wasn't found, maybe it was already
			// bound in this class.

			Type currentType = ownerClass;
			while (currentType != null) {
				if (currentType instanceof Class) {
					currentType = ((Class< ? >) currentType)
							.getGenericSuperclass();
				} else if (currentType instanceof ParameterizedType) {
					currentType = ((ParameterizedType) currentType)
							.getRawType();
				}

				if (currentType instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) currentType;
					Type rawType = pt.getRawType();
					if (rawType instanceof Class) {
						return reifyType(typeToReify, (Class< ? >) rawType,
								pt.getActualTypeArguments());
					}
				}
			}
		} else if (typeToReify instanceof ParameterizedType) {
			final ParameterizedType parameterizedType = (ParameterizedType) typeToReify;
			Type[] parameters = parameterizedType.getActualTypeArguments();
			boolean useCopy = false;
			final Type[] copiedParameters = new Type[parameters.length];

			for (int i = 0; i < parameters.length; i++) {
				copiedParameters[i] = reifyType(parameters[i], ownerClass,
						typeArgs);
				useCopy |= copiedParameters[i] != parameters[i];
			}

			if (useCopy) {
				return new ParameterizedType() {

					@Override
					public Type getRawType() {
						return parameterizedType.getRawType();
					}

					@Override
					public Type getOwnerType() {
						return parameterizedType.getOwnerType();
					}

					@Override
					public Type[] getActualTypeArguments() {
						return Arrays.copyOf(copiedParameters,
								copiedParameters.length);
					}
				};
			}
		} else if (typeToReify instanceof GenericArrayType) {
			GenericArrayType type = (GenericArrayType) typeToReify;
			Type genericComponentType = type.getGenericComponentType();
			final Type reifiedType = reifyType(genericComponentType, ownerClass,
					typeArgs);

			if (reifiedType != genericComponentType) {
				return new GenericArrayType() {

					@Override
					public Type getGenericComponentType() {
						return reifiedType;
					}
				};
			}
		}

		return typeToReify;
	}

	private List<String> getNames(Class< ? > cls) {
		List<String> names = new ArrayList<>();
		for (Field field : cls.getFields()) {
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers))
				continue;

			String name = field.getName();
			if (!names.contains(name))
				names.add(name);

		}
		return names;
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	private Map convertToMap(InternalConverter converter) {
		Map m = mapView(object, sourceClass, converter);
		if (m == null)
			return null;

		Class< ? > ctrCls = INTERFACE_IMPLS.get(targetClass);
		if (ctrCls == null)
			ctrCls = targetClass;

		Map instance = (Map) createMapOrCollection(ctrCls, m.size());
		if (instance == null)
			return null;

		for (Map.Entry entry : (Set<Entry>) m.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			key = convertMapKey(key, converter);
			value = convertMapValue(value, converter);
			instance.put(key, value);
		}

		return instance;
	}

	Object convertCollectionValue(Object element, InternalConverter converter) {
		Type type = null;
		if (typeArguments != null && typeArguments.length > 0) {
			type = typeArguments[0];
		}

		if (element != null) {
			if (type != null) {
				element = converter.convert(element).to(type);
			} else {
				Class< ? > cls = element.getClass();
				if (isCopyRequiredType(cls)) {
					cls = getConstructableType(cls);
				}
				// Either force source as DTO, or lenient DTO type
				if (sourceAsDTO || DTOUtil.isDTOType(cls, true))
					element = converter.convert(element).sourceAsDTO().to(cls);
				else
					element = converter.convert(element).to(cls);
			}
		}
		return element;
	}

	Object convertMapKey(Object key, InternalConverter converter) {
		return convertMapElement(key, 0, converter);
	}

	Object convertMapValue(Object value, InternalConverter converter) {
		return convertMapElement(value, 1, converter);
	}

	private Object convertMapElement(Object element, int typeIdx,
			InternalConverter converter) {
		Type type = null;
		if (typeArguments != null && typeArguments.length > typeIdx) {
			type = typeArguments[typeIdx];
		}

		if (element != null) {
			if (type != null) {
				element = converter.convert(element).to(type);
			} else {
				Class< ? > cls = element.getClass();
				if (isCopyRequiredType(cls)) {
					cls = getConstructableType(cls);
				}
				// Either force source as DTO, or lenient DTO type
				if (sourceAsDTO || DTOUtil.isDTOType(cls, true))
					element = converter.convert(element).sourceAsDTO().to(cls);
				else
					element = converter.convert(element).to(cls);
			}
		}
		return element;
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	private Map convertToMapDelegate(InternalConverter converter) {
		if (Map.class.isAssignableFrom(sourceClass)) {
			return MapDelegate.forMap((Map) object, this, converter);
		} else if (Dictionary.class.isAssignableFrom(sourceClass)) {
			return MapDelegate.forDictionary((Dictionary) object, this,
					converter);
		} else if (DTOUtil.isDTOType(sourceClass, true) || sourceAsDTO) {
			return MapDelegate.forDTO(object, sourceClass, this, converter);
		} else if (sourceAsJavaBean) {
			return MapDelegate.forBean(object, sourceClass, this, converter);
		} else if (hasGetProperties(sourceClass)) {
			return null; // Handled in convertToMap()
		}

		// Assume it's an interface
		Set<Class< ? >> interfaces = getInterfaces(sourceClass);
		if (interfaces.size() > 0) {
			return MapDelegate.forInterface(object,
					interfaces.iterator().next(), this, converter);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private Object convertToMapType(InternalConverter converter) {
		if (!isMapType(sourceClass, sourceAsJavaBean, sourceAsDTO)) {
			throw new ConversionException(
					"Cannot convert " + object + " to " + targetAsClass);
		}

		if (Map.class.equals(targetClass) && liveView) {
			Map res = convertToMapDelegate(converter);
			if (res != null)
				return res;
		}

		if (Map.class.isAssignableFrom(targetAsClass))
			return convertToMap(converter);
		else if (Dictionary.class.isAssignableFrom(targetAsClass))
			return convertToDictionary(converter);
		else if (targetAsDTO || DTOUtil.isDTOType(targetAsClass, false))
			return convertToDTO(sourceClass, targetAsClass, converter);
		else if (targetAsClass.isInterface())
			return convertToInterface(sourceClass, targetAsClass, converter);
		else if (targetAsJavaBean)
			return convertToJavaBean(sourceClass, targetAsClass, converter);
		throw new ConversionException(
				"Cannot convert " + object + " to " + targetAsClass);
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	private Object convertToDictionary(InternalConverter converter) {
		return new Hashtable(
				(Map) converter.convert(object).to(new ParameterizedType() {
					@Override
					public Type getRawType() {
						return HashMap.class;
					}

					@Override
					public Type getOwnerType() {
						return null;
					}

					@SuppressWarnings("synthetic-access")
					@Override
					public Type[] getActualTypeArguments() {
						return typeArguments;
					}
				}));
	}

	private Object convertToJavaBean(Class< ? > sourceCls, Class< ? > targetCls,
			InternalConverter converter) {
		String prefix = Util.getPrefix(targetCls);

		@SuppressWarnings("rawtypes")
		Map m = mapView(object, sourceCls, converter);
		try {
			Object res = targetClass.newInstance();
			for (Method setter : getSetters(targetCls)) {
				String setterName = setter.getName();
				StringBuilder propName = new StringBuilder(
						setterName.length() - 3).append(
								Character.toLowerCase(setterName.charAt(3)))
								.append(setterName.substring(4));

				Class< ? > setterType = setter.getParameterTypes()[0];
				String key = propName.toString();
				Object val = m.get(Util.unMangleName(prefix, key));
				setter.invoke(res, converter.convert(val).to(setterType));
			}
			return res;
		} catch (Exception e) {
			throw new ConversionException(
					"Cannot convert to class: " + targetCls.getName()
							+ ". Not a JavaBean with a Zero-arg Constructor.",
					e);
		}
	}

	@SuppressWarnings("rawtypes")
	private Object convertToInterface(Class< ? > sourceCls,
			final Class< ? > targetCls, InternalConverter converter) {
		InternalConverting ic = converter.convert(object);
		ic.sourceAs(sourceAsClass).view();
		if (sourceAsDTO)
			ic.sourceAsDTO();
		if (sourceAsJavaBean)
			ic.sourceAsBean();
		final Map m = ic.to(Map.class);

		return createProxy(targetCls, m, converter);
	}

	private Object createProxy(final Class< ? > cls, final Map< ? , ? > data,
			final InternalConverter converter) {
		return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {
				cls
		}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				Class< ? > mdDecl = method.getDeclaringClass();
				if (mdDecl.equals(Object.class))
					switch (method.getName()) {
						case "equals" :
							return Boolean.valueOf(proxy == args[0]);
						case "hashCode" :
							return Integer
									.valueOf(System.identityHashCode(proxy));
						case "toString" :
							return "Proxy for " + cls;
						default :
							throw new UnsupportedOperationException("Method "
									+ method + " not supported on proxy for "
									+ cls);
					}
				if (mdDecl.equals(Annotation.class)) {
					if ("annotationType".equals(method.getName())
							&& method.getParameterTypes().length == 0) {
						return cls;
					}
				}

				String propName = Util.getInterfacePropertyName(method,
						Util.getSingleElementAnnotationKey(cls, proxy), proxy);
				if (propName == null) {
					throw new ConversionException(
							"Can not convert. Calculated propertyName is `null` method: "
									+ method);
				}
				Object val = null;
				boolean handled = false;
				if (data.containsKey(propName)) {
					val = data.get(propName);
					handled = true;
				} else if (val == null && keysIgnoreCase) {
					// try in a case-insensitive way
					for (Iterator< ? > it = data.keySet().iterator(); it
							.hasNext() && val == null;) {
						String k = it.next().toString();
						if (propName.equalsIgnoreCase(k)) {
							val = data.get(k);
							handled = true;
							break;
						}
					}
				}

				// If no value is available take the default if specified
				if (!handled) {
					if (cls.isAnnotation()) {
						val = method.getDefaultValue();
						// still handled=false, annotations could not return
						// null
					} else if (method.isDefault()) {
						try {
							double javaVersion = Double.parseDouble(
									System.getProperty("java.class.version"));
							double java8 = 52.0d;
							if (javaVersion > java8) {
								val = MethodHandles.lookup()
										.findSpecial(method.getDeclaringClass(),
												method.getName(),
												MethodType.methodType(
														method.getReturnType(),
														new Class[] {}),
												method.getDeclaringClass())
										.bindTo(proxy)
										.invokeWithArguments(args);
								handled = true;
							} else {
								Constructor<Lookup> constructor = Lookup.class
										.getDeclaredConstructor(Class.class);
								if (!constructor.isAccessible()) {
									constructor.setAccessible(true);
								}
								val = constructor.newInstance(cls)
										.in(cls)
										.unreflectSpecial(method, cls)
										.bindTo(proxy)
										.invokeWithArguments(args);
								handled = true;
							}
						} catch (Exception e) {
							throw new ConversionException(
									"Can not convert. Exception is thrown in default method: "
											+ method,
									e);
						}
					}

					if (!handled) {
						if (val == null) {
							if (args != null && args.length == 1) {
								val = args[0];
							} else {
								throw new ConversionException(
										"No value for property: " + propName);
							}
						}
					}
				}

				@SuppressWarnings("synthetic-access")
				Type genericType = reifyType(method.getGenericReturnType(),
						targetAsClass, typeArguments);
				return converter.convert(val).to(genericType);
			}
		});
	}

	private Object handleNull(Class< ? > cls, InternalConverter converter) {
		if (hasDefault)
			return converter.convert(defaultValue).to(cls);

		Class< ? > boxed = Util.primitiveToBoxed(cls);
		if (boxed.equals(cls)) {
			if (cls.isArray()) {
				int i = 1;
				Class< ? > componentType = cls.getComponentType();
				while (componentType.isArray()) {
					i++;
					componentType = componentType.getComponentType();
				}

				if (i == 1) {
					return Array.newInstance(componentType, 0);
				} else {
					return Array.newInstance(componentType, new int[i]);
				}
			} else if (Collection.class.isAssignableFrom(cls)) {
				return converter.convert(Collections.emptyList()).to(cls);
			}
			else if (Optional.class.equals(cls)) {
				return Optional.empty();
			} else if (OptionalInt.class.equals(cls)) {
				return OptionalInt.empty();
			} else if (OptionalDouble.class.equals(cls)) {
				return OptionalDouble.empty();
			} else if (OptionalLong.class.equals(cls)) {
				return OptionalLong.empty();
			}

			// This is not a primitive, just return null
			return null;
		}

		return converter.convert(Integer.valueOf(0)).to(cls);
	}

	private static boolean isMapType(Class< ? > cls, boolean asJavaBean,
			boolean asDTO) {
		if (asDTO)
			return true;

		// All interface types that are not Collections are treated as maps
		if (Map.class.isAssignableFrom(cls))
			return true;
		if (Annotation.class.isAssignableFrom(cls))
			return true;
		if (getInterfaces(cls).size() > 0)
			return true;
		if (DTOUtil.isDTOType(cls, true))
			return true;
		if (asJavaBean && isWriteableJavaBean(cls))
			return true;
		return Dictionary.class.isAssignableFrom(cls);
	}

	private Object trySpecialCases(InternalConverter converter) {
		if (Boolean.class.equals(targetAsClass)) {
			if (object instanceof Collection
					&& ((Collection< ? >) object).size() == 0) {
				return Boolean.FALSE;
			}
		} else if (Number.class.isAssignableFrom(targetAsClass)) {
			Number value;
			if (object instanceof Boolean) {
				value = ((Boolean) object).booleanValue() ? Integer.valueOf(1)
						: Integer.valueOf(0);
			} else if (object instanceof Number) {
				value = (Number) object;
			} else {
				value = null;
			}
			if (value != null) {
				if (Byte.class.isAssignableFrom(targetAsClass)) {
					return Byte.valueOf(value.byteValue());
				} else if (Short.class.isAssignableFrom(targetAsClass)) {
					return Short.valueOf(value.shortValue());
				} else if (Integer.class.isAssignableFrom(targetAsClass)) {
					return Integer.valueOf(value.intValue());
				} else if (Long.class.isAssignableFrom(targetAsClass)) {
					return Long.valueOf(value.longValue());
				} else if (Float.class.isAssignableFrom(targetAsClass)) {
					return Float.valueOf(value.floatValue());
				} else if (Double.class.isAssignableFrom(targetAsClass)) {
					return Double.valueOf(value.doubleValue());
				}
			}
		} else if (targetAsClass.isEnum()) {
			if (object instanceof Number) {
				try {
					Method m = targetAsClass.getMethod("values");
					Object[] values = (Object[]) m.invoke(null);
					return values[((Number) object).intValue()];
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				try {
					Method m = targetAsClass.getMethod("valueOf", String.class);
					return m.invoke(null, object.toString());
				} catch (Exception e) {
					try {
						// Case insensitive fallback
						Method m = targetAsClass.getMethod("values");
						for (Object v : (Object[]) m.invoke(null)) {
							if (v.toString()
									.equalsIgnoreCase(object.toString())) {
								return v;
							}
						}
					} catch (Exception e1) {
						throw new RuntimeException(e1);
					}
				}
			}
		} else if (Annotation.class.isAssignableFrom(sourceClass)
				&& isMarkerAnnotation(sourceClass)) {
			// Special treatment for marker annotations
			String key = Util.getMarkerAnnotationKey(sourceClass, object);
			return converter
					.convert(Collections.singletonMap(key, Boolean.TRUE))
					.targetAs(targetAsClass)
					.to(targetType);
		} else if (Annotation.class.isAssignableFrom(targetAsClass)
				&& isMarkerAnnotation(targetAsClass)) {
			Map<String,Boolean> representation = Converters.standardConverter()
					.convert(object)
					.to(new TypeReference<Map<String,Boolean>>() {
						/* empty subclass */
					});
			if (Boolean.TRUE.equals(
					representation.get(Util.toSingleElementAnnotationKey(
							targetAsClass.getSimpleName())))) {
				return createProxy(targetClass, Collections.emptyMap(),
						converter);
			} else {
				throw new ConversionException("Cannot convert " + object
						+ " to marker annotation " + targetAsClass);
			}
		} else if (Optional.class.equals(targetAsClass)) {
			return convertToOptionalType(converter);
		} else if (OptionalInt.class.equals(targetAsClass)) {
			return convertToOptionalInt(converter);
		} else if (OptionalDouble.class.equals(targetAsClass)) {
			return convertToOptionalDouble(converter);
		} else if (OptionalLong.class.equals(targetAsClass)) {
			return convertToOptionalLong(converter);
		}
		return null;
	}

	private static boolean isMarkerAnnotation(Class< ? > annClass) {
		for (Method m : annClass.getMethods()) {
			if (m.getDeclaringClass() != annClass) {
				// this is a base annotation or object method
				continue;
			}
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private <T> T tryStandardMethods() {
		try {
			// Section 707.4.2.3 and 707.4.2.5 require valueOf to be public and
			// static
			Method m = targetAsClass.getMethod("valueOf", String.class);
			if (m != null && Modifier.isStatic(m.getModifiers())) {
				return (T) m.invoke(null, object.toString());
			}
		} catch (Exception e) {
			try {
				Constructor< ? > ctr = targetAsClass
						.getConstructor(String.class);
				return (T) ctr.newInstance(object.toString());
			} catch (Exception e2) {
				// Ignore
			}
		}
		return null;
	}

	private Collection< ? > collectionView(InternalConverter converter) {
		if (object == null)
			return null;

		Collection< ? > c = asCollection(converter);
		if (c == null)
			return Collections.singleton(object);
		else
			return c;
	}

	private Collection< ? > asCollection(InternalConverter converter) {
		if (object instanceof Collection)
			return (Collection< ? >) object;
		else if ((object = asBoxedArray(object)) instanceof Object[])
			return Arrays.asList((Object[]) object);
		else if (isMapType(sourceClass, sourceAsJavaBean, sourceAsDTO))
			return mapView(object, sourceClass, converter).entrySet();
		else
			return null;
	}

	private static Object asBoxedArray(Object obj) {
		Class< ? > objClass = obj.getClass();
		if (!objClass.isArray())
			return obj;

		int len = Array.getLength(obj);
		Object arr = Array.newInstance(
				Util.primitiveToBoxed(objClass.getComponentType()), len);
		for (int i = 0; i < len; i++) {
			Object val = Array.get(obj, i);
			Array.set(arr, i, val);
		}
		return arr;
	}

	@SuppressWarnings("rawtypes")
	private static Map createMapFromBeanAccessors(Object obj,
			Class< ? > sourceCls) {
		Set<String> invokedMethods = new HashSet<>();

		Map result = new HashMap();
		// Bean accessors must be public
		for (Method md : sourceCls.getMethods()) {
			handleBeanMethod(obj, md, invokedMethods, result);
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	private Map createMapFromDTO(Object obj, InternalConverter converter) {
		Set<String> handledFields = new HashSet<>();

		Map result = new HashMap();
		// We only use public fields for mapping a DTO
		for (Field f : obj.getClass().getFields()) {
			handleDTOField(obj, f, handledFields, result, converter);
		}
		return result;
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	private static Map createMapFromInterface(Object obj, Class< ? > srcCls) {
		Map result = new HashMap();

		if (Annotation.class.isAssignableFrom(srcCls)
				&& isMarkerAnnotation(((Annotation) obj).annotationType())) {
			// We special case this if the source is a marker annotation because
			// we will end up with no
			// interface methods otherwise
			result.put(
					Util.getMarkerAnnotationKey(
							((Annotation) obj).annotationType(), obj),
					Boolean.TRUE);
			return result;
		} else {
			for (Class i : getInterfaces(srcCls)) {
				for (Method md : i.getMethods()) {
					handleInterfaceMethod(obj, i, md, new HashSet<String>(),
							result);
				}
				if (result.size() > 0)
					return result;
			}
		}
		throw new ConversionException("Cannot be converted to map: " + obj);
	}

	private static Object createMapOrCollection(Class< ? > cls,
			int initialSize) {
		try {
			Constructor< ? > ctor = cls.getConstructor(int.class);
			return ctor.newInstance(Integer.valueOf(initialSize));
		} catch (Exception e1) {
			try {
				Constructor< ? > ctor2 = cls.getConstructor();
				return ctor2.newInstance();
			} catch (Exception e2) {
				// ignore
			}
		}
		return null;
	}

	private static Class< ? > getConstructableType(Class< ? > targetCls) {
		if (targetCls.isArray())
			return targetCls;

		Class< ? > cls = targetCls;
		do {
			try {
				cls.getConstructor(int.class);
				return cls; // If no exception the constructor is there
			} catch (NoSuchMethodException e) {
				try {
					cls.getConstructor();
					return cls; // If no exception the constructor is there
				} catch (NoSuchMethodException e1) {
					// There is no constructor with this name
				}
			}
			for (Class< ? > intf : cls.getInterfaces()) {
				Class< ? > impl = INTERFACE_IMPLS.get(intf);
				if (impl != null)
					return impl;
			}

			cls = cls.getSuperclass();
		} while (!Object.class.equals(cls));

		return null;
	}

	// Returns an ordered set
	private static Set<Class< ? >> getInterfaces(Class< ? > cls) {
		if (NO_MAP_VIEW_TYPES.contains(cls))
			return Collections.emptySet();

		Set<Class< ? >> interfaces = getInterfaces0(cls);
		outer: for (Iterator<Class< ? >> it = interfaces.iterator(); it
				.hasNext();) {
			Class< ? > intf = it.next();
			Method[] methods = intf.getMethods();
			for (Method method : methods) {
				if (method.getDeclaringClass() == intf) {
					continue outer;
				}
			}
			if (intf == cls && methods.length == 0) {
				continue outer;
			}
			it.remove();
		}

		interfaces.removeAll(NO_MAP_VIEW_TYPES);

		return interfaces;
	}

	// Returns an ordered set
	private static Set<Class< ? >> getInterfaces0(Class< ? > cls) {
		if (cls == null)
			return Collections.emptySet();

		Set<Class< ? >> classes = new LinkedHashSet<>();
		if (cls.isInterface()) {
			classes.add(cls);
		}
		for (Class< ? > intf : cls.getInterfaces()) {
			classes.addAll(getInterfaces(intf));
		}

		classes.addAll(getInterfaces(cls.getSuperclass()));

		return classes;
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	private void handleDTOField(Object obj, Field field,
			Set<String> handledFields, Map result,
			InternalConverter converter) {
		String fn = Util.getDTOKey(field);
		if (fn == null)
			return;

		if (handledFields.contains(fn))
			return; // Field with this name was already handled

		try {
			Object fVal = field.get(obj);
			result.put(fn, fVal);
			handledFields.add(fn);
		} catch (Exception e) {
			// Ignore
		}
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	private static void handleBeanMethod(Object obj, Method md,
			Set<String> invokedMethods, Map res) {
		String bp = Util.getBeanKey(md);
		if (bp == null)
			return;

		if (invokedMethods.contains(bp))
			return; // method with this name already invoked

		try {
			res.put(bp, md.invoke(obj));
			invokedMethods.add(bp);
		} catch (Exception e) {
			// Ignore
		}
	}

	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	private static void handleInterfaceMethod(Object obj, Class< ? > intf,
			Method md, Set<String> invokedMethods, Map res) {
		String mn = md.getName();
		if (invokedMethods.contains(mn))
			return; // method with this name already invoked

		String propName = Util.getInterfacePropertyName(md,
				Util.getSingleElementAnnotationKey(intf, obj), obj);
		if (propName == null)
			return;

		try {
			Object r = Util.getInterfaceProperty(obj, md);
			if (r == null)
				return;

			res.put(propName, r);
			invokedMethods.add(mn);
		} catch (Exception e) {
			// Ignore
		}
	}

	private Map< ? , ? > mapView(Object obj, Class< ? > sourceCls,
			InternalConverter converter) {
		if (Map.class.isAssignableFrom(sourceCls)
				|| (DTOUtil.isDTOType(sourceCls, true) && obj instanceof Map))
			return (Map< ? , ? >) obj;
		else if (Dictionary.class.isAssignableFrom(sourceCls))
			return MapDelegate.forDictionary((Dictionary< ? , ? >) object, this,
					converter);
		else if (DTOUtil.isDTOType(sourceCls, true) || sourceAsDTO)
			return createMapFromDTO(obj, converter);
		else if (sourceAsJavaBean) {
			Map< ? , ? > m = createMapFromBeanAccessors(obj, sourceCls);
			if (m.size() > 0)
				return m;
		} else if (hasGetProperties(sourceCls)) {
			return getPropertiesDelegate(obj, sourceCls, converter);
		}
		return createMapFromInterface(obj, sourceClass);
	}

	private boolean hasGetProperties(Class< ? > cls) {
		try {
			// Section 707.4.4.4.8 says getProperties must be public
			Method m = cls.getMethod("getProperties");
			return m != null;
		} catch (Exception e) {
			return false;
		}
	}

	private Map< ? , ? > getPropertiesDelegate(Object obj, Class< ? > cls,
			InternalConverter converter) {
		try {
			// Section 707.4.4.4.8 says getProperties must be public
			Method m = cls.getMethod("getProperties");

			return converter.convert(m.invoke(obj)).to(Map.class);
		} catch (Exception e) {
			return Collections.emptyMap();
		}
	}

	private static boolean isCopyRequiredType(Class< ? > cls) {
		if (cls.isEnum())
			return false;
		return Map.class.isAssignableFrom(cls)
				|| Collection.class.isAssignableFrom(cls)
				|| DTOUtil.isDTOType(cls, true) || cls.isArray();
	}

	private static boolean isWriteableJavaBean(Class< ? > cls) {
		boolean hasNoArgCtor = false;
		for (Constructor< ? > ctor : cls.getConstructors()) {
			if (ctor.getParameterTypes().length == 0)
				hasNoArgCtor = true;
		}
		if (!hasNoArgCtor)
			return false; // A JavaBean must have a public no-arg constructor

		return getSetters(cls).size() > 0;
	}

	private static Set<Method> getSetters(Class< ? > cls) {
		Set<Method> setters = new HashSet<>();
		while (!Object.class.equals(cls)) {
			Set<Method> methods = new HashSet<>();
			// Only public methods can be Java Bean setters
			methods.addAll(Arrays.asList(cls.getMethods()));
			for (Method md : methods) {
				if (md.getParameterTypes().length != 1)
					continue; // Only setters with a single argument
				String name = md.getName();
				if (name.length() < 4)
					continue;
				if (name.startsWith("set")
						&& Character.isUpperCase(name.charAt(3)))
					setters.add(md);
			}
			cls = cls.getSuperclass();
		}
		return setters;
	}
}
