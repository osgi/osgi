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

package javax.naming.directory;
public class SearchControls implements java.io.Serializable {
	public final static int OBJECT_SCOPE = 0;
	public final static int ONELEVEL_SCOPE = 1;
	public final static int SUBTREE_SCOPE = 2;
	public SearchControls() { } 
	public SearchControls(int var0, long var1, int var2, java.lang.String[] var3, boolean var4, boolean var5) { } 
	public long getCountLimit() { return 0l; }
	public boolean getDerefLinkFlag() { return false; }
	public java.lang.String[] getReturningAttributes() { return null; }
	public boolean getReturningObjFlag() { return false; }
	public int getSearchScope() { return 0; }
	public int getTimeLimit() { return 0; }
	public void setCountLimit(long var0) { }
	public void setDerefLinkFlag(boolean var0) { }
	public void setReturningAttributes(java.lang.String[] var0) { }
	public void setReturningObjFlag(boolean var0) { }
	public void setSearchScope(int var0) { }
	public void setTimeLimit(int var0) { }
}

