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
         // if one is null, the must both be null.
         if (value == null) {
             return other.value == null;
         }
         // and vice versa
         if (other.value == null) {
             return false;
         }
         // compare the values
         if (!value.equals(other.value)) {
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
}
