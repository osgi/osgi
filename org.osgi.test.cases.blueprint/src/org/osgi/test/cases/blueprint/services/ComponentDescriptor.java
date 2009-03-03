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
 * Representation of a component-reference value stored inside of a
 * component.
 */
public class ComponentDescriptor extends ValueDescriptor {
     // a potential component snapshot, held as a Dictionary bundle
     protected Dictionary snapshot;

     /**
      * Create a component value from a name and value object.
      *
      * @param name   The name of the value entity.
      * @param value  The object value.
      */
     public ComponentDescriptor(String name, Object value) {
         this(name, value, null);
     }

     public ComponentDescriptor(String name, Object value, Class clz) {
         super(name, value, clz);
         // if we have a a real value, then snag some additional
         // information about this.
         if (value != null) {
             // if this value is another injected component, attach a snapshot of
             // the object's properties at the point of verifcation.
             if (value instanceof ComponentTestInfo) {
                 snapshot = ((ComponentTestInfo)value).getComponentProperties();
             }
         }
     }

     // return a snapshot of the component's internal properties
     // at the point of injection.
     public Dictionary getSnapShot() {
         return snapshot;
     }
}
