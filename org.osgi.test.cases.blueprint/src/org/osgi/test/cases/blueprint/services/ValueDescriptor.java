/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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

package org.osgi.test.cases.blueprint.services;

import java.lang.reflect.Array;
import java.util.Arrays;

import junit.framework.Assert;

/**
 * Representation of a value stored inside of a component.  These are
 * further subdivided into Argument and Property values.
 */
public class ValueDescriptor extends Assert {
     // the internal name of the property.
     protected String name;
     // the actual object value
     protected Object value;
     // the class of object this is.  For primitive values, the value
     // object will be a Wrapper instance and the class will be the primitive
     // type (e.g.,
     protected Class clz;

     /**
      * Create a component value from a name and value object.
      *
      * @param name   The name of the value entity.
      * @param value  The object value.
      */
     public ValueDescriptor(String name, Object value) {
         this(name, value, null);
     }

     public ValueDescriptor(String name, Object value, Class clz) {
         this.name = name;
         this.value = value;
         this.clz = clz;
         // if we have a a real value, then snag some additional
         // information about this.
         if (value != null) {
             // if not given a class, then just use the class of
             // the real value
             if (clz == null) {
                 this.clz = value.getClass();
             }
         }
     }

     /**
      * Perform a comparison of object values
      *
      * @param o      The target object.
      *
      * @return true if the values are equal, false otherwise.
      */
     public boolean equals(Object o) {
         if (!(o instanceof ValueDescriptor)) {
             return false;
         }

         ValueDescriptor other = (ValueDescriptor)o;
         
         if (!compareObjects(value, other.value)) {
             return false;
         }

         // if our expected class is not the same as the value class, this
         // is a failure too.  This is mostly to bypass confusion over
         // wrapper vs. primitive types.
         if (clz != other.clz) {
             return false;
         }
         return true;
     }

     /**
      * Override hashCode() when overrided the equals()
      */
     public int hashCode() {
         int r = 11;
         r = 17*r + this.name.hashCode();
         r = 17*r + this.value.hashCode();
         r = 17*r + this.clz.hashCode();
         return r;
     }

     // return the argument or property name of this value.
     public String getName() {
         return name;
     }

     // return the value associated this instance.
     public Object getValue() {
         return value;
     }

     public String toString() {
         return "Name=" + name + ", Value=" + value + ", Class=" + clz;
     }
     
     private static boolean compareObjects(Object obj1, Object obj2) {
         // if one is null, the must both be null.
         if (obj1 == null) {
             return obj2 == null;
         }
         // and vice versa
         if (obj2 == null) {
             return false;
         }
         // compare the values
         if (obj1.getClass().isArray()) {
             if (obj2.getClass().isArray()) {
                 return compareArrays(obj1, obj2);
             } else {
                 return false;
             }
         } else {
             return obj1.equals(obj2);
         }
     }
     
     private static boolean compareArrays(Object array1, Object array2) {
         int size1 = Array.getLength(array1);
         int size2 = Array.getLength(array2);
         if (size1 != size2) {
             return false;            
         }
         for (int i = 0; i < size1; i++) {
             Object obj1 = Array.get(array1, i);
             Object obj2 = Array.get(array2, i);             
             if (!compareObjects(obj2, obj2)) {
                 return false;
             }
         }
         return true;
     }
}
