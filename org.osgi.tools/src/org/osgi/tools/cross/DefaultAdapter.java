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

import org.objectweb.asm.*;

public class DefaultAdapter implements ClassVisitor, MethodVisitor {
	String		currentJar;
	
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		// TODO Auto-generated method stub
		
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		// TODO Auto-generated method stub
		return null;
	}

	public void visitAttribute(Attribute attr) {
		// TODO Auto-generated method stub
		
	}

	public void visitEnd() {
		// TODO Auto-generated method stub
		
	}

	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		// TODO Auto-generated method stub
		
	}

	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		// TODO Auto-generated method stub
		return null;
	}

	public void visitOuterClass(String owner, String name, String desc) {
		// TODO Auto-generated method stub
		
	}

	public void visitSource(String source, String debug) {
		// TODO Auto-generated method stub
		
	}

	public AnnotationVisitor visitAnnotationDefault() {
		// TODO Auto-generated method stub
		return null;
	}

	public void visitCode() {
		// TODO Auto-generated method stub
		
	}

	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		// TODO Auto-generated method stub
		
	}

	public void visitIincInsn(int var, int increment) {
		// TODO Auto-generated method stub
		
	}

	public void visitInsn(int opcode) {
		// TODO Auto-generated method stub
		
	}

	public void visitIntInsn(int opcode, int operand) {
		// TODO Auto-generated method stub
		
	}

	public void visitJumpInsn(int opcode, Label label) {
		// TODO Auto-generated method stub
		
	}

	public void visitLabel(Label label) {
		// TODO Auto-generated method stub
		
	}

	public void visitLdcInsn(Object cst) {
		// TODO Auto-generated method stub
		
	}

	public void visitLineNumber(int line, Label start) {
		// TODO Auto-generated method stub
		
	}

	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		// TODO Auto-generated method stub
		
	}

	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		// TODO Auto-generated method stub
		
	}

	public void visitMaxs(int maxStack, int maxLocals) {
		// TODO Auto-generated method stub
		
	}

	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		// TODO Auto-generated method stub
		
	}

	public void visitMultiANewArrayInsn(String desc, int dims) {
		// TODO Auto-generated method stub
		
	}

	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		// TODO Auto-generated method stub
		return null;
	}

	public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
		// TODO Auto-generated method stub
		
	}

	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		// TODO Auto-generated method stub
		
	}

	public void visitTypeInsn(int opcode, String desc) {
		// TODO Auto-generated method stub
		
	}

	public void visitVarInsn(int opcode, int var) {
		// TODO Auto-generated method stub
		
	}

}
