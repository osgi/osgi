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
public class ConfirmationCallback implements java.io.Serializable, javax.security.auth.callback.Callback {
	public ConfirmationCallback(int var0, int var1, int var2) { }
	public ConfirmationCallback(int var0, java.lang.String[] var1, int var2) { }
	public ConfirmationCallback(java.lang.String var0, int var1, int var2, int var3) { }
	public ConfirmationCallback(java.lang.String var0, int var1, java.lang.String[] var2, int var3) { }
	public int getDefaultOption() { return 0; }
	public int getMessageType() { return 0; }
	public int getOptionType() { return 0; }
	public java.lang.String[] getOptions() { return null; }
	public java.lang.String getPrompt() { return null; }
	public int getSelectedIndex() { return 0; }
	public void setSelectedIndex(int var0) { }
	public final static int CANCEL = 2;
	public final static int ERROR = 2;
	public final static int INFORMATION = 0;
	public final static int NO = 1;
	public final static int OK = 3;
	public final static int OK_CANCEL_OPTION = 2;
	public final static int UNSPECIFIED_OPTION = -1;
	public final static int WARNING = 1;
	public final static int YES = 0;
	public final static int YES_NO_CANCEL_OPTION = 1;
	public final static int YES_NO_OPTION = 0;
}

