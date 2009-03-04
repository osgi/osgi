/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.blueprint.services;

/**
 * Representation of an array value stored inside of a component.
 */
public class LongArrayValueDescriptor extends ValueDescriptor {

     /**
      * Create a component value from a name and value object.
      *
      * @param name   The name of the value entity.
      * @param value  The object value.
      */
     public LongArrayValueDescriptor(String name, long[] value) {
         super(name, value, long[].class);
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

         if (!(other.value instanceof long[])) {
             return false;
         }
         // now validate all of the data
         long[] thisValue = (long[])value;
         long[] otherValue = (long[])other.value;

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

