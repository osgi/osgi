/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.dto;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Super type for Data Transfer Objects.
 * 
 * All data transfer objects are easily serializable having only public fields
 * of primitive types and their wrapper classes, Strings, and DTOs. List, Set,
 * Map and array aggregates may also be used. The aggregates must only hold
 * objects of the listed types or aggregates.
 * 
 * @author $Id$
 * @NotThreadSafe
 */
public abstract class DTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Return a string representation of this DTO suitable for use when
     * debugging.
     * 
     * <p>
     * The format of the string representation is not specified and subject to
     * change.
     * 
     * @return A string representation of this DTO suitable for use when
     *         debugging.
     */
    @Override
    public String toString() {
        return appendValue(new StringBuilder(), new IdentityHashMap<Object, String>(), "#", this).toString();
    }

    /**
     * Append the specified DTO's string representation to the specified
     * StringBuilder.
     * 
     * <p>
     * This method handles circular DTO references.
     * 
     * @param result StringBuilder to which the string representation is
     *        appended.
     * @param objectRefs References to "seen" objects.
     * @param refpath The reference path of the specified DTO.
     * @param dto The DTO whose string representation is to be appended.
     * @return The specified StringBuilder.
     */
    private static StringBuilder appendDTO(final StringBuilder result, final Map<Object, String> objectRefs, final String refpath, final DTO dto) {
        result.append("{");
        String delim = "";
        for (Field field : dto.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            result.append(delim);
            final String name = field.getName();
            appendString(result, name);
            result.append(":");
            Object value = null;
            try {
                value = field.get(dto);
            } catch (IllegalAccessException e) {
                // use null value;
            }
            appendValue(result, objectRefs, refpath + "/" + name, value);
            delim = ", ";
        }
        result.append("}");
        return result;
    }

    /**
     * Append the specified value's string representation to the specified
     * StringBuilder.
     * 
     * @param result StringBuilder to which the string representation is
     *        appended.
     * @param objectRefs References to "seen" objects.
     * @param refpath The reference path of the specified value.
     * @param value The object whose string representation is to be appended.
     * @return The specified StringBuilder.
     */
    private static StringBuilder appendValue(final StringBuilder result, final Map<Object, String> objectRefs, final String refpath, final Object value) {
        if (value == null) {
            return result.append("null");
        }
        // Simple Java types
        if (value instanceof String || value instanceof Character) {
            return appendString(result, compress(value.toString()));
        }
        if (value instanceof Number || value instanceof Boolean) {
            return result.append(value.toString());
        }

        // Complex types
        final String path = objectRefs.get(value);
        if (path != null) {
            result.append("{\"$ref\":");
            appendString(result, path);
            result.append("}");
            return result;
        }
        objectRefs.put(value, refpath);

        if (value instanceof DTO) {
            return appendDTO(result, objectRefs, refpath, (DTO) value);
        }
        if (value instanceof Map) {
            return appendMap(result, objectRefs, refpath, (Map<?, ?>) value);
        }
        if (value instanceof List || value instanceof Set) {
            return appendIterable(result, objectRefs, refpath, (Iterable<?>) value);
        }
        if (value.getClass().isArray()) {
            return appendArray(result, objectRefs, refpath, value);
        }
        return appendString(result, compress(value.toString()));
    }

    /**
     * Append the specified array's string representation to the specified
     * StringBuilder.
     * 
     * @param result StringBuilder to which the string representation is
     *        appended.
     * @param objectRefs References to "seen" objects.
     * @param refpath The reference path of the specified array.
     * @param array The array whose string representation is to be appended.
     * @return The specified StringBuilder.
     */
    private static StringBuilder appendArray(final StringBuilder result, final Map<Object, String> objectRefs, final String refpath, final Object array) {
        result.append("[");
        final int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                result.append(",");
            }
            appendValue(result, objectRefs, refpath + "/" + i, Array.get(array, i));
        }
        result.append("]");
        return result;
    }

    /**
     * Append the specified iterable's string representation to the specified
     * StringBuilder.
     * 
     * @param result StringBuilder to which the string representation is
     *        appended.
     * @param objectRefs References to "seen" objects.
     * @param refpath The reference path of the specified list.
     * @param iterable The iterable whose string representation is to be
     *        appended.
     * @return The specified StringBuilder.
     */
    private static StringBuilder appendIterable(final StringBuilder result, final Map<Object, String> objectRefs, final String refpath, final Iterable<?> iterable) {
        result.append("[");
        int i = 0;
        for (Object item : iterable) {
            if (i > 0) {
                result.append(",");
            }
            appendValue(result, objectRefs, refpath + "/" + i, item);
            i++;
        }
        result.append("]");
        return result;
    }

    /**
     * Append the specified map's string representation to the specified
     * StringBuilder.
     * 
     * @param result StringBuilder to which the string representation is
     *        appended.
     * @param objectRefs References to "seen" objects.
     * @param refpath The reference path of the specified map.
     * @param map The map whose string representation is to be appended.
     * @return The specified StringBuilder.
     */
    private static StringBuilder appendMap(final StringBuilder result, final Map<Object, String> objectRefs, final String refpath, final Map<?, ?> map) {
        result.append("{");
        String delim = "";
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            result.append(delim);
            final String name = String.valueOf(entry.getKey());
            appendString(result, name);
            result.append(":");
            final Object value = entry.getValue();
            appendValue(result, objectRefs, refpath + "/" + name, value);
            delim = ", ";
        }
        result.append("}");
        return result;
    }

    /**
     * Append the specified string to the specified StringBuilder.
     * 
     * @param result StringBuilder to which the string is appended.
     * @param string The string to be appended.
     * @return The specified StringBuilder.
     */
    private static StringBuilder appendString(final StringBuilder result, final CharSequence string) {
        result.append("\"");
        int i = result.length();
        result.append(string);
        while (i < result.length()) { // escape if necessary
            char c = result.charAt(i);
            if ((c == '"') || (c == '\\')) {
                result.insert(i, '\\');
                i = i + 2;
                continue;
            }
            if (c < 0x20) {
                result.insert(i + 1, Integer.toHexString(c | 0x10000));
                result.replace(i, i + 2, "\\u");
                i = i + 6;
                continue;
            }
            i++;
        }
        result.append("\"");
        return result;
    }

    /**
     * Compress, in length, the specified string.
     * 
     * @param in The string to potentially compress.
     * @return The string compressed, if necessary.
     */
    private static CharSequence compress(final CharSequence in) {
        final int length = in.length();
        if (length <= 21) {
            return in;
        }
        StringBuilder result = new StringBuilder(21);
        result.append(in, 0, 9);
        result.append("...");
        result.append(in, length - 9, length);
        return result;
    }

    /**
     * Serialize this DTO using a proxy.
     * 
     * <p>
     * This method is protected so that subclasses will inherit the DTO
     * serialization behavior. DTO subclasses should not override this method.
     * 
     * @return A serialization proxy for this DTO.
     */
    protected Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * We use an Externalizable proxy to serialize a DTO to have complete
     * control over the serialized form. This includes serializing all of the
     * public fields in the DTO hierarchy and special support for Maps, Sets and
     * Lists which are serialized to non-implementation specific forms.
     */
    private static class SerializationProxy implements Externalizable {
        /** The DTO being serialized or deserialized */
        private DTO                                                       dto;
        /**
         * For each serialization or deserialization operation, there is an
         * objects identity map.
         * */
        private final static Map<Object, IdentityHashMap<Object, Object>> objectMaps = new IdentityHashMap<Object, IdentityHashMap<Object, Object>>();
        /** Map of objects to/from proxy objects. */
        private Map<Object, Object>                                       objects;

        /**
         * Establish the objects map for the specified
         * serialization/deserialization operation.
         * 
         * @param token An object (ObjectOutput/ObjectInput) representing the
         *        operation.
         * @return The token is this is a new operation or null of this is
         *         another DTO for a current operation.
         */
        private Object establishObjectsMap(final Object token) {
            synchronized (objectMaps) {
                IdentityHashMap<Object, Object> map = objectMaps.get(token);
                if (map != null) {
                    objects = map;
                    return null;
                }
                objects = map = new IdentityHashMap<Object, Object>();
                objectMaps.put(token, map);
                return token;
            }
        }

        /**
         * Release the operation for the specified token.
         * 
         * @param token If non-null release the objects map. If null, do
         *        nothing.
         */
        private void releaseObjectsMap(final Object token) {
            if (token != null) {
                synchronized (objectMaps) {
                    objectMaps.remove(token);
                }
            }
        }

        /*
         * ********************************************************************
         * Serialization
         * ********************************************************************
         */
        /**
         * Constructor used by {@link DTO#writeReplace()} to perform
         * serialization.
         * 
         * @param dto The DTO to serialize.
         */
        SerializationProxy(DTO dto) {
            this.dto = dto;
        }

        /**
         * Serialize the proxy to the specified ObjectOutput.
         * 
         * @param out The output to which the serialization is to be written.
         * @throws IOException If an error occurs during serialization.
         */
        public void writeExternal(ObjectOutput out) throws IOException {
            final Object token = establishObjectsMap(out);
            try {
                final List<ObjectStreamClass> descriptions = getDescription(dto.getClass());
                out.writeInt(descriptions.size());
                for (ObjectStreamClass desc : descriptions) {
                    out.writeObject(desc);
                }
                final List<Field> fields = getFields(descriptions);
                for (Field field : fields) {
                    writeField(out, field);
                }
            } finally {
                releaseObjectsMap(token);
            }
        }

        /**
         * Serialize the specified field.
         * 
         * @param out The output to which the serialization is to be written.
         * @param field The field to serialize.
         * @throws IOException If an error occurs during serialization.
         */
        private void writeField(ObjectOutput out, Field field) throws IOException {
            try {
                final Type type = Type.getType(field.getType());
                switch (type) {
                    case BYTE :
                        out.writeByte(field.getByte(dto));
                        break;
                    case CHARACTER :
                        out.writeChar(field.getChar(dto));
                        break;
                    case DOUBLE :
                        out.writeDouble(field.getDouble(dto));
                        break;
                    case FLOAT :
                        out.writeFloat(field.getFloat(dto));
                        break;
                    case INTEGER :
                        out.writeInt(field.getInt(dto));
                        break;
                    case LONG :
                        out.writeLong(field.getLong(dto));
                        break;
                    case SHORT :
                        out.writeShort(field.getShort(dto));
                        break;
                    case BOOLEAN :
                        out.writeBoolean(field.getBoolean(dto));
                        break;
                    case UNSUPPORTED :
                        throw new InvalidObjectException("unsupported type " + field.getType().getName());
                    default :
                        out.writeObject(getWriteObject(field.get(dto), type, field.getType()));
                        break;
                }
            } catch (IllegalAccessException e) {
                throw newInvalidClassException(field.getDeclaringClass().getName(), "unable to access field: " + field.getName(), e);
            }
        }

        /**
         * Return the object to serialize for the specified object. The returned
         * object may be a proxy for the specified object.
         * 
         * @param value The object to serialize.
         * @param type The Type of the object.
         * @param fieldType The type of the field containing the object.
         * @return The object to write out.
         * @throws IOException If an error occurs during serialization.
         */
        private Object getWriteObject(Object value, Type type, Class<?> fieldType) throws IOException {
            if (value == null) {
                return null;
            }
            switch (type) {
                case OBJECT :
                case DTO :
                    return value;
                case SET : {
                    Object result = objects.get(value);
                    if (result != null) {
                        return result;
                    }
                    @SuppressWarnings("unchecked")
                    final Set<Object> set = (Set<Object>) value;
                    final int size = set.size();
                    final SetProxy proxy = new SetProxy(size);
                    objects.put(value, proxy);
                    final Iterator<Object> iter = set.iterator();
                    final Object[] values = proxy.s;
                    for (int i = 0; i < size; i++) {
                        values[i] = getWriteObject(iter.next());
                    }
                    return proxy;
                }
                case LIST : {
                    Object result = objects.get(value);
                    if (result != null) {
                        return result;
                    }
                    @SuppressWarnings("unchecked")
                    final List<Object> list = (List<Object>) value;
                    final int size = list.size();
                    final ListProxy proxy = new ListProxy(size);
                    objects.put(value, proxy);
                    final Iterator<Object> iter = list.iterator();
                    final Object[] values = proxy.l;
                    for (int i = 0; i < size; i++) {
                        values[i] = getWriteObject(iter.next());
                    }
                    return proxy;
                }
                case MAP : {
                    Object result = objects.get(value);
                    if (result != null) {
                        return result;
                    }
                    @SuppressWarnings("unchecked")
                    final Map<Object, Object> map = (Map<Object, Object>) value;
                    final int size = map.size();
                    final MapProxy proxy = new MapProxy(size);
                    objects.put(value, proxy);
                    final Iterator<Map.Entry<Object, Object>> iter = map.entrySet().iterator();
                    final Object[] keys = proxy.k;
                    final Object[] values = proxy.v;
                    for (int i = 0; i < size; i++) {
                        Map.Entry<Object, Object> entry = iter.next();
                        keys[i] = getWriteObject(entry.getKey());
                        values[i] = getWriteObject(entry.getValue());
                    }
                    return proxy;
                }
                case ARRAY : {
                    Object result = objects.get(value);
                    if (result != null) {
                        return result;
                    }
                    final Object array = getWriteArrayObject(value, fieldType);
                    objects.put(value, array);
                    return array;
                }
            }
            throw new InvalidObjectException("unsupported type" + value.getClass().getName());
        }

        /**
         * Return the object to serialize for the specified object. The returned
         * object may be a proxy for the specified object.
         * 
         * @param value The object to serialize.
         * @return The object to write out.
         * @throws IOException If an error occurs during serialization.
         */
        private Object getWriteObject(Object value) throws IOException {
            if (value == null) {
                return null;
            }
            Class<?> clazz = value.getClass();
            Type type = Type.getType(clazz);
            return getWriteObject(value, type, clazz);
        }

        /**
         * Return the array to serialize for the specified array. The returned
         * array may be an array of proxy for the objects in the specified
         * array.
         * 
         * @param value The array to serialize.
         * @param fieldType The type of the field containing the array.
         * @return The array to write out.
         * @throws IOException If an error occurs during serialization.
         */
        private Object getWriteArrayObject(Object value, Class<?> fieldType) throws IOException {
            Class<?> component = fieldType;
            int count = 0;
            while (component.isArray()) {
                component = component.getComponentType();
                count++;
            }
            Class<?> proxy;
            final Type type = Type.getType(component);
            switch (type) {
                case BYTE :
                case CHARACTER :
                case DOUBLE :
                case FLOAT :
                case INTEGER :
                case LONG :
                case SHORT :
                case BOOLEAN :
                case OBJECT :
                case DTO :
                    return value;
                case LIST :
                    proxy = ListProxy.class;
                    break;
                case SET :
                    proxy = SetProxy.class;
                    break;
                case MAP :
                    proxy = MapProxy.class;
                    break;
                default :
                    throw new InvalidObjectException("unsupported type" + fieldType.getName());
            }
            final int[] dimensions = new int[count];
            Object[] v = (Object[]) value;
            for (int i = 0;;) {
                dimensions[i] = v.length;
                i++;
                if (i >= count) {
                    break;
                }
                v = (Object[]) v[0];
            }
            Object array = Array.newInstance(proxy, dimensions);
            walkWriteArrayObject((Object[]) value, (Object[]) array, 0, count - 1, type, component);
            return array;
        }

        /**
         * Walk the source and target arrays to map leaf objects to proxy
         * objects.
         * 
         * @param source The source array.
         * @param target The target array to serialize.
         * @param depth The current depth in the arrays.
         * @param maxDepth The max depth of the arrays.
         * @param type The type of the leaf objects.
         * @param fieldType The class of the leaf objects.
         * @throws IOException If an error occurs during serialization.
         */
        private void walkWriteArrayObject(Object[] source, Object[] target, int depth, int maxDepth, Type type, Class<?> fieldType) throws IOException {
            final int length = source.length;
            if (depth == maxDepth) {
                for (int i = 0; i < length; i++) {
                    target[i] = getWriteObject(source[i], type, fieldType);
                }
                return;
            }
            for (int i = 0; i < length; i++) {
                walkWriteArrayObject((Object[]) source[i], (Object[]) target[i], depth + 1, maxDepth, type, fieldType);
            }
        }

        /*
         * ********************************************************************
         * Deserialization
         * ********************************************************************
         */
        /**
         * Constructor used by the deserializer to create a serialization proxy.
         */
        @SuppressWarnings("unused")
        public SerializationProxy() {
            // required by Externalizable
        }

        /**
         * Used by the deserializer to obtain the deserialized DTO.
         * 
         * @return The deserialized DTO.
         */
        private Object readResolve() {
            return dto;
        }

        /**
         * Deserialize the proxy from the specified ObjectInput.
         * 
         * @param in The input from which the deserialization is to be read.
         * @throws IOException If an error occurs during deserialization.
         * @throws ClassNotFoundException If a needed class to deserialize
         *         cannot be found.
         */
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            final Object token = establishObjectsMap(in);
            try {
                final int length = in.readInt();
                final List<ObjectStreamClass> descriptions = new ArrayList<ObjectStreamClass>(length);
                for (int i = 0; i < length; i++) {
                    descriptions.add((ObjectStreamClass) in.readObject());
                }
                @SuppressWarnings("unchecked")
                final Class<DTO> dtoClass = (Class<DTO>) descriptions.get(0).forClass();
                if (dtoClass == null) {
                    throw new ClassNotFoundException("unable to load class: " + descriptions.get(0).getName());
                }
                try {
                    dto = dtoClass.newInstance();
                } catch (IllegalAccessException e) {
                    throw new ClassNotFoundException("unable to instantiate class: " + dtoClass.getName(), e);
                } catch (InstantiationException e) {
                    throw new ClassNotFoundException("unable to instantiate class: " + dtoClass.getName(), e);
                }
                final List<Field> fields = getFields(descriptions);
                for (Field field : fields) {
                    readField(in, field);
                }
            } finally {
                releaseObjectsMap(token);
            }
        }

        /**
         * Deserialize the specified field.
         * 
         * @param in The input from which the deserialization is to be read.
         * @param field The field to deserialize.
         * @throws IOException If an error occurs during deserialization.
         * @throws ClassNotFoundException If a needed class to deserialize
         *         cannot be found.
         */
        private void readField(ObjectInput in, Field field) throws IOException, ClassNotFoundException {
            try {
                final Type type = Type.getType(field.getType());
                switch (type) {
                    case BYTE :
                        field.setByte(dto, in.readByte());
                        break;
                    case CHARACTER :
                        field.setChar(dto, in.readChar());
                        break;
                    case DOUBLE :
                        field.setDouble(dto, in.readDouble());
                        break;
                    case FLOAT :
                        field.setFloat(dto, in.readFloat());
                        break;
                    case INTEGER :
                        field.setInt(dto, in.readInt());
                        break;
                    case LONG :
                        field.setLong(dto, in.readLong());
                        break;
                    case SHORT :
                        field.setShort(dto, in.readShort());
                        break;
                    case BOOLEAN :
                        field.setBoolean(dto, in.readBoolean());
                        break;
                    case UNSUPPORTED :
                        throw new InvalidObjectException("unsupported type " + field.getType().getName());
                    default :
                        field.set(dto, getReadObject(in.readObject(), type, field.getType()));
                        break;
                }
            } catch (IllegalAccessException e) {
                throw newInvalidClassException(field.getDeclaringClass().getName(), "unable to access field: " + field.getName(), e);
            }
        }

        /**
         * Return the object for the specified deserialized object. The
         * specified object may be a proxy for the returned object.
         * 
         * @param value The deserialized object.
         * @param type The Type of the object.
         * @param fieldType The type of the field containing the returned
         *        object.
         * @return The object to store in the field.
         * @throws IOException If an error occurs during deserialization.
         * @throws IllegalAccessException If a field cannot be accessed.
         * @throws ClassNotFoundException If a needed class to deserialize
         *         cannot be found.
         */
        private Object getReadObject(Object value, Type type, Class<?> fieldType) throws IOException, IllegalAccessException, ClassNotFoundException {
            if (value == null) {
                return null;
            }
            switch (type) {
                case OBJECT :
                    return value;
                case DTO : {
                    if (value instanceof SerializationProxy) {
                        SerializationProxy proxy = (SerializationProxy) value;
                        return proxy.readResolve();
                    }
                    return value;
                }
                case SET : {
                    Object result = objects.get(value);
                    if (result != null) {
                        return result;
                    }
                    final SetProxy proxy = (SetProxy) value;
                    final Object[] values = proxy.s;
                    final int size = values.length;
                    final Set<Object> set = SetProxy.newSet(fieldType, size);
                    objects.put(value, set);
                    for (int i = 0; i < size; i++) {
                        set.add(getReadObject(values[i]));
                    }
                    return set;
                }
                case LIST : {
                    Object result = objects.get(value);
                    if (result != null) {
                        return result;
                    }
                    final ListProxy proxy = (ListProxy) value;
                    final Object[] values = proxy.l;
                    final int size = values.length;
                    final List<Object> list = ListProxy.newList(fieldType, size);
                    objects.put(value, list);
                    for (int i = 0; i < size; i++) {
                        list.add(getReadObject(values[i]));
                    }
                    return list;
                }
                case MAP : {
                    Object result = objects.get(value);
                    if (result != null) {
                        return result;
                    }
                    final MapProxy proxy = (MapProxy) value;
                    final Object[] keys = proxy.k;
                    final Object[] values = proxy.v;
                    final int size = keys.length;
                    final Map<Object, Object> map = MapProxy.newMap(fieldType, size);
                    objects.put(value, map);
                    for (int i = 0; i < size; i++) {
                        map.put(getReadObject(keys[i]), getReadObject(values[i]));
                    }
                    return map;
                }
                case ARRAY : {
                    Object result = objects.get(value);
                    if (result != null) {
                        return result;
                    }
                    final Object array = getReadArrayObject(value, fieldType);
                    objects.put(value, array);
                    return array;
                }
            }
            throw new InvalidObjectException("unsupported type" + value.getClass().getName());
        }

        /**
         * Return the object for the specified deserialized object. The
         * specified object may be a proxy for the returned object.
         * 
         * @param value The deserialized object.
         * @return The object to store in the field.
         * @throws IOException If an error occurs during deserialization.
         * @throws IllegalAccessException If a field cannot be accessed.
         * @throws ClassNotFoundException If a needed class to deserialize
         *         cannot be found.
         */
        private Object getReadObject(Object value) throws IOException, IllegalAccessException, ClassNotFoundException {
            if (value == null) {
                return null;
            }
            Class<?> clazz = value.getClass();
            Type type = Type.getType(clazz);
            return getReadObject(value, type, clazz);
        }

        /**
         * Return the array for the specified deserialized array. The specified
         * array may be an array of proxy for the objects in the returned array.
         * 
         * @param value The deserialized array.
         * @param fieldType The type of the field containing the returned
         *        object.
         * @return The array to store in the field.
         * @throws IOException If an error occurs during deserialization.
         * @throws IllegalAccessException If a field cannot be accessed.
         * @throws ClassNotFoundException If a needed class to deserialize
         *         cannot be found.
         */
        private Object getReadArrayObject(Object value, Class<?> fieldType) throws IOException, IllegalAccessException, ClassNotFoundException {
            Class<?> component = fieldType;
            int count = 0;
            while (component.isArray()) {
                component = component.getComponentType();
                count++;
            }
            Class<?> target;
            final Type type = Type.getType(component);
            switch (type) {
                case BYTE :
                case CHARACTER :
                case DOUBLE :
                case FLOAT :
                case INTEGER :
                case LONG :
                case SHORT :
                case BOOLEAN :
                case OBJECT :
                case DTO :
                    return value;
                case LIST :
                    target = ListProxy.class.equals(component) ? List.class : component;
                    break;
                case SET :
                    target = SetProxy.class.equals(component) ? Set.class : component;
                    break;
                case MAP :
                    target = MapProxy.class.equals(component) ? Map.class : component;
                    break;
                default :
                    throw new InvalidObjectException("unsupported type " + fieldType.getName());
            }
            final int[] dimensions = new int[count];
            Object[] v = (Object[]) value;
            for (int i = 0;;) {
                dimensions[i] = v.length;
                i++;
                if (i >= count) {
                    break;
                }
                v = (Object[]) v[0];
            }
            Object array = Array.newInstance(target, dimensions);
            walkReadArrayObject((Object[]) value, (Object[]) array, 0, count - 1, type, target);
            return array;
        }

        /**
         * Walk the source and target arrays to map proxy objects to leaf
         * objects.
         * 
         * @param source The deserialized source array.
         * @param target The target array to store.
         * @param depth The current depth in the arrays.
         * @param maxDepth The max depth of the arrays.
         * @param type The type of the leaf objects.
         * @param fieldType The class of the leaf objects.
         * @throws IOException If an error occurs during deserialization.
         * @throws IllegalAccessException If a field cannot be accessed.
         * @throws ClassNotFoundException If a needed class to deserialize
         *         cannot be found.
         */
        private void walkReadArrayObject(Object[] source, Object[] target, int depth, int maxDepth, Type type, Class<?> fieldType) throws IOException, IllegalAccessException, ClassNotFoundException {
            final int length = source.length;
            if (depth == maxDepth) {
                for (int i = 0; i < length; i++) {
                    target[i] = getReadObject(source[i], type, fieldType);
                }
                return;
            }
            for (int i = 0; i < length; i++) {
                walkReadArrayObject((Object[]) source[i], (Object[]) target[i], depth + 1, maxDepth, type, fieldType);
            }
        }

        /**
         * Enum for switching bases upon type.
         */
        private static enum Type {
            BYTE,
            CHARACTER,
            DOUBLE,
            FLOAT,
            INTEGER,
            LONG,
            SHORT,
            BOOLEAN,
            OBJECT,
            DTO,
            LIST,
            SET,
            MAP,
            ARRAY,
            UNSUPPORTED;
            /** Map of base types to enum values. */
            private static final Map<Class<?>, Type> typesMap;
            static {
                typesMap = new HashMap<Class<?>, Type>();
                typesMap.put(byte.class, BYTE);
                typesMap.put(char.class, CHARACTER);
                typesMap.put(double.class, DOUBLE);
                typesMap.put(float.class, FLOAT);
                typesMap.put(int.class, INTEGER);
                typesMap.put(long.class, LONG);
                typesMap.put(short.class, SHORT);
                typesMap.put(boolean.class, BOOLEAN);
                typesMap.put(Byte.class, OBJECT);
                typesMap.put(Character.class, OBJECT);
                typesMap.put(Double.class, OBJECT);
                typesMap.put(Float.class, OBJECT);
                typesMap.put(Integer.class, OBJECT);
                typesMap.put(Long.class, OBJECT);
                typesMap.put(Short.class, OBJECT);
                typesMap.put(Boolean.class, OBJECT);
                typesMap.put(String.class, OBJECT);
                typesMap.put(ListProxy.class, LIST);
                typesMap.put(SetProxy.class, SET);
                typesMap.put(MapProxy.class, MAP);
            }

            /**
             * Get the enum value for the specified class.
             * 
             * @param clazz The class whose enum value is to be returned.
             * @return The enum value for the specified class.
             */
            public static Type getType(Class<?> clazz) {
                Type type = typesMap.get(clazz);
                if (type != null) {
                    return type;
                }
                if (DTO.class.isAssignableFrom(clazz)) {
                    return DTO;
                }
                if (List.class.isAssignableFrom(clazz)) {
                    return LIST;
                }
                if (Set.class.isAssignableFrom(clazz)) {
                    return SET;
                }
                if (Map.class.isAssignableFrom(clazz)) {
                    return MAP;
                }
                if (clazz.isArray()) {
                    return ARRAY;
                }
                return UNSUPPORTED;
            }
        }

        /* static helper methods */

        /**
         * Return the ObjectStreamClass objects for the specified DTO class.
         * 
         * @param dtoClass The DTO class.
         * @return The ObjectStreamClass objects for the specified DTO class.
         */
        private static List<ObjectStreamClass> getDescription(Class<?> dtoClass) {
            final List<ObjectStreamClass> descriptions = new ArrayList<ObjectStreamClass>();
            while (DTO.class.isAssignableFrom(dtoClass)) {
                descriptions.add(ObjectStreamClass.lookup(dtoClass));
                dtoClass = dtoClass.getSuperclass();
            }
            return descriptions;
        }

        /**
         * Return the Field objects for the specified ObjectStreamClass objects.
         * 
         * @param descriptions The ObjectStreamClass objects.
         * @return The Field objects for the specified ObjectStreamClass
         *         objects.
         * @throws InvalidClassException It a Field object is not present.
         */
        private static List<Field> getFields(List<ObjectStreamClass> descriptions) throws InvalidClassException {
            final List<Field> fields = new ArrayList<Field>();
            for (ObjectStreamClass desc : descriptions) {
                Class<?> dtoClass = desc.forClass();
                for (ObjectStreamField streamField : desc.getFields()) {
                    try {
                        Field field = dtoClass.getField(streamField.getName());
                        if (Modifier.isStatic(field.getModifiers())) {
                            continue;
                        }
                        fields.add(field);
                    } catch (NoSuchFieldException e) {
                        throw newInvalidClassException(dtoClass.getName(), "invalid field " + streamField.getName(), e);
                    }
                }
            }
            return fields;
        }

        /**
         * Return a new InvalidClassException with the specified class name,
         * reason and cause.
         * 
         * @param cname The name of the subject class.
         * @param reason The reason for the exception.
         * @param cause The cause of the exception.
         * @return A new InvalidClassException with the specified class name,
         *         reason and cause.
         */
        static InvalidClassException newInvalidClassException(String cname, String reason, Throwable cause) {
            InvalidClassException e = new InvalidClassException(cname, reason);
            e.initCause(cause);
            return e;
        }
    }

    /**
     * A proxy for a List object in the serialized stream.
     */
    private static class ListProxy implements Serializable {
        private static final long serialVersionUID = 1L;
        final Object[]            l;

        ListProxy(int size) {
            l = new Object[size];
        }

        /**
         * Return a new List of the specified target type.
         * 
         * @param targetType The requested target type.
         * @param size The initial size when the requested target type cannot be
         *        used.
         * @return A new List.
         * @throws InvalidClassException If the specified target type cannot be
         *         instantiated.
         */
        static List<Object> newList(Class<?> targetType, int size) throws InvalidClassException {
            if (List.class.equals(targetType) || ListProxy.class.equals(targetType)) {
                return new ArrayList<Object>(size);
            }
            try {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) targetType.newInstance();
                return list;
            } catch (Exception e) {
                throw SerializationProxy.newInvalidClassException(targetType.getName(), "unable to create instance", e);
            }
        }
    }

    /**
     * A proxy for a Set object in the serialized stream.
     */
    private static class SetProxy implements Serializable {
        private static final long serialVersionUID = 1L;
        final Object[]            s;

        SetProxy(int size) {
            s = new Object[size];
        }

        /**
         * Return a new Set of the specified target type.
         * 
         * @param targetType The requested target type.
         * @param size The initial size when the requested target type cannot be
         *        used.
         * @return A new Set.
         * @throws InvalidClassException If the specified target type cannot be
         *         instantiated.
         */
        static Set<Object> newSet(Class<?> targetType, int size) throws InvalidClassException {
            if (Set.class.equals(targetType) || SetProxy.class.equals(targetType)) {
                return new HashSet<Object>(size);
            }
            try {
                @SuppressWarnings("unchecked")
                Set<Object> set = (Set<Object>) targetType.newInstance();
                return set;
            } catch (Exception e) {
                throw SerializationProxy.newInvalidClassException(targetType.getName(), "unable to create instance", e);
            }
        }
    }

    /**
     * A proxy for a Map object in the serialized stream.
     */
    private static class MapProxy implements Serializable {
        private static final long serialVersionUID = 1L;
        final Object[]            k;
        final Object[]            v;

        MapProxy(int size) {
            k = new Object[size];
            v = new Object[size];
        }

        /**
         * Return a new Map of the specified target type.
         * 
         * @param targetType The requested target type.
         * @param size The initial size when the requested target type cannot be
         *        used.
         * @return A new Map.
         * @throws InvalidClassException If the specified target type cannot be
         *         instantiated.
         */
        static Map<Object, Object> newMap(Class<?> targetType, int size) throws InvalidClassException {
            if (Map.class.equals(targetType) || MapProxy.class.equals(targetType)) {
                return new HashMap<Object, Object>(size);
            }
            try {
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) targetType.newInstance();
                return map;
            } catch (Exception e) {
                throw SerializationProxy.newInvalidClassException(targetType.getName(), "unable to create instance", e);
            }
        }
    }
}
