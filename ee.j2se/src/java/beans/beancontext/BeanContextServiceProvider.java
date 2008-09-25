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

package java.beans.beancontext;
public abstract interface BeanContextServiceProvider {
	public abstract java.util.Iterator getCurrentServiceSelectors(java.beans.beancontext.BeanContextServices var0, java.lang.Class var1);
	public abstract java.lang.Object getService(java.beans.beancontext.BeanContextServices var0, java.lang.Object var1, java.lang.Class var2, java.lang.Object var3);
	public abstract void releaseService(java.beans.beancontext.BeanContextServices var0, java.lang.Object var1, java.lang.Object var2);
}

