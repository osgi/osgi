/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */

package org.osgi.impl.service.dmt.plugins;

import info.dmtree.*;

import java.lang.reflect.Array;
import java.util.*;

class ConfigEntry {
    private Type type;
    private Cardinality cardinality;
    private Value value;
    private Hashtable values;
    
    /**
     * Creates a new ConfigEntry object from a valid configuration property 
     * value.  <code>value</code> must not contain an empty Vector.
     */
    ConfigEntry(Object obj) {
        Class cl = obj.getClass();
        if(cl.equals(byte[].class) 
                || (!cl.equals(Vector.class) && !cl.isArray())) {
            
            // for scalar types and byte[]
            type = Type.getTypeByClass(obj.getClass());
            cardinality = Cardinality.SCALAR;
            value = new Value(obj, type);
            values = null;
            
        } else { // for non-scalar types (array and vector)
            
            value = null;
            values = new Hashtable();
            if(obj instanceof Vector) {
                // non-empty vector, also assuming homogeneity (CA works)
                Vector vector = (Vector) obj;
                type = Type.getTypeByClass(vector.firstElement().getClass());
                cardinality = Cardinality.VECTOR;
                Iterator iter = vector.iterator();
                for(int i = 0; iter.hasNext(); i++)
                    values.put(new Integer(i), new Value(iter.next(), type));
            } else { // obj is an array
                type = Type.getTypeByClass(obj.getClass().getComponentType());
                cardinality = Cardinality.ARRAY;
                for(int i = 0; i < Array.getLength(obj); i++)
                    values.put(new Integer(i), 
                            new Value(Array.get(obj, i), type));
            }
        }

        if(type == null) // sanity check
            throw new IllegalArgumentException(
                    "Invalid configuration entry type for " + obj);
    }
    
    ConfigEntry() {
        type = null;
        cardinality = null;
        value = null;
        values = null;
    }
    
    Type getType() {
        return type;
    }
    
    Cardinality getCardinality() {
        return cardinality;
    }
    
    Value getValue() {
        return value;
    }
    
    Hashtable getValues() {
        return values;
    }
    
    /**
     * Returns the element at the given index for non-scalar entries. If there
     * is no element at the given index, null is returned.
     * 
     * @throws ConfigPluginException if the entry is scalar or if the values
     *         node is not created yet
     */
    Value getElementAt(int index) throws ConfigPluginException {
        if(values == null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND, 
                    "The " + ConfigReadOnlySession.VALUES + " node does not " +
                    "exist: entry is scalar or node not created yet.");

        return (Value) values.get(new Integer(index));
    }
    
    /**
     * Retruns an array of the indices for non-scalar entries.
     * 
     * @throws ConfigPluginException if the entry is scalar or if the values
     *         node is not created yet
     */
    Integer[] getIndexArray() throws ConfigPluginException {
        if(values == null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND, 
                    "The " + ConfigReadOnlySession.VALUES + " node " +
                    "does not exist: entry is scalar or node not created yet.");
        
        return (Integer[]) values.keySet().toArray(new Integer[] {});
    }
    
    /**
     * Returns the configuration entry value as an object.
     * 
     * @throws ConfigPluginException if the entry is incomplete or the indexing
     *         of array/vector contents is not continuous
     */
    Object getObject() throws ConfigPluginException {
        if(type == null || cardinality == null || 
                (value == null && values == null))
            throw new ConfigPluginException(DmtException.METADATA_MISMATCH, 
                    "A value is not fully defined in the tree: the type, " +
                    "cardinality or value is missing.");
        
        if(value != null) // scalar property
            return value.getObject();
        
        // array or vector
        
        boolean isArray = cardinality == Cardinality.ARRAY;
        int size = values.size();

        Object obj;
        if(isArray)
            obj = Array.newInstance(type.getTypeClass(), size);
        else
            obj = new Vector(size);
        
        for (int i = 0; i < size; i++) {
            Value v = (Value) values.get(new Integer(i));
            if(v == null) // the indices are not continuous from 0 to size
                throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                        "An array or vector value " +
                        "is not in a consistent state in the tree: the " +
                        "index numbering is not continuous."); 
            if(isArray)
                Array.set(obj, i, v.getObject());
            else
                ((Vector)obj).add(v.getObject());
        }
            
