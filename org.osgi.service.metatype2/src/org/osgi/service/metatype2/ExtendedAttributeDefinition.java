/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
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
