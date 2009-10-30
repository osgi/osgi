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
public class DefaultFormatter extends javax.swing.JFormattedTextField.AbstractFormatter implements java.io.Serializable, java.lang.Cloneable {
	public DefaultFormatter() { } 
	public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
	public boolean getAllowsInvalid() { return false; }
	public boolean getCommitsOnValidEdit() { return false; }
	public boolean getOverwriteMode() { return false; }
	public java.lang.Class<?> getValueClass() { return null; }
	public void setAllowsInvalid(boolean var0) { }
	public void setCommitsOnValidEdit(boolean var0) { }
	public void setOverwriteMode(boolean var0) { }
	public void setValueClass(java.lang.Class<?> var0) { }
	public java.lang.Object stringToValue(java.lang.String var0) throws java.text.ParseException { return null; }
	public java.lang.String valueToString(java.lang.Object var0) throws java.text.ParseException { return null; }
}

