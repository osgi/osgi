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
