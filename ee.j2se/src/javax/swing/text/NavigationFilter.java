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

package javax.swing.text;
public class NavigationFilter {
	public static abstract class FilterBypass {
		public FilterBypass() { } 
		public abstract javax.swing.text.Caret getCaret();
		public abstract void moveDot(int var0, javax.swing.text.Position.Bias var1);
		public abstract void setDot(int var0, javax.swing.text.Position.Bias var1);
	}
	public NavigationFilter() { } 
	public int getNextVisualPositionFrom(javax.swing.text.JTextComponent var0, int var1, javax.swing.text.Position.Bias var2, int var3, javax.swing.text.Position.Bias[] var4) throws javax.swing.text.BadLocationException { return 0; }
	public void moveDot(javax.swing.text.NavigationFilter.FilterBypass var0, int var1, javax.swing.text.Position.Bias var2) { }
	public void setDot(javax.swing.text.NavigationFilter.FilterBypass var0, int var1, javax.swing.text.Position.Bias var2) { }
}

