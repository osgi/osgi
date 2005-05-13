/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.metatype;

/**
 * An interface to describe an attribute.
 * 
 * <p>
 * An <code>AttributeDefinition</code> object defines a description of the data
 * type of a property/attribute.
 * 
 * @version $Revision$
 */
public interface AttributeDefinition {
	/**
	 * The <code>STRING</code> (1) type.
	 * 
	 * <p>
	 * Attributes of this type should be stored as <code>String</code>,
	 * <code>Vector</code> with <code>String</code> or <code>String[]</code> objects,
	 * depending on the <code>getCardinality()</code> value.
	 */
	final int	STRING		= 1;
	/**
	 * The <code>LONG</code> (2) type.
	 * 
	 * Attributes of this type should be stored as <code>Long</code>,
	 * <code>Vector</code> with <code>Long</code> or <code>long[]</code> objects,
	 * depending on the <code>getCardinality()</code> value.
	 */
	final int	LONG		= 2;
	/**
	 * The <code>INTEGER</code> (3) type.
	 * 
	 * Attributes of this type should be stored as <code>Integer</code>,
	 * <code>Vector</code> with <code>Integer</code> or <code>int[]</code> objects,
	 * depending on the <code>getCardinality()</code> value.
	 */
	final int	INTEGER		= 3;
	/**
	 * The <code>SHORT</code> (4) type.
	 * 
	 * Attributes of this type should be stored as <code>Short</code>,
	 * <code>Vector</code> with <code>Short</code> or <code>short[]</code> objects,
	 * depending on the <code>getCardinality()</code> value.
	 */
	final int	SHORT		= 4;
	/**
	 * The <code>CHARACTER</code> (5) type.
	 * 
	 * Attributes of this type should be stored as <code>Character</code>,
	 * <code>Vector</code> with <code>Character</code> or <code>char[]</code> objects,
	 * depending on the <code>getCardinality()</code> value.
	 */
	final int	CHARACTER	= 5;
	/**
	 * The <code>BYTE</code> (6) type.
	 * 
	 * Attributes of this type should be stored as <code>Byte</code>,
	 * <code>Vector</code> with <code>Byte</code> or <code>byte[]</code> objects,
	 * depending on the <code>getCardinality()</code> value.
	 */
	final int	BYTE		= 6;
	/**
	 * The <code>DOUBLE</code> (7) type.
	 * 
	 * Attributes of this type should be stored as <code>Double</code>,
	 * <code>Vector</code> with <code>Double</code> or <code>double[]</code> objects,
	 * depending on the <code>getCardinality()</code> value.
	 */
	final int	DOUBLE		= 7;
	/**
	 * The <code>FLOAT</code> (8) type.
	 * 
	 * Attributes of this type should be stored as <code>Float</code>,
	 * <code>Vector</code> with <code>Float</code> or <code>float[]</code> objects,
	 * depending on the <code>getCardinality()</code> value.
	 */
	final int	FLOAT		= 8;
	/**
	 * The <code>BIGINTEGER</code> (9) type.
	 * 
	 * Attributes of this type should be stored as <code>BigInteger</code>,
	 * <code>Vector</code> with <code>BigInteger</code> or <code>BigInteger[]</code>
	 * objects, depending on the <code>getCardinality()</code> value.
	 * 
	 * @deprecated Since 1.1
	 */
	final int	BIGINTEGER	= 9;
	/**
	 * The <code>BIGDECIMAL</code> (10) type.
	 * 
	 * Attributes of this type should be stored as <code>BigDecimal</code>,
	 * <code>Vector</code> with <code>BigDecimal</code> or <code>BigDecimal[]</code>
	 * objects depending on <code>getCardinality()</code>.
	 * 
	 * @deprecated Since 1.1
	 */
	final int	BIGDECIMAL	= 10;
	/**
	 * The <code>BOOLEAN</code> (11) type.
	 * 
	 * Attributes of this type should be stored as <code>Boolean</code>,
	 * <code>Vector</code> with <code>Boolean</code> or <code>boolean[]</code> objects
	 * depending on <code>getCardinality()</code>.
	 */
	final int	BOOLEAN		= 11;

	/**
	 * Get the name of the attribute. This name may be localized.
	 * 
	 * @return The localized name of the definition.
	 */
	String getName();

	/**
	 * Unique identity for this attribute.
	 * 
	 * Attributes share a global namespace in the registry. E.g. an attribute
	 * <code>cn</code> or <code>commonName</code> must always be a <code>String</code>
	 * and the semantics are always a name of some object. They share this
	 * aspect with LDAP/X.500 attributes. In these standards the OSI Object
	 * Identifier (OID) is used to uniquely identify an attribute. If such an
	 * OID exists, (which can be requested at several standard organisations and
	 * many companies already have a node in the tree) it can be returned here.
	 * Otherwise, a unique id should be returned which can be a Java class name
	 * (reverse domain name) or generated with a GUID algorithm. Note that all
	 * LDAP defined attributes already have an OID. It is strongly advised to
	 * define the attributes from existing LDAP schemes which will give the OID.
	 * Many such schemes exist ranging from postal addresses to DHCP parameters.
	 * 
	 * @return The id or oid
	 */
	String getID();

