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

package javax.print;
public abstract class ServiceUIFactory {
	public ServiceUIFactory() { }
	public abstract java.lang.Object getUI(int var0, java.lang.String var1);
	public abstract java.lang.String[] getUIClassNamesForRole(int var0);
	public final static int ABOUT_UIROLE = 1;
	public final static int ADMIN_UIROLE = 2;
	public final static java.lang.String DIALOG_UI = "java.awt.Dialog";
	public final static java.lang.String JCOMPONENT_UI = "javax.swing.JComponent";
	public final static java.lang.String JDIALOG_UI = "javax.swing.JDialog";
	public final static int MAIN_UIROLE = 3;
	public final static java.lang.String PANEL_UI = "java.awt.Panel";
	public final static int RESERVED_UIROLE = 99;
}

