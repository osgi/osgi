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
public interface AccessibleAction {
	public final static java.lang.String CLICK = null;
	public final static java.lang.String DECREMENT = null;
	public final static java.lang.String INCREMENT = null;
	public final static java.lang.String TOGGLE_EXPAND = null;
	public final static java.lang.String TOGGLE_POPUP = null;
	boolean doAccessibleAction(int var0);
	int getAccessibleActionCount();
	java.lang.String getAccessibleActionDescription(int var0);
}