        return obj;
    }
    
    /**
     * Create the type node and set it to the given type.  The type parameter
     * must not be null.
     * 
     * @throws ConfigPluginExceptionn if the type node already exists, or if the
     *         cardinality or value(s) conflict with the given type 
     */
    void createType(Type type) throws ConfigPluginException {
        if(this.type != null)
            throw new ConfigPluginException(DmtException.NODE_ALREADY_EXISTS,
                    "The " + ConfigReadOnlySession.TYPE + " node has " +
                    "already been created.");
        
        if(type.isPrimitive() && (value != null || 
                (cardinality != null && !cardinality.equals(Cardinality.ARRAY))))
            throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                    "Attempted to set primitive type for non-array entry.");

        if(type.getTypeClass().equals(byte[].class) && (values != null ||
                (cardinality != null && !cardinality.equals(Cardinality.SCALAR)))) 
            throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                    "Attempted to set byte[] type for non-scalar entry.");
        
        if(value != null)
            value.setType(type);
        else if(values != null) {
            Iterator i = values.values().iterator();
            while (i.hasNext())
                ((Value) i.next()).setType(type);
        }
        
        this.type = type;
    }
    
    /**
     * Create the cardinality node and set it to the given type.  The 
     * cardinality parameter must not be null.
     * 
     * @throws ConfigPluginExceptionn if the cardinality node already exists, 
     *         or if the type or value(s) conflict with the given cardinality 
     */
    void createCardinality(Cardinality cardinality) 
            throws ConfigPluginException {
        if(this.cardinality != null)
            throw new ConfigPluginException(DmtException.NODE_ALREADY_EXISTS,
                    "The " + ConfigReadOnlySession.CARDINALITY + 
                    " node has already been created.");
        
        if(values != null && cardinality.equals(Cardinality.SCALAR))
            throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                    "Attempted to set scalar cardinality for entry with " +
                    "multiple values.");
        
        if(value != null && !cardinality.equals(Cardinality.SCALAR))
            throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                    "Attempted to set non-scalar cardinality for entry with " +
                    "a single value.");
            
        if(type != null) {
            if(type.isPrimitive() && !cardinality.equals(Cardinality.ARRAY))
                throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                        "Attempted to set non-array cardinality for entry " +
                        "with primitive type.");
            
            if(type.getTypeClass().equals(byte[].class) &&
                    !cardinality.equals(Cardinality.SCALAR))
                throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                        "Attempted to set non-scalar cardinality for entry " +
                        "with byte[] type.");
        }
        
        this.cardinality = cardinality;
    }
    
    /**
     * Create the value node and set it to the given data.
     *
     * @throws ConfigPluginException if the entry is non-scalar, if the value
     *         node already exists, or if the given data is incompatible with
     *         the other parameters of the entry
     */
    void createValue(DmtData data) throws ConfigPluginException {
        
        if(values != null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "The specified key contains a non-scalar value.");
        
        if(value != null)
            throw new ConfigPluginException(DmtException.NODE_ALREADY_EXISTS,
                    "The " + ConfigReadOnlySession.VALUE + " node " +
                    "already exists.");

        if(cardinality != null && !Cardinality.SCALAR.equals(cardinality))
            throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                    "The specified key must contain non-scalar data.");
        
        value = new Value(data, type);
    }
    
    /**
     * Create the values node.
     *
     * @throws ConfigPluginException if the entry is scalar, if the values node
     *         already exists, or if creating the values node conflicts with the
     *         other parameters of the entry
     */
    void createValues() throws ConfigPluginException {
        
        if(value != null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "The specified key contains a scalar value.");
        
        if(values != null)
            throw new ConfigPluginException(DmtException.NODE_ALREADY_EXISTS,
                    "The " + ConfigReadOnlySession.VALUES + " node " +
                    "already exists.");
        
        if(Cardinality.SCALAR.equals(cardinality))
            throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                    "The specified key must contain scalar data.");
        
        if(Type.getTypeByClass(byte[].class).equals(type))
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND, 
                    "The specified key must contain binary data as a scalar " +
                    "value.");

        values = new Hashtable();
    }
    
    /**
     * Creates a new element node for the given index and sets it to the given 
     * data.
     * 
     * @throws ConfigPluginException if the entry is scalar, if the values node
     *         is not created yet or an element with the given index already
     *         exists, or if the given data is incompatible with the other 
     *         parameters of the entry
     */
    void createElementAt(int index, DmtData data) throws ConfigPluginException {
        if(value != null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "The specified key contains a scalar value.");
        
        if(values == null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "The " + ConfigReadOnlySession.VALUES + " node " +
                    "has not been created yet.");
        
        if(values.containsKey(new Integer(index)))
            throw new ConfigPluginException(DmtException.NODE_ALREADY_EXISTS,
                    "An element with the given index already exists.");
        
        if(data.getFormat() == DmtData.FORMAT_BINARY)
            throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                    "Attempted to set binary data as an element of a " +
                    "non-scalar entry.");
        
        // non-null values guarantees non-scalar cardinality
        // type conformance (if type is set) checked by Value constructor
        values.put(new Integer(index), new Value(data, type));
    }

    
    /**
     * Sets the scalar value of the entry to the given data.
     * 
     * @throws ConfigPluginException if the entry is non-scalar, if the value
     *         node is not created yet, or if the given data is incompatible
     *         with the other parameters of the entry
     */
    void setValue(DmtData data) throws ConfigPluginException {
        
        if(values != null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "The specified key contains a non-scalar value.");
        
        if(value == null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "The given node has not been created yet.");
        
        // non-null value guarantees scalar cardinality
        // type conformance (if type is set) checked by Value constructor
        value = new Value(data, type);
    }
    
    /**
     * Sets the element at the given index to the given data.
     * 
     * @throws ConfigPluginException if the entry is scalar, if the values node
     *         is not created yet or there is no element with the given index,
     *         or if the given data is incompatible with the other parameters of
     *         the entry
     */
    void setElementAt(int index, DmtData data) throws ConfigPluginException {
        if(value != null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "The specified key contains a scalar value.");
        
        if(values == null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "The given node has not been created yet.");
        
        if(!values.containsKey(new Integer(index)))
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "There is no element with the given index.");
        
        if(data.getFormat() == DmtData.FORMAT_BINARY)
            throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                    "Attempted to set binary data as an element of a " +
                    "non-scalar entry.");
        
        // non-null values guarantees non-scalar cardinality
        // type conformance (if type is set) checked by Value constructor
        values.put(new Integer(index), new Value(data, type));
    }
    
    /**
     * Moves an element node to a different index.
     * 
     * @throws ConfigPluginException if the entry is scalar, if the values node
     *         is not created yet, if there is no element with the given index
     *         or an element with the new index already exists
     */
    void renameElementAt(int oldIndex, int newIndex) 
            throws ConfigPluginException {
        
        if(value != null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "The specified key contains a scalar value.");
        
        if(values == null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "The given node has not been created yet.");
        
        if(!values.containsKey(new Integer(oldIndex)))
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "There is no element with the given index.");
        
        if(values.containsKey(new Integer(newIndex)))
            throw new ConfigPluginException(DmtException.NODE_ALREADY_EXISTS,
                    "An element with the new index already exists.");
        
        values.put(new Integer(newIndex), values.remove(new Integer(oldIndex)));
    }
    
    /**
     * Removes the element at the given index for non-scalar entries.
     * 
     * @throws ConfigPluginException if the entry is scalar, if the values node
     *         is not created yet, or if there is no entry for the given index
     */
    void removeElementAt(int index) throws ConfigPluginException {
        if(value != null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND,
                    "The specified key contains a scalar value.");
        
        if(values == null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND, 
                    "The " + ConfigReadOnlySession.VALUES + " node " +
                    "has not been created yet.");
        
        if(values.remove(new Integer(index)) == null)
            throw new ConfigPluginException(DmtException.NODE_NOT_FOUND, 
                    "No element exists in the array/vector with the given " +
                    "index.");
    }
}

