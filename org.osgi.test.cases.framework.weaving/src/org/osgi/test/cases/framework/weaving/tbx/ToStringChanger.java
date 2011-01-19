package org.osgi.test.cases.framework.weaving.tbx;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class ToStringChanger implements MethodVisitor {

	private final MethodVisitor visitor;

	private final String expectedConst;
	private final String newConst;
	
	public ToStringChanger(MethodVisitor mv, String expected,
			String changeTo) {
		visitor = mv;
		expectedConst = expected;
		newConst = changeTo;
	}

	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		return visitor.visitAnnotation(arg0, arg1);
	}

	public AnnotationVisitor visitAnnotationDefault() {
		return visitor.visitAnnotationDefault();
	}

	public void visitAttribute(Attribute arg0) {
		visitor.visitAttribute(arg0);
	}

	public void visitCode() {
		visitor.visitCode();
	}

	public void visitEnd() {
		visitor.visitEnd();
	}

	public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {
		visitor.visitFieldInsn(arg0, arg1, arg2, arg3);
	}

	public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3,
			Object[] arg4) {
		visitor.visitFrame(arg0, arg1, arg2, arg3, arg4);
	}

	public void visitIincInsn(int arg0, int arg1) {
		visitor.visitIincInsn(arg0, arg1);
	}

	public void visitInsn(int arg0) {
		visitor.visitInsn(arg0);
	}

	public void visitIntInsn(int arg0, int arg1) {
		visitor.visitIntInsn(arg0, arg1);
	}

	public void visitJumpInsn(int arg0, Label arg1) {
		visitor.visitJumpInsn(arg0, arg1);
	}

	public void visitLabel(Label arg0) {
		visitor.visitLabel(arg0);
	}

	public void visitLdcInsn(Object arg0) {
		if(!!!expectedConst.equals(arg0))
			throw new RuntimeException("Weaving error - expected to see " + expectedConst +
					" but instead saw " + arg0);
		visitor.visitLdcInsn(newConst);
	}

	public void visitLineNumber(int arg0, Label arg1) {
		visitor.visitLineNumber(arg0, arg1);
	}

	public void visitLocalVariable(String arg0, String arg1, String arg2,
			Label arg3, Label arg4, int arg5) {
		visitor.visitLocalVariable(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
		visitor.visitLookupSwitchInsn(arg0, arg1, arg2);
	}

	public void visitMaxs(int arg0, int arg1) {
		visitor.visitMaxs(arg0, arg1);
	}

	public void visitMethodInsn(int arg0, String arg1, String arg2, String arg3) {
		visitor.visitMethodInsn(arg0, arg1, arg2, arg3);
	}

	public void visitMultiANewArrayInsn(String arg0, int arg1) {
		visitor.visitMultiANewArrayInsn(arg0, arg1);
	}

	public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1,
			boolean arg2) {
		return visitor.visitParameterAnnotation(arg0, arg1, arg2);
	}

	public void visitTableSwitchInsn(int arg0, int arg1, Label arg2,
			Label[] arg3) {
		visitor.visitTableSwitchInsn(arg0, arg1, arg2, arg3);
	}

	public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2,
			String arg3) {
		visitor.visitTryCatchBlock(arg0, arg1, arg2, arg3);
	}

	public void visitTypeInsn(int arg0, String arg1) {
		visitor.visitTypeInsn(arg0, arg1);
	}

	public void visitVarInsn(int arg0, int arg1) {
		visitor.visitVarInsn(arg0, arg1);
	}

}
