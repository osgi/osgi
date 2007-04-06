/*
 * $Id$
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

import java.util.*;

import org.objectweb.asm.*;

public class XRef extends DefaultAdapter {
	String	currentClass;
	Map		targetAPI;
	Map		interfacesAPI;
	Method	currentMethod;
	String	interfaces[];
	
	XRef(Map targetAPI, Map interfacesAPI) {
		this.targetAPI = targetAPI;
		this.interfacesAPI = interfacesAPI;
	}
	
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		currentClass = name.replace('/', '.');
		this.interfaces = interfaces;
		for ( int i=0; i<interfaces.length; i++ )
			interfaces[i] = interfaces[i].replace('/', '.');
	}

	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
		currentMethod = new Method(currentClass, name, desc, currentJar);
		for ( int i =0; interfaces!=null && i<interfaces.length; i++ ) {
			Method callee = new Method(interfaces[i],name,desc, null);
			Set set = (Set) interfacesAPI.get(callee);
			if ( set != null )
				set.add(currentMethod);
		}
		return this;
	}
	
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		if ( owner.indexOf("/")<0)
			return;
		
		Method called = new Method(owner.replace('/', '.'), name, desc, currentJar);

		Set set = (Set) targetAPI.get(called);
		if ( set != null ) {
			set.add(currentMethod);
		}
		
	}
}