	/**
	 * Return a description of this attribute.
	 * 
	 * The description may be localized and must describe the semantics of this
	 * type and any constraints.
	 * 
	 * @return The localized description of the definition.
	 */
	String getDescription();

	/**
	 * Return the cardinality of this attribute.
	 * 
	 * The OSGi environment handles multi valued attributes in arrays ([]) or in
	 * <code>Vector</code> objects. The return value is defined as follows:
	 * 
	 * <pre>
	 * 
	 *    x = Integer.MIN_VALUE    no limit, but use Vector
	 *    x &lt; 0                    -x = max occurrences, store in Vector
	 *    x &gt; 0                     x = max occurrences, store in array []
	 *    x = Integer.MAX_VALUE    no limit, but use array []
	 *    x = 0                     1 occurrence required
	 *  
	 * </pre>
	 */
	int getCardinality();

	/**
	 * Return the type for this attribute.
	 * 
	 * <p>
	 * Defined in the following constants which map to the appropriate Java
	 * type. <code>STRING</code>,<code>LONG</code>,<code>INTEGER</code>,
	 * <code>CHAR</code>,<code>BYTE</code>,<code>DOUBLE</code>,<code>FLOAT</code>,
	 * <code>BOOLEAN</code>.
	 */
	int getType();

	/**
	 * Return a list of option values that this attribute can take.
	 * 
	 * <p>
	 * If the function returns <code>null</code>, there are no option values
	 * available.
	 * 
	 * <p>
	 * Each value must be acceptable to validate() (return "") and must be a
	 * <code>String</code> object that can be converted to the data type defined
	 * by getType() for this attribute.
	 * 
	 * <p>
	 * This list must be in the same sequence as <code>getOptionLabels()</code>.
	 * I.e. for each index i in <code>getOptionValues</code>, i in
	 * <code>getOptionLabels()</code> should be the label.
	 * 
	 * <p>
	 * For example, if an attribute can have the value male, female, unknown,
	 * this list can return
	 * <code>new String[] { "male", "female", "unknown" }</code>.
	 * 
	 * @return A list values
	 */
	String[] getOptionValues();

	/**
	 * Return a list of labels of option values.
	 * 
	 * <p>
	 * The purpose of this method is to allow menus with localized labels. It is
	 * associated with <code>getOptionValues</code>. The labels returned here are
	 * ordered in the same way as the values in that method.
	 * 
	 * <p>
	 * If the function returns <code>null</code>, there are no option labels
	 * available.
	 * <p>
	 * This list must be in the same sequence as the <code>getOptionValues()</code>
	 * method. I.e. for each index i in <code>getOptionLabels</code>, i in
	 * <code>getOptionValues()</code> should be the associated value.
	 * 
	 * <p>
	 * For example, if an attribute can have the value male, female, unknown,
	 * this list can return (for dutch)
	 * <code>new String[] { "Man", "Vrouw", "Onbekend" }</code>.
	 * 
	 * @return A list values
	 */
	String[] getOptionLabels();

	/**
	 * Validate an attribute in <code>String</code> form.
	 * 
	 * An attribute might be further constrained in value. This method will
	 * attempt to validate the attribute according to these constraints. It can
	 * return three different values:
	 * 
	 * <table>
	 * <tr>
	 *  <td><code>null</code></td> <td>no validation present</td>
	 * </tr><tr> 
	 *  <td>&quot;&quot;</td> <td>no problems detected</td>
	 * </tr><tr> 
	 *  <td>&quot;...&quot;</td> <td>A localized description of why the value is wrong</td>
	 * </tr> 
	 * </table>
	 * 
	 * @param value The value before turning it into the basic data type
	 * @return <code>null</code>, "", or another string
	 */
	String validate(String value);

	/**
	 * Return a default for this attribute.
	 * 
	 * The object must be of the appropriate type as defined by the cardinality
	 * and <code>getType()</code>. The return type is a list of <code>String</code>
	 * objects that can be converted to the appropriate type. The cardinality of
	 * the return array must follow the absolute cardinality of this type. E.g.
	 * if the cardinality = 0, the array must contain 1 element. If the
	 * cardinality is 1, it must contain 0 or 1 elements. If it is -5, it must
	 * contain from 0 to max 5 elements. Note that the special case of a 0
	 * cardinality, meaning a single value, does not allow arrays or vectors of
	 * 0 elements.
	 * 
	 * @return Return a default value or <code>null</code> if no default exists.
	 */
	String[] getDefaultValue();
}
