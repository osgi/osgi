/*
 * $Date$
 *
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

package javax.accessibility;
public abstract interface AccessibleText {
	public abstract java.lang.String getAfterIndex(int var0, int var1);
	public abstract java.lang.String getAtIndex(int var0, int var1);
	public abstract java.lang.String getBeforeIndex(int var0, int var1);
	public abstract int getCaretPosition();
	public abstract int getCharCount();
	public abstract javax.swing.text.AttributeSet getCharacterAttribute(int var0);
	public abstract java.awt.Rectangle getCharacterBounds(int var0);
	public abstract int getIndexAtPoint(java.awt.Point var0);
	public abstract java.lang.String getSelectedText();
	public abstract int getSelectionEnd();
	public abstract int getSelectionStart();
	public final static int CHARACTER = 1;
	public final static int SENTENCE = 3;
	public final static int WORD = 2;
}

