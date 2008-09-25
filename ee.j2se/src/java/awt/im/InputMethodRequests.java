/*
 * $Revision$
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

package java.awt.im;
public abstract interface InputMethodRequests {
	public abstract java.text.AttributedCharacterIterator cancelLatestCommittedText(java.text.AttributedCharacterIterator.Attribute[] var0);
	public abstract java.text.AttributedCharacterIterator getCommittedText(int var0, int var1, java.text.AttributedCharacterIterator.Attribute[] var2);
	public abstract int getCommittedTextLength();
	public abstract int getInsertPositionOffset();
	public abstract java.awt.font.TextHitInfo getLocationOffset(int var0, int var1);
	public abstract java.text.AttributedCharacterIterator getSelectedText(java.text.AttributedCharacterIterator.Attribute[] var0);
	public abstract java.awt.Rectangle getTextLocation(java.awt.font.TextHitInfo var0);
}

