/*
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package javax.swing.text;
public class DocumentFilter {
	public DocumentFilter() { }
	public void insertString(javax.swing.text.DocumentFilter.FilterBypass var0, int var1, java.lang.String var2, javax.swing.text.AttributeSet var3) throws javax.swing.text.BadLocationException { }
	public void remove(javax.swing.text.DocumentFilter.FilterBypass var0, int var1, int var2) throws javax.swing.text.BadLocationException { }
	public void replace(javax.swing.text.DocumentFilter.FilterBypass var0, int var1, int var2, java.lang.String var3, javax.swing.text.AttributeSet var4) throws javax.swing.text.BadLocationException { }
	public static abstract class FilterBypass {
		public FilterBypass() { }
		public abstract javax.swing.text.Document getDocument();
		public abstract void insertString(int var0, java.lang.String var1, javax.swing.text.AttributeSet var2) throws javax.swing.text.BadLocationException;
		public abstract void remove(int var0, int var1) throws javax.swing.text.BadLocationException;
		public abstract void replace(int var0, int var1, java.lang.String var2, javax.swing.text.AttributeSet var3) throws javax.swing.text.BadLocationException;
	}
}

