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

import java.util.Dictionary;

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
