/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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

package java.text;
public interface AttributedCharacterIterator extends java.text.CharacterIterator {
	public static class Attribute implements java.io.Serializable {
		public final static java.text.AttributedCharacterIterator.Attribute INPUT_METHOD_SEGMENT; static { INPUT_METHOD_SEGMENT = null; }
		public final static java.text.AttributedCharacterIterator.Attribute LANGUAGE; static { LANGUAGE = null; }
		public final static java.text.AttributedCharacterIterator.Attribute READING; static { READING = null; }
		protected Attribute(java.lang.String var0) { } 
		public final boolean equals(java.lang.Object var0) { return false; }
		protected java.lang.String getName() { return null; }
		public final int hashCode() { return 0; }
		protected java.lang.Object readResolve() throws java.io.InvalidObjectException { return null; }
	}
	java.util.Set<java.text.AttributedCharacterIterator.Attribute> getAllAttributeKeys();
	java.lang.Object getAttribute(java.text.AttributedCharacterIterator.Attribute var0);
	java.util.Map<java.text.AttributedCharacterIterator.Attribute,java.lang.Object> getAttributes();
	int getRunLimit();
	int getRunLimit(java.text.AttributedCharacterIterator.Attribute var0);
	int getRunLimit(java.util.Set<? extends java.text.AttributedCharacterIterator.Attribute> var0);
	int getRunStart();
	int getRunStart(java.text.AttributedCharacterIterator.Attribute var0);
	int getRunStart(java.util.Set<? extends java.text.AttributedCharacterIterator.Attribute> var0);
}

