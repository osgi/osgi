/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2001).
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
import java.io.*;

/**
 * Description for the data type information of an
 * objectclass.
 *
 * @version $Revision$
 * @deprecated The org.osgi.service.metatype package has been replaced by 
 * the org.osgi.service.metatype2 package.
 */
public interface ObjectClassDefinition {

    /**
	 * Argument for <tt>getAttributeDefinitions(int)</tt>. <tt>REQUIRED</tt> indicates
	 * that only the required definitions are returned. The value is 1.
	 */
    final int REQUIRED              = 1;

    /**
	 * Argument for <tt>getAttributeDefinitions(int)</tt>. <tt>OPTIONAL</tt> indicates
	 * that only the optional definitions are returned. The value is 2.
	 */
    final int OPTIONAL              = 2;

	/**
	 * Argument for <tt>getAttributeDefinitions(int)</tt>. <tt>ALL</tt> indicates
	 * that all the definitions are returned. The value is -1.
	 */
    final int ALL                   = 0xFFFFFFFF;

    /**
	 * Return the name of this class.
	 *
	 * @return  The name of the described class.
	 */
    String getName();

    /**
	 * Return the id of this object class.
	 *
	 * <p><tt>ObjectDefintion</tt> objects share a global namespace in the registry. They
	 * share this aspect with LDAP/X.500 attributes. In these
	 * standards the OSI Object Identifier (OID) is used to uniquely
	 * identify object classes. If such an OID exists, (which can be requested
	 * at several standard organisations and many companies already have
	 * a node in the tree) it can be returned here. Otherwise, a unique
	 * id should be returned which can be a java class name (reverse
	 * domain name) or generated with a GUID algorithm. Note that all
	 * LDAP defined object classes already have an OID associated. It is
	 * strongly advised to define the object classes from existing
	 * LDAP schemes which will give the OID for free. Many such
	 * schemes exist ranging from postal addresses to DHCP parameters.
	 *
	 * @return  The id or oid
	 */
     String getID();

    /**
	 * Return a description of this object class.
	 *
	 * The description may be localized.
	 *
	 * @return  The localized description of the definition.
	 */
    String getDescription();

    /**
	 * Return the attribute definitions.
	 *
	 * <p>Return a set of attributes. The filter parameter can distinguish between
	 * <tt>ALL</tt>, <tt>REQUIRED</tt> or the <tt>OPTIONAL</tt> attributes.
	 *
	 * @param   filter      <tt>ALL</tt>, <tt>REQUIRED</tt>, <tt>OPTIONAL</tt>
	 * @return An array of attribute definitions or <tt>null</tt> if no attributes are selected
	 */
    AttributeDefinition[] getAttributeDefinitions(int filter);

    /**
	 * Return an <tt>InputStream</tt> object that can be used to create an icon from.
	 *
	 * <p>Indicate the size and return an <tt>InputStream</tt> object containing an
	 * icon. The returned icon maybe larger or smaller than the indicated
	 * size.
	 *
	 * <p>The icon may depend on the localization.
	 *
	 * @param   size        size of an icon, e.g. a 16x16 pixels icon then size = 16
	 * @return  An InputStream representing an icon or <tt>null</tt>
	 */
    InputStream getIcon( int size ) throws IOException;
}

