/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2001, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.service.metatype;
/**
 * An interface to describe an attribute.
 *
 * <p>An <tt>AttributeDefinition</tt> object defines a description of the
 * data type of a property/attribute.
 *
 * @version $Revision$
 * @deprecated The org.osgi.service.metatype package has been replaced by 
 * the org.osgi.service.metatype2 package.
 */
public interface AttributeDefinition {

    /**
	 * The <tt>STRING</tt>(1) type.
	 *
	 * <p>Attributes of this type should be stored as <tt>String</tt>,
	 * <tt>Vector</tt> with <tt>String</tt> or <tt>String[]</tt> objects, depending
	 * on the <tt>getCardinality()</tt> value.
	 */
    final int STRING            = 1;

    /**
	 * The <tt>LONG</tt>(2) type.
	 *
	 * Attributes of this type should be stored as <tt>Long</tt>,
	 * <tt>Vector</tt> with <tt>Long</tt> or <tt>long[]</tt> objects, depending
	 * on the <tt>getCardinality()</tt> value.
	 */
    final int LONG              = 2;

    /**
	 * The <tt>INTEGER</tt>(3) type.
	 *
	 * Attributes of this type should be stored as <tt>Integer</tt>,
	 * <tt>Vector</tt> with <tt>Integer</tt> or <tt>int[]</tt> objects, depending
	 * on the <tt>getCardinality()</tt> value.
	 */
    final int INTEGER           = 3;

    /**
	 * The <tt>SHORT</tt>(4) type.
	 *
	 * Attributes of this type should be stored as <tt>Short</tt>,
	 * <tt>Vector</tt> with <tt>Short</tt> or <tt>short[]</tt> objects, depending
	 * on the <tt>getCardinality()</tt> value.
	 */
    final int SHORT             = 4;

    /**
	 * The <tt>CHARACTER</tt>(5) type.
	 *
	 * Attributes of this type should be stored as <tt>Character</tt>,
	 * <tt>Vector</tt> with <tt>Character</tt> or <tt>char[]</tt> objects, depending
	 * on the <tt>getCardinality()</tt> value.
	 */
    final int CHARACTER         = 5;

    /**
	 * The <tt>BYTE</tt>(6) type.
	 *
	 * Attributes of this type should be stored as <tt>Byte</tt>,
	 * <tt>Vector</tt> with <tt>Byte</tt> or <tt>byte[]</tt> objects, depending
	 * on the <tt>getCardinality()</tt> value.
	 */
    final int BYTE              = 6;

    /**
	 * The <tt>DOUBLE</tt>(7) type.
	 *
	 * Attributes of this type should be stored as <tt>Double</tt>,
	 * <tt>Vector</tt> with <tt>Double</tt> or <tt>double[]</tt> objects, depending
	 * on the <tt>getCardinality()</tt> value.
	 */
    final int DOUBLE            = 7;

    /**
	 * The <tt>FLOAT</tt>(8) type.
	 *
	 * Attributes of this type should be stored as <tt>Float</tt>, 
	 * <tt>Vector</tt> with <tt>Float</tt> or <tt>float[]</tt> objects, depending
	 * on the <tt>getCardinality()</tt> value.
	 */
    final int FLOAT             = 8;

    /**
	 * The <tt>BIGINTEGER</tt>(9) type.
	 *
	 * Attributes of this type should be stored as <tt>BigInteger</tt>, 
	 * <tt>Vector</tt> with <tt>BigInteger</tt> or <tt>BigInteger[]</tt> objects, depending
	 * on the <tt>getCardinality()</tt> value.
	 */
    final int BIGINTEGER        = 9;

    /**
	 * The <tt>BIGDECIMAL</tt>(10) type.
	 *
	 * Attributes of this type should be stored as <tt>BigDecimal</tt>, 
	 * <tt>Vector</tt> with <tt>BigDecimal</tt> or <tt>BigDecimal[]</tt> objects depending
	 * on <tt>getCardinality()</tt>.
	 */
    final int BIGDECIMAL    = 10;


    /**
     * The <tt>BOOLEAN</tt>(11) type.
     *
     * Attributes of this type should be stored as <tt>Boolean</tt>, 
     * <tt>Vector</tt> with <tt>Boolean</tt> or <tt>boolean[]</tt> objects  depending
     * on <tt>getCardinality()</tt>.
    */
    final int BOOLEAN       = 11;


    /**
	 * Get the name of the attribute. This name may be
	 * localized.
	 *
	 * @return The localized name of the definition.
	 */
    String getName();

