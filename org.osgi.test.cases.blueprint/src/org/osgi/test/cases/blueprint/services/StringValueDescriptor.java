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


/**
 * Representation of a String-derived value stored inside of a component.
 */
public class StringValueDescriptor extends ValueDescriptor {

     /**
      * Create a component value from a name and value object.
      *
      * @param name   The name of the value entity.
      * @param value  The object value.
      */
     public StringValueDescriptor(String name, Object value) {
         this(name, value, null);
     }

     public StringValueDescriptor(String name, Object value, Class clz) {
         super(name, value, clz);
     }

     /**
      * Constructors for creating value objects from the different
      * primitive types.
      *
      * @param name   The name.
      * @param v      The primitive value.
      */
     public StringValueDescriptor(String name, boolean v) {
         this(name, new Boolean(v), Boolean.TYPE);
     }

     public StringValueDescriptor(String name, byte v) {
         this(name, new Byte(v), Byte.TYPE);
     }

     public StringValueDescriptor(String name, char v) {
         this(name, new Character(v), Character.TYPE);
     }

     public StringValueDescriptor(String name, short v) {
         this(name, new Short(v), Short.TYPE);
     }

     public StringValueDescriptor(String name, int v) {
         this(name, new Integer(v), Integer.TYPE);
     }

     public StringValueDescriptor(String name, long v) {
         this(name, new Long(v), Long.TYPE);
     }

     public StringValueDescriptor(String name, double v) {
         this(name, new Double(v), Double.TYPE);
     }

     public StringValueDescriptor(String name, float v) {
         this(name, new Float(v), Float.TYPE);
     }
}
