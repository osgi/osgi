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

package java.awt.im;
public class InputMethodHighlight {
	public InputMethodHighlight(boolean var0, int var1) { }
	public InputMethodHighlight(boolean var0, int var1, int var2) { }
	public InputMethodHighlight(boolean var0, int var1, int var2, java.util.Map var3) { }
	public int getState() { return 0; }
	public java.util.Map getStyle() { return null; }
	public int getVariation() { return 0; }
	public boolean isSelected() { return false; }
	public final static int CONVERTED_TEXT = 1;
	public final static int RAW_TEXT = 0;
	public final static java.awt.im.InputMethodHighlight SELECTED_CONVERTED_TEXT_HIGHLIGHT; static { SELECTED_CONVERTED_TEXT_HIGHLIGHT = null; }
	public final static java.awt.im.InputMethodHighlight SELECTED_RAW_TEXT_HIGHLIGHT; static { SELECTED_RAW_TEXT_HIGHLIGHT = null; }
	public final static java.awt.im.InputMethodHighlight UNSELECTED_CONVERTED_TEXT_HIGHLIGHT; static { UNSELECTED_CONVERTED_TEXT_HIGHLIGHT = null; }
	public final static java.awt.im.InputMethodHighlight UNSELECTED_RAW_TEXT_HIGHLIGHT; static { UNSELECTED_RAW_TEXT_HIGHLIGHT = null; }
}

