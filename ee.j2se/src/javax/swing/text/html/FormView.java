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

package javax.swing.text.html;
public class FormView extends javax.swing.text.ComponentView implements java.awt.event.ActionListener {
	public FormView(javax.swing.text.Element var0) { super((javax.swing.text.Element) null); }
	public void actionPerformed(java.awt.event.ActionEvent var0) { }
	protected void imageSubmit(java.lang.String var0) { }
	protected void submitData(java.lang.String var0) { }
	/** @deprecated */ public final static java.lang.String RESET; static { RESET = null; }
	/** @deprecated */ public final static java.lang.String SUBMIT; static { SUBMIT = null; }
	protected class MouseEventListener extends java.awt.event.MouseAdapter {
		protected MouseEventListener() { }
	}
}

