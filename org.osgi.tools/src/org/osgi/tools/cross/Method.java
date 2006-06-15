/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2006). All Rights Reserved.
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

package org.osgi.tools.cross;

public class Method implements Comparable {
	String	clazz;
	String	name;
	String	proto;
	String  pckge;
	String  jar;
	
	Method(String clazz, String name, String proto, String jar ) {
		this.clazz = clazz;
		this.name = name;
		this.proto = proto;
		this.pckge = clazz.substring(0, clazz.lastIndexOf("."));
		this.jar = jar;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Method))
			return false;

		Method oo = (Method) o;
		return clazz.equals(oo.clazz)
				&& name.equals(oo.name) && proto.equals(oo.proto);
	}
	
	public int hashCode() {
		return clazz.hashCode() + name.hashCode() + proto.hashCode();
	}
	
	public String toString() {
		return clazz + "." + name + proto;
	}

	public int compareTo(Object o) {
		Method oo = (Method) o;
		int result = clazz.compareTo(oo.clazz);
		if ( result != 0)
			return result;
		result = name.compareTo(oo.name);
		if ( result != 0)
			return result;
		
		return proto.compareTo(oo.proto);
	}
}
