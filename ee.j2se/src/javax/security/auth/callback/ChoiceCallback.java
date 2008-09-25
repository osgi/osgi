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

package javax.security.auth.callback;
public class ChoiceCallback implements java.io.Serializable, javax.security.auth.callback.Callback {
	public ChoiceCallback(java.lang.String var0, java.lang.String[] var1, int var2, boolean var3) { }
	public boolean allowMultipleSelections() { return false; }
	public java.lang.String[] getChoices() { return null; }
	public int getDefaultChoice() { return 0; }
	public java.lang.String getPrompt() { return null; }
	public int[] getSelectedIndexes() { return null; }
	public void setSelectedIndex(int var0) { }
	public void setSelectedIndexes(int[] var0) { }
}

