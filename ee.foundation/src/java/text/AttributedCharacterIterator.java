/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.text;
public abstract interface AttributedCharacterIterator extends java.text.CharacterIterator {
	public abstract java.util.Set getAllAttributeKeys();
	public abstract java.lang.Object getAttribute(java.text.AttributedCharacterIterator.Attribute var0);
	public abstract java.util.Map getAttributes();
	public abstract int getRunLimit();
	public abstract int getRunLimit(java.text.AttributedCharacterIterator.Attribute var0);
	public abstract int getRunLimit(java.util.Set var0);
	public abstract int getRunStart();
	public abstract int getRunStart(java.text.AttributedCharacterIterator.Attribute var0);
	public abstract int getRunStart(java.util.Set var0);
	public static class Attribute implements java.io.Serializable {
		protected Attribute(java.lang.String var0) { }
		public final boolean equals(java.lang.Object var0) { return false; }
		protected java.lang.String getName() { return null; }
		public final int hashCode() { return 0; }
		protected java.lang.Object readResolve() throws java.io.InvalidObjectException { return null; }
		public java.lang.String toString() { return null; }
		public final static java.text.AttributedCharacterIterator.Attribute INPUT_METHOD_SEGMENT; static { INPUT_METHOD_SEGMENT = null; }
		public final static java.text.AttributedCharacterIterator.Attribute LANGUAGE; static { LANGUAGE = null; }
		public final static java.text.AttributedCharacterIterator.Attribute READING; static { READING = null; }
	}
}

