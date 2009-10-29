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

package java.text;
public abstract class BreakIterator implements java.lang.Cloneable {
	protected BreakIterator() { }
	public java.lang.Object clone() { return null; }
	public abstract int current();
	public abstract int first();
	public abstract int following(int var0);
	public static java.util.Locale[] getAvailableLocales() { return null; }
	public static java.text.BreakIterator getCharacterInstance() { return null; }
	public static java.text.BreakIterator getCharacterInstance(java.util.Locale var0) { return null; }
	public static java.text.BreakIterator getLineInstance() { return null; }
	public static java.text.BreakIterator getLineInstance(java.util.Locale var0) { return null; }
	public static java.text.BreakIterator getSentenceInstance() { return null; }
	public static java.text.BreakIterator getSentenceInstance(java.util.Locale var0) { return null; }
	public abstract java.text.CharacterIterator getText();
	public static java.text.BreakIterator getWordInstance() { return null; }
	public static java.text.BreakIterator getWordInstance(java.util.Locale var0) { return null; }
	public boolean isBoundary(int var0) { return false; }
	public abstract int last();
	public abstract int next();
	public abstract int next(int var0);
	public int preceding(int var0) { return 0; }
	public abstract int previous();
	public void setText(java.lang.String var0) { }
	public abstract void setText(java.text.CharacterIterator var0);
	public final static int DONE = -1;
}

