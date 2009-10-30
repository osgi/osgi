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
public abstract class Format implements java.io.Serializable, java.lang.Cloneable {
	public static class Field extends java.text.AttributedCharacterIterator.Attribute {
		protected Field(java.lang.String var0)  { super((java.lang.String) null); } 
	}
	public Format() { } 
	public java.lang.Object clone() { return null; }
	public final java.lang.String format(java.lang.Object var0) { return null; }
	public abstract java.lang.StringBuffer format(java.lang.Object var0, java.lang.StringBuffer var1, java.text.FieldPosition var2);
	public java.text.AttributedCharacterIterator formatToCharacterIterator(java.lang.Object var0) { return null; }
	public java.lang.Object parseObject(java.lang.String var0) throws java.text.ParseException { return null; }
	public abstract java.lang.Object parseObject(java.lang.String var0, java.text.ParsePosition var1);
}