class Cardinality {
    static final Cardinality SCALAR = new Cardinality("scalar");
    static final Cardinality VECTOR = new Cardinality("vector");
    static final Cardinality ARRAY  = new Cardinality("array");
    
    static final DmtData[] ALL_CARDINALITY_DATA = new DmtData[] {
        SCALAR.getData(), VECTOR.getData(), ARRAY.getData() 
    }; 
    
    static Cardinality getCardinalityByData(DmtData data) {
        if(data.equals(ALL_CARDINALITY_DATA[0]))
            return SCALAR;
        if(data.equals(ALL_CARDINALITY_DATA[1]))
            return VECTOR;
        if(data.equals(ALL_CARDINALITY_DATA[2]))
            return ARRAY;
        return null;
    }
    
    private DmtData data;
    
    private Cardinality(String name) {
        data = new DmtData(name);
    }
    
    DmtData getData() {
        return data;
    }
}

class Type {
    static final Map T = new HashMap();
    static final DmtData[] ALL_TYPE_DATA;
    
    static {
        initTypes();
        
        ALL_TYPE_DATA = new DmtData[T.size()];
        Iterator iter = T.values().iterator();
        for(int i = 0; iter.hasNext(); i++)
            ALL_TYPE_DATA[i] = ((Type) iter.next()).getData();
    }
    
