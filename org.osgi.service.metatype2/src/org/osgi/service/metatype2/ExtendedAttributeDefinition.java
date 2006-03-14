/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.service.metatype2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import org.osgi.service.metatype.AttributeDefinition;

/**
 * An interface to describe a complex attribute.
 *
 * <p>An <code>ExtendedAttributeDefinition</code> object defines the description of an
 * attribute whose data type can be complex.
 *
 * @version $Revision$
 */
public interface ExtendedAttributeDefinition extends AttributeDefinition {

  /**
   * The <code>OBJECT_CLASS</code>(12) type.
   *
   * Attributes of this type should be stored as an object, 
   * <code>Vector</code> with objects or an array of these objects depending
   * on <code>getCardinality()</code>.
   */
  static final int OBJECT_CLASS = 12;

  /**
   * Key used to store and/or retrieve the minimum value of the attribute
   * from its additional properties.
   */
  static final String MINIMUM = "minValue";

  /**
   * Key used to store and/or retrieve the maximum value of the attribute
   * from its additional properties.
   */
  static final String MAXIMUM = "maxValue";

  /**
   * Key used to store and/or retrieve the step value of the attribute
   * from its additional properties.
   */
  static final String STEP = "stepValue";

  /**
   * Key used to indicate if the attribute is hidden or not.
   */
  static final String HIDDEN = "hidden";

  /**
   * Key used to indicate if the attribute is read only or not.
   */
  static final String READ_ONLY = "readOnly";

  /**
   * Return the additional properties of this attribute.
   *
   * @return Return a dictionary that contains the additional properties or null
   * if there is no additional properties.
   */
  Dictionary getProperties();

  /**
   * Return the attribute definitions attached to this attribute if 
   * the type (as returned by <code>getType()</code>) is OBJECT_CLASS. 
   * 
   * @return Returns the attribute definitions of the class if this 
   * attribute has the type OBJECT_CLASS, null otherwise.
   */
  ExtendedAttributeDefinition[] getAttributeDefinitions();
  
  /**
   * Return an <code>InputStream</code> object that can be used to create an
   * icon from.
   * 
   * <p>
   * Indicate the size and return an <code>InputStream</code> object containing
   * an icon. The returned icon maybe larger or smaller than the indicated
   * size.
   * 
   * <p>
   * The icon may depend on the localization.
   * 
   * @param size Requested size of an icon, e.g. a 16x16 pixels icon then size =
   *        16
   * @return An InputStream representing an icon or <code>null</code>
   * @throws IOException if an I/O error occurs
   */
  InputStream getIcon(int size) throws IOException;
}
