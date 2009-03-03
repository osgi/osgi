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

import java.util.Dictionary;
import junit.framework.Assert;

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
