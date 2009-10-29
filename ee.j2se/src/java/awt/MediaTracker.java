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

package java.awt;
public class MediaTracker implements java.io.Serializable {
	public MediaTracker(java.awt.Component var0) { }
	public void addImage(java.awt.Image var0, int var1) { }
	public void addImage(java.awt.Image var0, int var1, int var2, int var3) { }
	public boolean checkAll() { return false; }
	public boolean checkAll(boolean var0) { return false; }
	public boolean checkID(int var0) { return false; }
	public boolean checkID(int var0, boolean var1) { return false; }
	public java.lang.Object[] getErrorsAny() { return null; }
	public java.lang.Object[] getErrorsID(int var0) { return null; }
	public boolean isErrorAny() { return false; }
	public boolean isErrorID(int var0) { return false; }
	public void removeImage(java.awt.Image var0) { }
	public void removeImage(java.awt.Image var0, int var1) { }
	public void removeImage(java.awt.Image var0, int var1, int var2, int var3) { }
	public int statusAll(boolean var0) { return 0; }
	public int statusID(int var0, boolean var1) { return 0; }
	public void waitForAll() throws java.lang.InterruptedException { }
	public boolean waitForAll(long var0) throws java.lang.InterruptedException { return false; }
	public void waitForID(int var0) throws java.lang.InterruptedException { }
	public boolean waitForID(int var0, long var1) throws java.lang.InterruptedException { return false; }
	public final static int ABORTED = 2;
	public final static int COMPLETE = 8;
	public final static int ERRORED = 4;
	public final static int LOADING = 1;
}