    private static void initTypes() {
        addType(String.class, String.class.getName(), DmtData.FORMAT_STRING);
        addType(Byte.class,      Byte.TYPE,      DmtData.FORMAT_STRING);
        addType(Character.class, Character.TYPE, DmtData.FORMAT_STRING);
        addType(Short.class,     Short.TYPE,     DmtData.FORMAT_STRING);
        addType(Long.class,      Long.TYPE,      DmtData.FORMAT_STRING);
        addType(Double.class,    Double.TYPE,    DmtData.FORMAT_STRING);
        addType(Integer.class,   Integer.TYPE,   DmtData.FORMAT_INTEGER);
        addType(Float.class,     Float.TYPE,     DmtData.FORMAT_FLOAT);
        addType(Boolean.class,   Boolean.TYPE,   DmtData.FORMAT_BOOLEAN);
        addType(byte[].class,    "byte[]",       DmtData.FORMAT_BINARY);
    }
    
    private static void addType(Class cl, String name, int format) {
        T.put(cl, new Type(cl, name, null, format));
    }
    
    private static void addType(Class cl, Class prim, int format) {
        Type t = new Type(cl, cl.getName(), null, format);
        T.put(cl, t);
        T.put(prim, new Type(prim, prim.getName(), t, format));
    }

    static Type getTypeByClass(Class cl) {
        return (Type) T.get(cl);
    }
    
    static Type getTypeByData(DmtData data) {
        if(data.getFormat() != DmtData.FORMAT_STRING)
            return null;
        
        String name = data.getString();
        Iterator i = T.values().iterator();
        while (i.hasNext()) {
            Type type = (Type) i.next();
            if(type.getName().equals(name))
                return type;
        }
        return null;
    }
    
    private Class cl;
    private String name;
    private Type wrapper;
    private int format;
    private DmtData data; // redundant
    
    private Type(Class cl, String name, Type wrapper, int format) {
        this.cl = cl;
        this.name = name;
        this.wrapper = wrapper;
        this.format = format;
        
        data = new DmtData(name);
    }
    
    DmtData getData() {
        return data;
    }
    
    String getName() {
        return name;
    }
    
    Class getTypeClass() {
        return cl;
    }
    
    Type getWrapper() {
        return wrapper;
    }
    
    int getFormat() {
        return format;
    }
    
    boolean isPrimitive() {
        return cl.isPrimitive();
    }
    
    public String toString() {
        return name;
    }
}

class Value {
    /**
     * Stores the DmtData object, as it is in the tree.
     */
    DmtData data;
    
    /**
     * Stores the value object, which can be the String representation if the
     * value was constructed using a DmtData and the real type cannot be
     * uniquely determined.  Primitive values are always stored in their
     * corresponding Object wrappers.
     */
    Object value;
    
    /** 
     * The type of the stored value.  Can be null if not known yet, in this 
     * case the value is stored as a string, and the value object cannot be 
     * retrieved until a type is specified externally.  In case of primitive 
     * types the wrapper type is stored. 
     */
    Type type;
    
