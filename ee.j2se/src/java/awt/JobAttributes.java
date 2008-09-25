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

package java.awt;
public final class JobAttributes implements java.lang.Cloneable {
	public JobAttributes() { }
	public JobAttributes(int var0, java.awt.JobAttributes.DefaultSelectionType var1, java.awt.JobAttributes.DestinationType var2, java.awt.JobAttributes.DialogType var3, java.lang.String var4, int var5, int var6, java.awt.JobAttributes.MultipleDocumentHandlingType var7, int[][] var8, java.lang.String var9, java.awt.JobAttributes.SidesType var10) { }
	public JobAttributes(java.awt.JobAttributes var0) { }
	public java.lang.Object clone() { return null; }
	public int getCopies() { return 0; }
	public java.awt.JobAttributes.DefaultSelectionType getDefaultSelection() { return null; }
	public java.awt.JobAttributes.DestinationType getDestination() { return null; }
	public java.awt.JobAttributes.DialogType getDialog() { return null; }
	public java.lang.String getFileName() { return null; }
	public int getFromPage() { return 0; }
	public int getMaxPage() { return 0; }
	public int getMinPage() { return 0; }
	public java.awt.JobAttributes.MultipleDocumentHandlingType getMultipleDocumentHandling() { return null; }
	public int[][] getPageRanges() { return null; }
	public java.lang.String getPrinter() { return null; }
	public java.awt.JobAttributes.SidesType getSides() { return null; }
	public int getToPage() { return 0; }
	public int hashCode() { return 0; }
	public void set(java.awt.JobAttributes var0) { }
	public void setCopies(int var0) { }
	public void setCopiesToDefault() { }
	public void setDefaultSelection(java.awt.JobAttributes.DefaultSelectionType var0) { }
	public void setDestination(java.awt.JobAttributes.DestinationType var0) { }
	public void setDialog(java.awt.JobAttributes.DialogType var0) { }
	public void setFileName(java.lang.String var0) { }
	public void setFromPage(int var0) { }
	public void setMaxPage(int var0) { }
	public void setMinPage(int var0) { }
	public void setMultipleDocumentHandling(java.awt.JobAttributes.MultipleDocumentHandlingType var0) { }
	public void setMultipleDocumentHandlingToDefault() { }
	public void setPageRanges(int[][] var0) { }
	public void setPrinter(java.lang.String var0) { }
	public void setSides(java.awt.JobAttributes.SidesType var0) { }
	public void setSidesToDefault() { }
	public void setToPage(int var0) { }
	public static final class DefaultSelectionType extends java.awt.AttributeValue {
		public final static java.awt.JobAttributes.DefaultSelectionType ALL; static { ALL = null; }
		public final static java.awt.JobAttributes.DefaultSelectionType RANGE; static { RANGE = null; }
		public final static java.awt.JobAttributes.DefaultSelectionType SELECTION; static { SELECTION = null; }
		private DefaultSelectionType() { super(0, (java.lang.String[]) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
	public static final class DestinationType extends java.awt.AttributeValue {
		public final static java.awt.JobAttributes.DestinationType FILE; static { FILE = null; }
		public final static java.awt.JobAttributes.DestinationType PRINTER; static { PRINTER = null; }
		private DestinationType() { super(0, (java.lang.String[]) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
	public static final class DialogType extends java.awt.AttributeValue {
		public final static java.awt.JobAttributes.DialogType COMMON; static { COMMON = null; }
		public final static java.awt.JobAttributes.DialogType NATIVE; static { NATIVE = null; }
		public final static java.awt.JobAttributes.DialogType NONE; static { NONE = null; }
		private DialogType() { super(0, (java.lang.String[]) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
	public static final class MultipleDocumentHandlingType extends java.awt.AttributeValue {
		public final static java.awt.JobAttributes.MultipleDocumentHandlingType SEPARATE_DOCUMENTS_COLLATED_COPIES; static { SEPARATE_DOCUMENTS_COLLATED_COPIES = null; }
		public final static java.awt.JobAttributes.MultipleDocumentHandlingType SEPARATE_DOCUMENTS_UNCOLLATED_COPIES; static { SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = null; }
		private MultipleDocumentHandlingType() { super(0, (java.lang.String[]) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
	public static final class SidesType extends java.awt.AttributeValue {
		public final static java.awt.JobAttributes.SidesType ONE_SIDED; static { ONE_SIDED = null; }
		public final static java.awt.JobAttributes.SidesType TWO_SIDED_LONG_EDGE; static { TWO_SIDED_LONG_EDGE = null; }
		public final static java.awt.JobAttributes.SidesType TWO_SIDED_SHORT_EDGE; static { TWO_SIDED_SHORT_EDGE = null; }
		private SidesType() { super(0, (java.lang.String[]) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
}