    /**
	 * Unique identity for this attribute.
	 *
	 * Attributes share a global namespace in the registry. E.g.
	 * an attribute <tt>cn</tt> or <tt>commonName</tt> must always be a <tt>String</tt>
	 * and the semantics are always a name of some object. They
	 * share this aspect with LDAP/X.500 attributes. In these
	 * standards the OSI Object Identifier (OID) is used to uniquely
	 * identify an attribute. If such an OID exists, (which can be requested
	 * at several standard organisations and many companies already have
	 * a node in the tree) it can be returned here. Otherwise, a unique
	 * id should be returned which can be a Java class name (reverse
	 * domain name) or generated with a GUID algorithm. Note that all
	 * LDAP defined attributes already have an OID. It is
	 * strongly advised to define the attributes from existing
	 * LDAP schemes which will give the OID. Many such
	 * schemes exist ranging from postal addresses to DHCP parameters.
	 *
	 * @return The id or oid
	 */
    String getID();

    /**
	 * Return a description of this attribute.
	 *
	 * The description may be localized and must describe
	 * the semantics of this type and any constraints.
	 *
	 * @return The localized description of the definition.
	 */
    String getDescription();

    /**
	 * Return the cardinality of this attribute.
	 *
	 * The OSGi environment handles multi valued
	 * attributes in arrays ([]) or in <tt>Vector</tt> objects. The
	 * return value is defined as follows:
	 * <pre>
	 * x = Integer.MIN_VALUE    no limit, but use Vector
	 * x < 0                    -x = max occurrences, store in Vector
	 * x > 0                     x = max occurrences, store in array []
	 * x = Integer.MAX_VALUE    no limit, but use array []
	 * x = 0                     1 occurrence required
	 * </pre>
	 */
    int getCardinality();

    /**
	 * Return the type for this attribute.
	
	 * <p>Defined in the
	 * following constants which map to the appropriate Java type.
	 * <tt>STRING</tt>, <tt>LONG</tt>, <tt>INTEGER</tt>, <tt>CHAR</tt>,
	 * <tt>BYTE</tt>, <tt>DOUBLE</tt>, <tt>FLOAT</tt>, <tt>BIGINTEGER</tt>,
	 * <tt>BIGDECIMAL</tt>, <tt>BOOLEAN</tt>.
	 */
    int getType();

    /**
	 * Return a list of option values that this attribute can take.
	 *
	 * <p>If the function returns <tt>null</tt>, there are no option values available.
	 *
	 * <p>Each value must be acceptable to validate() (return "") and
	 * must be a <tt>String</tt> object that can be converted to the data type
	 * defined by getType() for this attribute.
	 *
	 * <p>This list must be in the same sequence as <tt>getOptionLabels()</tt>.
	 * I.e. for each index i in <tt>getOptionValues</tt>, i in <tt>getOptionLabels()</tt>
	 * should be the label.
	 *
	 * <p>For example, if an attribute can have the value male, female, unknown,
	 * this list can return <tt>new String[] { "male", "female", "unknown" }</tt>.
	 *
	 * @return A list values
	 */
    String[] getOptionValues();

    /**
	 * Return a list of labels of option values.
	 *
	 * <p>The purpose of this method is to allow menus with localized labels. It
	 * is associated with <tt>getOptionValues</tt>. The labels returned here are
	 * ordered in the same way as the values in that method.
	 *
	 * <p>If the function returns <tt>null</tt>, there are no option labels available.
	 * <p>This list must be in the same sequence as the <tt>getOptionValues()</tt> method.
	 * I.e. for each index i in <tt>getOptionLabels</tt>, i in <tt>getOptionValues()</tt>
	 * should be the associated value.
	 *
	 * <p>For example, if an attribute can have the value male, female, unknown,
	 * this list can return (for dutch) <tt>new String[] { "Man", "Vrouw", "Onbekend" }</tt>.
	 *
	 * @return A list values
	 */
    String[] getOptionLabels();

    /**
	 * Validate an attribute in <tt>String</tt> form.
	 *
	 * An attribute might be further constrained in value. This method
	 * will attempt to validate the attribute according to these
	 * constraints. It can return three different values:
	 * <pre>
	 * <tt>null</tt>                no validation present
	 * ""                   no problems detected
	 * "..."                A localized description of why the value is wrong
	 * </pre>
	 * @param       value   The value before turning it into the basic data type
	 * @return <tt>null</tt>, "", or another string
	 */
    String validate( String value );

    /**
	 * Return a default for this attribute.
	 *
	 * The object must be of the appropriate type as defined
	 * by the cardinality and <tt>getType()</tt>. The return type is a list
	 * of <tt>String</tt> objects that can be converted to the appropriate type.
	 * The cardinality of the return array must follow the
	 * absolute cardinality of this type. E.g. if the cardinality = 0, the
	 * array must contain 1 element. If the cardinality is 1, it
	 * must contain 0 or 1 elements. If it is -5, it must contain
	 * from 0 to max 5 elements. Note that the special case of a 0 cardinality,
	 * meaning a single value, does not allow arrays or vectors of 0 elements.
	 *
	 * @return Return a default value or <tt>null</tt> if no default exists.
	 */

    String [] getDefaultValue();
}