    /**
     * Creates a Value instance based on a DmtData object and possibly a type
     * specifier.  The type parameter can be null if it is not set in the tree
     * yet.
     * 
     * @throws ConfigPluginException if the given type conflicts with the 
     * data 
     */
    Value(DmtData data, Type type) throws ConfigPluginException {
        
        int format = data.getFormat();
        switch(format) {
        case DmtData.FORMAT_STRING:
            value = data.getString();
            this.type = null;
            break;
        case DmtData.FORMAT_INTEGER:
            value = new Integer(data.getInt());
            this.type = Type.getTypeByClass(Integer.class);
            break;
        case DmtData.FORMAT_BOOLEAN:
            value = new Boolean(data.getBoolean());
            this.type = Type.getTypeByClass(Boolean.class);
            break;
        case DmtData.FORMAT_FLOAT:
            value = new Float(data.getFloat());
            this.type = Type.getTypeByClass(Float.class);
            break;
        case DmtData.FORMAT_BINARY:
            value = data.getBinary();
            this.type = Type.getTypeByClass(byte[].class);
        default:
            // meta-data should guarantee that data has a valid format
            throw new IllegalArgumentException("Data received with invalid " +
                    "format '" + format + "'.");
        }
        
        this.data = data;

        if(type != null)
            setType(type);
    }
    
    /**
     * Creates a Value instance based on a value object and its type.  The
     * given type must not be null.
     */
    Value(Object value, Type type) {
        this.type = getObjectType(type); // use the wrapper type for primitives
        this.value = value;
        
        // should never happen if Configuration Admin/Plugin works correctly
        if(!Type.getTypeByClass(value.getClass()).equals(this.type))
            throw new IllegalStateException("Value object created with " +
                    "incompatible type and value parameters.");
        
        if(value instanceof byte[])
            data = new DmtData((byte[]) value);
        else if(value instanceof Integer)
            data = new DmtData(((Integer) value).intValue());
        else if(value instanceof Float)
            data = new DmtData(((Float) value).floatValue());
        else if(value instanceof Boolean)
            data = new DmtData(((Boolean) value).booleanValue());
        else
            data = new DmtData(value.toString());
    }
    
    /**
     * Set the type of the value if it is not already set.  The type parameter
     * must not be null.
     *   
     * @throws ConfigPluginException if the given type conflicts with the 
     * stored value or the previously known type
     */
    void setType(Type type) throws ConfigPluginException {
        type = getObjectType(type);
        
        if(this.type != null) {
            if(!this.type.equals(type))
                throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                        "The given type conflicts with the previously given " +
                        "or calculated type.");
            return;
        }
        
        // this.type == null, implies that value is a String, and that the type
        // can only be one of the types without a native DM representation
        String valueString = (String) value;
        
        Class cl = type.getTypeClass();
        this.type = type;
        
        if(String.class.equals(cl))
            return; // nothing to do, value is already a string
        
        try {
            if(Long.class.equals(cl)) {
                value = new Long(valueString);
                return;
            } 
            
            if(Double.class.equals(cl)) {
                value = new Double(valueString);
                return;
            }
            
            if(Byte.class.equals(cl)) {
                value = new Byte(valueString);
                return;
            }
        } catch(NumberFormatException e) {
            type = null;
            throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                    "Data string given in tree cannot be parsed to the " +
                    "correct type: " + e.getMessage());
        }
        
        if(Character.class.equals(cl)) {
            if(valueString.length() != 0) {
                value = new Character(valueString.charAt(0));
                return;
            }
            
            type = null;
            throw new ConfigPluginException(DmtException.METADATA_MISMATCH,
                    "Empty value string given in tree for a character typed " +
                    "configuration value.");
        }
        
        // no other types can be set by string data
        throw new ConfigPluginException(DmtException.METADATA_MISMATCH, 
                "Cannot set a string value for the given type.");        
    }

    /**
     * Returns the stored value in a DmtData object.
     */
    DmtData getData() {
        return data;
    }
    
    /**
     * Returns the stored value object in its proper type.
     */ 
    Object getObject() {
        if(type == null) // shouldn't happen, type must be set at this point
            throw new IllegalStateException("The type of the stored value is " +
                    "not known yet.");
        
        return value;
    }
    
    /**
     * Returns the wrapper type for primitive types or the parameter type itself
     * for any other types.
     */
    private Type getObjectType(Type type) {
        Type wrappedType = type.getWrapper();
        return wrappedType == null ? type : wrappedType;
    }
}
