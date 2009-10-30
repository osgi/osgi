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

package javax.accessibility;
public interface AccessibleText {
	public final static int CHARACTER = 1;
	public final static int SENTENCE = 3;
	public final static int WORD = 2;
	java.lang.String getAfterIndex(int var0, int var1);
	java.lang.String getAtIndex(int var0, int var1);
	java.lang.String getBeforeIndex(int var0, int var1);
	int getCaretPosition();
	int getCharCount();
	javax.swing.text.AttributeSet getCharacterAttribute(int var0);
	java.awt.Rectangle getCharacterBounds(int var0);
	int getIndexAtPoint(java.awt.Point var0);
	java.lang.String getSelectedText();
	int getSelectionEnd();
	int getSelectionStart();
}

