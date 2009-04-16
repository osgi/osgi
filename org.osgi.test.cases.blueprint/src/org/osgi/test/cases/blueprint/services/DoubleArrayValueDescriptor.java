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
 * Representation of an array value stored inside of a component.
 */
public class DoubleArrayValueDescriptor extends ValueDescriptor {

     /**
      * Create a component value from a name and value object.
      *
      * @param name   The name of the value entity.
      * @param value  The object value.
      */
     public DoubleArrayValueDescriptor(String name, double[] value) {
         super(name, value, double[].class);
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

         if (!(other.value instanceof double[])) {
             return false;
         }
         // now validate all of the data
         double[] thisValue = (double[])value;
         double[] otherValue = (double[])other.value;

         if (thisValue.length != otherValue.length) {
             return false;
         }

         for (int i = 0; i < thisValue.length; i++) {
             if (thisValue[i] != otherValue[i]) {
                 return false;
             }
         }
         return true;
     }
}

