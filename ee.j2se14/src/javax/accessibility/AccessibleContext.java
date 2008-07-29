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

package javax.accessibility;
public abstract class AccessibleContext {
	public AccessibleContext() { }
	public void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void firePropertyChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) { }
	public javax.accessibility.AccessibleAction getAccessibleAction() { return null; }
	public abstract javax.accessibility.Accessible getAccessibleChild(int var0);
	public abstract int getAccessibleChildrenCount();
	public javax.accessibility.AccessibleComponent getAccessibleComponent() { return null; }
	public java.lang.String getAccessibleDescription() { return null; }
	public javax.accessibility.AccessibleEditableText getAccessibleEditableText() { return null; }
	public javax.accessibility.AccessibleIcon[] getAccessibleIcon() { return null; }
	public abstract int getAccessibleIndexInParent();
	public java.lang.String getAccessibleName() { return null; }
	public javax.accessibility.Accessible getAccessibleParent() { return null; }
	public javax.accessibility.AccessibleRelationSet getAccessibleRelationSet() { return null; }
	public abstract javax.accessibility.AccessibleRole getAccessibleRole();
	public javax.accessibility.AccessibleSelection getAccessibleSelection() { return null; }
	public abstract javax.accessibility.AccessibleStateSet getAccessibleStateSet();
	public javax.accessibility.AccessibleTable getAccessibleTable() { return null; }
	public javax.accessibility.AccessibleText getAccessibleText() { return null; }
	public javax.accessibility.AccessibleValue getAccessibleValue() { return null; }
	public abstract java.util.Locale getLocale();
	public void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void setAccessibleDescription(java.lang.String var0) { }
	public void setAccessibleName(java.lang.String var0) { }
	public void setAccessibleParent(javax.accessibility.Accessible var0) { }
	public final static java.lang.String ACCESSIBLE_ACTION_PROPERTY = "accessibleActionProperty";
	public final static java.lang.String ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY = "AccessibleActiveDescendant";
	public final static java.lang.String ACCESSIBLE_CARET_PROPERTY = "AccessibleCaret";
	public final static java.lang.String ACCESSIBLE_CHILD_PROPERTY = "AccessibleChild";
	public final static java.lang.String ACCESSIBLE_DESCRIPTION_PROPERTY = "AccessibleDescription";
	public final static java.lang.String ACCESSIBLE_HYPERTEXT_OFFSET = "AccessibleHypertextOffset";
	public final static java.lang.String ACCESSIBLE_NAME_PROPERTY = "AccessibleName";
	public final static java.lang.String ACCESSIBLE_SELECTION_PROPERTY = "AccessibleSelection";
	public final static java.lang.String ACCESSIBLE_STATE_PROPERTY = "AccessibleState";
	public final static java.lang.String ACCESSIBLE_TABLE_CAPTION_CHANGED = "accessibleTableCaptionChanged";
	public final static java.lang.String ACCESSIBLE_TABLE_COLUMN_DESCRIPTION_CHANGED = "accessibleTableColumnDescriptionChanged";
	public final static java.lang.String ACCESSIBLE_TABLE_COLUMN_HEADER_CHANGED = "accessibleTableColumnHeaderChanged";
	public final static java.lang.String ACCESSIBLE_TABLE_MODEL_CHANGED = "accessibleTableModelChanged";
	public final static java.lang.String ACCESSIBLE_TABLE_ROW_DESCRIPTION_CHANGED = "accessibleTableRowDescriptionChanged";
	public final static java.lang.String ACCESSIBLE_TABLE_ROW_HEADER_CHANGED = "accessibleTableRowHeaderChanged";
	public final static java.lang.String ACCESSIBLE_TABLE_SUMMARY_CHANGED = "accessibleTableSummaryChanged";
	public final static java.lang.String ACCESSIBLE_TEXT_PROPERTY = "AccessibleText";
	public final static java.lang.String ACCESSIBLE_VALUE_PROPERTY = "AccessibleValue";
	public final static java.lang.String ACCESSIBLE_VISIBLE_DATA_PROPERTY = "AccessibleVisibleData";
	protected java.lang.String accessibleDescription;
	protected java.lang.String accessibleName;
	protected javax.accessibility.Accessible accessibleParent;
}

