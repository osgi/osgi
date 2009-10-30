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

package java.awt;
public final class PageAttributes implements java.lang.Cloneable {
	public static final class ColorType extends java.awt.AttributeValue {
		public final static java.awt.PageAttributes.ColorType COLOR; static { COLOR = null; }
		public final static java.awt.PageAttributes.ColorType MONOCHROME; static { MONOCHROME = null; }
		private ColorType()  { super(0, (java.lang.String[]) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
	public static final class MediaType extends java.awt.AttributeValue {
		public final static java.awt.PageAttributes.MediaType A; static { A = null; }
		public final static java.awt.PageAttributes.MediaType A0; static { A0 = null; }
		public final static java.awt.PageAttributes.MediaType A1; static { A1 = null; }
		public final static java.awt.PageAttributes.MediaType A10; static { A10 = null; }
		public final static java.awt.PageAttributes.MediaType A2; static { A2 = null; }
		public final static java.awt.PageAttributes.MediaType A3; static { A3 = null; }
		public final static java.awt.PageAttributes.MediaType A4; static { A4 = null; }
		public final static java.awt.PageAttributes.MediaType A5; static { A5 = null; }
		public final static java.awt.PageAttributes.MediaType A6; static { A6 = null; }
		public final static java.awt.PageAttributes.MediaType A7; static { A7 = null; }
		public final static java.awt.PageAttributes.MediaType A8; static { A8 = null; }
		public final static java.awt.PageAttributes.MediaType A9; static { A9 = null; }
		public final static java.awt.PageAttributes.MediaType B; static { B = null; }
		public final static java.awt.PageAttributes.MediaType B0; static { B0 = null; }
		public final static java.awt.PageAttributes.MediaType B1; static { B1 = null; }
		public final static java.awt.PageAttributes.MediaType B10; static { B10 = null; }
		public final static java.awt.PageAttributes.MediaType B2; static { B2 = null; }
		public final static java.awt.PageAttributes.MediaType B3; static { B3 = null; }
		public final static java.awt.PageAttributes.MediaType B4; static { B4 = null; }
		public final static java.awt.PageAttributes.MediaType B5; static { B5 = null; }
		public final static java.awt.PageAttributes.MediaType B6; static { B6 = null; }
		public final static java.awt.PageAttributes.MediaType B7; static { B7 = null; }
		public final static java.awt.PageAttributes.MediaType B8; static { B8 = null; }
		public final static java.awt.PageAttributes.MediaType B9; static { B9 = null; }
		public final static java.awt.PageAttributes.MediaType C; static { C = null; }
		public final static java.awt.PageAttributes.MediaType C0; static { C0 = null; }
		public final static java.awt.PageAttributes.MediaType C1; static { C1 = null; }
		public final static java.awt.PageAttributes.MediaType C10; static { C10 = null; }
		public final static java.awt.PageAttributes.MediaType C2; static { C2 = null; }
		public final static java.awt.PageAttributes.MediaType C3; static { C3 = null; }
		public final static java.awt.PageAttributes.MediaType C4; static { C4 = null; }
		public final static java.awt.PageAttributes.MediaType C5; static { C5 = null; }
		public final static java.awt.PageAttributes.MediaType C6; static { C6 = null; }
		public final static java.awt.PageAttributes.MediaType C7; static { C7 = null; }
		public final static java.awt.PageAttributes.MediaType C8; static { C8 = null; }
		public final static java.awt.PageAttributes.MediaType C9; static { C9 = null; }
		public final static java.awt.PageAttributes.MediaType D; static { D = null; }
		public final static java.awt.PageAttributes.MediaType E; static { E = null; }
		public final static java.awt.PageAttributes.MediaType ENV_10; static { ENV_10 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_10X13; static { ENV_10X13 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_10X14; static { ENV_10X14 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_10X15; static { ENV_10X15 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_11; static { ENV_11 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_12; static { ENV_12 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_14; static { ENV_14 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_6X9; static { ENV_6X9 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_7X9; static { ENV_7X9 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_9; static { ENV_9 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_9X11; static { ENV_9X11 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_9X12; static { ENV_9X12 = null; }
		public final static java.awt.PageAttributes.MediaType ENV_INVITE; static { ENV_INVITE = null; }
		public final static java.awt.PageAttributes.MediaType ENV_ITALY; static { ENV_ITALY = null; }
		public final static java.awt.PageAttributes.MediaType ENV_MONARCH; static { ENV_MONARCH = null; }
		public final static java.awt.PageAttributes.MediaType ENV_PERSONAL; static { ENV_PERSONAL = null; }
		public final static java.awt.PageAttributes.MediaType EXECUTIVE; static { EXECUTIVE = null; }
		public final static java.awt.PageAttributes.MediaType FOLIO; static { FOLIO = null; }
		public final static java.awt.PageAttributes.MediaType INVITE; static { INVITE = null; }
		public final static java.awt.PageAttributes.MediaType INVITE_ENVELOPE; static { INVITE_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType INVOICE; static { INVOICE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_2A0; static { ISO_2A0 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_4A0; static { ISO_4A0 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_A0; static { ISO_A0 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_A1; static { ISO_A1 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_A10; static { ISO_A10 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_A2; static { ISO_A2 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_A3; static { ISO_A3 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_A4; static { ISO_A4 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_A5; static { ISO_A5 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_A6; static { ISO_A6 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_A7; static { ISO_A7 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_A8; static { ISO_A8 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_A9; static { ISO_A9 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B0; static { ISO_B0 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B1; static { ISO_B1 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B10; static { ISO_B10 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B2; static { ISO_B2 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B3; static { ISO_B3 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B4; static { ISO_B4 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B4_ENVELOPE; static { ISO_B4_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B5; static { ISO_B5 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B5_ENVELOPE; static { ISO_B5_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B6; static { ISO_B6 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B7; static { ISO_B7 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B8; static { ISO_B8 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_B9; static { ISO_B9 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C0; static { ISO_C0 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C0_ENVELOPE; static { ISO_C0_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C1; static { ISO_C1 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C10; static { ISO_C10 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C10_ENVELOPE; static { ISO_C10_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C1_ENVELOPE; static { ISO_C1_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C2; static { ISO_C2 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C2_ENVELOPE; static { ISO_C2_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C3; static { ISO_C3 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C3_ENVELOPE; static { ISO_C3_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C4; static { ISO_C4 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C4_ENVELOPE; static { ISO_C4_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C5; static { ISO_C5 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C5_ENVELOPE; static { ISO_C5_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C6; static { ISO_C6 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C6_ENVELOPE; static { ISO_C6_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C7; static { ISO_C7 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C7_ENVELOPE; static { ISO_C7_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C8; static { ISO_C8 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C8_ENVELOPE; static { ISO_C8_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C9; static { ISO_C9 = null; }
		public final static java.awt.PageAttributes.MediaType ISO_C9_ENVELOPE; static { ISO_C9_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ISO_DESIGNATED_LONG; static { ISO_DESIGNATED_LONG = null; }
		public final static java.awt.PageAttributes.MediaType ISO_DESIGNATED_LONG_ENVELOPE; static { ISO_DESIGNATED_LONG_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType ITALY; static { ITALY = null; }
		public final static java.awt.PageAttributes.MediaType ITALY_ENVELOPE; static { ITALY_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType JIS_B0; static { JIS_B0 = null; }
		public final static java.awt.PageAttributes.MediaType JIS_B1; static { JIS_B1 = null; }
		public final static java.awt.PageAttributes.MediaType JIS_B10; static { JIS_B10 = null; }
		public final static java.awt.PageAttributes.MediaType JIS_B2; static { JIS_B2 = null; }
		public final static java.awt.PageAttributes.MediaType JIS_B3; static { JIS_B3 = null; }
		public final static java.awt.PageAttributes.MediaType JIS_B4; static { JIS_B4 = null; }
		public final static java.awt.PageAttributes.MediaType JIS_B5; static { JIS_B5 = null; }
		public final static java.awt.PageAttributes.MediaType JIS_B6; static { JIS_B6 = null; }
		public final static java.awt.PageAttributes.MediaType JIS_B7; static { JIS_B7 = null; }
		public final static java.awt.PageAttributes.MediaType JIS_B8; static { JIS_B8 = null; }
		public final static java.awt.PageAttributes.MediaType JIS_B9; static { JIS_B9 = null; }
		public final static java.awt.PageAttributes.MediaType LEDGER; static { LEDGER = null; }
		public final static java.awt.PageAttributes.MediaType LEGAL; static { LEGAL = null; }
		public final static java.awt.PageAttributes.MediaType LETTER; static { LETTER = null; }
		public final static java.awt.PageAttributes.MediaType MONARCH; static { MONARCH = null; }
		public final static java.awt.PageAttributes.MediaType MONARCH_ENVELOPE; static { MONARCH_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_10X13_ENVELOPE; static { NA_10X13_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_10X14_ENVELOPE; static { NA_10X14_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_10X15_ENVELOPE; static { NA_10X15_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_6X9_ENVELOPE; static { NA_6X9_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_7X9_ENVELOPE; static { NA_7X9_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_9X11_ENVELOPE; static { NA_9X11_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_9X12_ENVELOPE; static { NA_9X12_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_LEGAL; static { NA_LEGAL = null; }
		public final static java.awt.PageAttributes.MediaType NA_LETTER; static { NA_LETTER = null; }
		public final static java.awt.PageAttributes.MediaType NA_NUMBER_10_ENVELOPE; static { NA_NUMBER_10_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_NUMBER_11_ENVELOPE; static { NA_NUMBER_11_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_NUMBER_12_ENVELOPE; static { NA_NUMBER_12_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_NUMBER_14_ENVELOPE; static { NA_NUMBER_14_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NA_NUMBER_9_ENVELOPE; static { NA_NUMBER_9_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType NOTE; static { NOTE = null; }
		public final static java.awt.PageAttributes.MediaType PERSONAL; static { PERSONAL = null; }
		public final static java.awt.PageAttributes.MediaType PERSONAL_ENVELOPE; static { PERSONAL_ENVELOPE = null; }
		public final static java.awt.PageAttributes.MediaType QUARTO; static { QUARTO = null; }
		public final static java.awt.PageAttributes.MediaType STATEMENT; static { STATEMENT = null; }
		public final static java.awt.PageAttributes.MediaType TABLOID; static { TABLOID = null; }
		private MediaType()  { super(0, (java.lang.String[]) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
	public static final class OrientationRequestedType extends java.awt.AttributeValue {
		public final static java.awt.PageAttributes.OrientationRequestedType LANDSCAPE; static { LANDSCAPE = null; }
		public final static java.awt.PageAttributes.OrientationRequestedType PORTRAIT; static { PORTRAIT = null; }
		private OrientationRequestedType()  { super(0, (java.lang.String[]) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
	public static final class OriginType extends java.awt.AttributeValue {
		public final static java.awt.PageAttributes.OriginType PHYSICAL; static { PHYSICAL = null; }
		public final static java.awt.PageAttributes.OriginType PRINTABLE; static { PRINTABLE = null; }
		private OriginType()  { super(0, (java.lang.String[]) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
	public static final class PrintQualityType extends java.awt.AttributeValue {
		public final static java.awt.PageAttributes.PrintQualityType DRAFT; static { DRAFT = null; }
		public final static java.awt.PageAttributes.PrintQualityType HIGH; static { HIGH = null; }
		public final static java.awt.PageAttributes.PrintQualityType NORMAL; static { NORMAL = null; }
		private PrintQualityType()  { super(0, (java.lang.String[]) null); } /* generated constructor to prevent compiler adding default public constructor */
	}
	public PageAttributes() { } 
	public PageAttributes(java.awt.PageAttributes.ColorType var0, java.awt.PageAttributes.MediaType var1, java.awt.PageAttributes.OrientationRequestedType var2, java.awt.PageAttributes.OriginType var3, java.awt.PageAttributes.PrintQualityType var4, int[] var5) { } 
	public PageAttributes(java.awt.PageAttributes var0) { } 
	public java.lang.Object clone() { return null; }
	public java.awt.PageAttributes.ColorType getColor() { return null; }
	public java.awt.PageAttributes.MediaType getMedia() { return null; }
	public java.awt.PageAttributes.OrientationRequestedType getOrientationRequested() { return null; }
	public java.awt.PageAttributes.OriginType getOrigin() { return null; }
	public java.awt.PageAttributes.PrintQualityType getPrintQuality() { return null; }
	public int[] getPrinterResolution() { return null; }
	public int hashCode() { return 0; }
	public void set(java.awt.PageAttributes var0) { }
	public void setColor(java.awt.PageAttributes.ColorType var0) { }
	public void setMedia(java.awt.PageAttributes.MediaType var0) { }
	public void setMediaToDefault() { }
	public void setOrientationRequested(int var0) { }
	public void setOrientationRequested(java.awt.PageAttributes.OrientationRequestedType var0) { }
	public void setOrientationRequestedToDefault() { }
	public void setOrigin(java.awt.PageAttributes.OriginType var0) { }
	public void setPrintQuality(int var0) { }
	public void setPrintQuality(java.awt.PageAttributes.PrintQualityType var0) { }
	public void setPrintQualityToDefault() { }
	public void setPrinterResolution(int var0) { }
	public void setPrinterResolution(int[] var0) { }
	public void setPrinterResolutionToDefault() { }
}

